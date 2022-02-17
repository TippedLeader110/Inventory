package com.itc.inventory.ui.laporan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.itc.inventory.DatabaseHandler;
import com.itc.inventory.R;
import com.itc.inventory.databinding.ActivityTransaksiAddBinding;
import com.itc.inventory.databinding.FragmentStockBinding;
import com.itc.inventory.ui.stock.StockBarang;

import java.util.ArrayList;
import java.util.Calendar;

public class AddTransaksi extends AppCompatActivity {

    Integer selected_Stock;
    FloatingActionButton save;
    AutoCompleteTextView dropdown_stock;
    ArrayList<StockBarang> stockBarangArrayList;
    ArrayList<String> dropdown_name;
    ArrayList<String> dropdown_kode;
    TextInputLayout daftar_stock, stock, nama, tgl, catatan;
    ProgressDialog pd;
    TextView title;
    DatabaseHandler databaseHandler;
    Integer tipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_add);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Tambah transaksi");
        actionBar.setDisplayHomeAsUpEnabled(true);

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
        save = findViewById(R.id.transaksi_add_fab);
        dropdown_stock = findViewById(R.id.transaksi_auto);

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
                TransaksiBarang transaksiBarang = new TransaksiBarang();
                transaksiBarang.setTgl_transaksi(tgl.getEditText().getText().toString());
                transaksiBarang.setTipe_transaksi(tipe);
                transaksiBarang.setNama(nama.getEditText().getText().toString());
                transaksiBarang.setCatatan(catatan.getEditText().getText().toString());
                transaksiBarang.setJumlah(Float.valueOf(stock.getEditText().getText().toString()));
                transaksiBarang.setKode_barang(dropdown_kode.get(selected_Stock));

                Toast.makeText(AddTransaksi.this, databaseHandler.addTransaksi(transaksiBarang), Toast.LENGTH_SHORT).show();;

                Intent intent = new Intent();
                intent.putExtra("refresh", "true");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        setDropDown();
    }

    void setDropDown(){
        pd.setTitle("Memuat daftar stock");
        pd.setMessage("Memuat........");
        pd.show();
        stockBarangArrayList = databaseHandler.getStock();
        for (StockBarang cur: stockBarangArrayList){
            dropdown_kode.add(cur.getKode_barang());
            dropdown_name.add((cur.getNama_barang()) + " - " + cur.getKode_barang());
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdownmenu, dropdown_name);
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
