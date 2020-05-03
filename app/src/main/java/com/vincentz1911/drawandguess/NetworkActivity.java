package com.vincentz1911.drawandguess;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkActivity extends AppCompatActivity {
    final static int PORT = 3456;
    Button btnSend;
    EditText inputIP, inputMessage;
    TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network);
        btnSend = findViewById(R.id.btn_send);
        inputIP = findViewById(R.id.input_ip);
        inputMessage = findViewById(R.id.input_message);
        txtMessage = findViewById(R.id.txt_message);

        txtMessage.setText(getLocalIpAddress());
        Thread server = new Thread(new server());
        server.start();

        btnSend.setOnClickListener(view -> {
            BackgroundTask bgt = new BackgroundTask();
            bgt.execute(inputIP.getText().toString(), inputMessage.getText().toString());

        });
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    class server implements Runnable {
        ServerSocket ss;
        Socket socket;
        DataInputStream dis;
        Handler handler = new Handler();
        String message;

        @Override
        public void run() {
            try {
                ss = new ServerSocket(PORT);

                while (true) {
                    socket = ss.accept();
                    dis = new DataInputStream(socket.getInputStream());
                    message = dis.readUTF();

                    handler.post(() -> txtMessage.setText(message));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class BackgroundTask extends AsyncTask<String, Void, String> {

        Socket s;
        DataOutputStream dos;
        String ip, message;

        @Override
        protected String doInBackground(String... params) {
            ip = params[0];
            message = params[1];
            try {
                s = new Socket(ip, PORT);
                dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(message);
                dos.close();
                s.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

