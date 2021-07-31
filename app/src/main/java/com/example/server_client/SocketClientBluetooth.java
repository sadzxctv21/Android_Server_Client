package com.example.server_client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.InputStream;
import java.util.UUID;

public class SocketClientBluetooth extends Thread {

    private final UUID MY_UUID = UUID
            .fromString("70a477ca-af3a-41c1-93f8-23383438754a");//和客戶端相同的UUID
    BluetoothDevice device;
    private BluetoothSocket socket;
    private InputStream in;//輸入流
    public SocketClientBluetooth(BluetoothAdapter mBluetoothAdapter) {
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        byte[] bis = new byte[1024];
        byte[] bis2;
        int n = 0;
        try {

            in = socket.getInputStream();
            Log.d("aaaaaaa","連線成功");
            while ((n = in.read(bis)) != -1) {
                bis2 = new byte[n];
                for (int a = 0; a < n; a++) {
                    bis2[a] = bis[a];
                }
                String line = new String(bis2);
                Log.d("aaaaaaa",line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

