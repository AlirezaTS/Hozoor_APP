package com.example.hozoor2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnterExitActivity extends AppCompatActivity {

    private String token, name;
    String baseurl;
    Context context = this;
//    LocationManager locationManager;
//    private String provider;

    SharedPreferences sp;
    boolean type;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_exit);
        if (!GpsCheck()) {
            showGPSDialog();
        }
        baseurl = BuildConfig.URL_API;
        sp = getSharedPreferences("user_data", MODE_PRIVATE);
        token = sp.getString("token", null);
        name = sp.getString("name", null);
        TextView tvName = findViewById(R.id.tvName);
        tvName.setText(name + " خوش آمدید");
        if (token == null) {
            finish();
        }
        checkToken(token);
        button = findViewById(R.id.btnEnterExit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Criteria criteria = new Criteria();
//                provider = locationManager.getBestProvider(criteria, false);
//                if (ActivityCompat.checkSelfPermission(EnterExitActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EnterExitActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//                Location location = locationManager.getLastKnownLocation(provider);
                send_token_to_server(token,type);
            }
        });
    }

    public Boolean GpsCheck() {
        boolean aBoolean;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return aBoolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void showGPSDialog() {
        new AlertDialog.Builder(context).setTitle("لطفا GPS خود را روشن کنید")
                .setPositiveButton("روشن", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("لغو", null)
                .setCancelable(false)
                .show();
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
                if (response.isSuccessful()){
                    type = response.body().getType();
                    if(response.body().getStatus().equals("Already_Done")){
                        button.setText("برای امروز تمام");
                        button.setClickable(false);
                    }else{
                        button.setClickable(true);
                        if(response.body().getType()){
                            button.setText("ثبت ورود");
                        }else{
                            button.setText("ثبت خروج");
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiModel.checkToken> call, Throwable t) {
            }
        };
        call.enqueue(callback);
    }
    private void send_token_to_server(String token,boolean type) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ApiModel.checkInOut> call = apiInterface.check_in_out(type,token);

        Callback<ApiModel.checkInOut> callback = new Callback<ApiModel.checkInOut>() {
            @Override
            public void onResponse(Call<ApiModel.checkInOut> call, Response<ApiModel.checkInOut> response) {
                if (response.isSuccessful()) {
                    checkToken(token);
                } else {
                    new AlertDialog.Builder(EnterExitActivity.this).setTitle("خطا")
                            .setMessage("مشکلی در ارتباط با سرور به وجود آمده است لطفا اتصال اینترنت خود را چک کنید")
                            .setCancelable(false)
                            .setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            }).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModel.checkInOut> call, Throwable t) {
                new AlertDialog.Builder(EnterExitActivity.this).setTitle("خطا")
                        .setMessage("مشکلی در ارتباط با سرور به وجود آمده است لطفا اتصال اینترنت خود را چک کنید")
                        .setCancelable(false)
                        .setPositiveButton("تایید", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        };
        call.enqueue(callback);
    }
}