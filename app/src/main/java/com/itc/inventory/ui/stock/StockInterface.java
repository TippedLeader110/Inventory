package com.itc.inventory.ui.stock;

import com.itc.inventory.ResponseData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface StockInterface {

    @FormUrlEncoded
    @POST("hapus_stock")
    Call<ResponseData> hapusStock(@Field("kode_barang") String kode_barang);

    @FormUrlEncoded
    @POST("edit_stock")
    Call<ResponseData> editStock(@Field("oldid") String oldid,
                                @Field("kode_barang") String kode_barang,
                                @Field("nama_barang") String nama_barang,
                                @Field("stock") Float stock,
                                @Field("satuan") String satuan,
                                @Field("nilai_satuan") Float nilai_Satuan,
                                @Field("harga") Float harga);

    @FormUrlEncoded
    @POST("add_stock")
    Call<ResponseData> addStock(@Field("kode_barang") String kode_barang,
                                @Field("nama_barang") String nama_barang,
                                @Field("stock") Float stock,
                                @Field("satuan") String satuan,
                                @Field("nilai_satuan") Float nilai_Satuan,
                                @Field("harga") Float harga);

    @FormUrlEncoded
    @POST("inv_stock_id")
    Call<StockBarang> getStockInfo(@Field("mode") Integer mode,
                                   @Field("kode_barang") String kode_barang);

    @GET("inv_stock")
    Call<ArrayList<StockBarang>> getStocks();

    @GET("inv_stock_hitung")
    Call<ArrayList<StockBarang>> getStocksHitung();
}
