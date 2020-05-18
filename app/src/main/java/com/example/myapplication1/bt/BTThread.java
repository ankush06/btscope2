package com.example.myapplication1.bt;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication1.scan_input;

import java.util.Set;

public class BTThread extends Thread {
    private static final String TAG = "BTThread";

    public BTThread() {    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG,"onReceive............\n"+ action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.d(TAG, "Bluetooth device found\n");
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG,"found device "+deviceName+ " " + deviceHardwareAddress);
            }
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device .getBondState() == BluetoothDevice.BOND_BONDED) {
                    //means device paired
                }

            }
        }
    };

    public void performDiscovery(BluetoothAdapter bTAdapter) {
        Log.i(TAG, "performing discovery");
        Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();
        Log.i(TAG,"looking for paired devices");
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG, "pairedDevices: "+ deviceName);
            }
        }
        Log.i(TAG,"done with looking for paired devices");

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //registerReceiver(receiver, filter);

        Log.i(TAG,"Starting discovery");
        if(bTAdapter.startDiscovery() == true)
        {
            Log.i(TAG,"Discovery started\n");
        }
        else
        {
            Log.e(TAG,"Discovery start failed\n");
        }

        while (bTAdapter.isDiscovering())
        {
            // Log.i(TAG,"waiting for discvery to complete");
        }
        Log.i(TAG,"done with discovery");

    }

    public void run()
    {
        System.out.println("starting discovery Thread");
    }
}
