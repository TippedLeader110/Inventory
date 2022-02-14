package com.itc.inventory.ui.laporan;

public class TransaksiBarang {

    String kode_barang;
    String nama_transaksi;
    String nama;
    Float jumlah;
    int tipe_transaksi;
    String tgl_transaksi;
    String catatan;

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
