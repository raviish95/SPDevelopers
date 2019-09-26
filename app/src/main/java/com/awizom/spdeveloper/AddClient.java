package com.awizom.spdeveloper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.awizom.spdeveloper.Helper.ClientHelper;

public class AddClient extends AppCompatActivity {
    EditText name, email, mobno, altmobno, address;
    br.com.simplepass.loading_button_lib.customViews.CircularProgressButton submit;
    ProgressDialog progressDialog;
    String result = "";
    String empid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addclient);
        InitView();
    }

    private void InitView() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Client");
        toolbar.setBackgroundColor(Color.parseColor("#488586"));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        empid = String.valueOf(SharedPrefManager.getInstance(AddClient.this).getUser().getEmployeeID());
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        mobno = findViewById(R.id.editTextMobile);
        altmobno = findViewById(R.id.editTextAltMobile);
        address = findViewById(R.id.editTextAddress);
        submit = findViewById(R.id.cirSubmitButton);
        progressDialog = new ProgressDialog(this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addclient();
            }
        });
    }

    private void addclient() {
        if (name.getText().toString().isEmpty()) {
            name.setError("Please enter valid Name");
            name.requestFocus();
        } else if (email.getText().toString().isEmpty()) {
            email.setError("Please enter valid Email");
            email.requestFocus();
        } else if (mobno.getText().toString().isEmpty()) {
            mobno.setError("Please enter valid Mobno");
            mobno.requestFocus();
        } else if (altmobno.getText().toString().isEmpty()) {
            altmobno.setError("Please enter valid alt mobno");
            altmobno.requestFocus();
        } else if (address.getText().toString().isEmpty()) {
            address.setError("Please enter valid Address");
            address.requestFocus();
        } else {
          /*  progressDialog.setMessage("Please wait");
            progressDialog.show();*/
            submit.startAnimation();
            try {
                result = new ClientHelper.AddClient().execute(name.getText().toString(), email.getText().toString(), mobno.getText().toString(), altmobno.getText().toString(), address.getText().toString(), empid.toString()).get();
                if (result.isEmpty()) {

                    result = new ClientHelper.AddClient().execute(name.getText().toString(), email.getText().toString(), mobno.getText().toString(), altmobno.getText().toString(), address.getText().toString(), empid.toString()).get();
                } else {
                    Toast.makeText(getApplicationContext(), result + " Success", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(AddClient.this, ClientPropertyDetail.class);
                    intent.putExtra("ClientID", String.valueOf(result));
                    startActivity(intent);
                    submit.revertAnimation();
                  /*  progressDialog.dismiss();*/
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}