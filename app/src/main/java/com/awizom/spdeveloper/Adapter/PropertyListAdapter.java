package com.awizom.spdeveloper.Adapter;

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
import com.awizom.spdeveloper.Config.AppConfig;
import com.awizom.spdeveloper.Model.ClientDetailModel;
import com.awizom.spdeveloper.Model.PropertyModel;
import com.awizom.spdeveloper.R;
import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoViewAttacher;


public class PropertyListAdapter extends RecyclerView.Adapter<PropertyListAdapter.MyViewHolder> {

    ProgressDialog progressDialog;
    private List<PropertyModel> propertylist;
    private Context mCtx;


    public PropertyListAdapter(Context baseContext, List<PropertyModel> clientList) {
        this.propertylist = clientList;
        this.mCtx = baseContext;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        PropertyModel c = propertylist.get(position);
        holder.cpropertyid.setText(String.valueOf(c.getPropertyID()));
        holder.pname.setText(String.valueOf(c.getPropertyName()));
        holder.property_area.setText(String.valueOf(c.getPropertyArea()));
        holder.address.setText(String.valueOf(c.getAddress()));
        holder.createdon.setText(String.valueOf(c.getCreatedOn().split("T")[0]));
        holder.img_link.setText(String.valueOf(AppConfig.BASE_URL +c.getPhoto().toString()));
        try {
            Glide.with(mCtx).load(AppConfig.BASE_URL + c.getPhoto().toString()).into(holder.propertyImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.propertyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mCtx,"filled",Toast.LENGTH_LONG).show();
                openZommImage(holder.img_link.getText().toString(), mCtx);
            }
        });

    }
    private void openZommImage(String imagelinkid, Context mCtx) {

        final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(mCtx);

        LayoutInflater inflater = LayoutInflater.from(mCtx);
        String image = imagelinkid;
        final View dialogView = inflater.inflate(R.layout.image_view_alert, null);
        ImageView zoomImageView = dialogView.findViewById(R.id.zoomImage);
        ImageView close = dialogView.findViewById(R.id.close);
        Glide.with(mCtx).load(image).into(zoomImageView);
        PhotoViewAttacher pAttacher;
        pAttacher = new PhotoViewAttacher(zoomImageView);
        pAttacher.update();
        dialogBuilder.setView(dialogView);
        dialogView.setBackgroundColor(Color.parseColor("#F0F8FF"));
        final android.support.v7.app.AlertDialog b = dialogBuilder.create();
        b.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
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

        return propertylist.size();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_propertylist, parent, false);

        return new MyViewHolder(v);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView pname, cpropertyid, property_area, createdon, address,img_link;
        ImageView propertyImage;
        @RequiresApi(api = Build.VERSION_CODES.M)
        public MyViewHolder(View view) {
            super(view);
            progressDialog = new ProgressDialog(mCtx);
            img_link=view.findViewById(R.id.img_link);
            pname = view.findViewById(R.id.property_name);
            property_area = view.findViewById(R.id.property_area);
            createdon = view.findViewById(R.id.createdon);
            address = view.findViewById(R.id.createdon);
            cpropertyid = view.findViewById(R.id.cpropertyid);
            address = view.findViewById(R.id.address);
            propertyImage = view.findViewById(R.id.propertyImage);
        }

    }

}