package com.example.too1.blesensorreader.Gui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.too1.blesensorreader.LeDeviceProcessor;
import com.example.too1.blesensorreader.R;

import java.util.Random;

/**
 * Created by too1 on 03.07.2015.
 */
public class BleAdvertiser extends View implements LeDeviceProcessor.LeDeviceChangedListener {

    boolean         mShowName;
    DrawableString  mStringName         = new DrawableString("");

    DrawableString  mStringRssi         = new DrawableString("");
    DrawableString  mStringInterval     = new DrawableString("");

    DrawableString  mStringTemperature  = new DrawableString("");

    Rect            mRect, mRectRssiSlider, mRectIntervalSlider;
    int             mColorBackground, mColorTextName=0xFFe9efff, mColorTextRssi = 0xFF54f079, mColorTextInterval = 0xFF51bde5;
    Paint           mPaintTextName, mPaintTextRssi, mPaintTextInterval, mPaintTextShadow, mPaintBackground;
    float           mShadowOffset;
    float           mRssiSliderFactor, mIntervalSliderFactor;

    int             left, right, top, bottom;
    float           pW, pH, maxSquareSide;

    Drawable        mGraphicsBackground, mGraphicsFrame, mGraphicsIcon = null;

    Resources       mResMng;

    LeDeviceProcessor.LeDevice mLinkedDevice = null;

    static Random   mRandom = new Random();

    public BleAdvertiser(Context context) {
        super(context);
        init(context);
    }

    public BleAdvertiser(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.BleAdvertiser, 0, 0);

        try {
            mShowName = a.getBoolean(R.styleable.BleAdvertiser_showName, false);
            mStringName.setString(a.getString(R.styleable.BleAdvertiser_name));
            mStringRssi.setString(a.getString(R.styleable.BleAdvertiser_rssi));
        } finally {
            a.recycle();
        }

        init(context);
    }

    public BleAdvertiser(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setName(String name){
        mStringName.setString(name);
    }

    public void setRssi(int rssi){
        mStringRssi.setString(String.valueOf(rssi) + " dBm");
    }

    public void setInterval(long interval){
        mStringInterval.setString(String.valueOf(interval) + " ms");
    }

    public void setTemperature(float temperature){
        mStringTemperature.setString(String.valueOf(temperature) + " C");
    }

    public void setLinkedDevice(LeDeviceProcessor.LeDevice linkedDevice){
        mLinkedDevice = linkedDevice;
        mLinkedDevice.setListener(this);
        if(mLinkedDevice.pName == null) setName(mLinkedDevice.pAddrString);
        else setName(mLinkedDevice.pName);
        setRssi(mLinkedDevice.pRssi);
        setInterval(mLinkedDevice.pTimeDifference);
        if(linkedDevice.mShowTemperature) setTemperature(linkedDevice.mTemperature);
        mRssiSliderFactor = (float)(mLinkedDevice.pRssi + 100) / 90.0f;
        if(mRssiSliderFactor < 0.0f) mRssiSliderFactor = 0.0f;
        else if(mRssiSliderFactor > 1.0f) mRssiSliderFactor = 1.0f;
        mIntervalSliderFactor = ((float)Math.log(mLinkedDevice.pTimeDifference) / 4.5f) - 1.0f;
        if(mIntervalSliderFactor < 0.0f) mIntervalSliderFactor = 0.0f;
        else if(mIntervalSliderFactor > 1.0f) mIntervalSliderFactor = 1.0f;
        mIntervalSliderFactor = 1.0f - mIntervalSliderFactor;
        if(linkedDevice.pKnownDeviceIndex != -1){
            if(mGraphicsIcon == null) {
                mGraphicsIcon = mResMng.getDrawable(linkedDevice.pDeviceIcon);
            }
        }
        else {
            mGraphicsIcon = null;
        }
        invalidate();
        requestLayout();
    }

    @Override
    public void leDeviceChanged(LeDeviceProcessor.LeDevice leDevice) {
        setLinkedDevice(leDevice);
    }

    void init(Context context){
        mRect = new Rect();
        mRectRssiSlider = new Rect();
        mRectIntervalSlider = new Rect();

        mResMng = context.getResources();
        mGraphicsBackground = mResMng.getDrawable(R.drawable.adv_device_background);
        mGraphicsFrame = mResMng.getDrawable(R.drawable.adv_device_frame);

        Typeface typeFace=Typeface.createFromAsset(context.getAssets(),"fonts/AGENCYB.TTF");

        mPaintTextShadow = new Paint();
        mPaintTextShadow.setColor(0xFF000000);
        mPaintTextShadow.setAntiAlias(true);
        mPaintTextShadow.setTypeface(typeFace);

        mPaintTextName = new Paint();
        mPaintTextName.setColor(mColorTextName);
        mPaintTextName.setAntiAlias(true);
        mPaintTextName.setTypeface(typeFace);
        mStringName.setPaint(mPaintTextName);

        mPaintTextRssi = new Paint();
        mPaintTextRssi.setAntiAlias(true);
        mPaintTextRssi.setTypeface(typeFace);
        mStringRssi.setPaint(mPaintTextRssi);

        mStringInterval.setPaint(mPaintTextRssi);
        mStringTemperature.setPaint(mPaintTextRssi);

        float hsv[] = {0.4f, 0.5f, 0.8f};
        hsv[0] = mRandom.nextFloat()*360.0f;
        hsv[1] = 0.0f + mRandom.nextFloat() * 0.9f;
        hsv[2] = 0.4f + mRandom.nextFloat() * 0.6f;
        mColorBackground = Color.HSVToColor(hsv);
        mPaintBackground = new Paint();
        mPaintBackground.setColor(mColorBackground);
        mPaintBackground.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        pW = (float)(w - getPaddingLeft() - getPaddingRight());
        pH = (float)(h - getPaddingBottom() - getPaddingTop());
        maxSquareSide = Math.min(pW, pH);

        left = (int)(pW - maxSquareSide) / 2;
        right = w - left;
        top = (int)(pH - maxSquareSide) / 2;
        bottom = h - top;

        mGraphicsBackground.setBounds(left, top, right, bottom);
        mGraphicsFrame.setBounds(left, top, right, bottom);

        if(mGraphicsIcon != null) {
            mGraphicsIcon.setBounds(left + (int) (maxSquareSide * 0.2f), top + (int) (maxSquareSide * 0.3f),
                                    left + (int) (maxSquareSide * 0.6f), top + (int) (maxSquareSide * 0.6f));
        }

        mPaintTextName.setTextSize(maxSquareSide * 0.11f);
        mStringName.setPos(left + (int) (maxSquareSide * 0.5f), top + (int)(maxSquareSide * 0.07f), DrawableString.DrawableStringAlignment.MID_UNDER);

        mPaintTextRssi.setTextSize(maxSquareSide * 0.07f);

        int valuesLeft = left + (int)(maxSquareSide * 0.42f);
        int rssiTop = bottom - (int)(maxSquareSide * 0.2f);
        mStringRssi.setPos(left + (int)(maxSquareSide * 0.83f), top + (int)(maxSquareSide * 0.925f), DrawableString.DrawableStringAlignment.RIGHT);

        int intervalTop = bottom - (int)(maxSquareSide * 0.05f);
        mStringInterval.setPos(left + (int)(maxSquareSide * 0.15f), top + (int)(maxSquareSide * 0.925f), DrawableString.DrawableStringAlignment.LEFT);

        mStringTemperature.setPos(left + (int) (maxSquareSide * 0.5f), top + (int) (maxSquareSide * 0.80f), DrawableString.DrawableStringAlignment.MID);
        mShadowOffset = maxSquareSide * 0.005f;

        mRect.set(left, top, right, bottom);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mGraphicsBackground.draw(canvas);

        mPaintTextRssi.setColor(mColorTextRssi);
        mStringRssi.drawShadowed(canvas, mShadowOffset);
        mRectRssiSlider.set(left + (int) (maxSquareSide * 0.87f), top + (int) (maxSquareSide * (0.95f - mRssiSliderFactor * 0.75f)),
                left + (int) (maxSquareSide * 0.95f), top + (int) (maxSquareSide * 0.95f));
        mPaintBackground.setColor(mColorTextRssi);
        canvas.drawRect(mRectRssiSlider, mPaintBackground);

        mPaintTextRssi.setColor(mColorTextInterval);
        mStringInterval.drawShadowed(canvas, mShadowOffset);
        mRectIntervalSlider.set(left + (int) (maxSquareSide * 0.04f), top + (int) (maxSquareSide * (0.95f - mIntervalSliderFactor * 0.75f)),
                left + (int) (maxSquareSide * 0.12f), top + (int) (maxSquareSide * 0.95f));
        mPaintBackground.setColor(mColorTextInterval);
        canvas.drawRect(mRectIntervalSlider, mPaintBackground);

        if(mGraphicsIcon != null){
            //mGraphicsIcon.setBounds(left, top, right, bottom);
            mGraphicsIcon.draw(canvas);
        }

        if(mStringTemperature.getString() != ""){
            mStringTemperature.draw(canvas);
        }

        mGraphicsFrame.draw(canvas);

        mStringName.drawShadowed(canvas, mShadowOffset);
    }
}
