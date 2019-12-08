package com.example.qrcodescan;


import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.zxing.integration.android.IntentIntegrator;

import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class ScanQR extends AppCompatActivity {

    private IntentIntegrator qrScan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.setPrompt("QR코드를 카메라로 찍으세요");
        qrScan.initiateScan();

    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        TextView myshoeinfosize= (TextView)findViewById(R.id.myshoeinfosize);
        TextView myshoeinfolength= (TextView)findViewById(R.id.myshoeinfolength);
        TextView myshoeinfowidth= (TextView)findViewById(R.id.myshoeinfowidth);
        Button sendbutton=(Button)findViewById(R.id.sendbutton);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "스캔 취소됨", Toast.LENGTH_LONG).show();
                finish();
            } else {
                String cookie = result.getContents();
                int idx;
                idx=cookie.indexOf("/");
                myshoeinfosize.setText(cookie.substring(0,idx));
                cookie=cookie.substring(idx+1);
                idx=cookie.indexOf("/");
                myshoeinfolength.setText(cookie.substring(0,idx)+"cm");
                cookie=cookie.substring(idx+1);
                idx=cookie.indexOf("/");
                myshoeinfowidth.setText(cookie.substring(0,idx)+"cm");
                cookie=cookie.substring(idx+1);
                //QR코드가 스캔되면, 그정보를 신발 추천 액티비티에 같이 던져줌.
                sendbutton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
                        Intent intent = new Intent(ScanQR.this, RecommandShoe.class).putExtra("Cookie",result.getContents());;
                        startActivity(intent);

                    }
                });

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
