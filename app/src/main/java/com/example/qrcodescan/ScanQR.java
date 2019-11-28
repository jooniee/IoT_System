package com.example.qrcodescan;


import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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
        TextView resultText=(TextView)findViewById(R.id.resulttext);
        Button sendbutton=(Button)findViewById(R.id.sendbutton);
/*
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:c136b3bc-9f17-492d-9373-fdd2485f7a1b", // 자격 증명 풀 ID
                Regions.AP_NORTHEAST_2 // 리전
        );

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        final TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

*/
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                resultText.setText(result.getContents());

                sendbutton.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View v){
/*
                        File temp = null;
                        try {
                            temp=File.createTempFile("My_shoes_info",".txt");
                            FileWriter writer= new FileWriter(temp,true);
                            writer.write(result.getContents().toString());
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        TransferObserver observer = transferUtility.upload(
                                "myqrcodescan",
                                "My_shoes_info.txt",
                                temp
                        );
                        Toast.makeText(ScanQR.this, "전송 완료", Toast.LENGTH_LONG).show();
                        temp.deleteOnExit();
                         finish();
*/
                        Intent intent = new Intent(ScanQR.this, RecommandShoe.class);
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
