package com.example.server_client;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;

import static com.example.server_client.MainActivity.Switch;
import static com.example.server_client.MainActivity.buttonCastMsg;
import static com.example.server_client.MainActivity.editText;
import static com.example.server_client.MainActivity.L01;

public class SocketClientWIFI extends Fragment {
    String state = "客戶端";

    public SocketClientWIFI() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TextView t01;
    Button b01, b02;
    EditText e01, e02;
    ScrollView Sc01;
    Context context;
    socketClient socketClient= new socketClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fr_socket_client_wifi, container, false);
        t01 = (TextView) root.findViewById(R.id.t01);

        b01 = (Button) root.findViewById(R.id.b01);
        b02 = (Button) root.findViewById(R.id.b02);
        buttonCastMsg = (Button) root.findViewById(R.id.buttonCastMsg);

        e01 = (EditText) root.findViewById(R.id.e01);//輸入IP
        e02 = (EditText) root.findViewById(R.id.e02);//輸入Port
        editText = (EditText) root.findViewById(R.id.enterText);//輸入文字

        Sc01 = (ScrollView) root.findViewById(R.id.Sc01);
        L01 = (LinearLayout) root.findViewById(R.id.L01);

        b01.setOnClickListener(b01());
        b02.setOnClickListener(b02());
        buttonCastMsg.setOnClickListener(b03());
        L01.setOnLongClickListener(Clear());

        context = ((MainActivity) getActivity()).getContext();

        e01.setText(Tool.read(context, "IP"));
        e02.setText(Tool.read(context, "Port"));

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Where you get exception write that code inside this.
        }



        return root;
    }


    View.OnClickListener b01() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                String IP = e01.getText().toString();
                int Port = 0;
                try {
                    Port = Integer.parseInt(e02.getText().toString());
                } catch (Exception e) {
                    ((MainActivity) getActivity()).enterText("請輸入正確Port");
                }

                if (((MainActivity) getActivity()).haveInternet()) {

                        socketClient=new socketClient();
                        socketClient.start(IP, Port);


                } else {
                    ((MainActivity) getActivity()).enterText("請開啟WIFI功能");
                }

            }
        };
    }//連線客戶端

    View.OnClickListener b02() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.commit();
                SocketServerWIFI socketServerWIFI = new SocketServerWIFI();
                transaction.replace(R.id.nav_host_fragment, socketServerWIFI);
                try {
                    if (socketClient.clientSocket != null) {
                        socketClient.clientSocket.shutdownInput();
                        socketClient.clientSocket.shutdownOutput();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }//切換介面->藍芽_伺服端

    View.OnClickListener b03() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                socketClient.castMsg(editText.getText().toString());
            }
        };
    }//傳送字串

    View.OnLongClickListener Clear() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((MainActivity) getActivity()).Clear(L01);
                return false;
            }
        };
    }//清除字串


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

        public void start(String IP, int Port){
            this.IP = IP;
            this.Port = Port;
            start();
        }

        @Override
        public void run() {
            InetAddress serverIp;// server端的IP
            try {
                serverIp = InetAddress.getByName(IP);// 以內定(本機電腦端)IP為Server端
                int serverPort = Port;
                clientSocket = new Socket(serverIp, serverPort);
                Log.v("連線狀況", "已連線");

                int byteMax=1024;
                byte[] bis = new byte[byteMax];
                InputStream in = clientSocket.getInputStream();
                ((MainActivity) getActivity()).addText("系統：連線成功", IP, L01);

                ((MainActivity) getActivity()).displayTextView(null, t01, "客戶端(已連線)");
                ((MainActivity) getActivity()).enterText("連線成功");


                Tool.save(context, IP, "IP");
                Tool.save(context, Port + "", "Port");
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
                        ((MainActivity) getActivity()).addText("客戶端：" + data, IP, L01);
                        ((MainActivity) getActivity()).fullScroll(Sc01);
                    }
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {

                    }
                    bis = new byte[byteMax];
                }
                ((MainActivity) getActivity()).addText("系統：斷開連線", "伺服端", L01);
                ((MainActivity) getActivity()).displayTextView(null, t01, "客戶端(未連線)");
                Log.v("連線狀況", "連線失敗");
                interrupt();
                //clientSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
                ((MainActivity) getActivity()).addText("系統：連線失敗", "伺服端", L01);
                ((MainActivity) getActivity()).displayTextView(null, t01, "客戶端(未連線)");
                Log.v("連線狀況", "連線失敗");
                interrupt();
                //clientSocket = null;
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

                    ((MainActivity) getActivity()).fullScroll(Sc01);
                    ((MainActivity) getActivity()).addText(state + " : " + text, "", L01);


                    OutputStream out = clientSocket.getOutputStream();// 寫入訊息
                    out.write(temp, 0, temp.length);// 立即發送   //  "|n"
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    ((MainActivity) getActivity()).addText("輸入錯誤，請輸入00~FF", "系統", L01);
                }
            }
        }
    }
}