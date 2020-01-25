package com.awizom.spdeveloper;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Helper.ProfileHelper;


public class RegisterActivity extends AppCompatActivity {

    String[] regtype = {"Agent", "Employee"};
    EditText name, email, mobno, altmobno, address;
    String registertype = "", result = "";
    br.com.simplepass.loading_button_lib.customViews.CircularProgressButton register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();
        if(new InternetDialog(RegisterActivity.this).getInternetStatus()){
            InitView();
            //   Toast.makeText(HomePage.this, "INTERNET VALIDATION PASSED", Toast.LENGTH_SHORT).show();
        }
    }

    private void InitView() {

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        name = findViewById(R.id.editTextName);
        email = findViewById(R.id.editTextEmail);
        mobno = findViewById(R.id.editTextMobile);
        altmobno = findViewById(R.id.editaltmobno);
        address = findViewById(R.id.editTextAddress);
        register = findViewById(R.id.cirRegisterButton);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, regtype);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getApplicationContext(), regtype[position], Toast.LENGTH_LONG).show();
                registertype = regtype[position].toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (name.getText().toString().isEmpty()) {
                    name.setError("Please Enter valid UserName");
                    name.requestFocus();
                } else if (email.getText().toString().isEmpty()) {
                    email.setError("Please Enter valid email");
                    email.requestFocus();
                } else if ((mobno.getText().toString().isEmpty()) || mobno.getText().toString().length()<10 || mobno.getText().toString().length()>10) {
                    mobno.setError("Please Enter valid mobno");
                    mobno.requestFocus();
                }  else if (address.getText().toString().isEmpty()) {
                    address.setError("Please Enter valid Address");
                    address.requestFocus();
                } else {
                    register.startAnimation();
                    try {
                        result = new ProfileHelper.AddRegister().execute(name.getText().toString(), email.getText().toString(), mobno.getText().toString(), altmobno.getText().toString(), address.getText().toString(), registertype.toString()).get();
                        if (result.isEmpty()) {

                            result = new ProfileHelper.AddRegister().execute(name.getText().toString(), email.getText().toString(), mobno.getText().toString(), altmobno.getText().toString(), address.getText().toString(), registertype.toString()).get();
                        } else {

                            openConfirm();

                          /*  Toast.makeText(getApplicationContext(), "Your registration request was successfully sent to admin", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);*/
                            register.revertAnimation();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void openConfirm() {

        android.support.v7.app.AlertDialog b;
        final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(RegisterActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.open_search_list, null);
        br.com.simplepass.loading_button_lib.customViews.CircularProgressButton okbtn =dialogView.findViewById(R.id.cirRegisterButton);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        dialogBuilder.setView(dialogView);
        dialogView.setBackgroundColor(Color.parseColor("#F0F8FF"));
        b = dialogBuilder.create();
        b.show();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);

    }


}