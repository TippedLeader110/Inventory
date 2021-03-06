package com.itc.inventory.ui.stock;

import com.google.gson.annotations.SerializedName;

public class StockBarang {
    @SerializedName("kode_barang")
    String kode_barang;

    @SerializedName("nama_barang")
    String nama_barang;

    @SerializedName("nilai_satuan")
    float nilai_satuan;

    @SerializedName("satuan")
    String satuan;

    @SerializedName("harga")
    Float harga;

    @SerializedName("stock")
    Float stock;

    public String getKode_barang() {
        return kode_barang;
    }

    public void setKode_barang(String kode_barang) {
        this.kode_barang = kode_barang;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public Float getNilai_satuan() {
        return nilai_satuan;
    }

    public void setNilai_satuan(Float nilai_satuan) {
        this.nilai_satuan = nilai_satuan;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public Float getHarga() {
        return harga;
    }

    public void setHarga(Float harga) {
        this.harga = harga;
    }

    public Float getStock() {
        return stock;
    }

    public void setStock(Float stock) {
        this.stock = stock;
    }
}
