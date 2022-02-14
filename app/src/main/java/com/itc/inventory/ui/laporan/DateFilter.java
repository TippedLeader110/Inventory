package com.itc.inventory.ui.laporan;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.itc.inventory.ui.keluar.KeluarFragment;
import com.itc.inventory.ui.masuk.MasukFragment;

public class DateFilter extends BottomSheetDialogFragment {

    Button cari;
    String start, end, title;
    DatePickerDialog.OnDateSetListener dateSetListenerStart, dateSetListenerEnd;
    TextInputLayout tstart, send;
    int tipe;

    public DateFilter(int tipe) {
        this.tipe = tipe;
    }


}
