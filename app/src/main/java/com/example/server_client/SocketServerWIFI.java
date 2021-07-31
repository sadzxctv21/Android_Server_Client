package com.example.server_client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.server_client.MainActivity.Switch;
import static com.example.server_client.MainActivity.Test;
import static com.example.server_client.MainActivity.buttonCastMsg;
import static com.example.server_client.MainActivity.editText;
import static com.example.server_client.MainActivity.port;
import static com.example.server_client.MainActivity.L01;

public class SocketServerWIFI extends Fragment {


    public SocketServerWIFI() {
        port="伺服端";
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    TextView t01, ipText;
    private String line = "";
    Button b01, b02, b03;
    EditText e01;
    ScrollView Sc01;
    Context context;
    View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fr_socket_server_wifi, container, false);
        t01 = (TextView) root.findViewById(R.id.t01);
        ipText = (TextView) root. findViewById(R.id.IP);

        b01 = (Button) root. findViewById(R.id.b01);
        b02 = (Button) root. findViewById(R.id.b02);
        b03 = (Button) root. findViewById(R.id.b03);
        buttonCastMsg = (Button) root. findViewById(R.id.buttonCastMsg);

        e01 = (EditText) root. findViewById(R.id.e01);//輸入Port
        editText = (EditText)  root.findViewById(R.id.enterText);//輸入文字


        Sc01 = (ScrollView) root. findViewById(R.id.Sc01);
        L01 = (LinearLayout)  root.findViewById(R.id.L01);

        b01.setOnClickListener(b01());
        b02.setOnClickListener(b02());
        b03.setOnClickListener(b03());
        buttonCastMsg.setOnClickListener(b04());

        L01.setOnLongClickListener(Clear());


        context=((MainActivity)getActivity()).getContext();
    //    new Updata().start();

        return root;
    }


    //-------------------------------------------------------
   MainActivity mainActivity;
    View.OnClickListener b03() {
        mainActivity=((MainActivity)getActivity()).mainActivity();
        return new View.OnClickListener() {
            public void onClick(View v) {
                String[] ips=new String[players.size()];
                Log.d("aaaaaaaa",players.size()+"  aaaaaaa");
                for (int a=0;a<players.size();a++){
                    ips[a]=players.get(a).getInetAddress().getHostAddress();
                }
            //    ips = (String[]) players.toArray(new String[players.size()]);
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(mainActivity);
                dialog_list.setTitle("選擇IP")
                        .setItems(ips, new DialogInterface.OnClickListener(){
                            @Override
                            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
                            public void onClick(DialogInterface dialog, int which) {
                                socketSelect = players.get(which);
                                b03.setText(players.get(which).getInetAddress().getHostAddress());
                            }
                        })
                        .show();
            }
        };

    }//選擇IP
    View.OnClickListener select(final TextView textView, final Socket socket) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                socketSelect = socket;
                textView.setText("IP:" + socket.getInetAddress().getHostAddress());
                b03.setText(socket.getInetAddress().getHostAddress());
            }
        };

    }//選擇IP


    //------------------------------------------------------------------------------------
    View.OnClickListener b01() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (((MainActivity)getActivity()).haveInternet()) {
                    int Port = 0;
                    try {
                        Port = Integer.parseInt(e01.getText().toString());
                        new thread(Port).start();
                        b01.setEnabled(false);
                    } catch (Exception e) {
                        ((MainActivity)getActivity()).enterText("請輸入正確Port");

                    }


                } else {
                    ((MainActivity)getActivity()).enterText("請開啟WIFI功能");
                }

            }
        };
    }//啟動 伺服端

    View.OnClickListener b02() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager manager =getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.commit();
                SocketClientWIFI socketClientWIFI = new SocketClientWIFI();
                transaction.replace(R.id.nav_host_fragment,socketClientWIFI);
                try {
                    if (socketSelect!=null){
                        socketSelect.shutdownInput();
                        socketSelect.shutdownOutput();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }//切換介面->WIFI_客戶端



    View.OnLongClickListener Clear() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((MainActivity)getActivity()).Clear(L01);
                return false;
            }
        };
    }//清除資料

    View.OnClickListener b04() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                castMsg(editText.getText().toString());
            }
        };

    }

    public void castMsg(final String text) {
        if (players.size() != 0 & socketSelect != null) {
            socketSelect = players.get(0);
        }

        byte[] temp = new byte[1];
        try {
            if (Switch == true) {
                if (Test == true) {
                    try {
                        Integer.parseInt(text);
                        temp = (text).getBytes();
                    } catch (Exception e) {
                        ((MainActivity)getActivity()).enterText("傳送 : " + "請輸入0~255");
                        e.printStackTrace();
                    }
                } else {
                    temp = (text).getBytes();
                }

            } else {
                if (Test == true) {
                    try {
                        temp = new byte[1];
                        temp[0] = (byte) Integer.parseInt(text, 16);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //        Toast.makeText(SocketServerWIFI.this, "傳送 : " + "請輸入0~255", Toast.LENGTH_SHORT).show();
                    }

                } else {
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


            OutputStream out = socketSelect.getOutputStream();// 寫入訊息
            out.write(temp, 0, temp.length);// 立即發送   //  "|n"
            out.flush();
            //     Log.d("aaaaaaaa",temp.length+"  aa");


        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    //-------------------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------------------
    Socket socketSelect = null;
    ArrayList<Socket> players = new ArrayList<Socket>();


    public class thread extends Thread {
        ServerSocket serverSocket;
        int Port;
        String line = "";

        public thread(int Port) {
            this.Port = Port;
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(Port);
                ((MainActivity)getActivity()).displayTextView(ipText,t01,"伺服端(已啟動)");
                ((MainActivity)getActivity()).addText("系統：" + "成功建立伺服端", "",L01);

                Tool.save(context, Port + "", "Port");

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
                        Log.d("aaaaaaaa", (
                                socket.getInetAddress().getAddress()[0] & 0xFF) + "."
                                + (socket.getInetAddress().getAddress()[1] & 0xFF) + "."
                                + (socket.getInetAddress().getAddress()[2] & 0xFF) + "."
                                + (socket.getInetAddress().getAddress()[3] & 0xFF));


                        ((MainActivity)getActivity()).addText("系統：連線成功", IP,L01);

                        InputStream in = socket.getInputStream();

                        byte[] bis = new byte[1024];
                        byte[] bis2;
                        int n = 0;
                        while ((n = in.read(bis)) != -1) {
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

                            //              Log.d("aaaaaa",line+"   "+in.available());
                            if (line.equals("") != true) {
                                ((MainActivity)getActivity()). addText("客戶端：" + line, IP,L01);
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

                    } catch (Exception e) {
                        e.getStackTrace();
                    } finally {

                        ((MainActivity)getActivity()).addText("系統：斷開連線", IP,L01);

                        IP_string = IP_string.replaceFirst("\r\n", "");
                        IP_string = IP_string.replace(IP, "");
                        Log.d("aaaaaaaa", "結束傳值");
                        players.remove(socket);
                        socketSelect = null;
                        //			System.out.println("連線人數：" + count);
                    }

                }
            });
            t.start();
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
                if (b04!=null&L01!=null){
                    try {
                        Switch= ((MainActivity)getActivity()).getSwitch(b04,Switch);
                        Test= ((MainActivity)getActivity()).getTest("客戶端",L01,Test);
                    }catch (Exception e){

                    }

                }
*/
            }
        }
    }

    //-----------------------------------------------------

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

}