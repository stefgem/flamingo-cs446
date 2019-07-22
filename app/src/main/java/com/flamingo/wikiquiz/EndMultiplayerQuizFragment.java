package com.flamingo.wikiquiz;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EndMultiplayerQuizFragment extends Fragment {

    private QuestionViewModel questionViewModel;
    private ArrayList<QuestionContent> readQC;
    private QuestionContent tempQC;
    private int nQuestions;

    private static boolean IS_CLIENT;

    private boolean[] opponentBTCorrectLog;
    private long[] opponentBTTimestampLog;
    private boolean[] opponentBTHintLog;

    private boolean[] playerBTCorrectLog;
    private long[] playerBTTimestampLog;
    private boolean[] playerBTHintLog;

    private ConnectedThread mConnectedThread;

    private static final int MESSAGE_READ = 1;
    private static final int MESSAGE_WRITE = 2;
    private static final int MESSAGE_TOAST = 3;

    public EndMultiplayerQuizFragment() {
        // Required Empty Constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        IS_CLIENT = getArguments().getBoolean("isClient");
        playerBTCorrectLog = getArguments().getBooleanArray("btCorrectLog");
        playerBTTimestampLog = getArguments().getLongArray("btTimestampLog");
        playerBTHintLog = getArguments().getBooleanArray("btHintLog");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_launch_multiplayer, container, false);

        TextView player1View = view.findViewById(R.id.player1ScoreView);

        TextView player2View = view.findViewById(R.id.player2ScoreView);

        questionViewModel = ViewModelProviders.of(getActivity())
                .get(QuestionViewModel.class);

        opponentBTCorrectLog = new boolean[questionViewModel.getNUM_TOTAL_QUESTIONS()];
        opponentBTTimestampLog = new long[questionViewModel.getNUM_TOTAL_QUESTIONS()];
        opponentBTHintLog = new boolean[questionViewModel.getNUM_TOTAL_QUESTIONS()];

        readQC = new ArrayList<>();
        tempQC = new QuestionContent();
        nQuestions = 0;

        App app = (App) getActivity().getApplication();
        BluetoothSocket socket = app.getBTSocket();
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Send the play logs to the other device over bluetooth
        byte[] message = new byte[questionViewModel.getNUM_TOTAL_QUESTIONS() * 10];
        for (int i = 0; i < questionViewModel.getNUM_TOTAL_QUESTIONS(); i++) {
            if (playerBTCorrectLog[i]) {
                message[i] = 1;
            }
            else {
                message[i] = 0;
            }
            ByteBuffer bb = ByteBuffer.allocate(8);
            bb.putLong(playerBTTimestampLog[i]);
            for (byte b : bb.array()) {
                message[i] = b;
                i++;
            }
            if (playerBTHintLog[i]) {
                message[i] = 1;
            }
            else {
                message[i] = 0;
            }
        }
        mConnectedThread.write(message, 0);

        return view;
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    int index = 0;
                    int timestampIndex = 0;
                    int paramIndex = 0;
                    byte[] timestamp = new byte[8];
                    for (byte b : readBuf) {
                        if (paramIndex == 0) {
                            opponentBTCorrectLog[index] = b != 0;
                            paramIndex = 1;
                        }
                        else if (paramIndex == 1) {
                            timestamp[timestampIndex] = b;
                            timestampIndex++;
                            if (timestampIndex == 8) {
                                opponentBTTimestampLog[index] = ByteBuffer.wrap(timestamp).getInt();
                                paramIndex = 2;
                                timestampIndex = 0;
                            }
                        }
                        else {
                            opponentBTHintLog[index] = b != 0;
                            paramIndex = 0;
                            index++;
                        }
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Toast.makeText(getActivity(), "Sent Question Contents",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

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
//            final int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                int numBytes = 0;
                try {
                    numBytes = mmInStream.read(mmBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Send the obtained bytes to the UI activity.
                Message readMsg = handler.obtainMessage(
                        MESSAGE_READ, numBytes, -1,
                        mmBuffer);
                readMsg.sendToTarget();
            }
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
