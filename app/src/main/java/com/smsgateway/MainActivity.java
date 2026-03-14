package com.smsgateway;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

Handler handler=new Handler();
boolean running=false;
EditText apiUrl;
TextView status;

Runnable task=new Runnable(){
 public void run(){
  if(running) checkSMS();
  handler.postDelayed(this,1000);
 }
};

protected void onCreate(Bundle b){
 super.onCreate(b);
 setContentView(R.layout.activity_main);

 apiUrl=findViewById(R.id.apiUrl);
 status=findViewById(R.id.status);

 SharedPreferences sp=getSharedPreferences("cfg",MODE_PRIVATE);
 apiUrl.setText(sp.getString("url",""));

 findViewById(R.id.startBtn).setOnClickListener(v->{
  running=true;
  status.setText("Running");
  sp.edit().putString("url",apiUrl.getText().toString()).apply();
 });

 findViewById(R.id.stopBtn).setOnClickListener(v->{
  running=false;
  status.setText("Stopped");
 });

 handler.post(task);
}

void checkSMS(){

 String url=apiUrl.getText().toString();
 if(url.isEmpty()) return;

 RequestQueue q=Volley.newRequestQueue(this);

 JsonObjectRequest r=new JsonObjectRequest(Request.Method.GET,url,null,
  res->{
   try{
    if(res.has("number")){
     String number=res.getString("number");
     String msg=res.getString("message");
     SmsManager.getDefault().sendTextMessage(number,null,msg,null,null);
    }
   }catch(Exception e){}
  },
  err->{}
 );

 q.add(r);
}

}