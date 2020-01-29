package com.awizom.spdeveloper;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;

import com.awizom.spdeveloper.Adapter.ClientListAdapter;
import com.awizom.spdeveloper.Adapter.PropertyListAdapter;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Helper.WebHelper;
import com.awizom.spdeveloper.Model.ProjectViewModel;
import com.awizom.spdeveloper.Model.PropertyModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class PropertiesListActivity extends AppCompatActivity {

    String result="";
    List<ProjectViewModel> propertyModels;
    PropertyListAdapter adapterPropertyList;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_list);
        InitView();
    }

    private void InitView() {
        String PType = getIntent().getStringExtra("PType");
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (PType.equals("Completed")) {
            toolbar.setTitle("Completed Properties");
        } else if (PType.equals("Running")) {
            toolbar.setTitle("Running Properties");
        }else {
            toolbar.setTitle("Pending Properties");
        }

        toolbar.setBackgroundColor(Color.parseColor("#488586"));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (PType.equals("Completed")) {
            getcompleteproperty();
        } else if (PType.equals("Running")) {
            getrunningproperty();
        } else {
            getpendingproperty();
        }

    }

    private void getpendingproperty() {
        try {
            result = new WebHelper.GetPendingProperty().execute().get();
            if (result.isEmpty()) {
                result = new WebHelper.GetPendingProperty().execute().get();
            } else {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ProjectViewModel>>() {
                }.getType();
                propertyModels = new Gson().fromJson(result, listType);
                adapterPropertyList = new PropertyListAdapter(PropertiesListActivity.this, propertyModels);
                recyclerView.setAdapter(adapterPropertyList);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getrunningproperty() {
        try {
            result = new WebHelper.GetRunningProperty().execute().get();
            if (result.isEmpty()) {
                result = new WebHelper.GetRunningProperty().execute().get();
            } else {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ProjectViewModel>>() {
                }.getType();
                propertyModels = new Gson().fromJson(result, listType);
                adapterPropertyList = new PropertyListAdapter(PropertiesListActivity.this, propertyModels);
                recyclerView.setAdapter(adapterPropertyList);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getcompleteproperty() {

        try {
            result = new WebHelper.GetCompletedProperty().execute().get();
            if (result.isEmpty()) {
                result = new WebHelper.GetCompletedProperty().execute().get();
            } else {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ProjectViewModel>>() {
                }.getType();
                propertyModels = new Gson().fromJson(result, listType);
                adapterPropertyList = new PropertyListAdapter(PropertiesListActivity.this, propertyModels);
                recyclerView.setAdapter(adapterPropertyList);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
