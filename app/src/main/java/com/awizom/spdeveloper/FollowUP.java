package com.awizom.spdeveloper;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.awizom.spdeveloper.Adapter.ClientFollowListAdapter;
import com.awizom.spdeveloper.Adapter.ClientListAdapter;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Model.ClientDetailModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class FollowUP extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ClientDetailModel> clientlist;
    ClientFollowListAdapter adapterClientFollowList;
    EditText client, location;
    br.com.simplepass.loading_button_lib.customViews.CircularProgressButton filterby;
    String lct = "null";
    String clt = "null";
    String mob = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_up);
        if(new InternetDialog(FollowUP.this).getInternetStatus()){
            InitView();
        }

    }

    private void InitView() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Follow Up");
        toolbar.setBackgroundColor(Color.parseColor("#488586"));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statusbar_color));
        client = findViewById(R.id.client);
        location = findViewById(R.id.location);
        filterby = findViewById(R.id.filterButton);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.scrollToPosition(0);
        recyclerView.smoothScrollToPosition(0);
        getClientList();
        filterby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClientFilterList();
            }
        });
    }

    private void getClientFilterList() {
        String empid = String.valueOf(SharedPrefManager.getInstance(this).getUser().getEmployeeID());
        if (!location.getText().toString().isEmpty()) {
            lct = location.getText().toString();
        }
        if (!client.getText().toString().isEmpty()) {
            clt = client.getText().toString();
        }

        try {
            // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
            String result = new ClientHelper.GetClientFilterList().execute(empid.toString(), "null", lct.toString(), clt.toString(), "null").get();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ClientDetailModel>>() {
            }.getType();
            clientlist = new Gson().fromJson(result, listType);
            adapterClientFollowList = new ClientFollowListAdapter(FollowUP.this, clientlist);
            recyclerView.setAdapter(adapterClientFollowList);
            lct = "null";
            clt = "null";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getClientList() {

        String empid = String.valueOf(SharedPrefManager.getInstance(this).getUser().getEmployeeID());
        try {
            // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
            String result = new ClientHelper.GetClientFollowList().execute(empid.toString()).get();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ClientDetailModel>>() {
            }.getType();
            clientlist = new Gson().fromJson(result, listType);
            adapterClientFollowList = new ClientFollowListAdapter(FollowUP.this, clientlist);
            recyclerView.setAdapter(adapterClientFollowList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
