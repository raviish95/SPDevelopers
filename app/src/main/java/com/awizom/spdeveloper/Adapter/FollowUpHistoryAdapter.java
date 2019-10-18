package com.awizom.spdeveloper.Adapter;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.awizom.spdeveloper.ClientPropertyDetail;
import com.awizom.spdeveloper.FollowUpHistory;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Model.ClientDetailModel;
import com.awizom.spdeveloper.Model.FollowUpModel;
import com.awizom.spdeveloper.R;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;


public class FollowUpHistoryAdapter extends RecyclerView.Adapter<FollowUpHistoryAdapter.MyViewHolder> {

    ProgressDialog progressDialog;
    private List<FollowUpModel> followhistlist;
    private Context mCtx;
    TextView editTextFeedback;
    private static int TIMER = 300;
    String result="";
    public FollowUpHistoryAdapter(Context baseContext, List<FollowUpModel> followhistlist) {
        this.followhistlist = followhistlist;
        this.mCtx = baseContext;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        FollowUpModel c = followhistlist.get(position);
        holder.followupid.setText(String.valueOf(c.getFollowUpID()));
        holder.cname.setText(String.valueOf(c.getName()));
        holder.followupdate.setText(String.valueOf(c.getFollowUpDate().split("T")[0]));
            if (c.getFeedback().toString().equals("0")) {
                holder.feedbacktv.setBackgroundColor(Color.parseColor("#FE1902"));
            } else {
                holder.feedbacktv.setBackgroundColor(Color.parseColor("#6AB628"));
            }
        holder.feedbacktv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenFollowUpForm(holder.followupid.getText().toString());
            }
        });
        String dates = String.valueOf(c.getFollowUpDate().replace("T", " "));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = dateFormatter.parse(dates);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Get time from date
        SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a");
        String displayValue = timeFormatter.format(date);
        holder.followuptime.setText(displayValue);
        holder.followupmethod.setText(String.valueOf(c.getFollowUpMethod()));
    }

    private void OpenFollowUpForm(final String followupid) {

        final br.com.simplepass.loading_button_lib.customViews.CircularProgressButton submits;
        final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(mCtx);
        LayoutInflater inflater =  LayoutInflater.from(mCtx);
        final View dialogView = inflater.inflate(R.layout.addfollowfeedback, null);
        editTextFeedback=dialogView.findViewById(R.id.editTextFeedback);
        submits = dialogView.findViewById(R.id.cirSubmitButton);

        submits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        if (editTextFeedback.getText().toString().isEmpty()) {
                            editTextFeedback.setError("Please enter follow up response");
                            editTextFeedback.requestFocus();

                        } else {
                            submits.startAnimation();
                            try {
                                {
                                    result = new ClientHelper.EditFollowUp().execute(followupid.toString(),editTextFeedback.getText().toString()).get();
                                    if (result.isEmpty()) {
                                        result = new ClientHelper.EditFollowUp().execute(followupid.toString(),editTextFeedback.getText().toString()).get();

                                    } else {
                                        Intent intent = new Intent(mCtx, FollowUpHistory.class);
                                        mCtx.startActivity(intent);
                                        submits.revertAnimation();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, TIMER);


            }
        });


        dialogBuilder.setView(dialogView);
        dialogView.setBackgroundColor(Color.parseColor("#F0F8FF"));
        final android.support.v7.app.AlertDialog b = dialogBuilder.create();

        b.show();
    }

    private void dismissmethod() {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 100);
    }


    @Override
    public int getItemCount() {

        return followhistlist.size();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_followhistory, parent, false);

        return new MyViewHolder(v);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView cname, followupmethod, followupid, followupdate, followuptime, feedbacktv;

        @RequiresApi(api = Build.VERSION_CODES.M)
        public MyViewHolder(View view) {
            super(view);
            progressDialog = new ProgressDialog(mCtx);
            followuptime = view.findViewById(R.id.followuptime);
            progressDialog.setMessage("Please wait");
            followupid = view.findViewById(R.id.followupid);
            followupdate = view.findViewById(R.id.followupdate);
            cname = view.findViewById(R.id.client_name);
            followupmethod = view.findViewById(R.id.followupmethod);
            feedbacktv = view.findViewById(R.id.feedback);
        }

    }

}