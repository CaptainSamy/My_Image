package com.example.my_image.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.my_image.R;
import com.example.my_image.utils.Network;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    public boolean networkConnect;
    public AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man__hinh__chao_);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // set cho ảnh full màn hình

        setContentView(R.layout.activity_man__hinh__chao_);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();  // ẩn thanh actionBar

        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT); // handler giúp lên lịch chuyển sang màn hình main sau 3s
    }

    //check network
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = Network.getConnectionStatusString(context);
            networkConnect = status == Network.TYPE_STATUS_NOT_CONNECTED;
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (status == Network.TYPE_STATUS_NOT_CONNECTED) {
                    showDialogNetwork();
                } else {

                }
            }
        }
    };

    private void showDialogNetwork() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
            }
        });

        // Create the AlertDialog
        dialog = builder.create();
        dialog.show();
    }
}

