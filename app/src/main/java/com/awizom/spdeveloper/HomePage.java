package com.awizom.spdeveloper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.awizom.spdeveloper.Adapter.ClientListAdapter;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Helper.ProfileHelper;
import com.awizom.spdeveloper.Model.ClientDetailModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    String result = "";
    String empid = "";
    GridView gridView;
    List<ClientDetailModel> clientlist;
    ClientListAdapter adapterClientList;
    ProgressDialog progressDialog;
    SwipeRefreshLayout refresh;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        empid = String.valueOf(SharedPrefManager.getInstance(HomePage.this).getUser().getEmployeeID());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new GridImageAdapter(this));
        progressDialog = new ProgressDialog(this);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.scrollToPosition(0);
        recyclerView.smoothScrollToPosition(0);
        refresh=findViewById(R.id.swipeRefreshLayout);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(new InternetDialog(HomePage.this).getInternetStatus()){
                    getClientList();
                 //   Toast.makeText(HomePage.this, "INTERNET VALIDATION PASSED", Toast.LENGTH_SHORT).show();
                }
                refresh.setRefreshing(false);
            }
        });
        if(new InternetDialog(this).getInternetStatus()){
            getClientList();
           // Toast.makeText(this, "INTERNET VALIDATION PASSED", Toast.LENGTH_SHORT).show();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Please wait");
                progressDialog.show();
                Intent intent = new Intent(HomePage.this, AddClient.class);
                startActivity(intent);
                progressDialog.dismiss();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void checkInternet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network


            connected = true;
            getClientList();
            //    Toast.makeText(getApplicationContext(), "Internet is On", Toast.LENGTH_SHORT).show();
        } else {
            connected = false;

        }
    }
    private void getClientList() {

        String empid = String.valueOf(SharedPrefManager.getInstance(this).getUser().getEmployeeID());
        try {
            // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
            String result = new ClientHelper.GetClientList().execute(empid.toString()).get();
            if(result.isEmpty())
            {
                result = new ClientHelper.GetClientList().execute(empid.toString()).get();

            }
            else {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ClientDetailModel>>() {
                }.getType();
                clientlist = new Gson().fromJson(result, listType);
                adapterClientList = new ClientListAdapter(HomePage.this, clientlist);
                recyclerView.setAdapter(adapterClientList);
            }
            } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {


            AlertDialog.Builder alertbox = new AlertDialog.Builder(HomePage.this);
            alertbox.setCancelable(false);
            alertbox.setIcon(R.drawable.ic_lock_open_black_24dp);
            alertbox.setTitle("Are you sure, You want to logout?");
            alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // finish used for destroyed activity
                    try {
                        progressDialog.show();
                        SharedPrefManager.getInstance(HomePage.this).logout();
                        Intent intent = new Intent(HomePage.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }


                }
            });

            alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // Nothing will be happened when clicked on no button
                    // of Dialog
                }
            });
            alertbox.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lead) {
            progressDialog.show();
            Intent intent = new Intent(this, ClientHistory.class);
            startActivity(intent);

            progressDialog.dismiss();
            // Handle the camera action
        } else if (id == R.id.nav_follow) {
            progressDialog.show();
            Intent intent = new Intent(this, FollowUP.class);
            startActivity(intent);
            progressDialog.dismiss();

        } else if (id == R.id.nav_addlead) {
            progressDialog.setMessage("Please wait");
            progressDialog.show();
            Intent intent = new Intent(HomePage.this, AddClient.class);
            startActivity(intent);
            progressDialog.dismiss();
        } else if (id == R.id.nav_followhistory) {
            progressDialog.setMessage("Please wait");
            progressDialog.show();
            Intent intent = new Intent(HomePage.this, FollowUpHistory.class);
            startActivity(intent);
            progressDialog.dismiss();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
