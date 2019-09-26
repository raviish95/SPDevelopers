package com.awizom.spdeveloper.Adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.awizom.spdeveloper.ClientPropertyDetail;
import com.awizom.spdeveloper.Model.ClientDetailModel;
import com.awizom.spdeveloper.Model.FollowUpModel;
import com.awizom.spdeveloper.R;
import com.bumptech.glide.Glide;


public class FollowUpHistoryAdapter extends RecyclerView.Adapter<FollowUpHistoryAdapter.MyViewHolder> {

    ProgressDialog progressDialog;
    private List<FollowUpModel> followhistlist;
    private Context mCtx;


    public FollowUpHistoryAdapter(Context baseContext, List<FollowUpModel> followhistlist) {
        this.followhistlist = followhistlist;
        this.mCtx = baseContext;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        FollowUpModel c = followhistlist.get(position);
        holder.followupid.setText(String.valueOf(c.getFollowUpID()));
        holder.cname.setText(String.valueOf(c.getName()));
        holder.followupdate.setText(String.valueOf(c.getFollowUpDate().split("T")[0]));
        String dates=String.valueOf(c.getFollowUpDate().replace("T"," "));
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

        TextView cname,  followupmethod,followupid,followupdate,followuptime;

        @RequiresApi(api = Build.VERSION_CODES.M)
        public MyViewHolder(View view) {
            super(view);
            progressDialog=new ProgressDialog(mCtx);
            followuptime=view.findViewById(R.id.followuptime);
            progressDialog.setMessage("Please wait");
            followupid=view.findViewById(R.id.followupid);
            followupdate=view.findViewById(R.id.followupdate);
            cname=view.findViewById(R.id.client_name);
            followupmethod=view.findViewById(R.id.followupmethod);
        }

    }

}