
package com.smsgateway;

import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.telephony.SubscriptionInfo;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.*;
import com.android.volley.toolbox.*;

import java.util.*;

public class MainActivity extends AppCompatActivity {

Handler handler=new Handler();
boolean running=false;
EditText apiUrl;
Spinner simSelect;

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
 simSelect=findViewById(R.id.simSelect);

 ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"SIM 1","SIM 2"});
 simSelect.setAdapter(adapter);

 findViewById(R.id.startBtn).setOnClickListener(v-> running=true);
 findViewById(R.id.stopBtn).setOnClickListener(v-> running=false);

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
    String id=res.getString("id");

    int simIndex=simSelect.getSelectedItemPosition();

    SubscriptionManager sm=(SubscriptionManager)getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
    List<SubscriptionInfo> list=sm.getActiveSubscriptionInfoList();

    int subId=list.get(simIndex).getSubscriptionId();

    SmsManager sms=SmsManager.getSmsManagerForSubscriptionId(subId);

    sms.sendTextMessage(number,null,msg,null,null);

    updateStatus(id,"sent");

   }

  }catch(Exception e){}
 },
 err->{});

 q.add(r);

}

void updateStatus(String id,String status){

 RequestQueue q=Volley.newRequestQueue(this);

 String url="https://yourdomain.com/update_sms.php";

 StringRequest r=new StringRequest(Request.Method.POST,url,
 res->{},
 err->{}){

 protected Map<String,String> getParams(){

 Map<String,String> p=new HashMap<>();
 p.put("id",id);
 p.put("status",status);
 return p;

 }

 };

 q.add(r);

}

}
