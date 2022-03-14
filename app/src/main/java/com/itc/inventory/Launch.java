package com.itc.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.itc.inventory.ui.Login;
import com.itc.inventory.ui.LoginInterface;
import com.itc.inventory.ui.laporan.DateFilter;
import com.itc.inventory.ui.laporan.LaporanActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Launch extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE_READ = 1000;
    private static final int PERMISSION_STORAGE_CODE_WRITE = 1001;
    private static final int PERMISSION_STORAGE_CODE = 1002;
    ConstraintLayout cl;
    Boolean trial;
    Context context;
    Button login;
    TextInputLayout username, password;
    LoginInterface loginInterface;
    RetroClient retroClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_launch);
        trialCode();
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        cl = findViewById(R.id.launch_constraint);
        context = this;

        if(trial){
            Toast.makeText(context, "Klik dimana saja untuk melanjutkan", Toast.LENGTH_SHORT).show();
        }
        retroClient = new RetroClient();
        loginInterface = retroClient.getClient().create(LoginInterface.class);

        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Login> loginUser = loginInterface.Login(username.getEditText().getText().toString(), password.getEditText().getText().toString());

                loginUser.enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        if(response.body().getStatus()==1){
                            Intent intent = new Intent(Launch.this, MainActivity.class);
                            startActivity(intent);
                        }else if(response.body().getStatus()==2){
                            Intent intent = new Intent(Launch.this, LaporanActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(context, "Username/Password tidak valid", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {
                        Log.w("error login", t);
                    }
                });

//                if(trial){
//
//                    Intent intent = new Intent(Launch.this, MainActivity.class);
//                    startActivity(intent);
//                }else{
//                    Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        checkFilePermission();
    }

    public void trialCode(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate = null;
        try {
            strDate = sdf.parse("02/28/2022");
            if (new Date().after(strDate)) {
                trial = false;
            }else{

                trial = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public boolean checkFilePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(Launch.this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(Launch.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(Launch.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        }
    }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
//                    PackageManager.PERMISSION_DENIED){
//                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                requestPermissions(permission, PERMISSION_STORAGE_CODE_WRITE);
//            }
//            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
//                    PackageManager.PERMISSION_DENIED){
//                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
//                requestPermissions(permission, PERMISSION_STORAGE_CODE_WRITE);
//            }
//        }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE_WRITE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(context, "Akses Penyimpanan External Ditolak", Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSION_STORAGE_CODE_READ: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(context, "Akses Penyimpanan External Ditolak", Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSION_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
//                    downloadFile(url, filePath);
                } else {
                    Toast.makeText(context, "Akses Penyimpanan External Ditolak", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}