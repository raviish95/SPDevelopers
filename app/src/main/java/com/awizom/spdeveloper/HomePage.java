package com.awizom.spdeveloper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.awizom.spdeveloper.Adapter.ClientListAdapter;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Helper.ProfileHelper;
import com.awizom.spdeveloper.Model.ClientDetailModel;
import com.awizom.spdeveloper.Model.EmployeeDetailModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    String result = "";
    String empid = "";
    GridView gridView;
    List<ClientDetailModel> clientlist;
    ClientListAdapter adapterClientList;
    ProgressDialog progressDialog;
    SwipeRefreshLayout refresh;
    boolean connected = false;
    TextView textCartItemCount;
    int mCartItemCount = 10;
    NavigationView navigationView;
    String teamleaderid = "";
    private ImageView notification;
    MenuItem menuItem;
    TextView name,uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        empid = String.valueOf(SharedPrefManager.getInstance(HomePage.this).getUser().getEmployeeID());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new GridImageAdapter(this));
        progressDialog = new ProgressDialog(this);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.scrollToPosition(0);
        recyclerView.smoothScrollToPosition(0);
        refresh = findViewById(R.id.swipeRefreshLayout);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (new InternetDialog(HomePage.this).getInternetStatus()) {
                    getClientList();
                    GetLeaderOrNot();
                    GetMyProfile();
                    //   Toast.makeText(HomePage.this, "INTERNET VALIDATION PASSED", Toast.LENGTH_SHORT).show();
                }
                refresh.setRefreshing(false);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Please wait");
                progressDialog.show();
                Intent intent = new Intent(HomePage.this, AddClient.class);
                startActivity(intent);
                progressDialog.dismiss();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        name =  header.findViewById(R.id.name);
        uid =  header.findViewById(R.id.uid);
        if (new InternetDialog(this).getInternetStatus()) {
            getClientList();
            GetCheckFollowList();
            GetMyProfile();
            // Toast.makeText(this, "INTERNET VALIDATION PASSED", Toast.LENGTH_SHORT).show();
        }
    }

    private void GetMyProfile() {

        String empid = String.valueOf(SharedPrefManager.getInstance(this).getUser().getEmployeeID());
        try {
            // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
           String response = new ClientHelper.GetMyProfile().execute(empid.toString()).get();
            if (response.isEmpty()) {

                response = new ClientHelper.GetMyProfile().execute(empid.toString()).get();
            } else {
                Type listype=new TypeToken<EmployeeDetailModel>(){}.getType();
                EmployeeDetailModel cmodel=new Gson().fromJson(response, listype);
                uid.setPaintFlags(uid.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                name.setPaintFlags(name.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                name.setText(cmodel.getEmployeeName().toString());
                uid.setText(cmodel.getEmployeeNo());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetLeaderOrNot() {

        String empid = String.valueOf(SharedPrefManager.getInstance(this).getUser().getEmployeeID());
        try {
            // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
            teamleaderid = new ClientHelper.GetLeaderOrNot().execute(empid.toString()).get();
            if (teamleaderid.isEmpty()) {
                Toast.makeText(getApplicationContext(), teamleaderid, Toast.LENGTH_LONG).show();
                teamleaderid = new ClientHelper.GetLeaderOrNot().execute(empid.toString()).get();
            } else {
          /*      Toast.makeText(getApplicationContext(), teamleaderid, Toast.LENGTH_LONG).show();*/
                if (teamleaderid.equals("0")) {
                    MenuItem item = navigationView.getMenu().findItem(R.id.nav_leader);
                    item.setVisible(false);
                }
                else{
                    menuItem.setVisible(true);
             GetNotiCount(teamleaderid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetNotiCount(String tid) {
        try {
            String results = new ClientHelper.GetNotiCount().execute(tid.toString()).get();
            // Toast.makeText(getApplicationContext(),result.toString(),Toast.LENGTH_LONG).show();
            mCartItemCount = Integer.parseInt(results);
            if (textCartItemCount != null) {
                if (mCartItemCount == 0) {
                    if (textCartItemCount.getVisibility() != View.GONE) {
                        textCartItemCount.setVisibility(View.GONE);
                    }
                } else {
                    textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                    if (textCartItemCount.getVisibility() != View.VISIBLE) {
                        textCartItemCount.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void GetCheckFollowList() {

        String empid = String.valueOf(SharedPrefManager.getInstance(this).getUser().getEmployeeID());
        try {
            // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
            String result = new ClientHelper.GetCheckFollowList().execute(empid.toString()).get();
            if (result.isEmpty()) {
                result = new ClientHelper.GetCheckFollowList().execute(empid.toString()).get();

            } else {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ClientDetailModel>>() {
                }.getType();
                clientlist = new Gson().fromJson(result, listType);

                for (int i = 0; i < clientlist.size(); i++) {
                    final Intent emptyIntent = new Intent(HomePage.this, FollowUP.class);
                    NotificationManager notificationManager = (NotificationManager) HomePage.this.getSystemService(Context.NOTIFICATION_SERVICE);
                    String channelId = "channel-01";
                    String channelName = "Channel Name";
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel mChannel = new NotificationChannel(
                                channelId, channelName, importance);
                        notificationManager.createNotificationChannel(mChannel);
                    }
                    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(HomePage.this, channelId)
                            .setSmallIcon(R.drawable.sp_logo)
                            .setContentTitle("Hey! " + clientlist.get(i).getName() + " is going down in follow up")
                            .setSound(uri)
                            .setContentText(String.valueOf("SpDeveloper,Follow Now!"));

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(HomePage.this);
                    /*   stackBuilder.addNextIntent(intent);*/
                    PendingIntent pendingIntent = PendingIntent.getActivity(HomePage.this, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(pendingIntent);

                    notificationManager.notify(i, mBuilder.build());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkInternet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
            getClientList();
            //    Toast.makeText(getApplicationContext(), "Internet is On", Toast.LENGTH_SHORT).show();
        } else {
            connected = false;

        }
    }

    private void getClientList() {

        String empid = String.valueOf(SharedPrefManager.getInstance(this).getUser().getEmployeeID());
        try {
            // Toast.makeText(getApplicationContext(), "deviceid->" + FirebaseInstanceId.getInstance().getToken(), Toast.LENGTH_LONG).show();
            String result = new ClientHelper.GetClientFollowList().execute(empid.toString()).get();
            if (result.isEmpty()) {
                result = new ClientHelper.GetClientFollowList().execute(empid.toString()).get();

            } else {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ClientDetailModel>>() {
                }.getType();
                clientlist = new Gson().fromJson(result, listType);
                adapterClientList = new ClientListAdapter(HomePage.this, clientlist);
                recyclerView.setAdapter(adapterClientList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            final AlertDialog.Builder alertbox = new AlertDialog.Builder(HomePage.this);//R.style.AlertDialogTheme (Sakshee change alert)
            alertbox.setIcon(R.drawable.close_blue);
            alertbox.setIconAttribute(90);
            alertbox.setTitle("Do You Want To Exit ?");
            alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // finish used for destroyed activity
                    finishAffinity();
                    System.exit(0);
                }
            });

            alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // Nothing will be happened when clicked on no button
                    // of Dialog
                }
            });
            alertbox.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        menuItem = menu.findItem(R.id.admin1);
        View actionView = MenuItemCompat.getActionView(menuItem);
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);
        notification = (ImageView) actionView.findViewById(R.id.notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomePage.this, NotificationActivity.class);
                intent.putExtra("TeamLID",teamleaderid);
                startActivity(intent);
            }
        });
        GetLeaderOrNot();
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            AlertDialog.Builder alertbox = new AlertDialog.Builder(HomePage.this);
            alertbox.setCancelable(false);
            alertbox.setIcon(R.drawable.ic_lock_open_black_24dp);
            alertbox.setTitle("Are you sure, You want to logout?");
            alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // finish used for destroyed activity
                    try {
                        progressDialog.show();
                        SharedPrefManager.getInstance(HomePage.this).logout();
                        Intent intent = new Intent(HomePage.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }


                }
            });

            alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // Nothing will be happened when clicked on no button
                    // of Dialog
                }
            });
            alertbox.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
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
                progressDialog.dismiss();
            }
        }, 1000);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lead) {
            showCustomLoadingDialog();
            Intent intent = new Intent(this, ClientHistory.class);
            startActivity(intent);


            // Handle the camera action
        } else if (id == R.id.nav_follow) {

            Intent intent = new Intent(this, FollowUP.class);
            startActivity(intent);

        } else if (id == R.id.nav_addlead) {
            showCustomLoadingDialog();
            Intent intent = new Intent(HomePage.this, AddClient.class);
            startActivity(intent);

        } else if (id == R.id.nav_followhistory) {
            showCustomLoadingDialog();
            Intent intent = new Intent(HomePage.this, FollowUpHistory.class);
            startActivity(intent);
        } else if (id == R.id.nav_leader) {
            showCustomLoadingDialog();
            Intent intent = new Intent(HomePage.this, MyEmpTeam.class);
            /*        Toast.makeText(getApplicationContext(),teamleaderid,Toast.LENGTH_LONG).show();*/
            intent.putExtra("TeamLeaderID", teamleaderid);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
