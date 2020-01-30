package com.awizom.spdeveloper;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.awizom.spdeveloper.Helper.ProfileHelper;
import com.awizom.spdeveloper.Model.LoginModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class LoginActivity extends AppCompatActivity {
    EditText editUserName, password;
    br.com.simplepass.loading_button_lib.customViews.CircularProgressButton login;
    private AlertDialog progressDialog;
    TextView logintext;
    String result = "";
    ProgressDialog viewDialog;
    String registrclck = "", loginclck = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(SharedPrefManager.getInstance(LoginActivity.this).getUser().getEmployeeID() == 0)) {
            Intent intent = new Intent(LoginActivity.this, HomePage.class);
            startActivity(intent);
        }
        //for changing status bar icon colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        checkAppPermission();
        setContentView(R.layout.activity_login);
        if (new InternetDialog(LoginActivity.this).getInternetStatus()) {
            initview();
            //   Toast.makeText(HomePage.this, "INTERNET VALIDATION PASSED", Toast.LENGTH_SHORT).show();
        }

    }

    private void checkAppPermission() {
        ActivityCompat.requestPermissions(LoginActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(LoginActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void initview() {
        loginclck = getIntent().getStringExtra("LoginClick");
        registrclck = getIntent().getStringExtra("RegisterClick");
        progressDialog = new ProgressDialog(this);
        editUserName = findViewById(R.id.editUserName);
        password = findViewById(R.id.editTextPassword);
        login = findViewById(R.id.cirLoginButton);
        viewDialog = new ProgressDialog(this);
        viewDialog.setMessage("Please wait...");
        viewDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostLogin();
            }
        });
        logintext = findViewById(R.id.logintext);
        try {
            if (registrclck.equals("Yes")) {
                logintext.setTextColor(Color.parseColor("#641E16"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("RegisterClick", "Yes");
                intent.putExtra("LoginClick", loginclck);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });
    }

    private void PostLogin() {

        showCustomLoadingDialog();

        if (editUserName.getText().toString().isEmpty() || editUserName.getText().toString().contains(" ")) {
            progressDialog.dismiss();
            editUserName.setError("Please Enter valid UserName");
            editUserName.requestFocus();
        } else if (password.getText().toString().isEmpty() || password.getText().toString().contains(" ")) {
            progressDialog.dismiss();
            password.setError("Please Enter valid Password");
            password.requestFocus();
        } else {
            // showCustomLoadingDialog();
            login.startAnimation();
            try {
                result = new ProfileHelper.LogIn().execute(editUserName.getText().toString(), password.getText().toString()).get();
                if (result.isEmpty()) {

                    PostLogin();
                } else {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<LoginModel>() {
                    }.getType();
                    LoginModel loginModel = new Gson().fromJson(result, listType);

                    String empid = String.valueOf(loginModel.getEmployeeID());
                    if (!empid.equals("0")) {
                        if (!loginModel.isActive()) {
                            openConfirm();
                            login.revertAnimation();
                        } else {
                            String userID = loginModel.getUserID().toString();
                            LoginModel loginModel1 = new LoginModel();
                            loginModel1.EmployeeID = Integer.parseInt((empid));
                            loginModel1.UserID = String.valueOf(userID.toString());
                            loginModel1.UserName = editUserName.getText().toString();

                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(loginModel1);
                            Intent intent = new Intent(LoginActivity.this, HomePage.class);
                            intent.putExtra("EmployeeID", empid.toString());
                            intent.putExtra("UserName", editUserName.getText().toString());
                            intent.putExtra("Welcome", "welcometo");
                            startActivity(intent);
                        }
                        //   progressDialog.dismiss();
                    } else {
                        //   progressDialog.dismiss();
                        login.revertAnimation();
                        editUserName.setError("Wrong UserId or Password");
                        password.setError("Wrong UserId or Password");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openConfirm() {

        final android.support.v7.app.AlertDialog b;
        final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.open_not_active, null);
        br.com.simplepass.loading_button_lib.customViews.CircularProgressButton okbtn = dialogView.findViewById(R.id.cirRegisterButton);

        dialogBuilder.setView(dialogView);
        dialogView.setBackgroundColor(Color.parseColor("#F0F8FF"));
        b = dialogBuilder.create();
        b.show();
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUserName.setText("");
                password.setText("");
                b.dismiss();
            }
        });
    }

    private void showCustomLoadingDialog() {
        viewDialog.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //...here i'm waiting 5 seconds before hiding the custom dialog
                //...you can do whenever you want or whenever your work is done
                if (!LoginActivity.this.isFinishing() && viewDialog != null) {
                    viewDialog.dismiss();
                }
            }
        }, 1000);
    }
/*
    public void onLoginClick(View View) {

        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("RegisterClick", "Yes");
        intent.putExtra("LoginClick", loginclck);
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
    }*/
}