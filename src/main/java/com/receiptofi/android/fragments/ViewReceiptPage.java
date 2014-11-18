package com.receiptofi.android.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import com.receiptofi.android.HomePageActivity;
import com.receiptofi.android.R;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.http.ResponseHandler;
import com.receiptofi.android.http.ResponseParser;
import com.receiptofi.android.models.RecieptElement;
import com.receiptofi.android.utils.AppUtils;
import com.receiptofi.android.utils.UserUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

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

public class ViewReceiptPage extends Fragment {

    View screen;
    Context context;

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

        ((HomePageActivity) context).showLoader("Fetching receipt details");
        final File imageFile = new File(AppUtils.getImageDir() + File.separator + blobId + ".png");

        ArrayList<NameValuePair> headerData = new ArrayList<NameValuePair>();
        headerData.add(new BasicNameValuePair(API.key.XR_AUTH, UserUtils.getAuth()));
        headerData.add(new BasicNameValuePair(API.key.XR_MAIL, UserUtils.getEmail()));

        HTTPUtils.AsyncRequest(headerData, API.VIEW_RECEIPT_DETAIL + receiptId + ".json", HTTPUtils.HTTP_METHOD_GET, new ResponseHandler() {

            @Override
            public void onSuccess(String response) {
                // TODO Auto-generated method stub
                ((HomePageActivity) context).hideLoader();
                ArrayList<RecieptElement> elements = ResponseParser.getReceiptDetails(response);
                displayElements(elements);
                if (imageFile.exists()) {
                    disaplayImage(imageFile.getAbsolutePath());
                }
            }


            @Override
            public void onExeption(Exception exception) {
                // TODO Auto-generated method stub
                ((HomePageActivity) context).hideLoader();
            }

            @Override
            public void onError(String Error) {
                // TODO Auto-generated method stub
                ((HomePageActivity) context).hideLoader();
            }
        });


        if (!imageFile.exists()) {
            HTTPUtils.downloadImage(context, imageFile, API.DOWNLOAD_IMAGE + blobId + ".json", new ResponseHandler() {

                @Override
                public void onSuccess(String response) {
                    // TODO Auto-generated method stub
                    disaplayImage(imageFile.getAbsolutePath());
                }

                @Override
                public void onExeption(Exception exception) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onError(String Error) {
                    // TODO Auto-generated method stub

                }
            });

        }

    }

    private void disaplayImage(final String imgFilePath) {

        ((HomePageActivity) getActivity()).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                ((ImageView) ((Activity) context).findViewById(R.id.receiptImage)).setImageBitmap(BitmapFactory.decodeFile(imgFilePath));
                ((Activity) context).findViewById(R.id.receiptImage).setVisibility(View.VISIBLE);

                int width = ((HomePageActivity) context).getHeight();
                int height = ((HomePageActivity) context).getHeight();

                height = width * ((HomePageActivity) context).getAspectRatio();
                ((Activity) context).findViewById(R.id.receiptImage).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, height));

            }
        });
    }

    private void displayElements(ArrayList<RecieptElement> elements) {
        // TODO Auto-generated method stub
        if (elements != null) {

            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final ViewGroup elementsContainer = (ViewGroup) ((Activity) context).findViewById(R.id.receiptElements);

            for (RecieptElement element : elements) {
                final View view = inflator.inflate(R.layout.receiptdetail_element_row, null);
                ((TextView) view.findViewById(R.id.elementName)).setText(element.name);
                ((TextView) view.findViewById(R.id.elemntPrice)).setText("$" + element.price);
                ((HomePageActivity) getActivity()).runOnUiThread(new Runnable() {

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
