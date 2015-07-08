package com.example.too1.blesensorreader;

import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by Admin on 7/1/15.
 */
public class LeDeviceProcessor {
    String []mKnownDeviceName   = {"Big: nRF51","Medium: nRF52","Small: nRF52"};
    int    []mKnownDeviceIcon   = {R.drawable.solarpanel_big, R.drawable.solarpanel_medium, R.drawable.solarpanel_small};

    HashMap<String, Integer> mKnownDevices = new HashMap<String, Integer>();
    HashMap<String, LeDevice> mDeviceMap = new HashMap<String, LeDevice>();

    Calendar mCalendar = new GregorianCalendar();

    public interface LeDeviceProcessorListener {
        void leDeviceAdded(LeDevice leDevice);
    }

    private LeDeviceProcessorListener mLeDeviceProcessorListener = null;

    public LeDeviceProcessor(){
        // Small nRF52
        mKnownDevices.put("CB:85:DA:0C:B8:54", 0);
        // Medium nRF52
        mKnownDevices.put("1B:AB:3D:E3:5C:6D", 1);
        mKnownDevices.put("2E:D2:7A:9A:6E:71", 1);
        // Big nRF51
        mKnownDevices.put("F7:61:E7:01:A3:E2", 2);
    }

    public void setListener(LeDeviceProcessorListener listener){
        mLeDeviceProcessorListener = listener;
    }

    public void reportDevice(BluetoothDevice device, int rssi, byte [] rawData){
        if(mDeviceMap.containsKey(device.getAddress())){
            updateDevice(device, rssi, rawData);
        }
        else {
            if(rssi > -50) {
                addDevice(device, rssi, rawData);
            }
        }
    }

    private void addDevice(BluetoothDevice device, int rssi, byte [] rawData){
        LeDevice newDevice = new LeDevice();

        String logString = "";
        for(int i = 0; i < rawData.length; i++) {
            logString += String.valueOf(rawData[i]);
            logString += ", ";
        }
        Log.d("New DEVICE!", logString);

        if(mKnownDevices.containsKey(device.getAddress())){
            newDevice.pName = mKnownDeviceName[mKnownDevices.get(device.getAddress())];
            newDevice.pKnownDeviceIndex = mKnownDevices.get(device.getAddress());
            newDevice.pDeviceIcon = mKnownDeviceIcon[mKnownDevices.get(device.getAddress())];
        }
        else {
            newDevice.pName = device.getName();
        }

        newDevice.pAddrString = device.getAddress();
        newDevice.pRssi = rssi;
        mCalendar = GregorianCalendar.getInstance();
        newDevice.setPacketTime(mCalendar.getTimeInMillis());
        mDeviceMap.put(device.getAddress(), newDevice);
        mLeDeviceProcessorListener.leDeviceAdded(newDevice);

    }

    private int findServiceData(int uuid, byte []advData){
        int dataIndex = 0;
        int retVal;
        do{
            retVal = 0;
            if(advData[dataIndex+1] == 0x16){
                int tmpUuid = (int)advData[dataIndex+2] | ((int)advData[dataIndex+3] << 8);
                if(tmpUuid == uuid){
                    for(int i = 0; i < (advData[dataIndex]-3); i++)    {
                        retVal = advData[dataIndex + i + 4] | (retVal << 8);
                    }
                }
                return retVal;
            }
            dataIndex += (advData[dataIndex] + 1);
        }while(dataIndex < (advData.length-1));
        return 0;
    }

    private void updateDevice(BluetoothDevice device, int rssi, byte [] rawData){
        LeDevice currentDevice = mDeviceMap.get(device.getAddress());
        mCalendar = GregorianCalendar.getInstance();
        currentDevice.setPacketTime(mCalendar.getTimeInMillis());
        currentDevice.setRssi(rssi);

        int serviceData = findServiceData(0x2A6E, rawData);
        if(serviceData != 0){
            //Log.d("ServiceData!!", String.valueOf(serviceData));
            serviceData = (serviceData >> 8) | (serviceData << 8);
            serviceData &= 0xFFFF;
            currentDevice.mShowTemperature = true;
            currentDevice.mTemperature = (float)serviceData * 0.01f;
        }

        currentDevice.notifyListener();
    }

    public interface LeDeviceChangedListener {
        void leDeviceChanged(LeDevice leDevice);
    }

    public class LeDevice{
        public String   pName, pAddrString;
        public int      pRssi;
        public long     pTimeDifference;
        public int      pKnownDeviceIndex, pDeviceIcon;
        long            mLastReceivedPacketTime;

        public boolean  mShowTemperature, mShowPressure;
        public float    mTemperature, mPressure;


        LeDeviceChangedListener mChangedListener = null;

        public LeDevice(){
            mLastReceivedPacketTime = 0;
            pTimeDifference = 0;
            pKnownDeviceIndex = -1;
            mShowTemperature = mShowPressure = false;
        }

        public void setRssi(int rssi){
            pRssi = rssi;
        }

        public void setPacketTime(long milliseconds){
            if(mLastReceivedPacketTime != 0){
                pTimeDifference = milliseconds - mLastReceivedPacketTime;
                if(pTimeDifference < 100){
                    // TODO: Why is the time interval often so low
                }
            }
            mLastReceivedPacketTime = milliseconds;
        }

        public void setListener(LeDeviceChangedListener listener){
            mChangedListener = listener;
        }

        public void notifyListener(){
            if(mChangedListener != null){
                mChangedListener.leDeviceChanged(this);
            }
        }
    }
}
