package com.itc.inventory.ui.laporan;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.itc.inventory.databinding.FragmentLaporanBinding;

public class LaporanFragment extends Fragment {

    FragmentLaporanBinding binding;
    Button stock, masuk, keluar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentLaporanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        stock = binding.laporanStock;
        masuk = binding.laporanMasuk;
        keluar = binding.laporanKeluar;

        stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFilter("1");
            }
        });

        masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFilter("2");
            }
        });

        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateFilter("3");
            }
        });

        return root;
    }

    public void DateFilter(String i){
        Intent intent = new Intent(getActivity(), DateFilter.class);
        intent.putExtra("mode", i);
        startActivity(intent);
    }
}
