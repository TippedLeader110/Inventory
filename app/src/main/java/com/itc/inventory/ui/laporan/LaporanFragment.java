package com.itc.inventory.ui.laporan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.itc.inventory.databinding.FragmentLaporanBinding;

public class LaporanFragment extends Fragment {

    FragmentLaporanBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LaporanViewModel laporanViewModel =
                new ViewModelProvider(this).get(LaporanViewModel.class);

        binding = FragmentLaporanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGallery;
        laporanViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }
}
