
package com.itc.inventory.ui.stock;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.itc.inventory.DatabaseHandler;
import com.itc.inventory.R;

public class StockAdd extends AppCompatActivity {

    TextInputLayout kode, nama, satuan, nsatuan, harga, stock;
    FloatingActionButton save;
    Context context;
    InputManager inputManager;
    DatabaseHandler databaseHandler;
    StockBarang stockBarang;
    String skode, snama, ssatuan, snsatuan, sharga, sstock;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_add);

        kode = findViewById(R.id.stock_kode_barang);
        nama = findViewById(R.id.stock_nama_barang);
        satuan = findViewById(R.id.stock_satuan);
        nsatuan = findViewById(R.id.stock_nilai_satuan);
        harga = findViewById(R.id.stock_harga);
        stock = findViewById(R.id.stock_stock);
        context = this;
        builder = new AlertDialog.Builder(context);
        databaseHandler = new DatabaseHandler(context);


        save = findViewById(R.id.stock_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInputValue();
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
                String msg = databaseHandler.addStockData(stockBarang);
                progressDialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("refresh", "true");
                setResult(RESULT_OK, intent);
                finish();
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

}