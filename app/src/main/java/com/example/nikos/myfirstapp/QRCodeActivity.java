package com.example.nikos.myfirstapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class QRCodeActivity extends AppCompatActivity {

    ImageView qrView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        qrView = findViewById(R.id.qrCodeView);

        Intent intent = getIntent();
        Bitmap bitmap =  intent.getParcelableExtra("qr");

        qrView.setImageBitmap(bitmap);
    }
}
