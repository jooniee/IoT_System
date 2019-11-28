package com.example.qrcodescan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.os.SystemClock.sleep;

public class RecommandShoe extends AppCompatActivity {
    private LinearLayout dlayout;
    Bitmap bitmap;

    Integer shoenum=0;
    String[][] shoes=new String[100][8];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommand_shoe);

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-northeast-2:c136b3bc-9f17-492d-9373-fdd2485f7a1b", // 자격 증명 풀 ID
                Regions.AP_NORTHEAST_2 // 리전
        );

        TransferNetworkLossHandler.getInstance(getApplicationContext());

        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);

        s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
        s3.setEndpoint("s3.ap-northeast-2.amazonaws.com");

        TransferUtility transferUtility = TransferUtility.builder()
                .context(getApplicationContext())
                .defaultBucket("myqrcodescan") // 디폴트 버킷 이름.
                .s3Client(s3)
                .build();
        String sdPath = "/data/data/com.example.qrcodescan/data";
        sdPath += "/shoes.txt";
        File temp = new File(sdPath);
        temp = new File(sdPath);
        try {
            temp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TransferObserver transferObserver =
                transferUtility.download(
                        "myqrcodescan",
                        "shoes.txt",
                        temp
                );

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    Log.d("AWS", "DOWNLOAD Completed!");
                    readShoes();
                }
            }
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                int percentage = (int) (bytesCurrent/bytesTotal * 100);
                //Display percentage transfered to user
                Log.d("AWS", ""+percentage);
            }

            @Override
            public void onError(int id, Exception ex) {
                // do something
                Log.d("AWS", "error!");
            }
        });
        temp.deleteOnExit();

    }
    public void readShoes()
    {
        String line;
        File temp = new File("/data/data/com.example.qrcodescan/data/shoes.txt");
        try{

            BufferedReader buf = new BufferedReader(new FileReader(temp));
            line=buf.readLine();
            while(true){
                for(int i=0;i<8;i++)
                {
                    shoes[shoenum][i]=line;
                    line=buf.readLine();
                }
                shoenum++;
                if(line==null) break;
            }
            buf.close();

        }catch(IOException e){e.printStackTrace();}
        setShoes();
    }
    public void setShoes() {

        ScrollView scroll=(ScrollView)findViewById(R.id.scroll);
        LinearLayout list =(LinearLayout)scroll.findViewById(R.id.list);

        Log.d("test",shoenum.toString());
        for(int i=0;i<shoenum;i++)
        {
            LinearLayout dshoe = (LinearLayout) View.inflate(this,R.layout.shoe,null);
            dshoe.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
            ImageView img = (ImageView) dshoe.findViewById(R.id.shoeimage);
            Glide.with(this).load(shoes[i][0]).into(img);
            TextView brand=(TextView)dshoe.findViewById(R.id.brandtext);
            brand.setText(shoes[i][1]);
            TextView model=(TextView)dshoe.findViewById(R.id.modeltext);
            model.setText(shoes[i][2]);
            TextView size=(TextView)dshoe.findViewById(R.id.sizetext);
            size.setText(shoes[i][3]);
            TextView sale=(TextView)dshoe.findViewById(R.id.saletext);
            sale.setText(shoes[i][7]);
            list.addView(dshoe);
        }
    }
}
