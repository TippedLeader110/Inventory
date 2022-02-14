package com.itc.inventory.ui.masuk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itc.inventory.R;
import com.itc.inventory.ui.laporan.TransaksiBarang;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class MasukListAdapter extends RecyclerView.Adapter<MasukListAdapter.MasukViewHolder> implements Filterable {

    ArrayList<TransaksiBarang> transaksiBarangs;
    ArrayList<TransaksiBarang> transkasiTemp;
    Context context;
    Locale myIndonesianLocale;
    NumberFormat money;


    public MasukListAdapter(Context context) {
        this.context = context;
        transaksiBarangs = new ArrayList<>();
        transkasiTemp = new ArrayList<>();
        myIndonesianLocale = new Locale("in", "ID");
        money = NumberFormat.getCurrencyInstance(myIndonesianLocale);
    }

    public void setData(ArrayList<TransaksiBarang> data){
        transaksiBarangs.addAll(data);
        transkasiTemp.addAll(data);
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
    public MasukViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_menu, parent, false);
        return new MasukViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MasukViewHolder holder, int position) {
        int posisi = position;

        String kiri = "Tanggal : " + transaksiBarangs.get(posisi).getTgl_transaksi();
        String kanan = "Jumlah : " + fmt(transaksiBarangs.get(posisi).getJumlah());
        holder.setData(transaksiBarangs.get(posisi).getNama_transaksi() + " (" + transaksiBarangs.get(posisi).getNama() + ")", kiri, kanan);

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, MasukDetail.class);
//                intent.putExtra("kode_barang", MasukBarangs.get(posisi).getKode_barang());
//                context.startActivity(intent);
            }
        });

    }

    public class MasukViewHolder extends RecyclerView.ViewHolder{

        TextView nama_item, desk_kiri, desk_kanan;
        LinearLayout ll;

        public MasukViewHolder(@NonNull View itemView) {
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

    @Override
    public int getItemCount() {
        return transaksiBarangs.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<TransaksiBarang> result = new ArrayList<>();

            if(charSequence.toString().isEmpty()){
                result.addAll(transkasiTemp);
            }else{
                for (TransaksiBarang br : transaksiBarangs) {
                    if (br.getNama_transaksi().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        result.add(br);
                    } else {
                        if (br.getKode_barang().contains(charSequence.toString())) {
                            result.add(br);
                        } else {
                            if (br.getNama().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                result.add(br);
                            }
                        }
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = result;
            return filterResults;
        }
        @Override
        protected void publishResults (CharSequence charSequence, FilterResults filterResults){
            transaksiBarangs.clear();
            transaksiBarangs.addAll((Collection<? extends TransaksiBarang>) filterResults.values);
            notifyDataSetChanged();
        }
    };
}
