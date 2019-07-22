package com.flamingo.wikiquiz;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

import com.squareup.picasso.Picasso;

public class App extends Application {


    static final long MAX_SIZE = 524288000L; // 500 Mb

    private BluetoothSocket _socket;

    @Override
    public void onCreate() {
        super.onCreate();
        Picasso.Builder builder = new Picasso.Builder(this);
        Picasso.setSingletonInstance(builder.build());

    }

    public BluetoothSocket getBTSocket() {
        return _socket;
    }

    public void setBTSocket(BluetoothSocket socket) {
        _socket = socket;
    }
}
