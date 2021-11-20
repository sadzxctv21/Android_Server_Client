package com.example.server_client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceControl;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.w3c.dom.Text;

import java.net.Socket;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView,navigationView2;
    int CurrentMenuItem = 0;
    static public DrawerLayout drawer,drawer2;
    Group group;
    MainActivity mainActivity=MainActivity.this;
    public static LinearLayout L01;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);//固定橫向
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView2 = findViewById(R.id.nav_view2);

        int status_bar_height = 0;
        int resourceId =getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            status_bar_height = getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }


        Log.d("aaaaaaa",status_bar_height+"");


        mAppBarConfiguration = new AppBarConfiguration.Builder()
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        CurrentMenuItem = 0;//目前Navigation項目位置
        navigationView.getMenu().getItem(CurrentMenuItem).setChecked(true);//設置Navigation目前項目被選取狀態
        setUpNavigation();
//--------------------------------------------------

   //     setUpNavigation2();


        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.commit();
        SocketServerWIFI socketServerWIFI = new SocketServerWIFI();
        transaction.replace(R.id.nav_host_fragment, socketServerWIFI);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Where you get exception write that code inside this.
        }

        AutomaticInput detailFragment = new AutomaticInput(status_bar_height,this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.nav_view2, detailFragment)
                .commit();


    }
    String TAG_DETAIL_FRAGMENT = "TAG_DETAIL_FRAGMENT";

    private void setUpNavigation() {
        // Set navigation item selected listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Log.d("zzzzzzzzzzz", menuItem.toString() + "");
                drawer.closeDrawer(GravityCompat.START);

                FragmentManager manager =getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.commit();
                switch (menuItem.getItemId()) {
                    case R.id.server_wifi:
                        SocketServerWIFI socketServerWIFI = new SocketServerWIFI();
                        transaction.replace(R.id.nav_host_fragment,socketServerWIFI);
                        break;
                    case R.id.client_wifi:
                        SocketClientWIFI socketClientWIFI = new SocketClientWIFI();
                        transaction.replace(R.id.nav_host_fragment,socketClientWIFI);
                        break;
                }
                return true;
            }
        });

    }

    private void setUpNavigation2() {
        // Set navigation item selected listener
        navigationView2.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Log.d("zzzzzzzzzzz", menuItem.toString() + "");
                drawer.closeDrawer(GravityCompat.END);
                return false;
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public MainActivity mainActivity() {
        return mainActivity;

    }
    //--------------------------------------------------------------------
    public boolean haveInternet() {
        boolean result = false;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            result = false;
        } else {
            if (!info.isAvailable()) {
                result = false;
            } else {
                result = true;
            }
        }
        return result;
    }//是否連線

    public void enterText(final String text) {
        runOnUiThread(new Runnable() {             //將內容交給UI執行緒做顯示
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public Context getContext() {
        return getApplicationContext();
    }

    public void fullScroll(final ScrollView Sc01) {
        runOnUiThread(new Runnable() {             //將內容交給UI執行緒做顯示
            public void run() {
                Sc01.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    private String getMyIp() {

        //新增一個WifiManager物件並取得WIFI_SERVICE
        @SuppressLint("WifiManagerLeak") WifiManager wifi_service = (WifiManager) getSystemService(WIFI_SERVICE);
        //取得wifi資訊
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        //取得IP，但這會是一個詭異的數字，還要再自己換算才行
        int ipAddress = wifiInfo.getIpAddress();
        //利用位移運算和AND運算計算IP
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return ip;
    }//取得IP

    public void  addText(final String text, final String IP, final LinearLayout L01) {
        runOnUiThread(new Runnable() {             //將內容交給UI執行緒做顯示
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                int second = calendar.get(Calendar.SECOND);
                int msecond = calendar.get(Calendar.MILLISECOND);


                LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout linearLayout = new LinearLayout(MainActivity.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                TextView t01 = new TextView(MainActivity.this);
                t01.setText(text);
                t01.setTextSize(24);
                t01.setGravity(Gravity.LEFT);
                TextView time = new TextView(MainActivity.this);
                if (IP.length() != 0) {
                    time.setText("來源:" + IP + "  " + zero(hour, 2) + ":" + zero(minute, 2) + ":" + zero(second, 2) + "." + zero(msecond, 3));
                } else {
                    time.setText(zero(hour, 2) + ":" + zero(minute, 2) + ":" + zero(second, 2) + "." + zero(msecond, 3));

                }


                time.setTextSize(16);
                time.setGravity(Gravity.RIGHT);

                L01.addView(t01);

                L01.addView(time);

            }
        });
    }

    private String zero(int s, int number) {
        String S = s + "";
        String t = "";
        for (int i = 0; i < number - S.length(); i++) {
            t = t + "0";

        }
        t = t + s;

        return t;
    }

    public void displayTextView(final TextView ipText, final TextView t01, final String t01Text) {
        runOnUiThread(new Runnable() {             //將內容交給UI執行緒做顯示
            public void run() {
                Log.d("zzzzzzzzzzz", "qqqqqqqqqqq");
                if (ipText != null) {
                    ipText.setText("IP:" + getMyIp());
                }

                t01.setText(t01Text);
            }
        });

    }

    public void Clear(final LinearLayout L01) {
        //  View item = LayoutInflater.from(MainActivity.this).inflate(R.layout.display_data, null);
        //   LinearLayout L02 = (LinearLayout) item.findViewById(R.id.L01);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("清除資料")
                //          .setView(item)
                .setNeutralButton("清除", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        L01.removeAllViews();
                    }
                })
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    //----------------------------------------------------------------------
    public static Button buttonCastMsg;
    public static boolean Switch = true;//T:字串 F:位元

    public boolean getSwitch(final Button button, boolean Switch) {
        if (this.Switch != Switch) {

            if (this.Switch == false) {
                runOnUiThread(new Runnable() {             //將內容交給UI執行緒做顯示
                    public void run() {
                        button.setText("位元傳輸");

                    }
                });

            } else {
                runOnUiThread(new Runnable() {             //將內容交給UI執行緒做顯示
                    public void run() {
                        button.setText("字串傳輸");

                    }
                });
            }

        }

        return this.Switch;
    }

    public static String port="";


    //-----------------------------------------------------------------------------------------
    public static EditText editText=null;
    public void upText(final String enterTextS){
        if (enterTextS.length()!=0){
            runOnUiThread(new Runnable() {             //將內容交給UI執行緒做顯示
                public void run() {
                    editText.setText(enterTextS);
                }
            });
        }



    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {

            if (Switch == false) {
                buttonCastMsg.setText("字串傳輸");
                Switch = true;
            } else {
                buttonCastMsg.setText("位元傳輸");
                Switch = false;
            }

            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }//返回建 音量按鍵(按下)



}