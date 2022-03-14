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
                "id_transaksi INTEGER PRIMARY KEY ,  kode_barang text, nama_transaksi text, nama text , jumlah real , tipe_transaksi integer , tgl_transaksi text ,catatan text)";
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

    public String editTransaksi(TransaksiBarang transaksiBarang, int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("kode_barang", transaksiBarang.getKode_barang());
        contentValues.put("nama", transaksiBarang.getNama());
        contentValues.put("tgl_transaksi", transaksiBarang.getTgl_transaksi());
        contentValues.put("jumlah", transaksiBarang.getJumlah());
        contentValues.put("catatan", transaksiBarang.getCatatan());

        db = this.getWritableDatabase();

        int ret = db.update(TABLE_TRANSAKSI, contentValues, "id_transaksi = ?", new String[]{String.valueOf(id)});

        if (ret!=-1){
            return "Data berhasil disimpan";
        }else{
            return "Terjadi kesalahan saat menyimpan data";
        }
    };

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
            newTransaksi.setId_transaksi(mCursor.getInt(mCursor.getColumnIndex("id_transaksi")));
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

    public void truncate(){
        db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_STOCK);
        db.execSQL("delete from " + TABLE_TRANSAKSI);
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
            newTransaksi.setId_transaksi(mCursor.getInt(mCursor.getColumnIndex("id_transaksi")));
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
    public StockBarang getStockDetail(String kode_barang) {
        db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_STOCK + " where kode_barang = '" + kode_barang + "'", null);
        StockBarang stockBarang = new StockBarang();
        if (mCursor.moveToFirst())
        {
            Log.w("checkRow","Ada row");
            stockBarang.setNama_barang(mCursor.getString(mCursor.getColumnIndex("nama_barang")));
            stockBarang.setKode_barang(mCursor.getString(mCursor.getColumnIndex("kode_barang")));
            stockBarang.setStock(mCursor.getFloat(mCursor.getColumnIndex("stock")));
            stockBarang.setHarga(mCursor.getFloat(mCursor.getColumnIndex("harga")));
            stockBarang.setNilai_satuan(mCursor.getFloat(mCursor.getColumnIndex("nilai_satuan")));
            stockBarang.setSatuan(mCursor.getString(mCursor.getColumnIndex("satuan")));
            mCursor.close();
        }
        return stockBarang;
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

    public String editStockData(String oldid, StockBarang stockBarang) {
        ContentValues contentValues = new ContentValues();
        ContentValues contentValuesTransaksi = new ContentValues();
        contentValues.put("kode_barang", stockBarang.getKode_barang());
        contentValuesTransaksi.put("kode_barang", stockBarang.getKode_barang());
        contentValues.put("nama_barang", stockBarang.getNama_barang());
        contentValues.put("stock", stockBarang.getStock());
        contentValues.put("harga", stockBarang.getHarga());
        contentValues.put("nilai_satuan", stockBarang.getNilai_satuan());
        contentValues.put("satuan", stockBarang.getSatuan());

        db = this.getWritableDatabase();

        Integer ret, ret2;
        ret = db.update(TABLE_STOCK, contentValues, "kode_barang = ?" , new String[]{oldid});
        ret2 = db.update(TABLE_TRANSAKSI, contentValuesTransaksi, "kode_barang = ?" , new String[]{oldid});

        if (ret!=-1 && ret2!=-1){
            return "Data berhasil disimpan";
        }else{
            return "Terjadi kesalahan saat menyimpan data";
        }
    }

    public boolean checkRow(){
        db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_STOCK, null);
        if (mCursor.moveToFirst())
        {
            Log.w("checkRow","Ada row");
            mCursor.close();
            db.close();
            return true;
        }
        else
        {
            Log.w("checkRow","Tidak Ada row");
            mCursor.close();
            db.close();
            return false;
        }
    }

    public boolean deleteStock(String id) {
//        db = this.getWritableDatabase();
        String KEY_NAME = "kode_barang";
        if(db.delete(TABLE_STOCK, "kode_barang = '" + id + "'", null) > 0 )
        {
            db.delete(TABLE_TRANSAKSI, " kode_barang = '" + id + "'", null);
            return true;
        }else{
            return false;
        }
    };

    @SuppressLint("Range")
    public TransaksiBarang getTransaksiDetail(int id) {

        db = this.getReadableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + TABLE_TRANSAKSI + " where id_transaksi = '" + id + "'", null);
        TransaksiBarang transaksiBarang = new TransaksiBarang();
        if (mCursor.moveToFirst())
        {
            Log.w("checkRow","Ada row");
            transaksiBarang.setKode_barang(mCursor.getString(mCursor.getColumnIndex("kode_barang")));
            transaksiBarang.setId_transaksi(mCursor.getInt(mCursor.getColumnIndex("id_transaksi")));
            transaksiBarang.setNama(mCursor.getString(mCursor.getColumnIndex("nama")));
            transaksiBarang.setTipe_transaksi(mCursor.getInt(mCursor.getColumnIndex("tipe_transaksi")));
            transaksiBarang.setCatatan(mCursor.getString(mCursor.getColumnIndex("catatan")));
            transaksiBarang.setTgl_transaksi(mCursor.getString(mCursor.getColumnIndex("tgl_transaksi")));
            transaksiBarang.setJumlah(mCursor.getFloat(mCursor.getColumnIndex("jumlah")));
            mCursor.close();
        }
        return transaksiBarang;

    }

    public boolean deleteTransaksi(Integer id_intent) {

        if(db.delete(TABLE_TRANSAKSI, "id_transaksi = '" + id_intent + "'", null) > 0 )
        {
            return true;
        }else{
            return false;
        }

    }
}



