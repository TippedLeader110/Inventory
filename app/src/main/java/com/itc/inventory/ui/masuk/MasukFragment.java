package com.itc.inventory.ui.masuk;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListAdapter;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.itc.inventory.DatabaseHandler;
import com.itc.inventory.R;
import com.itc.inventory.databinding.FragmentMasukBinding;import java.util.ArrayList;
import java.util.Calendar;

public class MasukFragment extends Fragment{

    private FragmentMasukBinding binding;
    DatePickerDialog.OnDateSetListener start, end;
    RecyclerView recyclerView;
    MasukListAdapter MasukListAdapter;
    FloatingActionButton datefab, addfab;
    ProgressDialog pd;
    DatabaseHandler databaseHandler;
    String sstart, ends;
    int tipe;

    public MasukFragment() {
        tipe = 1;
        setHasOptionsMenu(true);
    }

    public void setTipe(int tipe) {
        this.tipe = tipe;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMasukBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setHasOptionsMenu(true);

        databaseHandler = new DatabaseHandler(getActivity());

        recyclerView = binding.rcMasuk;
        datefab = binding.rangeFab;
        addfab = binding.addFab;
        MasukListAdapter = new MasukListAdapter(getActivity());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MasukListAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(MasukListAdapter);

        datefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), masukad.class);
//                startActivity(intent);
            }
        });

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Loading");
        pd.setMessage("Mengambil Data.....");
        pd.setCancelable(false);
        pd.show();
        fetchData();
        return root;
    }

    public void fetchData(){
        MasukListAdapter.setData(databaseHandler.getTransaksi(tipe));
        pd.dismiss();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_view, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                MasukListAdapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void dateListener(){

        Snackbar.make(binding.getRoot(), "Masukan tanggal mulai", Snackbar.LENGTH_LONG).show();
        start = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year , int month , int day) {
                month = month + 1;
                String dayD = String.valueOf(day);
                if (day<10) {
                    dayD = "0"+dayD;
                }
                String monthD = String.valueOf(month);
                if (month<10) {
                    monthD= "0"+monthD;
                }
                Log.d( "onDateSet" , month + "/" + day + "/" + year );
                sstart =  year+"-"+monthD+"-"+dayD;
            }
        };

        start = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year , int month , int day) {
                month = month + 1;
                String dayD = String.valueOf(day);
                if (day<10) {
                    dayD = "0"+dayD;
                }
                String monthD = String.valueOf(month);
                if (month<10) {
                    monthD= "0"+monthD;
                }
                Log.d( "onDateSet" , month + "/" + day + "/" + year );
                ends =  year+"-"+monthD+"-"+dayD;
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog =  new DatePickerDialog(
                getActivity(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                start,
                year,month,day
        );
        dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatePickerDialog dialog2 =  new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        end,
                        year,month,day
                );
                dialog2.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialog2.show();
                dialog2.setButton(DialogInterface.BUTTON_POSITIVE, "Terapkan filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getActivity(), sstart + " - " + ends, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        dialog.show();
    }
}