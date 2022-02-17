package com.itc.inventory;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.itc.inventory.ui.laporan.TransaksiBarang;
import com.itc.inventory.ui.stock.StockBarang;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "saveapp";
    private static final String TABLE_STOCK = "stock";
    private static final String TABLE_TRANSAKSI = "transaksi";

    SQLiteDatabase db;

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String table_stock = " CREATE TABLE " + TABLE_STOCK + "( " +
                " kode_barang text, stock real ,nama_barang text, satuan text, nilai_satuan real, harga real )";
        Log.w("Bentuk Query tabel stock", table_stock);
        sqLiteDatabase.execSQL(table_stock);

        String table_transaksi = " CREATE TABLE " + TABLE_TRANSAKSI + "( " +
                " kode_barang text, nama_transaksi text, nama text , jumlah real , tipe_transaksi integer , tgl_transaksi text ,catatan text)";
        Log.w("Bentuk Query tabel stock", table_stock);
        sqLiteDatabase.execSQL(table_transaksi);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSAKSI);
        onCreate(db);
    }

    @SuppressLint("Range")
    public ArrayList<StockBarang> getStock(){
        ArrayList<StockBarang> stockBarangs = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_STOCK , null);
        int i = 0 ;
        while (mCursor.moveToNext()){
            StockBarang itemBarang = new StockBarang();
            itemBarang.setKode_barang(mCursor.getString(mCursor.getColumnIndex("kode_barang")));
            itemBarang.setNama_barang(mCursor.getString(mCursor.getColumnIndex("nama_barang")));
            itemBarang.setStock(mCursor.getFloat(mCursor.getColumnIndex("stock")));
            itemBarang.setHarga(mCursor.getFloat(mCursor.getColumnIndex("harga")));
            itemBarang.setNilai_satuan(mCursor.getFloat(mCursor.getColumnIndex("nilai_satuan")));
            itemBarang.setSatuan(mCursor.getString(mCursor.getColumnIndex("satuan")));
            stockBarangs.add(itemBarang);
        }
        mCursor.close();
        
        return stockBarangs;
    }

    public boolean stockDetail(){
        db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_STOCK, null);
        if (mCursor.moveToFirst())
        {
            Log.w("checkRow","Ada row");
            mCursor.close();
            return true;
        }
        else
        {
            Log.w("checkRow","Tidak Ada row");
            mCursor.close();
            return false;
        }
    }

    public String addTransaksi(TransaksiBarang transaksiBarang) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("kode_barang", transaksiBarang.getKode_barang());
        contentValues.put("nama", transaksiBarang.getNama());
        contentValues.put("tgl_transaksi", transaksiBarang.getTgl_transaksi());
        contentValues.put("jumlah", transaksiBarang.getJumlah());
        contentValues.put("tipe_transaksi", transaksiBarang.getTipe_transaksi());
        contentValues.put("catatan", transaksiBarang.getCatatan());

        db = this.getWritableDatabase();

        Long ret = db.insert(TABLE_TRANSAKSI, null, contentValues);

        if (ret!=-1){
            return "Data berhasil disimpan";
        }else{
            return "Terjadi kesalahan saat menyimpan data";
        }
    };

    public String addStockData(StockBarang stockBarang) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("kode_barang", stockBarang.getKode_barang());
        contentValues.put("nama_barang", stockBarang.getNama_barang());
        contentValues.put("stock", stockBarang.getStock());
        contentValues.put("harga", stockBarang.getHarga());
        contentValues.put("nilai_satuan", stockBarang.getNilai_satuan());
        contentValues.put("satuan", stockBarang.getSatuan());

        db = this.getWritableDatabase();

        if(!CheckDuplicateID(stockBarang.getKode_barang())){
            Long ret = db.insert(TABLE_STOCK, null, contentValues);
            
            if (ret!=-1){
                return "Data berhasil disimpan";
            }else{
                return "Terjadi kesalahan saat menyimpan data";
            }
        }else{
            return "Kode barang telah digunakan";
        }
    };

    private boolean CheckDuplicateID(String id) {

        db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_STOCK + " where kode_barang = '" + id + "'", null);
        if (mCursor.moveToFirst())
        {
            Log.w("checkRow","Ada row");
            mCursor.close();
            
            return true;
        }
        else
        {
            Log.w("checkRow","Tidak Ada row");
            mCursor.close();
            
            return false;
        }

    }

    @SuppressLint("Range")
    public ArrayList<TransaksiBarang> getTransaksi(int tipe) {

        ArrayList<TransaksiBarang> transaksi = new ArrayList<>();
        db = this.getReadableDatabase();

        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSAKSI + " where tipe_transaksi = " + tipe , null);
        int i = 0 ;
        while (mCursor.moveToNext()){
            TransaksiBarang newTransaksi = new TransaksiBarang();
            newTransaksi.setTgl_transaksi(mCursor.getString(mCursor.getColumnIndex("tgl_transaksi")));
            newTransaksi.setNama_transaksi(mCursor.getString(mCursor.getColumnIndex("nama_transaksi")));
            newTransaksi.setTipe_transaksi(tipe);
            newTransaksi.setCatatan(mCursor.getString(mCursor.getColumnIndex("catatan")));
            newTransaksi.setKode_barang(mCursor.getString(mCursor.getColumnIndex("kode_barang")));
            newTransaksi.setJumlah(mCursor.getFloat(mCursor.getColumnIndex("jumlah")));
            newTransaksi.setNama(mCursor.getString(mCursor.getColumnIndex("nama")));

            transaksi.add(newTransaksi);
        }
        mCursor.close();

        return transaksi;

    }

    @SuppressLint("Range")
    public ArrayList<TransaksiBarang> getTransaksibyDate(int tipe, String sstart, String ends) {

        ArrayList<TransaksiBarang> transaksi = new ArrayList<>();
        db = this.getReadableDatabase();
        Log.w("Query date", "select * from "+TABLE_TRANSAKSI+ " where tipe_transaksi = " + tipe + " " +
                "AND tgl_transaksi BETWEEN '"+ sstart +"' AND '"+ ends + "'");

//        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSAKSI + " where tipe_transaksi = " + tipe , null);
        Cursor mCursor = db.rawQuery( "select * from "+TABLE_TRANSAKSI+ " where tipe_transaksi = " + tipe + " " +
                "AND tgl_transaksi BETWEEN '"+ sstart +"' AND '"+ ends + "'", null );
        int i = 0 ;
        while (mCursor.moveToNext()){
            TransaksiBarang newTransaksi = new TransaksiBarang();
            newTransaksi.setTgl_transaksi(mCursor.getString(mCursor.getColumnIndex("tgl_transaksi")));
            newTransaksi.setNama_transaksi(mCursor.getString(mCursor.getColumnIndex("nama_transaksi")));
            newTransaksi.setTipe_transaksi(tipe);
            newTransaksi.setCatatan(mCursor.getString(mCursor.getColumnIndex("catatan")));
            newTransaksi.setKode_barang(mCursor.getString(mCursor.getColumnIndex("kode_barang")));
            newTransaksi.setJumlah(mCursor.getFloat(mCursor.getColumnIndex("jumlah")));
            newTransaksi.setNama(mCursor.getString(mCursor.getColumnIndex("nama")));

            transaksi.add(newTransaksi);
        }
        mCursor.close();

        return transaksi;

    }

    @SuppressLint("Range")
    public String getStockNama(String kode_barang) {
        db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_STOCK + " where kode_barang = '" + kode_barang + "'", null);
        String item = "";
        if (mCursor.moveToFirst())
        {
            Log.w("checkRow","Ada row");
            item = mCursor.getString(mCursor.getColumnIndex("nama_barang"));
            mCursor.close();
        }
        return item;
    }

    @SuppressLint("Range")
    public ArrayList<TransaksiBarang> getRecordTransaksi(String kode, int i) {
        ArrayList<TransaksiBarang> transaksiBarangs = new ArrayList<>();
        db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSAKSI + " where kode_barang = '" + kode + "' AND tipe_transaksi = " + i, null);
        String item = "";
        if (mCursor.moveToFirst())
        {
            TransaksiBarang transaksiBarang = new TransaksiBarang();
            Log.w("checkRow","Ada row");
            transaksiBarang.setJumlah(Float.valueOf(mCursor.getString(mCursor.getColumnIndex("jumlah"))));
            transaksiBarang.setTipe_transaksi(i);
            transaksiBarang.setTgl_transaksi(mCursor.getString(mCursor.getColumnIndex("tgl_transaksi")));
            transaksiBarang.setKode_barang(mCursor.getString(mCursor.getColumnIndex("kode_barang")));
            transaksiBarangs.add(transaksiBarang);
        }

        mCursor.close();
        return transaksiBarangs;
    }
}



