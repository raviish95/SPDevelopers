package com.awizom.spdeveloper;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);
        initView();
    }

    private void initView() {
        RelativeLayout relativeLayout=findViewById(R.id.rel3);
        TransitionDrawable trans = (TransitionDrawable) relativeLayout.getBackground();
        trans.startTransition(4000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!(SharedPrefManager.getInstance(SplashActivity.this).getUser().getEmployeeID() == 0)) {
                    Intent intent = new Intent(SplashActivity.this, HomePage.class);
                    startActivity(intent);
                }else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
        }, SPLASH_TIME_OUT);

    }

}

