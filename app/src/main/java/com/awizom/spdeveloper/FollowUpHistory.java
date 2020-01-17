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
import android.widget.EditText;

import com.awizom.spdeveloper.Adapter.ClientListAdapter;
import com.awizom.spdeveloper.Adapter.FollowUpHistoryAdapter;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Model.ClientDetailModel;
import com.awizom.spdeveloper.Model.FollowUpModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class FollowUpHistory extends AppCompatActivity {
    RecyclerView recyclerView;
    List<FollowUpModel> followUpModelList;
    FollowUpHistoryAdapter adapterfollowuphistoryList;
    ProgressDialog progressDialog;
    EditText client;
    br.com.simplepass.loading_button_lib.customViews.CircularProgressButton filterbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_up_history);
        if(new InternetDialog(FollowUpHistory.this).getInternetStatus()){
            Initview();
            //   Toast.makeText(HomePage.this, "INTERNET VALIDATION PASSED", Toast.LENGTH_SHORT).show();
        }

    }

    private void Initview() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Follow Up History");
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
        filterbtn = findViewById(R.id.filterButton);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.scrollToPosition(0);
        recyclerView.smoothScrollToPosition(0);
        getFollowUpList();
        filterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFollowUpFilterList();
            }
        });

    }

    private void getFollowUpFilterList() {
        if (client.getText().toString().isEmpty()) {
            client.setError("Please enter valid customer name");
            client.requestFocus();
        } else {
            filterbtn.startAnimation();
            String empid = String.valueOf(SharedPrefManager.getInstance(this).getUser().getEmployeeID());
            try {
                // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
                String result = new ClientHelper.GetFollowUpByFilterList().execute(empid.toString(), client.getText().toString()).get();
                Gson gson = new Gson();
                Type listType = new TypeToken<List<FollowUpModel>>() {
                }.getType();
                followUpModelList = new Gson().fromJson(result, listType);
                adapterfollowuphistoryList = new FollowUpHistoryAdapter(FollowUpHistory.this, followUpModelList);
                recyclerView.setAdapter(adapterfollowuphistoryList);
                filterbtn.revertAnimation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getFollowUpList() {
        String empid = String.valueOf(SharedPrefManager.getInstance(this).getUser().getEmployeeID());
        try {
            // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
            String result = new ClientHelper.GetFollowUpHistoryList().execute(empid.toString()).get();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<FollowUpModel>>() {
            }.getType();
            followUpModelList = new Gson().fromJson(result, listType);
            adapterfollowuphistoryList = new FollowUpHistoryAdapter(FollowUpHistory.this, followUpModelList);
            recyclerView.setAdapter(adapterfollowuphistoryList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
