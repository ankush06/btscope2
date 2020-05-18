package com.example.myapplication1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication1.bt.BTThread;

import java.util.ArrayList;
import java.util.Set;

public class scan_input extends AppCompatActivity {

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static final int REQUEST_BLUETOOTH = 1;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    ListView listView;
    private BluetoothAdapter BTAdapter;
    private static final String TAG = "scan_input";

    private ArrayAdapter mPairedDevicesArrayAdapter;
    private ArrayAdapter mNewDevicesArrayAdapter;
    private ArrayList<String> list ;

    private ArrayAdapter arrayAdapter ;
    public static final String DEVICE_NAME = "device_name";
    ListView newDevicesListView;

    // Create a BroadcastReceiver for ACTION_FOUND.
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
                if(deviceName == null)
                {
                    mNewDevicesArrayAdapter.add("UNKNOWN" + "-" + device.getAddress());
                }
                else
                {
                    mNewDevicesArrayAdapter.add(device.getName() + "-" + device.getAddress());
                }
            }
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device .getBondState() == BluetoothDevice.BOND_BONDED) {
                    //means device paired
                }

            }
        }
    };
    private void doDiscovery(){
        if (BTAdapter.isDiscovering()) {
            BTAdapter.cancelDiscovery();
        }
        Log.i(TAG,"Starting discovery");
        if(BTAdapter.startDiscovery() == true)
        {
            Log.i(TAG,"Discovery started\n");
        }
        else
        {
            Log.e(TAG,"Discovery start failed\n");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(scan_input.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COARSE_LOCATION);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_input);

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.activity_scan_input);
        mNewDevicesArrayAdapter = new ArrayAdapter(this, R.layout.activity_scan_input);


        // Phone does not support Bluetooth so let the user know and exit.
        if (BTAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        if (!BTAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_BLUETOOTH);
        }

        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
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
        registerReceiver(receiver, filter);

        doDiscovery();

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(scan_input.EXTRA_MESSAGE);

        //listView= (ListView) findViewById(R.id.id_input_list_view);
        list =new ArrayList<>();
        //list.add("ZIFI");
        mNewDevicesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1,list);
        newDevicesListView = (ListView) findViewById(R.id.id_input_list_view);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        //newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        //list.add("JBL");
        //mNewDevicesArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1,list);
        //arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, list);

        //ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1, list);
        //listView.setAdapter(arrayAdapter);

        newDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = ((TextView) view).getText().toString();
                Log.i(TAG,"selected address info "+ info);
                String address = info.substring(info.length() - 17);
                // Calling Application class (see application tag in AndroidManifest.xml)
                final BTinterface btInterface = (BTinterface) getApplicationContext();
                Log.i(TAG,"selected address "+ address);
                Toast.makeText(scan_input.this,"clicked Item "+position+" "+list.get(position).toString(),Toast.LENGTH_SHORT).show();
                Log.i(TAG,"slected "+ list.get(position).toString());
                btInterface.setInputDevice_MAC(list.get(position).toString());
                btInterface.setInputDevice_MAC(address);
            }

        });
        /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Calling Application class (see application tag in AndroidManifest.xml)
                final BTinterface btInterface = (BTinterface) getApplicationContext();
                Toast.makeText(scan_input.this,"clicked Item "+position+" "+list.get(position).toString(),Toast.LENGTH_SHORT).show();
                Log.i(TAG,"slected "+ list.get(position).toString());
                btInterface.setInputDevice_MAC(list.get(position).toString());
            }

        });*/
    }
/*
    // The on-click listener for all devices in the ListViews
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int position, long id) {
            // Cancel discovery because it's costly and we're about to connect
            BTAdapter.cancelDiscovery();
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            Log.i(TAG,"selected address info "+ info);
            String address = info.substring(info.length() - 17);
            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            final BTinterface btInterface = (BTinterface) getApplicationContext();
            Log.i(TAG,"selected address "+ address);
            btInterface.setInputDevice_MAC(address);
            Toast.makeText(scan_input.this,"clicked Item "+position+" "+list.get(position).toString(),Toast.LENGTH_SHORT).show();

            //finish();
        }
    };
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"Unregistring receiver");
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    public void refereshScanForInputDevice(View view) {
        doDiscovery();
        mNewDevicesArrayAdapter.clear();
         /*
        Log.i(TAG,"initiating discovery thread");
        BTThread bt = new BTThread();
        //bt.start();
        bt.performDiscovery(BTAdapter);
        Log.i(TAG,"done with discovery thread");

        Intent intent = new Intent(this, scan_input.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        intent.putExtra(Intent.EXTRA_TEXT, "hellow");
        startActivity(intent);
        //return "DeviceName1";
       */
    }
}
