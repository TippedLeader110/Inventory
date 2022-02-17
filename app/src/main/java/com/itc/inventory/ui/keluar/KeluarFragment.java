package com.itc.inventory.ui.keluar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.itc.inventory.databinding.FragmentKeluarBinding;
import com.itc.inventory.ui.masuk.MasukFragment;

public class KeluarFragment extends MasukFragment {

    public KeluarFragment() {
        setTipe(2);
    }
}