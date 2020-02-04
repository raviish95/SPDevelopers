package com.awizom.spdeveloper;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.awizom.spdeveloper.Adapter.ClientListAdapter;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Model.ClientDetailModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ClientHistory extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ClientDetailModel> clientlist;
    ClientListAdapter adapterClientList;
    ProgressDialog progressDialog;
    Spinner leadfilt;
    String[] leads = {"Highest", "Lowest"};
    String leadfilter = "Highest";
    EditText location, client, mobno;
    br.com.simplepass.loading_button_lib.customViews.CircularProgressButton filterbutton;
    String lct = "null";
    String clt = "null";
    String mob = "null";
    String Empid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_history);
        if(new InternetDialog(ClientHistory.this).getInternetStatus()){
            Initview();
            //   Toast.makeText(HomePage.this, "INTERNET VALIDATION PASSED", Toast.LENGTH_SHORT).show();
        }
    }

    private void Initview() {
        Empid=getIntent().getStringExtra("Empid");
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Lead History");
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
      /*  leadfilt = (Spinner) findViewById(R.id.spinner);*/
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statusbar_color));
        location = findViewById(R.id.location);
        client = findViewById(R.id.client);
        mobno = findViewById(R.id.mobno);
        filterbutton = findViewById(R.id.filterButton);
        filterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getClientFilterList();
            }
        });
     /*   ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, leads);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leadfilt.setAdapter(aa);*/
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.scrollToPosition(0);
        recyclerView.smoothScrollToPosition(0);
     /*   leadfilt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
              //  Toast.makeText(getApplicationContext(), leads[position], Toast.LENGTH_LONG).show();
                leadfilter = leads[position].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
     if(!Empid.equals(""))
     {
         getClientlistby();
     }
     else {
         getClientlist();
     }
    }

    private void getClientlistby() {

        try {
            // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
            String result = new ClientHelper.GetClientList().execute(Empid.toString()).get();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ClientDetailModel>>() {
            }.getType();
            clientlist = new Gson().fromJson(result, listType);
            adapterClientList = new ClientListAdapter(ClientHistory.this, clientlist);
            recyclerView.setAdapter(adapterClientList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getClientlist() {

        String empid = String.valueOf(SharedPrefManager.getInstance(this).getUser().getEmployeeID());
        try {
            // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
            String result = new ClientHelper.GetClientList().execute(empid.toString()).get();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ClientDetailModel>>() {
            }.getType();
            clientlist = new Gson().fromJson(result, listType);
            adapterClientList = new ClientListAdapter(ClientHistory.this, clientlist);
            recyclerView.setAdapter(adapterClientList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getClientFilterList() {
        String empid = String.valueOf(SharedPrefManager.getInstance(this).getUser().getEmployeeID());
        if (!location.getText().toString().isEmpty()) {
            lct = location.getText().toString();
        }
        if (!client.getText().toString().isEmpty()) {
            clt = client.getText().toString();
        }
        if (!mobno.getText().toString().isEmpty()) {
            mob = mobno.getText().toString();
        }
        try {
            // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
            String result = new ClientHelper.GetClientFilterList().execute(empid.toString(), leadfilter.toString(), lct.toString(), clt.toString(), mob.toString()).get();

            if (result.isEmpty()) {
                result = new ClientHelper.GetClientFilterList().execute(empid.toString(), leadfilter.toString(), lct.toString(), clt.toString(), mob.toString()).get();


            } else {
                lct="null";
                clt="null";
                mob="null";
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ClientDetailModel>>() {
                }.getType();
                clientlist = new Gson().fromJson(result, listType);
                adapterClientList = new ClientListAdapter(ClientHistory.this, clientlist);
                recyclerView.setAdapter(adapterClientList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
