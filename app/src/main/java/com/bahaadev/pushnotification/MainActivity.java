 package com.bahaadev.pushnotification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

 public class MainActivity extends AppCompatActivity {

     TextView tvResult;

     boolean isSuccess;
     int code;
     String message = null;

     String baseURL = "https://mcc-users-api.herokuapp.com/";
     String email,password;
     final HashMap<String, String> parameters = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

        Intent myIntent = getIntent();
         email = myIntent.getStringExtra("email");
         password = myIntent.getStringExtra("password");
        updateRegToken();

    }




     public void updateToken(String token){
         RequestQueue queue = Volley.newRequestQueue(this);

         if (email.isEmpty() || password.isEmpty() || token.trim().isEmpty()){
             return;
         }
         String url = baseURL + "add_reg_token";


         parameters.put("email",email);
         parameters.put("password",password);
         parameters.put("reg_token",token);

         StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
                 try {
                     JSONObject responseObj = new JSONObject(response);
                     isSuccess = responseObj.getBoolean("status");
                     code = responseObj.getInt("statusCode");
                     message = responseObj.getString("msg");
                     if (responseObj.has("data")) {
                         JSONObject data = responseObj.getJSONObject("data");

                         // Handle your server response data here
                     }

                     tvResult.setTextColor(Color.GREEN);
                     tvResult.setText(message);

                 } catch (Exception e) { // caught while parsing the response

                     Log.e("TAG", "problem occurred");
                     e.printStackTrace();
                 }
             }

         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 try {
                     byte[] htmlBodyBytes = error.networkResponse.data;
                     String responseMessage = new String(htmlBodyBytes);
                     // tvResult.setText(responseMessage);

                     responseMessage = responseMessage.substring(1, responseMessage.length()-1);
                     responseMessage = responseMessage.replace("\"","");
                     String[] detailsVal = responseMessage.split(",");


                     for(String pair : detailsVal)
                     {
                         String[] entry = pair.split(":");

                         if (entry[0].trim().equals("status")){
                             isSuccess = Boolean.parseBoolean(entry[1].trim());

                         }else if (entry[0].trim().equals("statusCode")){
                             code = Integer.parseInt(entry[1].trim());

                         } else if (entry[0].trim().equals("msg")){
                             message = entry[1].trim();
                         }

                     }

                     tvResult.setTextColor(Color.RED);
                     tvResult.setText(message);



                     //  Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show();

                 } catch (NullPointerException e) {
                     e.printStackTrace();
                 }

             }
         }){
             @Nullable
             @Override
             protected Map<String, String> getParams() throws AuthFailureError {
                 return parameters;
             }

             @Override
             public Map<String, String> getHeaders() throws AuthFailureError {
                 HashMap<String, String> headers = new HashMap<>();
                /*headers.put("Content-Type","application/json");
                headers.put("Accept","application/json");*/
                 headers.put("User-Agent","Mozilla/5.0");
                 // Add your Header paramters here
                 return headers;
             }
         };

         queue.add(stringRequest);
     }

     private void updateRegToken() {
         FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
             @Override
             public void onComplete(@NonNull Task<String> task) {
                 if (!task.isSuccessful()){
                     Toast.makeText(MainActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                     return;
                 }
                 String token = task.getResult();

                 updateToken(token);


             }
         });

     }
}