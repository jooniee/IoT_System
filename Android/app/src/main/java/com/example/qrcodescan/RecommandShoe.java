package com.example.qrcodescan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;

public class RecommandShoe extends AppCompatActivity {
    Bitmap bitmap;
    Integer shoenum=0;
    String[][] shoes=new String[1000][10];//신발 DB
    String[] myshoes=new String[10];//내신발 정보
    String selectshoe="모든 브랜드";
    Integer maxprice=9999999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommand_shoe);
    //내 신발 정보 저장
        String cookie = getIntent().getStringExtra("Cookie");
        int idx;
        for(int i=0;i<3;i++)
        {
            idx=cookie.indexOf("/");
            myshoes[i]=cookie.substring(0,idx);
            cookie=cookie.substring(idx+1);
        }

      // 스피너에서 브랜드 고르는 것
        Spinner spinner =(Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectshoe=parent.getItemAtPosition(position).toString();
                setShoes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //최대 가격 정하는 것
        Button button = (Button) findViewById(R.id.pricebutton);
        final EditText pricetext=(EditText)findViewById(R.id.maxpricetext);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(pricetext.getText().toString().equals("")){
                    maxprice=9999999;
                }
                else {
                    maxprice = Integer.parseInt(pricetext.getText().toString());
                }
                setShoes();
            }
        });

        //아마존 S3 버킷에서 파일 다운로드
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-west-2:ea72d10d-f1cd-4116-b42f-8b1b1e1e2d11", // 자격 증명 풀 ID
                Regions.US_WEST_2 // 리전
        );
        TransferNetworkLossHandler.getInstance(getApplicationContext());
        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(Regions.US_WEST_2));
        s3.setEndpoint("s3.us-west-2.amazonaws.com");
        final TransferUtility transferUtility = TransferUtility.builder()
                .context(getApplicationContext())
                .defaultBucket("lambdas3test4iot") // 디폴트 버킷 이름.
                .s3Client(s3)
                .build();
        readfile(transferUtility);

        ImageView reset=(ImageView)findViewById(R.id.resetbutton);
        reset.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                readfile(transferUtility);
                Toast.makeText(RecommandShoe.this, "신발 재고 불러오기 성공", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void readShoes()
    {
        //S3에서 받아온 파일을 파싱해서 String 배열에 저장해준다.
        String line;
        File temp = new File("/data/data/com.example.qrcodescan/data/shoes.txt");
        shoenum=0;
        try{

            BufferedReader buf = new BufferedReader(new InputStreamReader(new FileInputStream(temp),"euc-kr"));
            line=buf.readLine();
            while(true){
                for(int i=0;i<10;i++)
                {
                    shoes[shoenum][i]=line;
                    line=buf.readLine();
                }
                shoenum++;
                if(line==null) break;
            }
            buf.close();

        }catch(IOException e){e.printStackTrace();}

        //신발 판매량 순으로 정렬한다
        Arrays.sort(shoes,0,shoenum, new Comparator<String[]>() {
            @Override
            public int compare(final String[] entry1, final String[] entry2) {
                final String a = entry1[7];
                final String b = entry2[7];
                return b.compareTo(a);
            }
        });

        setShoes();
    }
    public void setShoes() {
        //String 배열로 저장되어있는 신발 DB를 토대로 List에 View들을 추가해준다
        ScrollView scroll=(ScrollView)findViewById(R.id.scroll);
        LinearLayout list =(LinearLayout)scroll.findViewById(R.id.list);
        list.removeAllViews();
        if(maxprice==0) maxprice=9999999;
        for(int i=0;i<shoenum;i++)
        {
            //최대 가격보다 비싼것 걸러주고, 사이즈 차이나는것도 걸러준다. Spinner에서 지정한 브랜드 것만 보여준다.
            if(Integer.parseInt(shoes[i][6])==0) continue;
            Log.d("test",""+i);
            if(maxprice<Integer.parseInt(shoes[i][8])) continue;
            if(Integer.parseInt(shoes[i][4])<Integer.parseInt(myshoes[1])-5 || Integer.parseInt(shoes[i][4])>Integer.parseInt(myshoes[1])+5) continue;
            if(Float.parseFloat(shoes[i][5])<Float.parseFloat(myshoes[2])-0.75 || Float.parseFloat(shoes[i][5])>Float.parseFloat(myshoes[2])+0.75) continue;
            if(selectshoe.equals("모든 브랜드") || selectshoe.equals(shoes[i][1])) {
                    LinearLayout dshoe = (LinearLayout) View.inflate(this, R.layout.shoe, null);
                    dshoe.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400));
                    ImageView img = (ImageView) dshoe.findViewById(R.id.shoeimage);
                    Glide.with(this).load(shoes[i][0]).into(img);
                    TextView size = (TextView) dshoe.findViewById(R.id.sizetext);
                    size.setText(shoes[i][3]);
                    TextView sale = (TextView) dshoe.findViewById(R.id.saletext);
                    sale.setText(shoes[i][7]);

                    TextView price= (TextView) dshoe.findViewById(R.id.pricetext);
                    String p=new String();
                    int sw=0;
                    for(int j=shoes[i][8].length()-1;j>=0;j--)
                    {
                        sw++;
                        p=shoes[i][8].charAt(j)+p;
                        if(sw==3 && j!=0)
                        {
                            p=","+p;
                            sw=0;
                        }
                    }
                    price.setText(p+"원");

                    TextView model = (TextView) dshoe.findViewById(R.id.modeltext);
                    model.setText(shoes[i][9]);
                    final int temp = i;
                    dshoe.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            Intent intent = new Intent(RecommandShoe.this, PopupActivity.class).putExtra("Cookie",shoes[temp]);
                            intent.putExtra("myShoeCookie",myshoes);
                            startActivity(intent);
                        }
                    });;
                    list.addView(dshoe);
            }
        }
    }
    public void readfile(TransferUtility transferUtility)
    {
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
                        "lambdas3test4iot",
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
}
