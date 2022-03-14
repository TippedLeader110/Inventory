package com.itc.inventory.ui.laporan;

import com.itc.inventory.ResponseData;
import com.itc.inventory.ui.stock.StockBarang;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TransaksiInterface {

    @FormUrlEncoded
    @POST("inv_transaksi")
    Call<ArrayList<TransaksiBarang>> getTransaksi(@Field("tipe_transaksi") Integer tipe_transaksi);

    @FormUrlEncoded
    @POST("inv_transaksi_filter")
    Call<ArrayList<TransaksiBarang>> getTransaksiFilter(@Field("tipe_transaksi") Integer tipe_transaksi, @Field("start") String start,
                                                        @Field("end") String end);

    @FormUrlEncoded
    @POST("inv_transaksi_detil")
    Call<TransaksiBarang> getTransaksiDetil(@Field("id_transaksi") Integer id_transaksi);

    @FormUrlEncoded
    @POST("hapus_transaksi")
    Call<ResponseData> hapusTransaksi(@Field("id_transaksi") Integer id_transaksi);

    @FormUrlEncoded
    @POST("add_transaksi")
    Call<ResponseData> tambahTransaksi(@Field("kode_barang") String kode_barang,
                                       @Field("jumlah") Float jumlah,
                                       @Field("catatan") String catatan,
                                       @Field("nama") String nama,
                                       @Field("tgl_transaksi") String tgl_transaksi,
                                       @Field("tipe_transaksi") Integer tipe_transaksi);

    @FormUrlEncoded
    @POST("edit_transaksi")
    Call<ResponseData> editTransaksi(@Field("id_transaksi") Integer id_transaksi,
                                     @Field("jumlah") Float jumlah,
                                     @Field("catatan") String catatan,
                                     @Field("nama") String nama,
                                     @Field("tgl_transaksi") String tgl_transaksi,
                                     @Field("tipe_transaksi") Integer tipe_transaksi);

    @GET("inv_stock")
    Call<ArrayList<StockBarang>> getStockID();

    @GET("inv_transaksiall")
    Call<ArrayList<TransaksiBarang>> getTransaksiAll();
}
