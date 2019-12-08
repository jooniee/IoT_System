package com.example.qrcodescan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PopupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        String shoes[] = getIntent().getStringArrayExtra("Cookie");
        ImageView img = (ImageView)findViewById(R.id.shoeimage);
        Glide.with(this).load(shoes[0]).into(img);
        TextView brand = (TextView)findViewById(R.id.ppbrandtext);
        brand.setText(shoes[1]);
        TextView size = (TextView) findViewById(R.id.ppsizetext);
        size.setText(shoes[3]);
        TextView size2 = (TextView) findViewById(R.id.actuallengthtext);
        size2.setText(shoes[4]+"cm");
        TextView size3 = (TextView) findViewById(R.id.actualwidhttext);
        size3.setText(shoes[5]+"cm");
        TextView price = (TextView)findViewById(R.id.pppricetext);
        String p=new String();
        int sw=0;
        for(int j=shoes[8].length()-1;j>=0;j--)
        {
            sw++;
            p=shoes[8].charAt(j)+p;
            if(sw==3 && j!=0)
            {
                p=","+p;
                sw=0;
            }
        }
        price.setText(p+"원");
        TextView model = (TextView)findViewById(R.id.ppmodeltext);
        model.setText(shoes[9]);
        String myshoes[]=getIntent().getStringArrayExtra("myShoeCookie");
        TextView myshoesize = (TextView)findViewById(R.id.myshoesize);
        myshoesize.setText(myshoes[0]);
        TextView myshoeslenght = (TextView)findViewById(R.id.myshoeslength);
        myshoeslenght.setText(myshoes[1]+"cm");
        TextView myshoeswidth = (TextView)findViewById(R.id.myshoeswidth);
        myshoeswidth.setText(myshoes[2]+"cm");
    }
}
