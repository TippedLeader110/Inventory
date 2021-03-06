package com.itc.inventory.ui.laporan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.itc.inventory.DatabaseHandler;
import com.itc.inventory.R;
import com.itc.inventory.ResponseData;
import com.itc.inventory.RetroClient;
import com.itc.inventory.databinding.ActivityTransaksiAddBinding;
import com.itc.inventory.databinding.FragmentStockBinding;
import com.itc.inventory.ui.stock.StockAdd;
import com.itc.inventory.ui.stock.StockBarang;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTransaksi extends AppCompatActivity {

    Integer selected_Stock;
    FloatingActionButton save, delete;
    AutoCompleteTextView dropdown_stock;
    ArrayList<StockBarang> stockBarangArrayList;
    ArrayList<String> dropdown_name;
    ArrayList<String> dropdown_kode;
    TextInputLayout daftar_stock, stock, nama, tgl, catatan;
    ProgressDialog pd;
    TextView title;
    DatabaseHandler databaseHandler;
    ArrayAdapter arrayAdapter;
    Integer tipe, id_transaksi;
    Boolean add, mode;
    AlertDialog.Builder builder;
    RetroClient retroClient;
    TransaksiInterface transaksiInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        retroClient = new RetroClient();
        transaksiInterface = retroClient.getClient().create(TransaksiInterface.class);

        setContentView(R.layout.activity_transaksi_add);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tambah transaksi");
        actionBar.setDisplayHomeAsUpEnabled(true);
        builder = new AlertDialog.Builder(this);
        dropdown_name = new ArrayList<>();
        dropdown_kode = new ArrayList<>();
        tipe = Integer.valueOf(getIntent().getStringExtra("tipe"));

        selected_Stock = 0;

        pd = new ProgressDialog(this);
        databaseHandler = new DatabaseHandler(this);

        title = findViewById(R.id.transaksi_title);
        daftar_stock = findViewById(R.id.transaksi_daftar_stock);
        stock = findViewById(R.id.transaksi_stock);
        nama = findViewById(R.id.transaksi_nama_user);
        tgl = findViewById(R.id.transaksi_tgl);
        catatan = findViewById(R.id.transaksi_catatan);
        delete = findViewById(R.id.transaksi_delete);
        save = findViewById(R.id.transaksi_add_fab);
        dropdown_stock = findViewById(R.id.transaksi_auto);
        mode = false;
        setDropDown();

        if(getIntent().getStringExtra("view").equals("1")){
            setData(Integer.valueOf(getIntent().getStringExtra("id")));
            setEnable(false);
            delete.setVisibility(View.VISIBLE);
            add = false;
            title.setText("Informasi detail transaksi");
            save.setImageDrawable(this.getDrawable(R.drawable.ic_baseline_assignment_24));
        }else{
            delete.setVisibility(View.GONE);
            add = true;
            setEnable(true);
        }

        if(tipe==1){
            title.setText("Tambah transaksi masuk");
        }else{
            title.setText("Tambah transaksi keluar");
        }

        tgl.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialogDate =  new DatePickerDialog(
                        AddTransaksi.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year , int month , int day) {
//                                Toast.makeText(AddTransaksi.this, "Masukan tanggal akhir", Toast.LENGTH_SHORT).show();
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
                                String tglnew =  year+"-"+monthD+"-"+dayD;
                                tgl.getEditText().setText(tglnew);
                            }
                        },
                        year, month, day
                );
                dialogDate.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialogDate.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(add){
                    getinputData();

                }else{
//                    title.setText("");
                    Toast.makeText(AddTransaksi.this, "Mode edit diaktifkan", Toast.LENGTH_SHORT).show();
                    add = true;
                    mode = true;
                    setEnable(true);
                }
            }
        });


    }

    private void getinputData() {
        TransaksiBarang transaksiBarang = new TransaksiBarang();
        transaksiBarang.setTipe_transaksi(tipe);
        transaksiBarang.setTgl_transaksi(tgl.getEditText().getText().toString());
        transaksiBarang.setNama(nama.getEditText().getText().toString());
        transaksiBarang.setCatatan(catatan.getEditText().getText().toString());
        transaksiBarang.setJumlah(Float.valueOf(stock.getEditText().getText().toString()));
        transaksiBarang.setKode_barang(dropdown_kode.get(selected_Stock));

        pd.setTitle("Mengirim data");
        pd.setMessage("Mohon menunggu.....");
        pd.show();
        builder.setTitle(R.string.app_name);
        builder.setMessage("Apakah data sudah benar ?");
//        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String msg;
                if(mode){
//                    msg = databaseHandler.editTransaksi(transaksiBarang, id_transaksi);
                    Call<ResponseData> editData = transaksiInterface.editTransaksi(id_transaksi, transaksiBarang.getJumlah(), transaksiBarang.getCatatan(), transaksiBarang.getNama(), transaksiBarang.getTgl_transaksi(), transaksiBarang.getTipe_transaksi());
                    editData.enqueue(new Callback<ResponseData>() {
                        @Override
                        public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                            doneJobe(response.body().getCode(), response.body().getMsg());
                            pd.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResponseData> call, Throwable t) {
                            Toast.makeText(AddTransaksi.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            Log.e("AddTransaksi", "Edit", t);
                            pd.dismiss();
                        }
                    });

                }else{
                    Call<ResponseData> editData = transaksiInterface.tambahTransaksi(transaksiBarang.kode_barang, transaksiBarang.getJumlah(), transaksiBarang.getCatatan(), transaksiBarang.getNama(), transaksiBarang.getTgl_transaksi(), transaksiBarang.getTipe_transaksi());
                    editData.enqueue(new Callback<ResponseData>() {
                        @Override
                        public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                            doneJobe(response.body().getCode(), response.body().getMsg());
                            pd.dismiss();
                        }

                        @Override
                        public void onFailure(Call<ResponseData> call, Throwable t) {
                            Toast.makeText(AddTransaksi.this, "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
                            Log.e("AddTransaksi", "Edit", t);
                            pd.dismiss();
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    void doneJobe(Integer code, String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        if(code!=999){
            Intent intent = new Intent();
            intent.putExtra("refresh", "true");
            setResult(RESULT_OK, intent);
            finish();
//                    Toast.makeText(AddTransaksi.this, databaseHandler.addTransaksi(transaksiBarang), Toast.LENGTH_SHORT).show();;
            Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
        }
    }

    private void setData(Integer id) {

        Call<ArrayList<StockBarang>> getStockID = transaksiInterface.getStockID();

        getStockID.enqueue(new Callback<ArrayList<StockBarang>>() {
            @Override
            public void onResponse(Call<ArrayList<StockBarang>> call, Response<ArrayList<StockBarang>> response) {
                stockBarangArrayList = new ArrayList<>();
                stockBarangArrayList.addAll(response.body());
                for (StockBarang cur: stockBarangArrayList){
                    dropdown_kode.add(cur.getKode_barang());
                    dropdown_name.add((cur.getNama_barang()) + " - " + cur.getKode_barang());
                }

                arrayAdapter = new ArrayAdapter(AddTransaksi.this, R.layout.dropdownmenu, dropdown_name);
                dropdown_stock.setText(arrayAdapter.getItem(0).toString(), false);
                dropdown_stock.setAdapter(arrayAdapter);
                pd.dismiss();

                dropdown_stock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selected_Stock = i;
                        Toast.makeText(AddTransaksi.this, dropdown_name.get(i) + " dipilih", Toast.LENGTH_SHORT).show();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id_intent = getIntent().getStringExtra("id");
                        builder.setTitle(R.string.app_name);
                        builder.setMessage("Apakah anda yakin ingin menghapus item ini ?");
//        builder.setIcon(R.drawable.ic_launcher);
                        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                pd.setTitle("Mengahpus data");
                                pd.setMessage("Mohon menunggu......");
                                pd.show();
                                Call<ResponseData> deleteTransaksi = transaksiInterface.hapusTransaksi(id_transaksi);
                                deleteTransaksi.enqueue(new Callback<ResponseData>() {
                                    @Override
                                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                                        pd.dismiss();
                                        doneJobe(response.body().getCode(), response.body().getMsg());
                                        dialog.dismiss();
                                        pd.setTitle("Mengambil Data");
                                        pd.setMessage("Mohon tunggu....");
                                        pd.show();

                                        Call<TransaksiBarang> getInfo = transaksiInterface.getTransaksiDetil(id);

                                        getInfo.enqueue(new Callback<TransaksiBarang>() {
                                            @Override
                                            public void onResponse(Call<TransaksiBarang> call, Response<TransaksiBarang> response) {
                                                TransaksiBarang transaksiBarang = response.body();
                                                tgl.getEditText().setText(transaksiBarang.getTgl_transaksi());
                                                nama.getEditText().setText(transaksiBarang.getNama());
                                                catatan.getEditText().setText(transaksiBarang.getCatatan());
                                                stock.getEditText().setText(transaksiBarang.getJumlah().toString());
                                                id_transaksi = id;
                                                int i = 0;
                                                for (String data : dropdown_kode){
                                                    if(data.equals(transaksiBarang.getKode_barang())){
                                                        break;
                                                    }
                                                    i++;
                                                }
                                                selected_Stock = i;
                                                dropdown_stock.setText(dropdown_name.get(i), false);
                                                dropdown_stock.setEnabled(false);
                                                pd.dismiss();
                                            }

                                            @Override
                                            public void onFailure(Call<TransaksiBarang> call, Throwable t) {
                                                Toast.makeText(AddTransaksi.this, "Terjadi kesalahan saat mengambil data", Toast.LENGTH_SHORT).show();
                                                Log.e("AddTransaksi", "Call Error", t);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure(Call<ResponseData> call, Throwable t) {
                                        pd.dismiss();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

            }

            @Override
            public void onFailure(Call<ArrayList<StockBarang>> call, Throwable t) {

            }
        });
//        TransaksiBarang transaksiBarang = new TransaksiBarang();

//        transaksiBarang = databaseHandler.getTransaksiDetail(id);


    }

    public void setEnable(Boolean d){
//        title.setEnabled(d);
//        daftar_stock.setEnabled(d);
        stock.setEnabled(d);
        nama.setEnabled(d);
        tgl.setEnabled(d);
        catatan.setEnabled(d);
//        delete.setEnabled(d);
//        save.setEnabled(d);
    }

    void setDropDown(){

        Call<ArrayList<StockBarang>> getStockID = transaksiInterface.getStockID();

        getStockID.enqueue(new Callback<ArrayList<StockBarang>>() {
            @Override
            public void onResponse(Call<ArrayList<StockBarang>> call, Response<ArrayList<StockBarang>> response) {
                stockBarangArrayList = new ArrayList<>();
                stockBarangArrayList.addAll(response.body());
                for (StockBarang cur: stockBarangArrayList){
                    dropdown_kode.add(cur.getKode_barang());
                    dropdown_name.add((cur.getNama_barang()) + " - " + cur.getKode_barang());
                }

                arrayAdapter = new ArrayAdapter(AddTransaksi.this, R.layout.dropdownmenu, dropdown_name);
                dropdown_stock.setText(arrayAdapter.getItem(0).toString(), false);
                dropdown_stock.setAdapter(arrayAdapter);
                pd.dismiss();

                dropdown_stock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selected_Stock = i;
                        Toast.makeText(AddTransaksi.this, dropdown_name.get(i) + " dipilih", Toast.LENGTH_SHORT).show();
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id_intent = getIntent().getStringExtra("id");
                        builder.setTitle(R.string.app_name);
                        builder.setMessage("Apakah anda yakin ingin menghapus item ini ?");
//        builder.setIcon(R.drawable.ic_launcher);
                        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                pd.setTitle("Mengahpus data");
                                pd.setMessage("Mohon menunggu......");
                                pd.show();
                                Call<ResponseData> deleteTransaksi = transaksiInterface.hapusTransaksi(id_transaksi);
                                deleteTransaksi.enqueue(new Callback<ResponseData>() {
                                    @Override
                                    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                                        pd.dismiss();
                                        doneJobe(response.body().getCode(), response.body().getMsg());
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseData> call, Throwable t) {
                                        pd.dismiss();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

            }

            @Override
            public void onFailure(Call<ArrayList<StockBarang>> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
