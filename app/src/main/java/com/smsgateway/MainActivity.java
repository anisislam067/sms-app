package com.smsgateway;

import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {

Handler handler = new Handler();

Runnable runnable = new Runnable() {
 @Override
 public void run() {
  checkSMS();
  handler.postDelayed(this,2000);
 }
};

@Override
protected void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);
 setContentView(R.layout.activity_main);
 handler.post(runnable);
}

private void checkSMS(){

 String url = "https://yourdomain.com/get_sms.php";

 RequestQueue queue = Volley.newRequestQueue(this);

 JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
 response -> {

 try{

  if(response.has("number")){

   String number = response.getString("number");
   String message = response.getString("message");
   String id = response.getString("id");

   SmsManager sms = SmsManager.getDefault();
   sms.sendTextMessage(number,null,message,null,null);

  }

 }catch(Exception e){}

 }, error -> {});

 queue.add(request);

}

}