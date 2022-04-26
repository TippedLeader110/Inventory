package com.itc.inventory.ui.laporan;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import com.itc.inventory.RetroClient;
import com.itc.inventory.ui.stock.StockBarang;
import com.itc.inventory.ui.stock.StockInterface;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.apache.commons.io.FileUtils;


public class DateFilter extends AppCompatActivity {

    DatabaseHandler databaseHandler;
    Integer mode;
    TextInputLayout start, end;
    Button next;
    RetroClient retroClient;
    TransaksiInterface transaksiInterface;
    StockInterface stockInterface;
    ArrayList<TransaksiBarang> listTransaksi;
    ArrayList<StockBarang> listStok;
    String edate, sdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_filter);
        retroClient = new RetroClient();
        transaksiInterface = retroClient.getClient().create(TransaksiInterface.class);
        stockInterface = retroClient.getClient().create(StockInterface.class);
        listTransaksi = new ArrayList<>();
        listStok = new ArrayList<>();
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
                sdate = start.getEditText().getText().toString();
                edate = end.getEditText().getText().toString();


                Call<ArrayList<TransaksiBarang>> getTransaksi = transaksiInterface.getTransaksiAll();
                getTransaksi.enqueue(new Callback<ArrayList<TransaksiBarang>>() {
                    @Override
                    public void onResponse(Call<ArrayList<TransaksiBarang>> call, Response<ArrayList<TransaksiBarang>> response) {
//                            listTransaksi.addAll(response.body());
                        databaseHandler.truncate();
                        for(TransaksiBarang val : response.body()){
                            databaseHandler.addTransaksi(val);
                        }
                        Call<ArrayList<StockBarang>> getStock = stockInterface.getStocks();
                        getStock.enqueue(new Callback<ArrayList<StockBarang>>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onResponse(Call<ArrayList<StockBarang>> call, Response<ArrayList<StockBarang>> response) {
                                listStok.addAll(response.body());

                                for(StockBarang val : response.body()){
                                    databaseHandler.addStockData(val);
                                }

                                listTransaksi.addAll((databaseHandler.getTransaksibyDate(mode-1, sdate, edate)));
                                exportData();

//                                if(mode==2 || mode==3){
//                                    listTransaksi.addAll((databaseHandler.getTransaksibyDate(mode-1, sdate, edate)));
//                                    exportData();
//                                }else{
//
//                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<StockBarang>> call, Throwable t) {
                                Log.w("Stock Export", t);
                            }
                        });

                    }

                    @Override
                    public void onFailure(Call<ArrayList<TransaksiBarang>> call, Throwable t) {
                        Log.w("Transaksi Filter export", t);
                    }
                });


//                exportData();
//                Toast.makeText(DateFilter.this, "mode : " + mode, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private static WritableCellFormat getCellFormat(WritableCellFormat format, Alignment alignment) {
        try {
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            format.setAlignment(alignment);
            format.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
            format.setBorder(Border.LEFT, BorderLineStyle.THIN);
            format.setBorder(Border.RIGHT, BorderLineStyle.THIN);
            format.setBorder(Border.TOP, BorderLineStyle.THIN);
        } catch (WriteException e) {
            return format;
        }
        return format;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void exportData() {
        NumberFormat money;
        Locale myIndonesianLocale = new Locale("in", "ID");
        money = NumberFormat.getCurrencyInstance(myIndonesianLocale);

        String titleFile = "";


        if(mode==1){
            titleFile = "Stock";
        }else if(mode==2){
            titleFile = "Transaksi Masuk";
        }else{
            titleFile = "Transaksi Keluar";
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
            cellFormat.setBorder(Border.BOTTOM, BorderLineStyle.DOUBLE);
            cellFormat.setBorder(Border.LEFT, BorderLineStyle.DOUBLE);
            cellFormat.setBorder(Border.RIGHT, BorderLineStyle.DOUBLE);
            cellFormat.setBorder(Border.TOP, BorderLineStyle.DOUBLE);

            WritableCellFormat format = new WritableCellFormat();
            format.setAlignment(Alignment.CENTRE);
            format.setVerticalAlignment(VerticalAlignment.CENTRE);
            format.setBorder(Border.TOP, BorderLineStyle.DOUBLE);
            format.setBorder(Border.BOTTOM, BorderLineStyle.DOUBLE);
            format.setBorder(Border.LEFT, BorderLineStyle.DOUBLE);
            format.setBorder(Border.RIGHT, BorderLineStyle.DOUBLE);

            InputStream raw = this.getAssets().open("logo.png");
//            Reader is = new BufferedReader(new InputStreamReader(raw, "UTF8"));
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_add_24);
//            AssetManager am = getAssets();
//            InputStream inputStream = am.open("Indroduction.pdf");
//            File f = new File("templogo.png");
//            File logo = new File("/path/to/logo.png");


//            URL url = new URL("http://192.168.208.134:3000/logo.png");
//            File logo = new File("file:///android_asset/logo.png");
            File logo = File.createTempFile( "logo", ".png" );
            FileUtils.copyToFile( raw, logo );
            sheetA.mergeCells(0, 0, 1, 3);

            sheetA.addCell(new Label(0,0,"", format));
            WritableImage im = new WritableImage(0, 0, 2, 4, logo);
            sheetA.addImage(im);

            if(mode==1){
                sheetA.mergeCells(2, 0, 4, 1);
                sheetA.mergeCells(2, 2, 4, 3);
                sheetA.addCell(new Label(2, 0, "Daftar Stok Barang", format));
                sheetA.addCell(new Label(2, 2, "MEDAN SUGAR INDUSTRY", format));
                sheetA.addCell(new Label(0, 5, "Kode Barang", cellFormat));
                sheetA.addCell(new Label(1, 5, "Nama Barang", cellFormat));
                sheetA.addCell(new Label(2, 5, "Satuan", cellFormat));
                sheetA.addCell(new Label(3, 5, "Harga", cellFormat));
                sheetA.addCell(new Label(4, 5, "Stok", cellFormat));
//                WriteableIm
//                sheetA.addImage(bitmap);
                int i = 6;
                for(StockBarang br : listStok){
                    Float stock = hitungStock(br.getStock(), databaseHandler.getRecordTransaksi(br.getKode_barang(), 1), databaseHandler.getRecordTransaksi(br.getKode_barang(), 2));
                    sheetA.addCell(new Label(0, i, br.getKode_barang(), cellFormat));
                    sheetA.addCell(new Label(1, i, br.getNama_barang(), cellFormat));
                    sheetA.addCell(new Label(2, i, br.getNilai_satuan() + "/" + br.getSatuan(), cellFormat));
                    sheetA.addCell(new Label(3, i, money.format(br.getHarga()), cellFormat));
                    sheetA.addCell(new Label(4, i, String.valueOf(br.getStock()), cellFormat));
                    i++;
                }
                i +=4;
                sheetA.addCell(new Label(3, i, "PT Medan Sugar Industry"));
                sheetA.addCell(new Label(3, i+1, "Industry Control"));
                sheetA.addCell(new Label(3, i+6, "Syahran Aran"));
            }

            if(mode==2 || mode==3){
                sheetA.mergeCells(2, 0, 5, 1);
                sheetA.mergeCells(2, 2, 5, 3);
                ArrayList<StockBarang> totalStock = new ArrayList();
                totalStock.addAll(databaseHandler.getStock());
                if(mode==2){
                    sheetA.addCell(new Label(2, 0, "Daftar Barang Masuk", format));
                }else{
                    sheetA.addCell(new Label(2, 0, "Daftar Barang Keluar", format));
                }
                sheetA.addCell(new Label(2, 2, "MEDAN SUGAR INDUSTRY", format));
                sheetA.addCell(new Label(0, 5, "Tanggal", cellFormat));
                sheetA.addCell(new Label(1, 5, "Kode Barang", cellFormat));
                sheetA.addCell(new Label(2, 5, "Nama Barang", cellFormat));
                sheetA.addCell(new Label(3, 5, "Nama yang bertanggung jawab", cellFormat));
                sheetA.addCell(new Label(4, 5, "Jumlah Transaksi", cellFormat));
                sheetA.addCell(new Label(5, 5, "Catatan", cellFormat));
                int i = 6;
                for (TransaksiBarang br : listTransaksi){
//                    databaseHandler.getRecordTransaksi(br.getKode_barang(), mode);
                    String total = "";

                    sheetA.addCell(new Label(0, i, br.getTgl_transaksi(), cellFormat));
                    sheetA.addCell(new Label(1, i, br.getKode_barang(), cellFormat));
                    sheetA.addCell(new Label(2, i, databaseHandler.getStockNama(br.getKode_barang()), cellFormat));
                    sheetA.addCell(new Label(3, i, br.getNama(), cellFormat));
                    sheetA.addCell(new Label(4, i, String.valueOf(br.getJumlah()), cellFormat));
                    sheetA.addCell(new Label(5, i, String.valueOf(br.getCatatan()), cellFormat));
                    i++;
                }

                i +=4;
                sheetA.addCell(new Label(5, i, "PT Medan Sugar Industry"));
                sheetA.addCell(new Label(5, i+1, "Industry Control"));
                sheetA.addCell(new Label(5, i+6, "Syahran Aran"));

            }
            for (int c=0; c<100; c++){
                CellView cell = sheetA.getColumnView(c);
                cell.setAutosize(true);
                sheetA.setColumnView(c, cell);
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