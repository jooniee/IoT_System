package com.example.qrcodescan;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOError;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button scanQRbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initProfile();
        scanQRbutton=(Button)findViewById(R.id.scanbutton);
        scanQRbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, RecommandShoe.class);
                startActivity(intent);
            }
        });

    }
    private void initProfile() {
        String cookie = getIntent().getStringExtra("Cookie");

        final Ajou ajou = new Ajou();
        final User user =  ajou.printUser(cookie);

        final ImageView imagePicture = (ImageView) findViewById(R.id.StuImg);
        final TextView textName = (TextView) findViewById(R.id.StuName);
        final TextView textNumber = (TextView) findViewById(R.id.StuNum);
        final TextView textMajor = (TextView) findViewById(R.id.StuMajor);

        imagePicture.post(new Runnable() {
            @Override
            public void run() {
                int image_height = imagePicture.getHeight();
                imagePicture.getLayoutParams().height = image_height;
                if(user.getNumber()!=0) {
                    imagePicture.setImageBitmap(ajou.printPicture(user.getNumber(),image_height));
                    textName.setText(user.getName());
                    textMajor.setText(user.getMajor());
                    textNumber.setText(user.getNumber()+"");
                }
            }
        });

    }
}
