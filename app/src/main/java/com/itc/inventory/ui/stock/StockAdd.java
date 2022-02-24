
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
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.itc.inventory.DatabaseHandler;
import com.itc.inventory.R;

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

        if(getIntent().getStringExtra("view").equals("1")){
            setData(getIntent().getStringExtra("id"));
            delete.setVisibility(View.VISIBLE);
            add = false;
            title.setText("Informasi detail");
            save.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_assignment_24));
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
                        if(databaseHandler.deleteStock(id_intent)){
                            Intent intent = new Intent();
                            intent.putExtra("refresh", "true");
                            setResult(RESULT_OK, intent);
                            finish();
                            Toast.makeText(context, "Data barang berhasil dihapus ", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Terjadi kesalahan ", Toast.LENGTH_SHORT).show();
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
                    msg = databaseHandler.editStockData(oldid,stockBarang);
                }else{
                    msg = databaseHandler.addStockData(stockBarang);
                }
                progressDialog.dismiss();
                if(msg.equals("Kode barang telah digunakan")){
//                    kode.getEditText().setText(oldid);
                    kode.setBoxStrokeColorStateList(AppCompatResources.getColorStateList(StockAdd.this, R.color.red));
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("refresh", "true");
                    setResult(RESULT_OK, intent);
                    finish();
                }
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();

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

    public void setData(String id){
        StockBarang newBarang = databaseHandler.getStockDetail(id);
        oldid = id;
        kode.getEditText().setText(newBarang.getKode_barang());
        nama.getEditText().setText(newBarang.getNama_barang());
        satuan.getEditText().setText(newBarang.getSatuan());
        nsatuan.getEditText().setText(newBarang.getNilai_satuan().toString());
        harga.getEditText().setText(newBarang.getHarga().toString());
        stock.getEditText().setText(newBarang.getStock().toString());
        setEnable(false);

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