package com.example.server_client.tool;

import static com.example.server_client.MainActivity.L01;
import static com.example.server_client.MainActivity.Switch;

import android.util.Log;

import com.example.server_client.MainActivity;
import com.example.server_client.Tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class socketClient extends Thread {
    Socket clientSocket;
    String IP = "";
    int Port = 0;
    String data = "";

    /**
     * 如果只是需要接收的資料，只要修改此處
     * @param dataO 未轉換資料
     * @param data 已轉換資料
     */
    public void Program(byte[] dataO,String data){
        //程式
    }

    public socketClient() {

    }

    public void setIP_Port(String IP, int Port){
        this.IP = IP;
        this.Port = Port;
    }

    @Override
    public void run() {
        InetAddress serverIp;// server端的IP
        try {
            serverIp = InetAddress.getByName(IP);// 以內定(本機電腦端)IP為Server端
            int serverPort = Port;
            clientSocket = new Socket(serverIp, serverPort);
            Log.v("連線狀況", "已連線");
            byte[] bis = new byte[1024];
            InputStream in = clientSocket.getInputStream();
            // 取得網路輸入串流
            int n = 0;
            while ((n = in.read(bis)) != -1) {
                if (Switch == true) {
                    //字串接收
                    data = new String(bis);
                } else {
                    //位元接收
                    data = "";
                    for (int a = 0; a < n; a++) {
                        data = data + " " + Conversion(Integer.parseInt(bis[a] + ""));
                    }
                    data = data.substring(1);
                }
                if (data.equals("") != true) {
                    Program(bis,data);
                }
                try {
                    Thread.sleep(200);
                } catch (Exception e) {

                }
            }
            Log.v("連線狀況", "連線失敗");
            clientSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("連線狀況", "連線失敗");
            clientSocket = null;
        }
    }

    private String Conversion(int data) {
        String t = "";

        if (data > 0) {
            t = Integer.toHexString(data);
        } else if (data < 0) {
            t = Integer.toHexString(data + 256);
        } else {
            t = "00";
        }
        return t;
    }

    public void castMsg(final String text) {
        // 取得網路輸出串流
        //傳送資料
        if (clientSocket != null) {
            byte[] temp ;
            try {
                if (Switch == true) {
                    temp = (text).getBytes();
                } else {
                    int n = 0;
                    n = text.split(" ").length;
                    temp = new byte[n];
                    for (int a = 0; a < n; a++) {
                        temp[a] = (byte) Integer.parseInt(text.split(" ")[a], 16);
                    }
                }
                OutputStream out = clientSocket.getOutputStream();// 寫入訊息
                out.write(temp, 0, temp.length);// 立即發送   //  "|n"
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
