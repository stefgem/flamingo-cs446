package com.flamingo.wikiquiz;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

import com.squareup.picasso.Picasso;

public class App extends Application {


    static final long MAX_SIZE = 524288000L; // 500 Mb

    private ConnectedThread _BTConnectedThreadClient;
    private ConnectedThread _BTConnectedThreadServer;

    @Override
    public void onCreate() {
        super.onCreate();
        Picasso.Builder builder = new Picasso.Builder(this);
        Picasso.setSingletonInstance(builder.build());

    }

    public ConnectedThread getBTConnectedThreadClient() {
        return _BTConnectedThreadClient;
    }

    public void setBTConnectedThreadClient(ConnectedThread thread) {
        _BTConnectedThreadClient = thread;
    }

    public ConnectedThread getBTConnectedThreadServer() {
        return _BTConnectedThreadServer;
    }

    public void setBTConnectedThreadServer(ConnectedThread thread) {
        _BTConnectedThreadServer = thread;
    }
}
