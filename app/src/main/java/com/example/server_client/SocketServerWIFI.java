package com.example.server_client;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static com.example.server_client.MainActivity.Switch;
import static com.example.server_client.MainActivity.buttonCastMsg;
import static com.example.server_client.MainActivity.editText;
import static com.example.server_client.MainActivity.L01;

public class SocketServerWIFI extends Fragment {

    String state="伺服端";
    public SocketServerWIFI() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    TextView t01, ipText;
    Button b01, b02, b03;
    EditText e01;
    ScrollView Sc01;
    Context context;
    View root;
    socketServer socketServer=new socketServer();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context=((MainActivity)getActivity()).getContext();

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

        e01.setText(Tool.read(context,"Port"));

        b01.setOnClickListener(b01());
        b02.setOnClickListener(b02());
        b03.setOnClickListener(b03());
        buttonCastMsg.setOnClickListener(b04());

        L01.setOnLongClickListener(Clear());

        return root;
    }


    //-------------------------------------------------------
   MainActivity mainActivity;
    View.OnClickListener b03() {
        mainActivity=((MainActivity)getActivity()).mainActivity();
        return new View.OnClickListener() {
            public void onClick(View v) {
                String[] ips=new String[socketServer.players.size()];
                Log.d("aaaaaaaa",socketServer.players.size()+"  aaaaaaa");
                for (int a=0;a<socketServer.players.size();a++){
                    ips[a]=socketServer.players.get(a).getInetAddress().getHostAddress();
                }
                AlertDialog.Builder dialog_list = new AlertDialog.Builder(mainActivity);
                dialog_list.setTitle("選擇IP")
                        .setItems(ips, new DialogInterface.OnClickListener(){
                            @Override
                            //只要你在onClick處理事件內，使用which參數，就可以知道按下陣列裡的哪一個了
                            public void onClick(DialogInterface dialog, int which) {
                                socketServer.socketSelect = socketServer.players.get(which);
                                b03.setText(socketServer.players.get(which).getInetAddress().getHostAddress());
                            }
                        })
                        .show();
            }
        };

    }//選擇IP
    View.OnClickListener select(final TextView textView, final Socket socket) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                socketServer.socketSelect = socket;
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
                        socketServer.setPort(Port);
                        socketServer.start();
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
                    if (socketServer.socketSelect!=null){
                        socketServer.socketSelect.shutdownInput();
                        socketServer.socketSelect.shutdownOutput();
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

                //傳送資料
                socketServer.castMsg(editText.getText().toString());
            }
        };

    }
    //------------------------------------------------------------

    public class socketServer extends Thread {
        Socket socketSelect = null;
        ArrayList<Socket> players = new ArrayList<Socket>();
        ServerSocket serverSocket;
        int Port;
        String data = "";


        public socketServer() {
        }

        public void setPort(int Port) {
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
                ((MainActivity) getActivity()).displayTextView(ipText, t01, "伺服端(已啟動)");
                ((MainActivity) getActivity()).addText("系統：" + "成功建立伺服端", "", L01);

                //紀錄Port值，下次使用時會自動填入上次輸入值
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


                        ((MainActivity) getActivity()).addText("系統：連線成功", IP, L01);

                        InputStream in = socket.getInputStream();

                        byte[] bis = new byte[1024];
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
                        }

                    } catch (Exception e) {
                        e.getStackTrace();
                    } finally {

                        ((MainActivity) getActivity()).addText("系統：斷開連線", IP, L01);

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

            byte[] temp = new byte[1];
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

                ((MainActivity)getActivity()).fullScroll(Sc01);
                ((MainActivity)getActivity()). addText(state+" : " + text, "",L01);

                OutputStream out = socketSelect.getOutputStream();// 寫入訊息
                out.write(temp, 0, temp.length);// 立即發送   //  "|n"
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}