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
import static com.example.server_client.MainActivity.Test;
import static com.example.server_client.MainActivity.buttonCastMsg;
import static com.example.server_client.MainActivity.editText;
import static com.example.server_client.MainActivity.port;
import static com.example.server_client.MainActivity.L01;

public class SocketClientWIFI extends Fragment {

    public SocketClientWIFI() {
        port="客戶端";
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

        Sc01 = (ScrollView) root. findViewById(R.id.Sc01);
        L01 = (LinearLayout)  root.findViewById(R.id.L01);

        b01.setOnClickListener(b01());
        b02.setOnClickListener(b02());
        buttonCastMsg.setOnClickListener(b03());
        L01.setOnLongClickListener(Clear());

        context=((MainActivity)getActivity()).getContext();

        e01.setText(Tool.read(context,"IP"));
        e02.setText(Tool.read(context,"Port"));

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Where you get exception write that code inside this.
        }

     //   new Updata().start();
        return root;
    }


    View.OnClickListener b01() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                String IP=e01.getText().toString();
                int Port=0;
                try {
                    Port=Integer.parseInt(e02.getText().toString());
                }catch (Exception e){
                    ((MainActivity)getActivity()).enterText("請輸入正確Port");
                }

                if (((MainActivity)getActivity()).haveInternet()) {
                    if(clientSocket==null){
                        if (IP.length()!=0&Port!=0){
                            new readData(IP, Port).start();
                        }

                    }

                }else {
                    ((MainActivity)getActivity()).enterText("請開啟WIFI功能");
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
                transaction.replace(R.id.nav_host_fragment,socketServerWIFI);
                try {
                    if (clientSocket!=null){
                        clientSocket.shutdownInput();
                        clientSocket.shutdownOutput();
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
                castMsg(editText.getText().toString());
            }
        };
    }//傳送字串

    View.OnLongClickListener Clear() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((MainActivity)getActivity()).Clear(L01);
                return false;
            }
        };
    }//清除字串

    public void castMsg(final String text) {
        // 取得網路輸出串流
        //傳送資料
        if (clientSocket!=null){
            byte[] temp=new byte[1];
            try {
                if (Switch == true) {
                    if (Test==true) {
                        try {
                            Integer.parseInt(text);
                            temp = (text).getBytes();
                        } catch (Exception e) {

                            ((MainActivity)getActivity()).enterText( "傳送 : " + "請輸入0~255");
                            e.printStackTrace();
                        }
                    }else{
                        temp = (text).getBytes();
                    }

                } else {
                    if (Test==true) {
                        try {
                            temp = new byte[1];
                            temp[0] = (byte) Integer.parseInt(text, 16);
                        }catch ( Exception e){
                            e.printStackTrace();
                            ((MainActivity)getActivity()).enterText("傳送 : " + "請輸入0~255");
                        }
                    }else {
                        int n = 0;
                        n = text.split(" ").length;

                        temp = new byte[n];
                        for (int a = 0; a < n; a++) {
                            temp[a] = (byte) Integer.parseInt(text.split(" ")[a], 16);
                        }
                    }

                }

                ((MainActivity)getActivity()).fullScroll(Sc01);
                ((MainActivity)getActivity()). addText(port+" : " + text, "",L01);


                OutputStream out = clientSocket.getOutputStream();// 寫入訊息
                out.write(temp, 0, temp.length);// 立即發送   //  "|n"
                out.flush();
                //     Log.d("aaaaaaaa",temp.length+"  aa");



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    Socket clientSocket;
    public class readData extends Thread {
        String IP = "";
        int Port = 0;
        String line = "";

        public readData(String IP, int Port) {
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
                byte[] bis2;
                InputStream in = clientSocket.getInputStream();
                ((MainActivity)getActivity()).addText("系統：連線成功", IP,L01);

                ((MainActivity)getActivity()).displayTextView(null,t01,"客戶端(已連線)");
                ((MainActivity)getActivity()).enterText( "連線成功");


                Tool.save(context,IP,"IP");
                Tool.save(context,Port+"","Port");
                // 取得網路輸入串流
                int n = 0;
                while ((n = in.read(bis)) != -1) {
            //       Switch= ((MainActivity)getActivity()).getSwitch(b03,Switch);
            //        Test= ((MainActivity)getActivity()).getTest("客戶端",L01,Test);
                    bis2 = new byte[n];
                    for (int a = 0; a < n; a++) {
                        bis2[a] = bis[a];
                    }
                    if (Switch == true) {//字串接收
                        line = new String(bis2);
                    } else {
                        line = "";
                        for (int a = 0; a < n; a++) {
                            if (a == 0) {
                                line = line + Conversion(Integer.parseInt(bis2[a] + ""));
                            } else {
                                line = line + " " + Conversion(Integer.parseInt(bis2[a] + ""));
                            }
                        }
                    }

                    if (line.equals("") != true) {
                        ((MainActivity)getActivity()).addText("伺服端：" + line,"",L01);
                        ((MainActivity)getActivity()).fullScroll(Sc01);

                    }



                    if (Test) {
                        if (Switch == true) {//字串接收

                            int TestInt = 0;
                            try {
                                TestInt = Integer.parseInt(line);
                                if ((TestInt + 1) < 256) {
                                    castMsg((TestInt + 1) + "");
                                }
                                if (TestInt==255|TestInt==254){
                                    Test=false;
                                    ((MainActivity)getActivity()). addText(port+" : " +"+1模式關閉", "",L01);
                                }

                            } catch (Exception e) {
                            }
                        } else {
                            Log.d("aaaaaaa",bis2[0] +"  ");
                            if (256+Integer.parseInt(bis2[0]+"")+1< 256) {
                                castMsg((Conversion(Integer.parseInt(bis2[0] + "") + 1)));
                            }
                            if (256+Integer.parseInt(bis2[0]+"")==255|256+Integer.parseInt(bis2[0]+"")==254){
                                Test=false;
                                ((MainActivity)getActivity()). addText(port+" : " +"+1模式關閉", "",L01);
                            }
                        }
                    }

                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {

                    }


                }
                ((MainActivity)getActivity()).addText("系統：斷開連線","伺服端",L01);
                ((MainActivity)getActivity()).displayTextView(null,t01,"客戶端(未連線)");
                Log.v("連線狀況", "連線失敗");
                clientSocket=null;
            } catch (IOException e) {
                e.printStackTrace();
                ((MainActivity)getActivity()).addText("系統：連線失敗","伺服端",L01);
                ((MainActivity)getActivity()).displayTextView(null,t01,"客戶端(未連線)");
                Log.v("連線狀況", "連線失敗");
                clientSocket=null;
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
    }
    public class Updata extends Thread {


        public Updata() {

        }

        @Override
        public void run() {
            while (true){
                try {
                    Thread.sleep(50);
                }catch (Exception e){

                }
                /*
                if (b03!=null&L01!=null){
                    try {
                        Switch= ((MainActivity)getActivity()).getSwitch(b03,Switch);
                        Test= ((MainActivity)getActivity()).getTest("客戶端",L01,Test);
                    }catch (Exception e){

                    }

                }
                */
            }
        }
    }
}