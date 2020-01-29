package com.awizom.spdeveloper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

public class PropertiesActivity extends AppCompatActivity {

    LinearLayout completed, pending, running;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties);
        intitview();
    }

    private void intitview() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Properties");
        toolbar.setTitleTextColor(Color.parseColor("#e82c8d"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressDialog=new ProgressDialog(this);
        completed = findViewById(R.id.completed);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pending = findViewById(R.id.pending);
        running = findViewById(R.id.running);
        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomLoadingDialog();
                Intent intent = new Intent(PropertiesActivity.this, PropertiesListActivity.class);
                intent.putExtra("PType", "Completed");
                startActivity(intent);
            }
        });
        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomLoadingDialog();
                Intent intent = new Intent(PropertiesActivity.this, PropertiesListActivity.class);
                intent.putExtra("PType", "Pending");
                startActivity(intent);
            }
        });
        running.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomLoadingDialog();
                Intent intent = new Intent(PropertiesActivity.this, PropertiesListActivity.class);
                intent.putExtra("PType", "Running");
                startActivity(intent);
            }
        });

    }

    public void showCustomLoadingDialog() {

        //..show gif
        progressDialog.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //...here i'm waiting 5 seconds before hiding the custom dialog
                //...you can do whenever you want or whenever your work is done
                progressDialog.dismiss();
            }
        }, 1000);
    }
}
