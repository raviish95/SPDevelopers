package com.awizom.spdeveloper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(toolbar);
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
        empid = String.valueOf(SharedPrefManager.getInstance(AddClient.this).getUser().getEmployeeID());
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        mobno = findViewById(R.id.editTextMobile);
        altmobno = findViewById(R.id.editTextAltMobile);
        address = findViewById(R.id.editTextAddress);
        submit = findViewById(R.id.cirSubmitButton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addclient();
            }
        });
    }

    private void addclient() {
        showCustomLoadingDialog();
        String emailPattern = "[a-zA-Z0-9+._%-+]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                "(" +
                "." +
                "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                ")+";
        if (name.getText().toString().isEmpty()) {
            name.setError("Please enter valid Name");
            name.requestFocus();
        } else if (mobno.getText().toString().isEmpty() ||mobno.getText().toString().length()< 10 || mobno.getText().toString().length()> 10) {
            mobno.setError("Please enter valid Mobno");
            mobno.requestFocus();
        }
        else if(mobno.getText().toString().equals(altmobno.getText().toString()))
        {
            altmobno.setError("Please enter different alt mobno");
            altmobno.requestFocus();
        }

        else {
          /*  progressDialog.setMessage("Please wait");
            progressDialog.show();*/
            showCustomLoadingDialog();
            submit.startAnimation();
            try {
                String altmobnos=altmobno.getText().toString();
                if(altmobnos.equals(""))
                {
                    altmobnos="nullval";
                }
                result = new ClientHelper.AddClient().execute(name.getText().toString(), email.getText().toString(), mobno.getText().toString(), altmobnos.toString(), address.getText().toString(), empid.toString()).get();
                if (result.isEmpty()) {
                    result = new ClientHelper.AddClient().execute(name.getText().toString(), email.getText().toString(), mobno.getText().toString(), altmobnos.toString(), address.getText().toString(), empid.toString()).get();
                }
                else if(result.equals("100"))
                {
                    Toast.makeText(getApplicationContext(), "EmailID or mobile no is already registered", Toast.LENGTH_LONG).show();
                    submit.revertAnimation();
                }
                else {
                    Toast.makeText(getApplicationContext(), " Success", Toast.LENGTH_LONG).show();
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

    public void showCustomLoadingDialog() {

        //..show gif
        progressDialog.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //...here i'm waiting 5 seconds before hiding the custom dialog
                //...you can do whenever you want or whenever your work is done
                if (!AddClient.this.isFinishing() && progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        }, 1000);
    }
}
