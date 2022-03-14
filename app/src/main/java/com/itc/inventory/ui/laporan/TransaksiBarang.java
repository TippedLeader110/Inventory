package com.itc.inventory.ui.laporan;

import com.google.gson.annotations.SerializedName;

public class TransaksiBarang {

    @SerializedName("id_transaksi")
    Integer id_transaksi;

    @SerializedName("kode_barang")
    String kode_barang;

    @SerializedName("nama_transaksi")
    String nama_transaksi;

    @SerializedName("nama")
    String nama;

    @SerializedName("jumlah")
    Float jumlah;

    @SerializedName("tipe_transaksi")
    int tipe_transaksi;

    @SerializedName("tgl_transaksi")
    String tgl_transaksi;

    @SerializedName("catatan")
    String catatan;

    public Integer getId_transaksi() {
        return id_transaksi;
    }

    public void setId_transaksi(Integer id_transaksi) {
        this.id_transaksi = id_transaksi;
    }

    public String getKode_barang() {
        return kode_barang;
    }

    public void setKode_barang(String kode_barang) {
        this.kode_barang = kode_barang;
    }

    public String getNama_transaksi() {
        return nama_transaksi;
    }

    public void setNama_transaksi(String nama_transaksi) {
        this.nama_transaksi = nama_transaksi;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Float getJumlah() {
        return jumlah;
    }

    public void setJumlah(Float jumlah) {
        this.jumlah = jumlah;
    }

    public int getTipe_transaksi() {
        return tipe_transaksi;
    }

    public void setTipe_transaksi(int tipe_transaksi) {
        this.tipe_transaksi = tipe_transaksi;
    }

    public String getTgl_transaksi() {
        return tgl_transaksi;
    }

    public void setTgl_transaksi(String tgl_transaksi) {
        this.tgl_transaksi = tgl_transaksi;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
}
