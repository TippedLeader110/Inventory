package com.itc.inventory.ui.stock;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itc.inventory.DatabaseHandler;
import com.itc.inventory.R;
import com.itc.inventory.ui.laporan.TransaksiBarang;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class StockListAdapter extends RecyclerView.Adapter<StockListAdapter.StockViewHolder> implements Filterable {

    ArrayList<StockBarang> stockBarangs;
    ArrayList<StockBarang> stockBarangsTemp;
    Context context;
    Locale myIndonesianLocale;
    NumberFormat money;
    DatabaseHandler databaseHandler;

    public StockListAdapter(Context context) {
        this.context = context;
        stockBarangs = new ArrayList<>();
        stockBarangsTemp = new ArrayList<>();
        myIndonesianLocale = new Locale("in", "ID");
        money = NumberFormat.getCurrencyInstance(myIndonesianLocale);
        databaseHandler = new DatabaseHandler(context);
    }

    public void setData(ArrayList<StockBarang> data){
        stockBarangs.addAll(data);
        stockBarangsTemp.addAll(data);
        notifyDataSetChanged();
    }

    public static String fmt(float d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_menu, parent, false);
        return new StockViewHolder(view);
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

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        int posisi = position;

        float stock = stockBarangs.get(position).getStock();
        float nilai_satuan = stockBarangs.get(position).getNilai_satuan();
        float harga = stockBarangs.get(position).getHarga();
        String satuan = stockBarangs.get(position).getSatuan();
        String kode = stockBarangs.get(posisi).getKode_barang();
        stock = hitungStock(stock, databaseHandler.getRecordTransaksi(kode, 1), databaseHandler.getRecordTransaksi(kode, 2));
        float totalStock = stock*nilai_satuan;

        String kiri = "Stock = " + fmt(stock) + "("+ fmt(totalStock) +" "+ satuan +")";
        String kanan = money.format(harga) + "/" + fmt(nilai_satuan) + " " + satuan;
        holder.setData(stockBarangs.get(posisi).getNama_barang() + " (" + stockBarangs.get(posisi).getKode_barang() + ")", kiri, kanan);

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, StockDetail.class);
//                intent.putExtra("kode_barang", stockBarangs.get(posisi).getKode_barang());
//                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return stockBarangs.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<StockBarang> result = new ArrayList<>();

            if(charSequence.toString().isEmpty()){
                result.addAll(stockBarangsTemp);
            }else{
                for(StockBarang br : stockBarangsTemp){
                    if(br.getNama_barang().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        result.add(br);
                    }else{
                        if(br.getKode_barang().contains(charSequence.toString())){
                            result.add(br);
                        }
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = result;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            stockBarangs.clear();
            stockBarangs.addAll((Collection<? extends StockBarang>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class StockViewHolder extends RecyclerView.ViewHolder{

        TextView nama_item, desk_kiri, desk_kanan;
        LinearLayout ll;

        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            ll = itemView.findViewById(R.id.ll);
            nama_item = itemView.findViewById(R.id.nama_item);
            desk_kanan = itemView.findViewById(R.id.deksripsi_kanan);
            desk_kiri = itemView.findViewById(R.id.deksripsi_kiri);
        }

        public void setData(String nama_item, String kiri, String kanan){
            this.nama_item.setText(nama_item);
            desk_kiri.setText(kiri);
            desk_kanan.setText(kanan);
        }
    }
}
