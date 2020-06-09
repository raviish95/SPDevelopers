package com.awizom.spdeveloper;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.awizom.spdeveloper.Adapter.PropertyListAdapter;
import com.awizom.spdeveloper.Adapter.TeamListAdapter;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Helper.WebHelper;
import com.awizom.spdeveloper.Model.ProjectViewModel;
import com.awizom.spdeveloper.Model.TeamLeaderModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MyEmpTeam extends AppCompatActivity {

    RecyclerView recyclerView;
    String teamid = "", result = "";
    List<TeamLeaderModel> teamLeaderModels;
    TeamListAdapter teamListAdapter;
    EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        teamid = getIntent().getStringExtra("TeamLeaderID");
        setContentView(R.layout.activity_my_emp_team);
        InitView();
    }

    private void InitView() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Team");
        toolbar.setBackgroundColor(Color.parseColor("#488586"));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        searchBox=findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
               filter(s.toString());
            }
        });
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
/*        Toast.makeText(getApplicationContext(), teamid, Toast.LENGTH_LONG).show();*/
        getEmployeeList();
    }

    void filter(String text){
        List<TeamLeaderModel> temp = new ArrayList();
        for(TeamLeaderModel d: teamLeaderModels){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getEmployeeName().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }

        }

        //update recyclerview

        teamListAdapter = new TeamListAdapter(MyEmpTeam.this, temp);
        recyclerView.setAdapter(teamListAdapter);
    }


    private void getEmployeeList() {
        try {
            result = new ClientHelper.GetEmployeeList().execute(teamid).get();
            if (result.isEmpty()) {
                result = new ClientHelper.GetEmployeeList().execute(teamid).get();
            } else {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<TeamLeaderModel>>() {
                }.getType();
                teamLeaderModels = new Gson().fromJson(result, listType);

                teamListAdapter = new TeamListAdapter(MyEmpTeam.this, teamLeaderModels);


                recyclerView.setAdapter(teamListAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
