package com.example.hozoor2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String token;
    String baseurl;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseurl = BuildConfig.URL_API;
        sp = getSharedPreferences("user_data", MODE_PRIVATE);
        editor = sp.edit();
        token = sp.getString("token", null);
        if(token != null){
            checkToken(token);
        }
        Button button = findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et = findViewById(R.id.etCode);
                send_secure_code_to_server(Integer.parseInt(et.getText().toString().trim()));
            }
        });
    }

    private void checkToken(String token){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ApiModel.checkToken> call = apiInterface.check_token(token);

        Callback<ApiModel.checkToken> callback = new Callback<ApiModel.checkToken>() {
            @Override
            public void onResponse(Call<ApiModel.checkToken> call, Response<ApiModel.checkToken> response) {
                Intent intent = new Intent(MainActivity.this,EnterExitActivity.class);
                editor.putString("name", response.body().getName());
                editor.apply();
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<ApiModel.checkToken> call, Throwable t) {
            }
        };
        call.enqueue(callback);
    }
    private void send_secure_code_to_server(int secureCode) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ApiModel.register> call = apiInterface.register(secureCode);

        Callback<ApiModel.register> callback = new Callback<ApiModel.register>() {
            @Override
            public void onResponse(Call<ApiModel.register> call, Response<ApiModel.register> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals("ok")){
                        editor.putString("token", response.body().getToken());
                        editor.apply();
                        checkToken(response.body().getToken());
                    }
                } else {
                    new AlertDialog.Builder(MainActivity.this).setTitle("خطا")
                            .setMessage("مشکلی در ارتباط با سرور به وجود آمده است لطفا اتصال اینترنت خود را چک کنید")
                            .setCancelable(false)
                            .setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ApiModel.register> call, Throwable t) {
                new AlertDialog.Builder(MainActivity.this).setTitle("خطا")
                        .setMessage("مشکلی در ارتباط با سرور به وجود آمده است لطفا اتصال اینترنت خود را چک کنید")
                        .setCancelable(false)
                        .setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).
                        setNegativeButton("تلاش مجدد", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .show();
            }
        };
        call.enqueue(callback);
    }
    private void save(){

    }
}