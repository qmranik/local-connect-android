package com.example.wifindhotspot;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;


public class ServerActivity extends AppCompatActivity {

    Thread Thread1 = null;
    EditText etIP, etPort;
    TextView tvMessages;
    EditText etMessage;
    Button btnSend;
    int SERVER_PORT=8080;
    ServerSocket server = null;
    Socket socket = null;
    PrintWriter printWriter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);


        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        try {
            server = new ServerSocket(5300);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // initServer();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



//                    try {
//                        printWriter = new PrintWriter(socket.getOutputStream());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    printWriter.print(etMessage.getText());
//                    etMessage.setText("");
//                    printWriter.flush();


                    new Thread(conn).start();


            }

        });
    }

    private void initServer() {

        new Thread(conn).start();
    }

    Runnable conn = new Runnable() {

        String TAG = "SERVER";

        public void run() {
            try {
                Log.e("TAG", "1");
                server = new ServerSocket(0);
                Log.e("TAG", server.getInetAddress().toString());
                Log.e("TAG", server.getLocalSocketAddress().toString());

                Log.e("TAG", String.valueOf(server.getLocalPort()));
                tvMessages.setText(server.getInetAddress().toString());

                while (true) {

                    socket = server.accept();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));

                    String str = in.readLine();
                    if(!str.isEmpty())

                        tvMessages.setText(str);


//                    if(str=="clr"){
//                        tvMessages.setText("");
//                    }else{
//                        String tmp = tvMessages.getText().toString();
//                        tmp+="\n";
//                        tmp+=str;
//                        tvMessages.setText(tmp);
//                    }
                    Log.i("received response from server", str);

//                    in.close();
//                    socket.close();
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
//        try {
//            server.close();
//
//            printWriter.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}