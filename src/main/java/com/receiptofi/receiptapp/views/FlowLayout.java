package com.receiptofi.receiptapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

/**
 * User: PT
 * Date: 4/25/15 3:22 PM
 */
public class FlowLayout extends ViewGroup {

    private int paddingHorizontal;
    private int paddingVertical;
    private boolean isRTL = false;

    public enum Align {Left, Right, center}

    private Align align = Align.Left;

    public FlowLayout(Context context) {
        super(context);
        init();
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paddingHorizontal = 5;//getResources().getDimensionPixelSize(R.dimen.flowlayout_horizontal_padding);
        paddingVertical = 5;//getResources().getDimensionPixelSize(R.dimen.flowlayout_vertical_padding);
    }

    public void setRTL(boolean isRTL) {
        this.isRTL = isRTL;
    }

    public void setAlign(Align align) {
        this.align = align;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isRTL) {
            // 100 is a dummy number, widthMeasureSpec should always be EXACTLY for FlowLayout
            int myWidth = resolveSize(100, widthMeasureSpec);
            int childLeft = myWidth - getPaddingRight();
            int childTop = getPaddingTop();
            int lineHeight = 0;
            int wantedHeight = 0;

            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                // let the child measure itself
                child.measure(
                        getChildMeasureSpec(widthMeasureSpec, 0, child.getLayoutParams().width),
                        getChildMeasureSpec(heightMeasureSpec, 0, child.getLayoutParams().height));
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                // lineheight is the height of current line, should be the height of the heightest view
                lineHeight = Math.max(childHeight, lineHeight);
                if (childLeft - (childWidth + getPaddingLeft()) < 0) {
                    // wrap this line
                    childLeft = myWidth - getPaddingRight();
                    childTop += paddingVertical + lineHeight;
                    lineHeight = 0;
                }
                childLeft -= childWidth + paddingHorizontal;
            }

            wantedHeight += childTop + lineHeight + getPaddingBottom();
            setMeasuredDimension(myWidth, resolveSize(wantedHeight, heightMeasureSpec));
        } else {

            int childLeft = getPaddingLeft();
            int childTop = getPaddingTop();
            int lineHeight = 0;
            // 100 is a dummy number, widthMeasureSpec should always be EXACTLY for FlowLayout
            int myWidth = resolveSize(100, widthMeasureSpec);
            int wantedHeight = 0;

            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                // let the child measure itself
                child.measure(
                        getChildMeasureSpec(widthMeasureSpec, 0, child.getLayoutParams().width),
                        getChildMeasureSpec(heightMeasureSpec, 0, child.getLayoutParams().height));
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                // lineheight is the height of current line, should be the height of the heightest view
                lineHeight = Math.max(childHeight, lineHeight);
                if (childWidth + childLeft + getPaddingRight() > myWidth) {
                    // wrap this line
                    childLeft = getPaddingLeft();
                    childTop += paddingVertical + lineHeight;
                    lineHeight = 0;
                }
                childLeft += childWidth + paddingHorizontal;
            }

            wantedHeight += childTop + lineHeight + getPaddingBottom();
            setMeasuredDimension(myWidth, resolveSize(wantedHeight, heightMeasureSpec));
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        LinkedList<MyView> row = new LinkedList<>();
        if (isRTL) {
            int layoutWidth = right - left;
            int childRight = layoutWidth - getPaddingRight();
            int childTop = getPaddingTop();
            int lineHeight = 0;

            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                lineHeight = Math.max(childHeight, lineHeight);

                if (childRight - (childWidth + getPaddingLeft()) < 0) {
                    applyAlign(row, row.getLast().left - getPaddingLeft());
                    row.clear();
                    childRight = layoutWidth - getPaddingRight();
                    childTop += paddingVertical + lineHeight;
                    lineHeight = 0;
                }

                row.add(new MyView(child, childRight - childWidth, childTop, childRight, childTop + childHeight));
                child.layout(childRight - childWidth, childTop, childRight, childTop + childHeight);
                childRight -= childWidth + paddingHorizontal;
            }
        } else {

            int childLeft = getPaddingLeft();
            int childTop = getPaddingTop();
            int lineHeight = 0;
            int myWidth = right - left;

            for (int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                lineHeight = Math.max(childHeight, lineHeight);

                if (childWidth + childLeft + getPaddingRight() > myWidth) {
                    applyAlign(row, myWidth - childLeft - getPaddingRight());
                    row.clear();
                    childLeft = getPaddingLeft();
                    childTop += paddingVertical + lineHeight;
                    lineHeight = 0;
                }

                row.add(new MyView(child, childLeft, childTop, childLeft + childWidth, childTop + childHeight));
                child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
                childLeft += childWidth + paddingHorizontal;
            }
        }
    }

    private void applyAlign(LinkedList<MyView> row, int gap) {
        switch (align) {
            case Left:
                if (isRTL) {
                    for (MyView v : row) {
                        v.left -= gap;
                        v.right -= gap;
                        v.layout();
                    }
                } else {
                    for (MyView v : row) {
                        v.layout();
                    }
                }
                break;
            case Right:
                if (isRTL) {
                    for (MyView v : row) {
                        v.layout();
                    }
                } else {
                    for (MyView v : row) {
                        v.left += gap;
                        v.right += gap;
                        v.layout();
                    }
                }
                break;
            case center: {
                int gap2 = gap / 2;
                if (isRTL) {
                    for (MyView v : row) {
                        v.left -= gap2;
                        v.right -= gap2;
                        v.layout();
                    }
                } else {
                    for (MyView v : row) {
                        v.left += gap2;
                        v.right += gap2;
                        v.layout();
                    }
                }
            }
        }
    }

    class MyView {

        int left, top, right, bottom;
        private View view;

        MyView(View view, int left, int top, int right, int bottom) {

            this.view = view;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public void layout() {
            view.layout(left, top, right, bottom);
        }
    }
}