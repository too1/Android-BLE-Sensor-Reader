package com.example.too1.blesensorreader.Gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by too1 on 03.07.2015.
 */
public class DynamicGridView extends ViewGroup {


    public DynamicGridView(Context context) {
        super(context);
    }

    public DynamicGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        //get the available size of child view
        int childLeft = this.getPaddingLeft();
        int childTop = this.getPaddingTop();
        int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        int childWidth = childRight - childLeft;
        int childHeight = childBottom - childTop;

        // TOO
        if(count > 0) {
            int childColNum, childRowNum, childColWidth, childRowHeight;
            float aspectRatio;
            if (childHeight > childWidth) {
                aspectRatio = (float) childHeight / (float) childWidth;
                childColNum = (int) Math.floor(Math.sqrt((float) count / aspectRatio) + 0.6);
                if(childColNum == 0) childColNum = 1;
                childRowNum = (count + childColNum - 1) / childColNum;
                childColWidth = childWidth / childColNum;
                childRowHeight = childHeight / childRowNum;
            } else {
                aspectRatio = (float) childWidth / (float) childHeight;
                childRowNum = (int) Math.floor(Math.sqrt((float) count / aspectRatio) + 0.6);
                childColNum = (count + childRowNum - 1) / childRowNum;
                childColWidth = childWidth / childColNum;
                childRowHeight = childHeight / childRowNum;
            }

            maxHeight = 0;

            int curCol = 0, curRow = 0;
            //walk through each child, and arrange it from left to right
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != GONE) {
                    //Get the maximum size of the child
                    child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                            MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
                    curWidth = child.getMeasuredWidth();
                    curHeight = child.getMeasuredHeight();

                    //do the layout
                    child.layout(childLeft + curCol * childColWidth, childTop + curRow * childRowHeight,
                            childLeft + (curCol + 1) * childColWidth, childTop + (curRow + 1) * childRowHeight);

                    if (++curCol >= childColNum) {
                        curCol = 0;
                        curRow++;
                    }
                }
            }
        }
    }

}
