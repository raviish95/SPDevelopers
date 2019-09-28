package com.awizom.spdeveloper.Adapter;

import java.util.List;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.awizom.spdeveloper.AddClient;
import com.awizom.spdeveloper.ClientPropertyDetail;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Model.ClientDetailModel;
import com.awizom.spdeveloper.R;
import com.awizom.spdeveloper.SharedPrefManager;
import com.bumptech.glide.Glide;
public class ClientFollowListAdapter extends RecyclerView.Adapter<ClientFollowListAdapter.MyViewHolder> {

    ProgressDialog progressDialog;
    private List<ClientDetailModel> clientList;
    private Context mCtx;
    String result = "";

    public ClientFollowListAdapter(Context baseContext, List<ClientDetailModel> clientList) {
        this.clientList = clientList;
        this.mCtx = baseContext;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ClientDetailModel c = clientList.get(position);
        holder.cid.setText(String.valueOf(c.getClientID()));
        holder.cname.setText(c.getName());
        holder.email.setText(c.getEmail());
        holder.mobno.setText(c.getMobNo());
        holder.address.setText(c.getAddress());
        holder.date.setText(String.valueOf(c.getCreatedOn().split("T")[0]));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                Intent intent = new Intent(mCtx, ClientPropertyDetail.class);
                intent.putExtra("ClientID", holder.cid.getText().toString());
                mCtx.startActivity(intent);
                dismissmethod();
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + holder.mobno.getText().toString()));//change the number
                if (ActivityCompat.checkSelfPermission(mCtx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mCtx.startActivity(callIntent);
                progressDialog.dismiss();
                PostFollowUp(holder.cid.getText().toString());
            }
        });

    }

    private void PostFollowUp(String clientid) {
        String empid = String.valueOf(SharedPrefManager.getInstance(mCtx).getUser().getEmployeeID());
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        try {
            result = new ClientHelper.AddFollowUp().execute(clientid.toString(), empid.toString()).get();
            if (result.isEmpty()) {

                result = new ClientHelper.AddFollowUp().execute(clientid.toString(), empid.toString()).get();
            } else {
                Toast.makeText(mCtx, "Successfully followed", Toast.LENGTH_LONG).show();
               /* Intent intent = new Intent(AddClient.this, ClientPropertyDetail.class);
                intent.putExtra("ClientID", String.valueOf(result));
                startActivity(intent);*/
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        return clientList.size();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_clientfollowlist, parent, false);

        return new MyViewHolder(v);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView cname, email, mobno, address, cid, date;
        ImageView call;

        @RequiresApi(api = Build.VERSION_CODES.M)
        public MyViewHolder(View view) {
            super(view);
            progressDialog = new ProgressDialog(mCtx);
            progressDialog.setMessage("Please wait");
            cid = view.findViewById(R.id.clientid);
            date = view.findViewById(R.id.createdon);
            cname = view.findViewById(R.id.client_name);
            email = view.findViewById(R.id.email);
            mobno = view.findViewById(R.id.mobno);
            address = view.findViewById(R.id.address);
            call = view.findViewById(R.id.call);
        }

    }

}