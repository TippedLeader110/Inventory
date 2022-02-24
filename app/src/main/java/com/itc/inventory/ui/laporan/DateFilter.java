package com.itc.inventory.ui.laporan;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.itc.inventory.DatabaseHandler;
import com.itc.inventory.R;
import com.itc.inventory.ui.stock.StockBarang;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class DateFilter extends AppCompatActivity {

    DatabaseHandler databaseHandler;
    Integer mode;
    TextInputLayout start, end;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_filter);

        databaseHandler = new DatabaseHandler(this);
        mode = Integer.valueOf(getIntent().getStringExtra("mode"));

        next = findViewById(R.id.df_save);
        start = findViewById(R.id.df_start);
        end = findViewById(R.id.df_end);

        start.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialogDate =  new DatePickerDialog(
                        DateFilter.this,
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
                                start.getEditText().setText(tglnew);
                            }
                        },
                        year, month, day
                );
                dialogDate.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialogDate.show();
            }
        });

        end.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialogDate =  new DatePickerDialog(
                        DateFilter.this,
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
                                end.getEditText().setText(tglnew);
                            }
                        },
                        year, month, day
                );
                dialogDate.getWindow().setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
                dialogDate.show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportData();
//                Toast.makeText(DateFilter.this, "mode : " + mode, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void exportData() {
        NumberFormat money;
        Locale myIndonesianLocale = new Locale("in", "ID");
        money = NumberFormat.getCurrencyInstance(myIndonesianLocale);

        String titleFile = "";
        String sdate = start.getEditText().getText().toString();
        String edate = end.getEditText().getText().toString();

        if(mode==1){
            titleFile = "Stock";
        }else if(mode==2){
            titleFile = "Transaksi Masuk";
        }else{
            titleFile = "Transaksi Keluar";
        }
        ArrayList<TransaksiBarang> listTransaksi = new ArrayList<>();
        ArrayList<StockBarang> listStok = new ArrayList<>();

        if(mode==2 || mode==3){
            listTransaksi.addAll((databaseHandler.getTransaksibyDate(mode-1, sdate, edate)));
        }else{
            listStok.addAll(databaseHandler.getStock());
        }


        File sd = Environment.getExternalStorageDirectory();
        String exportFile = titleFile + "-"+ sdate +"-"+ edate +".xls";
        File directory = new File(sd.getAbsolutePath());
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }
        //file path
        File file = new File(directory, exportFile);
        try {
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale(Locale.US.getLanguage(), Locale.US.getCountry()));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);


            WritableSheet sheetA = workbook.createSheet(titleFile, 0);
            int basePosition = 0;

            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

            if(mode==1){
                sheetA.addCell(new Label(0, 0, "Kode Barang"));
                sheetA.addCell(new Label(1, 0, "Nama Barang"));
                sheetA.addCell(new Label(2, 0, "Satuan"));
                sheetA.addCell(new Label(3, 0, "Harga"));
                sheetA.addCell(new Label(4, 0, "Stok"));
                int i = 1;
                for(StockBarang br : listStok){
                    Float stock = hitungStock(br.getStock(), databaseHandler.getRecordTransaksi(br.getKode_barang(), 1), databaseHandler.getRecordTransaksi(br.getKode_barang(), 2));
                    sheetA.addCell(new Label(0, i, br.getKode_barang()));
                    sheetA.addCell(new Label(1, i, br.getNama_barang()));
                    sheetA.addCell(new Label(2, i, br.getNilai_satuan() + "/" + br.getSatuan()));
                    sheetA.addCell(new Label(3, i, money.format(br.getHarga())));
                    sheetA.addCell(new Label(4, i, String.valueOf(stock)));
                    i++;
                }
            }

            if(mode==2 || mode==3){
                ArrayList<StockBarang> totalStock = new ArrayList();
                totalStock.addAll(databaseHandler.getStock());

                sheetA.addCell(new Label(0, 0, "Tanggal"));
                sheetA.addCell(new Label(1, 0, "Kode Barang"));
                sheetA.addCell(new Label(2, 0, "Nama Barang"));
                sheetA.addCell(new Label(3, 0, "Nama yang bertanggung jawab"));
                sheetA.addCell(new Label(4, 0, "Jumlah Transaksi"));
                sheetA.addCell(new Label(5, 0, "Catatan"));
                int i = 1;
                for (TransaksiBarang br : listTransaksi){
//                    databaseHandler.getRecordTransaksi(br.getKode_barang(), mode);
                    String total = "";

                    sheetA.addCell(new Label(0, i, br.getTgl_transaksi()));
                    sheetA.addCell(new Label(1, i, br.getKode_barang()));
                    sheetA.addCell(new Label(2, i, databaseHandler.getStockNama(br.getKode_barang())));
                    sheetA.addCell(new Label(3, i, br.getNama()));
                    sheetA.addCell(new Label(4, i, String.valueOf(br.getJumlah())));
                    sheetA.addCell(new Label(5, i, String.valueOf(br.getCatatan())));
                    i++;
                }

                for (int c=0; c<100; c++){
                    CellView cell = sheetA.getColumnView(c);
                    cell.setAutosize(true);
                    sheetA.setColumnView(c, cell);
                }
            }
            workbook.write();
            workbook.close();
            Toast.makeText(DateFilter.this, "File berhasil di buat", Toast.LENGTH_SHORT).show();

        }catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public float hitungStock(Float stock, ArrayList<TransaksiBarang> masuk, ArrayList<TransaksiBarang> keluar){
        double sstock = stock;
        double total_masuk = 0.0;
        double total_keluar = 0.0;
        if(masuk.size()>0){
            for (TransaksiBarang ms : masuk){
                total_masuk = total_masuk + ms.getJumlah();
            }
        }

        if (keluar.size()>0){
            for(TransaksiBarang ks : keluar){
                total_keluar = total_keluar + ks.getJumlah();
            }
        }
        Log.w("Stock", sstock + " => -" + total_keluar + " & +" + total_masuk);
        sstock = sstock-total_keluar;
        Log.w("Stock keluar", String.valueOf(sstock));
        sstock = sstock+total_masuk;
        Log.w("Stock masuk", String.valueOf(sstock));
        return (float) sstock;
    }


    private ArrayList<TransaksiBarang> getNamaBarang(ArrayList<TransaksiBarang> listTransaksi) {
//        ArrayList<TransaksiBarang> newList = new ArrayList<>();
        for(TransaksiBarang br : listTransaksi){
            br.setNama_transaksi(databaseHandler.getStockNama(br.getKode_barang()));
        }
        return listTransaksi;
    }
}