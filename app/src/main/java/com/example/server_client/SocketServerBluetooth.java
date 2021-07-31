package com.example.server_client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.InputStream;
import java.util.UUID;

public class SocketServerBluetooth extends Thread {

    private final UUID MY_UUID = UUID
            .fromString("70a477ca-af3a-41c1-93f8-23383438754a");//和客戶端相同的UUID
    private final String NAME = "Bluetooth_Socket";
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket socket;
    private InputStream in;//輸入流
    public SocketServerBluetooth(BluetoothAdapter mBluetoothAdapter) {
        try {
            serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        byte[] bis = new byte[1024];
        byte[] bis2;
        int n = 0;

        try {
            socket = serverSocket.accept();
            Log.d("aaaaaaa","連線成功");
            in = socket.getInputStream();
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

