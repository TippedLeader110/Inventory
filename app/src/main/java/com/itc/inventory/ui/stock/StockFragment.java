package com.itc.inventory.ui.stock;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.itc.inventory.DatabaseHandler;
import com.itc.inventory.MainActivity;
import com.itc.inventory.R;
import com.itc.inventory.RetroClient;
import com.itc.inventory.databinding.FragmentStockBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockFragment extends Fragment implements StockListAdapter.sendBack{

    private FragmentStockBinding binding;
    RecyclerView recyclerView;
    StockListAdapter stockListAdapter;
    FloatingActionButton floatingActionButton;
    ProgressDialog pd;
    RetroClient retroClient;
    StockInterface stockInterface;
    DatabaseHandler databaseHandler;
    MainActivity mainActivity;

    public StockFragment() {
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setHasOptionsMenu(true);

        databaseHandler = new DatabaseHandler(getActivity());

        recyclerView = binding.rcStock;
        floatingActionButton = binding.stockFab;
        stockListAdapter = new StockListAdapter(getActivity(), this);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        stockListAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(stockListAdapter);

        retroClient = new RetroClient();
        stockInterface = retroClient.getClient().create(StockInterface.class);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StockAdd.class);
                intent.putExtra("view", "0");
                activityResultLauncher.launch(intent);
//                startActivity(intent);
            }
        });

        fetchData();
//        mainActivity.fetchDataStocks();
//        mainActivity.getStock();
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        mainActivity = (MainActivity) context;
        super.onAttach(context);
    }

    public void fetchData(){
        Log.w("Fetch data", "true");
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Loading");
        pd.setMessage("Mengambil Data.....");
        pd.setCancelable(false);
        pd.show();
//        stockListAdapter.setData(databaseHandler.getStock());

        Call<ArrayList<StockBarang>> getStocks = stockInterface.getStocksHitung();

        getStocks.enqueue(new Callback<ArrayList<StockBarang>>() {
            @Override
            public void onResponse(Call<ArrayList<StockBarang>> call, Response<ArrayList<StockBarang>> response) {
                stockListAdapter.setData(response.body());
                Log.w("Call : " , String.valueOf(response.body().size()));
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<ArrayList<StockBarang>> call, Throwable t) {
                Log.w("requestFailed", "requestFailed", t);
                pd.dismiss();
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.search_view, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                stockListAdapter.getFilter().filter(s);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        fetchData();
                    }
                }
            }
    );

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void getData(String ss) {
        Toast.makeText(getContext(), "Selected : " + ss, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), StockAdd.class);
        intent.putExtra("id", ss);
        intent.putExtra("view", "1");
        activityResultLauncher.launch(intent);
    }

    public interface Listener{
        public ArrayList<StockBarang> getStock();
        public void fetchDataStocks();
    }
}