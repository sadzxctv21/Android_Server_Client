package com.example.server_client.tool;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class socketServer extends Thread {
    Socket socketSelect = null;
    ArrayList<Socket> players = new ArrayList<Socket>();
    ServerSocket serverSocket;
    int Port;
    String data = "";
    Boolean Switch;

    public socketServer(int Port) {
        this.Port = Port;
    }

    /**
     * 如果只是需要接收的資料，只要修改此處
     * @param dataO 未轉換資料
     * @param data 已轉換資料
     */
    public void Program(byte[] dataO,String data){
        //程式
    }


    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Port);

            System.out.println("Server is start.");
            // 顯示等待客戶端連接
            System.out.println("Waiting for clinet connect");
            // 當Server運作中時
            while (!serverSocket.isClosed()) {
                // 呼叫等待接受客戶端連接
                waitNewPlayer(serverSocket);
            }

        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("Server Socket ERROR");
        }
    }

    String IP_string = "";

    public void waitNewPlayer(ServerSocket serverSocket) {
        try {
            Socket socket = serverSocket.accept();
            if (players.size() == 0) {
                socketSelect = socket;
            }
            //防止重複IP加入
            //將連線成功的客戶端IP組成一串字串
            //當新IP與伺服端連線後，會判斷之前是否已加入此IP
            try {
                Log.d("aaaaa", IP_string);
                IP_string.substring(IP_string.indexOf(socket.getInetAddress().getHostAddress()));
            } catch (Exception e) {
                e.printStackTrace();
                IP_string = IP_string + socket.getInetAddress().getHostAddress() + "\r\n";
                System.out.println(socket.getInetAddress().getHostAddress());
                createNewPlayer(socket, socket.getInetAddress().getHostAddress());// 呼叫創造新的使用者
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void createNewPlayer(final Socket socket, final String IP) {
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    // 增加新的使用者
                    players.add(socket);

                    // 取得網路串流
                    Log.d("客戶端IP", (
                            socket.getInetAddress().getAddress()[0] & 0xFF) + "."
                            + (socket.getInetAddress().getAddress()[1] & 0xFF) + "."
                            + (socket.getInetAddress().getAddress()[2] & 0xFF) + "."
                            + (socket.getInetAddress().getAddress()[3] & 0xFF));
                    InputStream in = socket.getInputStream();

                    int byteMax=1024;
                    byte[] bis = new byte[byteMax];
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
                        bis = new byte[byteMax];
                    }

                } catch (Exception e) {
                    e.getStackTrace();
                } finally {
                    IP_string = IP_string.replaceFirst("\r\n", "");
                    IP_string = IP_string.replace(IP, "");
                    Log.d("aaaaaaaa", "結束傳值");
                    players.remove(socket);
                    socketSelect = null;
                    //System.out.println("連線人數：" + count);
                }

            }
        });
        t.start();
    }

    public String Conversion(int data) {
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

    /**
     * 傳送資料
     * @param text
     */
    public void castMsg(final String text) {
        if (players.size() != 0 & socketSelect != null) {
            socketSelect = players.get(0);
        }

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
            OutputStream out = socketSelect.getOutputStream();// 寫入訊息
            out.write(temp, 0, temp.length);// 立即發送   //  "|n"
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}