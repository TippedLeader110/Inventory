package com.itc.inventory.ui.masuk;

import android.app.Activity;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.itc.inventory.RetroClient;
import com.itc.inventory.databinding.FragmentMasukBinding;
import com.itc.inventory.ui.laporan.AddTransaksi;
import com.itc.inventory.ui.laporan.TransaksiBarang;
import com.itc.inventory.ui.laporan.TransaksiInterface;
import com.itc.inventory.ui.stock.StockBarang;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MasukFragment extends Fragment implements  MasukListAdapter.dataBack{

    private FragmentMasukBinding binding;
    DatePickerDialog.OnDateSetListener start, end;
    RecyclerView recyclerView;
    MasukListAdapter MasukListAdapter;
    FloatingActionButton datefab, addfab;
    ProgressDialog pd;
    DatabaseHandler databaseHandler;
    String sstart, ends;
    int tipe;
    RetroClient retroClient;
    TransaksiInterface transaksiInterface;

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
        retroClient = new RetroClient();
        transaksiInterface = retroClient.getClient().create(TransaksiInterface.class);

        databaseHandler = new DatabaseHandler(getActivity());

        recyclerView = binding.rcMasuk;
        datefab = binding.rangeFab;
        addfab = binding.addFab;
        MasukListAdapter = new MasukListAdapter(getActivity(), this);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MasukListAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(MasukListAdapter);

        dateListener();

        datefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog =  new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year , int month , int day) {
                                Toast.makeText(getActivity(), "Masukan tanggal mulai", Toast.LENGTH_SHORT).show();
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

                                DatePickerDialog dialog2 =  new DatePickerDialog(
                                        getActivity(),
                                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                        new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker datePicker, int year , int month , int day) {
                                                Toast.makeText(getActivity(), "Masukan tanggal akhir", Toast.LENGTH_SHORT).show();
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

                                                startFilter(sstart, ends);
                                            }
                                        },
                                        year, month, day
                                );
                                dialog2.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                                dialog2.show();
                            }
                        },
                        year, month, day
                );
                dialog.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );

                dialog.show();
            }
        });

        addfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddTransaksi.class);
                intent.putExtra("tipe", String.valueOf(tipe));
                intent.putExtra("view", "0");
                activityResultLauncher.launch(intent);
//                startActivity(intent);
            }
        });

        pd = new ProgressDialog(getActivity());
        pd.setTitle("Loading");
        pd.setMessage("Mengambil Data.....");
        pd.setCancelable(false);
        pd.show();
        fetchData();
        checkRow();
        return root;
    }

    private void checkRow() {
        Call<ArrayList<StockBarang>> getRow = transaksiInterface.getStockID();
        getRow.enqueue(new Callback<ArrayList<StockBarang>>() {
            @Override
            public void onResponse(Call<ArrayList<StockBarang>> call, Response<ArrayList<StockBarang>> response) {
                if (response.body().size()==0){
                    addfab.setEnabled(false);
                    datefab.setEnabled(false);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<StockBarang>> call, Throwable t) {

            }
        });
    }

    private void startFilter(String sstart, String ends) {
        pd.show();
        Call<ArrayList<TransaksiBarang>> getTransaksiDate = transaksiInterface.getTransaksiFilter(tipe, sstart, ends);
        getTransaksiDate.enqueue(new Callback<ArrayList<TransaksiBarang>>() {
            @Override
            public void onResponse(Call<ArrayList<TransaksiBarang>> call, Response<ArrayList<TransaksiBarang>> response) {
                MasukListAdapter.setData(response.body());
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<TransaksiBarang>> call, Throwable t) {
                Log.w("Transaksi filter", t);
                pd.dismiss();
            }
        });

    }

    public void fetchData(){
//        MasukListAdapter.setData(databaseHandler.getTransaksi(tipe));
        Call<ArrayList<TransaksiBarang>> transaksiCall = transaksiInterface.getTransaksi(tipe);

        transaksiCall.enqueue(new Callback<ArrayList<TransaksiBarang>>() {
            @Override
            public void onResponse(Call<ArrayList<TransaksiBarang>> call, Response<ArrayList<TransaksiBarang>> response) {
                MasukListAdapter.setData(response.body());
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<TransaksiBarang>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                Log.e("TransaksiMasuk", "Error", t);
                pd.dismiss();
            }
        });


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

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        fetchData();
                    }
                }
            }
    );

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void dateListener(){

//        Snackbar.make(getActivity().getCurrentFocus(), "Masukan tanggal mulai", Snackbar.LENGTH_LONG).show();
        start = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year , int month , int day) {
                Toast.makeText(getActivity(), "Masukan tanggal mulai", Toast.LENGTH_SHORT).show();
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


    }

    @Override
    public void detailTransaksi(Integer id_transaksi) {
        Intent intent = new Intent(getActivity(), AddTransaksi.class);
        intent.putExtra("tipe", String.valueOf(tipe));
        intent.putExtra("view", "1");
        intent.putExtra("id", String.valueOf(id_transaksi));
        activityResultLauncher.launch(intent);
    }
}