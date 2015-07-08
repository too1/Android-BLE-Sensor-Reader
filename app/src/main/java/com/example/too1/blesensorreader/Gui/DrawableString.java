package com.example.too1.blesensorreader.Gui;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by too1 on 03.07.2015.
 */
public class DrawableString {
    String  mString;
    int     mLeft, mTop, mDrawLeft, mDrawTop;

    public enum DrawableStringAlignment {
        LEFT, RIGHT, MID, LEFT_UNDER, RIGHT_UNDER, MID_UNDER
    }
    DrawableStringAlignment mAlignment;
    Paint                   mPaint;

    public DrawableString(String string){
        mString = string;
        mLeft = mTop = mDrawLeft = mDrawTop = 0;
        mAlignment = DrawableStringAlignment.LEFT;
    }

    public void setPaint(Paint textPaint){
        mPaint = textPaint;
        invalidatePos();
    }

    public void setString(String string){
        mString = (string != null ? string : "");
        invalidatePos();
    }

    public String getString(){
        return mString;
    }

    public void invalidatePos(){
        int mTextWidth = (int)mPaint.measureText(mString);
        switch(mAlignment){
            case LEFT:
                mDrawLeft = mLeft;
                mDrawTop = mTop;
                break;
            case RIGHT:
                mDrawLeft = mLeft - mTextWidth;
                mDrawTop = mTop;
                break;
            case MID:
                mDrawLeft = mLeft - (mTextWidth / 2);
                mDrawTop = mTop;
                break;
            case LEFT_UNDER:
                break;
            case RIGHT_UNDER:
                break;
            case MID_UNDER:
                mDrawLeft = mLeft - (mTextWidth / 2);
                mDrawTop = mTop + (int)mPaint.getTextSize();
                break;
        }
    }

    public void setPos(int left, int top){
        mLeft = left;
        mTop = top;
        invalidatePos();
    }

    public void setPos(int left, int top, DrawableStringAlignment alignment){
        mAlignment = alignment;
        setPos(left, top);
    }

    public void draw(Canvas canvas){
        canvas.drawText(mString, mDrawLeft, mDrawTop, mPaint);
    }

    public void drawShadowed(Canvas canvas, float shadowOffset){
        int textColor = mPaint.getColor();
        mPaint.setColor(0xFF000000);
        canvas.drawText(mString, mDrawLeft + shadowOffset, mDrawTop + shadowOffset, mPaint);
        mPaint.setColor(textColor);
        canvas.drawText(mString, mDrawLeft, mDrawTop, mPaint);
    }
}
