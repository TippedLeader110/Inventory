package com.itc.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Launch extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE_READ = 1000;
    private static final int PERMISSION_STORAGE_CODE_WRITE = 1001;
    private static final int PERMISSION_STORAGE_CODE = 1002;
    ConstraintLayout cl;
    Boolean trial;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_launch);
        trialCode();

        cl = findViewById(R.id.launch_constraint);
        context = this;

        if(trial){
            Toast.makeText(context, "Klik dimana saja untuk melanjutkan", Toast.LENGTH_SHORT).show();
        }

        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(trial){

                    Intent intent = new Intent(Launch.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(context, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                }
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

    public void checkFilePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_STORAGE_CODE_WRITE);
            }
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_STORAGE_CODE_WRITE);
            }
        }
    }

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