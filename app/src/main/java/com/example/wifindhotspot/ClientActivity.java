package com.example.wifindhotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ClientActivity extends AppCompatActivity {

    Socket socket = null;
    ServerSocket server = null;
    Thread Thread1 = null;
    TextView tvIP, tvPort;
    TextView tvMessages;
    EditText etMessage;
    TextView tvConStatus;
    Button buttonConnect;
    Button buttonSend;
    public static String SERVER_IP = "";
    public static final int SERVER_PORT = 8080;
    String message;
    PrintWriter printWriter=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        tvConStatus = findViewById(R.id.tvConnectionStatus);
        buttonConnect = findViewById(R.id.btnConnectB);
        buttonSend = findViewById(R.id.btnSend);


        buttonConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                tvMessages.setText("Connecting");
//                MakeConnectionWithServer();
//

                try {
                    SERVER_IP = getLocalIpAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                String TAG = "CLIENT";



                    try {


                        Log.e("TAG", "1");
                        Log.e("TAG", "1");
                        Log.e("TAG", SERVER_IP);
                        tvMessages.setText(SERVER_IP);
                        socket = new Socket(SERVER_IP,5300);

                        Log.e("TAG", "2");
                        printWriter = new PrintWriter(socket.getOutputStream());
                        printWriter.print("hello this is me");
                        printWriter.flush();
                        tvConStatus.setText("Connected");

//                        printWriter.print(etMessage.getText());
                        printWriter.flush();
                        printWriter.close();
//                        etMessage.setText("");

                    } catch (IOException e) {
                        Log.e("TAG", e.getMessage());
                    } catch (Exception e) {
                        Log.e("TAG", e.getMessage());
                    }
                }


        });

        buttonSend.setOnClickListener( view -> {

//               printWriter.print(etMessage.getText());
//               printWriter.flush();
//               etMessage.setText("");

        });

    }

    Runnable conn = new Runnable() {

        String TAG = "CLIENT";

        public void run() {

            try {

                socket = new Socket(SERVER_IP,5300);
                printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.print("hello");
                printWriter.flush();
                tvConStatus.setText("Connected");


                while(true){

                    InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String msg = bufferedReader.readLine();
                    if(msg=="clr"){
                        tvMessages.setText("");
                    }else{
                        String tmp = tvMessages.getText().toString();
                        tmp+="\n";
                        tmp+=msg;
                        tvMessages.setText(msg);
                    }

                }

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        printWriter.close();
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void MakeConnectionWithServer(){
        try {
            SERVER_IP = getLocalIpAddress();
            String hi = tvMessages.getText().toString();
            tvMessages.setText(hi+": "+SERVER_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        new Thread(conn).start();
    }

    private String getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }

}