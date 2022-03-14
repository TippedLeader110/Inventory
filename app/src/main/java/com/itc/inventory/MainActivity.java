package com.itc.inventory;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.itc.inventory.databinding.ActivityMainBinding;
import com.itc.inventory.ui.stock.StockBarang;
import com.itc.inventory.ui.stock.StockFragment;
import com.itc.inventory.ui.stock.StockInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements StockFragment.Listener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    ArrayList<StockBarang> stockBarang;
    RetroClient retroClient;
    ProgressDialog pd;
    StockInterface stockInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retroClient = new RetroClient();
        stockInterface = retroClient.getClient().create(StockInterface.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);


//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_stock, R.id.nav_keluar, R.id.nav_masuk, R.id.nav_laporan)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public ArrayList<StockBarang> getStock(){
        return stockBarang;
    }

    public void fetchDataStocks(){
        Log.w("Fetch data", "true");
        pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("Loading");
        pd.setMessage("Mengambil Data.....");
        pd.setCancelable(false);
        pd.show();
//        stockListAdapter.setData(databaseHandler.getStock());

        Call<ArrayList<StockBarang>> getStocks = stockInterface.getStocks();

        getStocks.enqueue(new Callback<ArrayList<StockBarang>>() {
            @Override
            public void onResponse(Call<ArrayList<StockBarang>> call, Response<ArrayList<StockBarang>> response) {
//                stockListAdapter.setData(response.body());
                Log.w("Call : " , String.valueOf(response.body().size()));
                stockBarang.addAll(response.body());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}