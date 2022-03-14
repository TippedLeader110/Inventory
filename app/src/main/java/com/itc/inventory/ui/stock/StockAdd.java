
package com.itc.inventory.ui.stock;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.itc.inventory.DatabaseHandler;
import com.itc.inventory.R;
import com.itc.inventory.ResponseData;
import com.itc.inventory.RetroClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockAdd extends AppCompatActivity {

    TextInputLayout kode, nama, satuan, nsatuan, harga, stock;
    FloatingActionButton save, delete;
    Boolean add, mode;
    Context context;
    TextView title;
    InputManager inputManager;
    DatabaseHandler databaseHandler;
    StockBarang stockBarang;
    String skode, snama, ssatuan, snsatuan, sharga, sstock;
    AlertDialog.Builder builder;
    String oldid;
    RetroClient retroClient;
    StockInterface stockInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_add);


        kode = findViewById(R.id.stock_kode_barang);
        nama = findViewById(R.id.stock_nama_barang);
        title = findViewById(R.id.stock_add_title);
        satuan = findViewById(R.id.stock_satuan);
        nsatuan = findViewById(R.id.stock_nilai_satuan);
        harga = findViewById(R.id.stock_harga);
        delete = findViewById(R.id.stock_delete);
        stock = findViewById(R.id.stock_stock);
        context = this;
        builder = new AlertDialog.Builder(context);
        databaseHandler = new DatabaseHandler(context);
        save = findViewById(R.id.stock_save);
        mode = false;

        retroClient = new RetroClient();
        stockInterface = retroClient.getClient().create(StockInterface.class);
        oldid = getIntent().getStringExtra("id");

        if(getIntent().getStringExtra("view").equals("1")){
            Call<StockBarang> getStockDetail = stockInterface.getStockInfo(1, oldid);
            getStockDetail.enqueue(new Callback<StockBarang>() {
                @Override
                public void onResponse(Call<StockBarang> call, Response<StockBarang> response) {
                    Log.w("Data Click fetch", "d");
                    setData(response.body());
                }

                @Override
                public void onFailure(Call<StockBarang> call, Throwable t) {
                    Log.w("Error", t);
                }
            });

            delete.setVisibility(View.VISIBLE);
            add = false;
            title.setText("Informasi detail");
            save.setImageDrawable(this.getDrawable(R.drawable.ic_baseline_assignment_24));
        }else{
            delete.setVisibility(View.GONE);
            add = true;
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(add){
                    getInputValue();
                }else{
                    Toast.makeText(context, "Mode edit diaktifkan", Toast.LENGTH_SHORT).show();
                    save.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_save_24));
                    setEnable(true);
                    add = true;
                    mode = true;
                }
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
                        deleteStock(id_intent);
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

    private void deleteStock(String id_intent) {
        Call<ResponseData> responseDataCall = stockInterface.hapusStock(id_intent);

        responseDataCall.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if(response.body().getCode()==200){
                    Intent intent = new Intent();
                    intent.putExtra("refresh", "true");
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
//                    Toast.makeText(context, "Terjadi kesalahan ", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(context, response.body().getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                Log.w("Delete F", "Gagal hapus", t);
            }
        });
    }

    private void getInputValue() {
        stockBarang = new StockBarang();
        stockBarang.setKode_barang(kode.getEditText().getText().toString());
        stockBarang.setNama_barang(nama.getEditText().getText().toString());
        stockBarang.setSatuan(satuan.getEditText().getText().toString());
        stockBarang.setNilai_satuan(Float.valueOf(nsatuan.getEditText().getText().toString()));
        stockBarang.setHarga(Float.valueOf(harga.getEditText().getText().toString()));
        stockBarang.setStock(Float.valueOf(stock.getEditText().getText().toString()));

        builder.setTitle(R.string.app_name);
        builder.setMessage("Apakah data sudah benar ?");
//        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Menyimpan data");
                progressDialog.setMessage("Memuat....");
                progressDialog.show();
                String msg;
                if(mode){
                    Call<ResponseData> addStock = stockInterface.editStock(oldid, stockBarang.getKode_barang(), stockBarang.getNama_barang(), stockBarang.getStock(), stockBarang.getSatuan(), stockBarang.getNilai_satuan(), stockBarang.getHarga());

                    addStock.enqueue(new Callback<ResponseData>() {
                        @Override
                        public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                            ResponseData rd = response.body();
                            progressDialog.dismiss();
                            doneJob(rd.getCode(), rd.getMsg());
                        }

                        @Override
                        public void onFailure(Call<ResponseData> call, Throwable t) {
                            Log.w("Error retrofit", t);
                            progressDialog.dismiss();
                        }
                    });
                }else{
                    Call<ResponseData> addStock = stockInterface.addStock(stockBarang.getKode_barang(), stockBarang.getNama_barang(), stockBarang.getStock(), stockBarang.getSatuan(), stockBarang.getNilai_satuan(), stockBarang.getHarga());

                    addStock.enqueue(new Callback<ResponseData>() {
                        @Override
                        public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                            ResponseData rd = response.body();
                            progressDialog.dismiss();
                            doneJob(rd.getCode(), rd.getMsg());
                        }

                        @Override
                        public void onFailure(Call<ResponseData> call, Throwable t) {
                            Log.w("Error retrofit", t);
                            progressDialog.dismiss();
                        }
                    });
//                    msg = databaseHandler.addStockData(stockBarang);
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

    public void setData(StockBarang newBarang){
        kode.getEditText().setText(newBarang.getKode_barang());
        nama.getEditText().setText(newBarang.getNama_barang());
        satuan.getEditText().setText(newBarang.getSatuan());
        nsatuan.getEditText().setText(newBarang.getNilai_satuan().toString());
        harga.getEditText().setText(newBarang.getHarga().toString());
        stock.getEditText().setText(newBarang.getStock().toString());
        setEnable(false);

    }

    public void doneJob(Integer code, String msg){
//        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        if(code==999){
//                    kode.getEditText().setText(oldid);
            kode.setBoxStrokeColorStateList(AppCompatResources.getColorStateList(StockAdd.this, R.color.red));
        }else if(code==200){
            Intent intent = new Intent();
            intent.putExtra("refresh", "true");
            intent.putExtra("msgr", msg);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void setEnable(Boolean status){
        kode.setEnabled(status);
        nama.setEnabled(status);
        satuan.setEnabled(status);
        nsatuan.setEnabled(status);
        harga.setEnabled(status);
        stock.setEnabled(status);
    }


}