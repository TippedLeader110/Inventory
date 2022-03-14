package com.itc.inventory.ui.laporan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.itc.inventory.R;
import com.itc.inventory.databinding.FragmentLaporanBinding;

public class LaporanActivity extends AppCompatActivity {

    Button stock, masuk, keluar;

    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_laporan);
        
        stock = findViewById(R.id.laporan_stock);
        masuk = findViewById(R.id.laporan_masuk);
        keluar = findViewById(R.id.laporan_keluar);

        stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateFilter("1");
            }
        });

        masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateFilter("2");
            }
        });

        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateFilter("3");
            }
        });

    }
    private void dateFilter(String i) {
        Intent intent = new Intent(LaporanActivity.this, DateFilter.class);
        intent.putExtra("mode", i);
        startActivity(intent);
    }
}

