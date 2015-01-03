package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.receiptofi.checkout.HomePageActivity_OLD;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.Protocol;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.models.ReceiptElement;
import com.receiptofi.checkout.utils.AppUtils;
import com.receiptofi.checkout.utils.UserUtils;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class ViewReceiptPage extends Fragment {

    private View screen;
    private Context context;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        context = getActivity();

        String receiptId = null;
        String blobId = null;
        if (getArguments() != null && getArguments().getString("receiptId") != null) {
            receiptId = getArguments().getString("receiptId");
        }
        if (getArguments() != null && getArguments().getString("blobId") != null) {
            blobId = getArguments().getString("blobId");
        }

        ((HomePageActivity_OLD) context).showLoader("Fetching receipt details");
        final File imageFile = new File(AppUtils.getImageDir() + File.separator + blobId + ".png");

        ArrayList<NameValuePair> headerData = new ArrayList<>();
        headerData.add(new BasicNameValuePair(API.key.XR_AUTH, UserUtils.getAuth()));
        headerData.add(new BasicNameValuePair(API.key.XR_MAIL, UserUtils.getEmail()));

        ExternalCall.AsyncRequest(
                headerData,
                API.VIEW_RECEIPT_DETAIL + receiptId + ".json",
                Protocol.GET.name(),
                new ResponseHandler() {

                    @Override
                    public void onSuccess(Header[] arr, String body) {
                        // TODO Auto-generated method stub
                        ((HomePageActivity_OLD) context).hideLoader();
                        ArrayList<ReceiptElement> elements = null; //ResponseParser.getReceiptDetails(response);
                        displayElements(elements);
                        if (imageFile.exists()) {
                            disaplayImage(imageFile.getAbsolutePath());
                        }
                    }


                    @Override
                    public void onException(Exception exception) {
                        // TODO Auto-generated method stub
                        ((HomePageActivity_OLD) context).hideLoader();
                    }

                    @Override
                    public void onError(int statusCode, String error) {
                        // TODO Auto-generated method stub
                        ((HomePageActivity_OLD) context).hideLoader();
                    }
                });


        if (!imageFile.exists()) {
            ExternalCall.downloadImage(context, imageFile, API.DOWNLOAD_IMAGE + blobId + ".json", new ResponseHandler() {

                @Override
                public void onSuccess(Header[] arr, String body) {
                    // TODO Auto-generated method stub
                    disaplayImage(imageFile.getAbsolutePath());
                }

                @Override
                public void onException(Exception exception) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onError(int statusCode, String error) {
                    // TODO Auto-generated method stub

                }
            });

        }

    }

    private void disaplayImage(final String imgFilePath) {

        ((HomePageActivity_OLD) getActivity()).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ((ImageView) ((Activity) context).findViewById(R.id.receiptImage)).setImageBitmap(BitmapFactory.decodeFile(imgFilePath));
                ((Activity) context).findViewById(R.id.receiptImage).setVisibility(View.VISIBLE);

                int width = ((HomePageActivity_OLD) context).getHeight();
                int height = ((HomePageActivity_OLD) context).getHeight();

                height = width * ((HomePageActivity_OLD) context).getAspectRatio();
                ((Activity) context).findViewById(R.id.receiptImage).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, height));

            }
        });
    }

    private void displayElements(ArrayList<ReceiptElement> elements) {
        // TODO Auto-generated method stub
        if (elements != null) {

            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup elementsContainer = (ViewGroup) ((Activity) context).findViewById(R.id.receiptElements);

            for (ReceiptElement element : elements) {
                final View view = inflator.inflate(R.layout.receiptdetail_element_row, null);
                ((TextView) view.findViewById(R.id.elementName)).setText(element.name);
                ((TextView) view.findViewById(R.id.elemntPrice)).setText("$" + element.price);
                ((HomePageActivity_OLD) getActivity()).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        String receiptName = getArguments().getString("receiptName");
                        if (receiptName != null) {
                            ((TextView) ((Activity) context).findViewById(R.id.receiptName)).setText(receiptName);
                        }

                        Date dateISO8601 = null;
                        String date = getArguments().getString("date");
//						if(date!=null){
//
//							try {
//								dateISO8601=ISO8601DateParser.parse(date);
//								
//							} catch (ParseException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
                        if (date != null) {
//						if(dateISO8601!=null){
//							((TextView)((Activity)context).findViewById(R.id.receiptName)).setText(dateISO8601.getMonth()+" "+dateISO8601.getDate()+" , "+dateISO8601.getYear());
                            ((TextView) ((Activity) context).findViewById(R.id.date)).setText(date.substring(0, 10));
                        }

                        double totalPrice = getArguments().getDouble("totalPrice");
                        if (totalPrice > 0) {
                            ((TextView) ((Activity) context).findViewById(R.id.totalPrice)).setText("$" + totalPrice);
                        }

                        ((Activity) context).findViewById(R.id.vrp_title_container).setVisibility(View.VISIBLE);
                        ((Activity) context).findViewById(R.id.vrp_header).setVisibility(View.VISIBLE);


                        elementsContainer.addView(view);
                    }
                });
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.receipt_detail, null);
        screen = view;
        return screen;
    }
}
