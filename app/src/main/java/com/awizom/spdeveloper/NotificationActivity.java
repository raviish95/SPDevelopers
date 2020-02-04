package com.awizom.spdeveloper;

import android.app.AlertDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.awizom.spdeveloper.Adapter.NotificationListAdapter;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Model.NotificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


/* created by ravi on 04-feb-2020*/

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    List<NotificationModel> notificationModelList;
    NotificationListAdapter notificationListAdapter;
    private AlertDialog progressDialog;
    String teamLID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initView();
    }

    private void initView() {
        teamLID=getIntent().getStringExtra("TeamLID");
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Notifications");

        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getNotifications();
    }

    private void getNotifications() {

        try {
            String result = new ClientHelper.GetNotificationList().execute(teamLID.toString()).get();
            Gson gson = new Gson();
            Type listType = new TypeToken<List<NotificationModel>>() {
            }.getType();
            notificationModelList = new Gson().fromJson(result, listType);
            notificationListAdapter = new NotificationListAdapter(NotificationActivity.this, notificationModelList);

            recyclerView.setAdapter(notificationListAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
