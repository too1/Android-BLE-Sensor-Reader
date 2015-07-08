package com.example.too1.blesensorreader;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.too1.blesensorreader.Gui.BleAdvertiser;
import com.example.too1.blesensorreader.Gui.DynamicGridView;
import com.example.too1.blesensorreader.R;


public class MainPage extends ActionBarActivity implements LeDeviceProcessor.LeDeviceProcessorListener{

    BluetoothAdapter mBluetoothAdapter;
    DynamicGridView  mMainGrid;
    LeDeviceProcessor mLeDeviceProcessor;

    static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // GUI config
        mMainGrid = (DynamicGridView)findViewById(R.id.main_grid_view);

        // BLE config
        mLeDeviceProcessor = new LeDeviceProcessor();
        mLeDeviceProcessor.setListener(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mBluetoothAdapter.startLeScan(leScanCallback);
    }
    int counter = 0;
    public BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            mLeDeviceProcessor.reportDevice(device, rssi, scanRecord);
        }
    };


    @Override
    public void leDeviceAdded(LeDeviceProcessor.LeDevice leDevice) {
        BleAdvertiser newAdvertiser = new BleAdvertiser(getApplicationContext());
        newAdvertiser.setLinkedDevice(leDevice);
        mMainGrid.addView(newAdvertiser);
        mMainGrid.invalidate();
        mMainGrid.requestLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
