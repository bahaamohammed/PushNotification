package com.bahaadev.pushnotification;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    TextView tvSignin,tvMessage;
    EditText etFirstName,etSecondName,etEmail,etPassword;
    Button btnSignup;

    boolean isSuccess;
    int code;
    String message = null;





    String baseURL = "https://mcc-users-api.herokuapp.com/";
    final HashMap<String, String> parameters = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        tvSignin = findViewById(R.id.tvSignin);
        tvMessage = findViewById(R.id.tvMessage);
        etFirstName = findViewById(R.id.etFirstName);
        etSecondName = findViewById(R.id.etSecondName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);

        String text2 = "Sign In";
        String text1 = "Already have an account? " + text2;


        Spannable spannable = new SpannableString(text1);

        spannable.setSpan(new ForegroundColorSpan(Color.WHITE), (text1.length() - text2.length()), (text1.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSignin.setText(spannable, TextView.BufferType.SPANNABLE);

        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SigninActivity.class);
                startActivity(intent);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirstName.getText().toString().trim();
                String secondName = etSecondName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (!firstName.isEmpty() && !secondName.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    Signup(firstName,secondName,email,password);
                }else{
                    Toast.makeText(SignupActivity.this, "Check Your Input Data", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    public void Signup(String firstName, String secondName, String email, String password){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = baseURL + "add_new_user";


        parameters.put("firstName",firstName);
        parameters.put("secondName",secondName);
        parameters.put("email",email);
        parameters.put("password",password);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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

                    tvMessage.setTextColor(Color.GREEN);
                    tvMessage.setText(message);

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

                    tvMessage.setTextColor(Color.RED);
                    tvMessage.setText(message);



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
}