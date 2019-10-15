package com.awizom.spdeveloper;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.awizom.spdeveloper.Adapter.ClientListAdapter;
import com.awizom.spdeveloper.Adapter.ClientPropertyListAdapter;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Helper.ProfileHelper;
import com.awizom.spdeveloper.Model.ClientDetailModel;
import com.awizom.spdeveloper.Model.ClientPropertyModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ClientPropertyDetail extends AppCompatActivity {

    String clientid = "", result;
    TextView name, mobno, altmobno, address, email;
    Button addProperty;
    String Check = "";
    Uri picUri;
    Uri outputFileUri;
    ImageView imageView;
    RecyclerView recyclerView;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int IMAGE_RESULT = 200;
    private static int TIMER = 300;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    EditText  editTextAddress;
    AutoCompleteTextView propertyname;
    EditText propertyarea;
    List<ClientPropertyModel> clientpropertylist;
    List<String> propertynamelist;
    ClientPropertyListAdapter clientPropertyListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_property_detail);
        InitView();
    }

    private void InitView() {
        clientid = getIntent().getStringExtra("ClientID");
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Client's Property");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        name = findViewById(R.id.name);
        mobno = findViewById(R.id.mobno);
        altmobno = findViewById(R.id.addmobno);
        address = findViewById(R.id.place);
        email = findViewById(R.id.email);
        addProperty = findViewById(R.id.addProperty);
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.scrollToPosition(0);
        recyclerView.smoothScrollToPosition(0);
        getpropertylist();
        addProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final br.com.simplepass.loading_button_lib.customViews.CircularProgressButton submits;
                final android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(ClientPropertyDetail.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.addproperty, null);
                propertyname = dialogView.findViewById(R.id.editTextName);
                propertyarea = dialogView.findViewById(R.id.editTextPropertyArea);
                editTextAddress = dialogView.findViewById(R.id.editTextAddress);
                imageView = dialogView.findViewById(R.id.imagesrc);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Check = "Image";
                        startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
                    }
                });

                submits = dialogView.findViewById(R.id.cirSubmitButton);
                getPropertyName();
                submits.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                imageView.buildDrawingCache();
                                Bitmap bitmap = imageView.getDrawingCache();
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                                byte[] image = stream.toByteArray();
                                System.out.println("byte array:" + image);
                                String img_str = Base64.encodeToString(image, 0);
                                String pname = propertyname.getText().toString();
                                String parea = propertyarea.getText().toString();
                                String addres = editTextAddress.getText().toString();
                                String id = clientid;

                                if (propertyname.getText().toString().isEmpty()) {
                                    propertyname.setError("Please enter property Name");
                                    propertyname.requestFocus();
                                } else if (propertyarea.getText().toString().isEmpty()) {
                                    propertyarea.setError("Please enter property Name");
                                    propertyarea.requestFocus();
                                } else if (editTextAddress.getText().toString().isEmpty()) {
                                    editTextAddress.setError("Please enter valid Address");
                                    editTextAddress.requestFocus();
                                } else {
                                    submits.startAnimation();
                                    try {
                                        {
                                            result = new ClientHelper.POSTProperty().execute(id, pname, parea, img_str, addres).get();
                                            if (result.isEmpty()) {
                                                result = new ClientHelper.POSTProperty().execute(id, pname, parea, img_str, addres).get();

                                            } else {
                                                Intent intent = new Intent(getApplicationContext(), ClientPropertyDetail.class);
                                                intent.putExtra("ClientID", id);
                                                startActivity(intent);
                                                Gson gson = new Gson();
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
        });
        getCLientdetails();
    }

    private void getPropertyName() {
        try {
            result = new ClientHelper.GetClientPropertyNameList().execute().get();
            if (result.isEmpty()) {
                result = new ClientHelper.GetClientPropertyNameList().execute().get();
            } else {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<String>>() {
                }.getType();
                propertynamelist = new Gson().fromJson(result, listType);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (this,android.R.layout.select_dialog_item,propertynamelist);
                propertyname.setThreshold(1);//will start working from first character
                propertyname.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                //  Toast.makeText(getApplicationContext(), result + " Success", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getpropertylist() {
        try {
            result = new ClientHelper.GetClientPropertyList().execute(clientid.toString()).get();
            if (result.isEmpty()) {
                result = new ClientHelper.GetClientPropertyList().execute(clientid.toString()).get();
            } else {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<ClientPropertyModel>>() {
                }.getType();
                clientpropertylist = new Gson().fromJson(result, listType);
                clientPropertyListAdapter = new ClientPropertyListAdapter(ClientPropertyDetail.this, clientpropertylist);
                recyclerView.setAdapter(clientPropertyListAdapter);
                //  Toast.makeText(getApplicationContext(), result + " Success", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Intent getPickImageChooserIntent() {
        outputFileUri = getCaptureImageOutputUri();
        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 201 && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            email.setText(accountName);
        }


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_RESULT) {
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                    if (Check == "Image") {
                        imageView.setImageBitmap(selectedImage);
                    } else {
                        //identityImage.setImageBitmap(selectedImage);
                    }
                }
            }
        }
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;
        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());
    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void getCLientdetails() {
        try {
            result = new ClientHelper.GetClientDetail().execute(clientid.toString()).get();
            if (result.isEmpty()) {
                result = new ClientHelper.GetClientDetail().execute(clientid.toString()).get();
            } else {
                Gson gson = new Gson();
                Type listType = new TypeToken<ClientDetailModel>() {
                }.getType();
                ClientDetailModel cdetail = new Gson().fromJson(result, listType);
                name.setText(String.valueOf(cdetail.getName()));
                email.setText(String.valueOf(cdetail.getEmail()));
                mobno.setText(String.valueOf(cdetail.getMobNo()));
                altmobno.setText(String.valueOf(cdetail.getAltMobNo()));
                address.setText(String.valueOf(cdetail.getAddress()));
                //  Toast.makeText(getApplicationContext(), result + " Success", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
