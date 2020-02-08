package com.awizom.spdeveloper.Adapter;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.awizom.spdeveloper.ClientHistory;
import com.awizom.spdeveloper.Config.AppConfig;
import com.awizom.spdeveloper.Model.ProjectViewModel;
import com.awizom.spdeveloper.Model.TeamLeaderModel;
import com.awizom.spdeveloper.R;
import com.bumptech.glide.Glide;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class TeamListAdapter extends RecyclerView.Adapter<TeamListAdapter.MyViewHolder> {

    ProgressDialog progressDialog;
    private List<TeamLeaderModel> teamLeaderModelList;
    private Context mCtx;


    public TeamListAdapter(Context baseContext, List<TeamLeaderModel> teamLeaderModelList) {
        this.teamLeaderModelList = teamLeaderModelList;
        this.mCtx = baseContext;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        TeamLeaderModel c = teamLeaderModelList.get(position);
         holder.emp_id.setText(String.valueOf(c.getEmployeeID()));
         holder.emp_name.setText(String.valueOf(c.getEmployeeName()));
         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 holder.cardlinear.setBackgroundColor(Color.parseColor("#BCE8F5"));

                 dismissmethod();
                 Intent intent=new Intent(mCtx, ClientHistory.class);
                 intent.putExtra("Empid",holder.emp_id.getText().toString());
                 mCtx.startActivity(intent);
              dismisscolor(holder.cardlinear);
             }
         });
    }

    private void dismisscolor(final LinearLayout cardlinear) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    cardlinear.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                catch (Exception e)
                {e.printStackTrace();}
            }
        }, 100);
    }


    private void dismissmethod() {
        progressDialog.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    progressDialog.dismiss();
                }
                catch (Exception e)
                {e.printStackTrace();}
            }
        }, 100);
    }


    @Override
    public int getItemCount() {

        return teamLeaderModelList.size();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_employeelist, parent, false);

        return new MyViewHolder(v);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView emp_id, emp_name;
        LinearLayout cardlinear;

        @RequiresApi(api = Build.VERSION_CODES.M)
        public MyViewHolder(View view) {
            super(view);
            progressDialog = new ProgressDialog(mCtx);
            progressDialog.setMessage("Loading...");
            emp_id = view.findViewById(R.id.emp_id);
            emp_name = view.findViewById(R.id.emp_name);
            cardlinear=view.findViewById(R.id.cardlinear);

        }

    }

}