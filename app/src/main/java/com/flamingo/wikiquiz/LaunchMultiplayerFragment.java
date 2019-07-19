package com.flamingo.wikiquiz;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.UUID;

public class LaunchMultiplayerFragment extends Fragment {

    private UUID app_uuid = UUID.fromString("29a7e265-1ad1-4879-873a-51e316bdfa2b");
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> deviceNamesAdapter;


    //TODO maybe delete
    //private ConnectedThread connectedThread; // bluetooth background worker thread to send and receive data
    //private BluetoothSocket btSocket = null; // bi-directional client-to-client data path


    public LaunchMultiplayerFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_launch_multiplayer, container, false);

        deviceNamesAdapter = new ArrayAdapter<String> (getContext(), android.R.layout.simple_list_item_1);
        bluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();

        ListView devicesListView = view.findViewById(R.id.devicesListView);
        devicesListView.setAdapter(deviceNamesAdapter);
        devicesListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //
//                if(!bluetoothAdapter.isEnabled()) {
//                    Toast.makeText(getContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
           //  mBluetoothStatus.setText("Connecting...");
               // Get the device MAC address, which is the last 17 chars in the View
               String info = ((TextView) view).getText().toString();
              final String address = info.substring(info.length() - 17);
              final String name = info.substring(0,info.length() - 17);
//
//                // Spawn a new thread to avoid blocking the GUI one
//                new Thread()
//                {
//                    public void run() {
//                        boolean fail = false;
//
//                        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
//
//                        try {
//                          btSocket  = createBluetoothSocket(device);
//                        } catch (IOException e) {
//                            fail = true;
//                            Toast.makeText(getContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
//                        }
//                        // Establish the Bluetooth socket connection.
//                        try {
//                            btSocket.connect();
//                        } catch (IOException e) {
//                            try {
//                                fail = true;
//                                btSocket.close();
//                                handler.obtainMessage(CONNECTING_STATUS, -1, -1)
//                                        .sendToTarget();
//                            } catch (IOException e2) {
//                                //insert code to deal with this
//                                Toast.makeText(getContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        if(fail == false) {
//                            mConnectedThread = new ConnectedThread(btSocket);
//                            mConnectedThread.start();
//
//                            handler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
//                                    .sendToTarget();
//                        }
//                    }
//                }.start();
//            }
//
//            }
    }});

        Button hostBtn = view.findViewById(R.id.hostSession);

        hostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEnableBluetooth()) {
                    Intent discoverableIntent =
                            new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                    startActivity(discoverableIntent);
                    Toast.makeText(getContext(), "Device now discoverable", Toast.LENGTH_SHORT).show();
                    AcceptThread acceptThread = new AcceptThread();
                    acceptThread.run();
                }
                else {
                    // Device does not support bluetooth
                }
            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 56789);
        }
//        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

        Button joinBtn = view.findViewById(R.id.joinSession);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEnableBluetooth()) {
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    bluetoothAdapter.startDiscovery();

                    getActivity().getApplicationContext().registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

                    Toast.makeText(getContext(), "Began discovery", Toast.LENGTH_SHORT).show();
                    //ConnectThread connectThread = new ConnectThread()
                }
                else {
                    // Device does not support bluetooth
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.getActivity().unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                Toast.makeText(context, "ACTION_FOUND", Toast.LENGTH_SHORT).show();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                deviceNamesAdapter.add(deviceName + "\n" + deviceHardwareAddress);
                deviceNamesAdapter.notifyDataSetChanged();
            }
        }
    };

    public boolean checkEnableBluetooth() {
        int REQUEST_ENABLE_BT = 1234;
        if (bluetoothAdapter == null) {
            return false;
        }
        if (!bluetoothAdapter.isEnabled()) {
            deviceNamesAdapter.clear();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        return true;
    }

//    public BluetoothDevice findBluetoothDevice() {
//        BluetoothDevice bluetoothDevice;
//        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//        if (pairedDevices.size() > 0) {
//            for (BluetoothDevice device : pairedDevices) {
//                String deviceName = device.getName();
//                String deviceHA = device.getAddress();
//                bluetoothDevice = device;
//            }
//        }
//        return bluetoothDevice;
//    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("WikiQuiz", app_uuid);
            } catch (IOException e) {
                Log.e("AcceptThread", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            while (true) {
                try {
                    Toast.makeText(getContext(), "Opening Socket", Toast.LENGTH_SHORT).show();
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e("AcceptThread", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    //manageMyConnectedSocket(socket);
                    cancel();
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("AcceptThread", "Socket's close() method failed", e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(app_uuid);
            } catch (IOException e) {
                Log.e("ConnectThread", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                cancel();
                Log.e("ConnectThread", "Socket's connect() method failed", connectException);
                return;
            }
            //manageMyConnectedSocket(mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("ConnectThread", "Could not close the client socket", closeException);
            }
        }
    }
}
