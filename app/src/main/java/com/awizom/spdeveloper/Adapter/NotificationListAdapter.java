package com.awizom.spdeveloper.Adapter;

import java.lang.reflect.Type;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.awizom.spdeveloper.ClientHistory;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Model.NotificationModel;
import com.awizom.spdeveloper.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import uk.co.senab.photoview.PhotoViewAttacher;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {

    private List<NotificationModel> notificationModelList;
    private Context mCtx;
    String result = "";
    private AlertDialog progressDialog;

    public NotificationListAdapter(Context baseContext, List<NotificationModel> notificationModelList) {
        this.notificationModelList = notificationModelList;
        this.mCtx = baseContext;

    }

    /* for solve issue of item change on scroll this method is set*/
    @Override
    public long getItemId(int position) {
        return position;
    }

    /* for solve issue of item change on scroll this method is set*/
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        NotificationModel c = notificationModelList.get(position);
        holder.orderid.setText(String.valueOf(c.getEmployeeID()));
        holder.notification_id.setText(String.valueOf(c.getClientLeadNotiId()));

        if (c.getRead()) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        holder.notification_body.setText(c.getMSGBody().toString());
      /*  holder.notification_date.setText(String.valueOf(c.getDate().toString()).split("T")[0]);*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readNotification(holder.notification_id.getText().toString());
                Intent intent=new Intent(mCtx, ClientHistory.class);
                intent.putExtra("Empid",holder.orderid.getText().toString());
                mCtx.startActivity(intent);
            }
        });
    }


    private void readNotification(String noti_id) {

        try {
            result = new ClientHelper.PostReadNotification().execute(noti_id).get();
            if (result.isEmpty()) {
                Toast.makeText(mCtx, "Invalid request", Toast.LENGTH_SHORT).show();
                result = new ClientHelper.PostReadNotification().execute(noti_id).get();
            } else {
                Gson gson = new Gson();
              /*  Type getType = new TypeToken<ResultModel>() {
                }.getType();
                ResultModel resultModel = new Gson().fromJson(result, getType);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {

        return notificationModelList.size();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_notificationlist, parent, false);

        return new MyViewHolder(v);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView notification_body,  notification_type, orderid, notification_id;
        CardView cardView;
        ImageView noti_typeimage;

        @RequiresApi(api = Build.VERSION_CODES.M)
        public MyViewHolder(View view) {
            super(view);

            notification_body = view.findViewById(R.id.noti_body);
            notification_id = view.findViewById(R.id.noti_ID);
            orderid = view.findViewById(R.id.noti_orderid);

            cardView = view.findViewById(R.id.card_view);
            notification_type = view.findViewById(R.id.noti_type);
            noti_typeimage = view.findViewById(R.id.noti_typeImage);
        }


    }

}