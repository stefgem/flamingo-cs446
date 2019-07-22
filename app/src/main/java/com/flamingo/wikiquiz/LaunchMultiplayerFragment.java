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
import android.os.Message;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

public class LaunchMultiplayerFragment extends Fragment {

    private UUID app_uuid = UUID.fromString("29a7e265-1ad1-4879-873a-51e316bdfa2b");
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> deviceNamesAdapter;

    private QuestionViewModel questionViewModel;
    private ArrayList<QuestionContent> readQC;
    private QuestionContent tempQC;
    private int readCount;
    private int nQuestions;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    private static final int MESSAGE_READ = 1;
    private static final int MESSAGE_WRITE = 2;
    private static final int MESSAGE_TOAST = 3;

    public LaunchMultiplayerFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_launch_multiplayer, container, false);

        deviceNamesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        questionViewModel = ViewModelProviders.of(getActivity())
                .get(QuestionViewModel.class);

        readQC = new ArrayList<>();
        tempQC = new QuestionContent();
        readCount = 0;
        nQuestions = 0;

        ListView devicesListView = view.findViewById(R.id.devicesListView);
        devicesListView.setAdapter(deviceNamesAdapter);
        devicesListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (!bluetoothAdapter.isEnabled()) {
                    Toast.makeText(getContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the device MAC address, which is the last 17 chars in the View
                String info = ((TextView) view).getText().toString();
                final String address = info.substring(info.length() - 17);
                final String name = info.substring(0, info.length() - 17);

                // Spawn a new thread to avoid blocking the GUI one
                new Thread() {
                    public void run() {
                        boolean fail = false;

                        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

                        mConnectThread = new ConnectThread(device);
                        mConnectThread.run();
                    }
                }.start();
//                while(!questionViewModel.getQuestionsSent()) {
//                    // Stay in this loop until server has sent all the question content
//                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("isBluetooth", true);
                bundle.putBoolean("isClient", true);
                NavHostFragment.findNavController(getParentFragment())
                        .navigate(R.id.action_launchMultiplayerFragment_to_questionFragment, bundle);
            }
        });

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
                    mAcceptThread = new AcceptThread();
                    mAcceptThread.run();
                    String sendMessage = "sent!";
                    byte[] send = sendMessage.getBytes();
                    //mConnectedThread.write(send, 0, 0);
                    questionViewModel.generatePreloadedQCs(3);
                    for (QuestionContent questionContent : questionViewModel.getAllPreloadedQCs()) {
                        ArrayList<ArrayList<byte[]>> questionContentArray = new ArrayList<>();
                        questionContentArray = questionContent.getContentByteArray();
                        for (ArrayList<byte[]> questionField : questionContentArray) {
                            mConnectedThread.write(questionField.get(0), 0);
                            mConnectedThread.write(questionField.get(1), 1);
                            mConnectedThread.write(questionField.get(3), 3);
                        }
                    }
                    questionViewModel.setQuestionsSent(true);
//                    mConnectedThread.write(send, -1, 0);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isBluetooth", true);
                    bundle.putBoolean("isClient", false);
                    NavHostFragment.findNavController(getParentFragment())
                            .navigate(R.id.action_launchMultiplayerFragment_to_questionFragment, bundle);
                } else {
                    // Device does not support bluetooth
                }
            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 56789);
        }

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
                } else {
                    // Device does not support bluetooth
                }
            }
        });



        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getApplicationContext().unregisterReceiver(receiver);
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

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    QuestionContent qc;
                    switch (readCount) {
                        case 0:
//                            qc = questionViewModel.getPreloadedAtIndex(msg.arg2);
//                            qc.setImageBlob(readBuf);
//                            questionViewModel.setPreloadedAtIndex(msg.arg2, qc);
                            tempQC.setImageBlob(readBuf);
                            readCount++;
                            break;
                        case 1:
//                            qc = questionViewModel.getPreloadedAtIndex(msg.arg2);
//                            qc.setQuestionString(readBuf);
//                            questionViewModel.setPreloadedAtIndex(msg.arg2, qc);
                            tempQC.setQuestionString(readBuf);
                            readCount++;
                            break;
                        case 3:
//                            qc = questionViewModel.getPreloadedAtIndex(msg.arg2);
//                            qc.setCorrectAnswer(readBuf);
//                            questionViewModel.setPreloadedAtIndex(msg.arg2, qc);
                            tempQC.setCorrectAnswer(readBuf);
                            readQC.add(tempQC);
                            if (readQC.size() == nQuestions) {
                                readCount = -1;
                            }
                            else {
                                readCount = 0;
                            }
                            tempQC = new QuestionContent();
                            break;
                        case -1:
                            questionViewModel.setAllPreloadedQCs(readQC);
                            questionViewModel.setQuestionsSent(true);
                        case -2:
                            nQuestions = ByteBuffer.wrap(readBuf).getInt();
                    }
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, readBuf.length);
//                    Toast.makeText(getActivity(), "Received Message: " + readMessage,
//                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Toast.makeText(getActivity(), "Sent Question Contents",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
//                    if (getActivity() != null) {
//                        Toast.makeText(getActivity(), msg.getData().getString("failed"),
//                                Toast.LENGTH_SHORT).show();
//                    }
                    break;
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

    private synchronized void manageMyConnectedSocket(BluetoothSocket socket,
                                                      BluetoothDevice device) {
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        //Cancel accept thread because we only want to connect to one device
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread= null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

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
                    synchronized (LaunchMultiplayerFragment.this) {
                        manageMyConnectedSocket(socket, socket.getRemoteDevice());
//                        mConnectedThread = new ConnectedThread(socket);
//                        App app = (App)getActivity().getApplication();
//                        app.setBTSocket(socket);
//                        app.setBTConnectedThreadServer(mConnectedThread);
//                        app.getBTConnectedThreadServer().start();
                        cancel();
                        break;
                    }
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

            synchronized (LaunchMultiplayerFragment.this) {
                mConnectThread = null;
            }
            manageMyConnectedSocket(mmSocket, mmDevice);

//            ConnectedThread thread = new ConnectedThread(mmSocket);
//            App app = (App)getActivity().getApplication();
//            app.setBTSocket(mmSocket);
//            app.setBTConnectedThreadClient(thread);
//            app.getBTConnectedThreadClient().start();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("ConnectThread", "Could not close the client socket", closeException);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e("ConnectedThread", "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("ConnectedThread", "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(
                            MESSAGE_READ, numBytes, -1,
                            mmBuffer);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d("ConnectedThread", "Input stream was disconnected", e);
                    break;
                }
            }
            int x = 0;
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes, int type) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                handler.obtainMessage(MESSAGE_WRITE, type, 1, bytes).sendToTarget();
            } catch (IOException e) {
                Log.e("ConnectedThread", "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("failed",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("ConnectedThread", "Could not close the connect socket", e);
            }
        }
    }
}
