package se.nosskirneh.webhookinvoker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class EventReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String url;

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            url = "http://192.168.1.17:3000/webhook/invoke/chromecast_off";
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            url = "http://192.168.1.17:3000/webhook/invoke/chromecast_on";
        } else {
            return;
        }

        try {
            final URL urlObj = new URL(url);
            final HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            final Thread thread = new Thread(() -> {
                try {
                    conn.getInputStream().close();
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Toast.makeText(context,"Failed to send request",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}