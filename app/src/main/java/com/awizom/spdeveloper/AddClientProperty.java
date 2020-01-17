package com.awizom.spdeveloper;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.awizom.spdeveloper.Adapter.ClientPropertyListAdapter;
import com.awizom.spdeveloper.Helper.ClientHelper;
import com.awizom.spdeveloper.Model.ClientDetailModel;
import com.awizom.spdeveloper.Model.ClientPropertyModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AddClientProperty extends AppCompatActivity {

    EditText editTextAddress;
    AutoCompleteTextView propertyname;
    EditText propertyarea;
    ImageView imageView;
    Uri picUri;
    br.com.simplepass.loading_button_lib.customViews.CircularProgressButton cirSubmitButton;
    String Check = "";
    Uri outputFileUri;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int IMAGE_RESULT = 200;
    private static int TIMER = 300;
    String clientid = "", name = "", result;
    TextView cname;
    List<String> propertynamelist;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client_property);
        InitView();
    }

    private void InitView() {
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Client's Property");
        toolbar.setBackgroundColor(Color.parseColor("#488586"));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.statusbar_color));
        clientid = getIntent().getStringExtra("ClientID");
        name = getIntent().getStringExtra("Name");
        cname = findViewById(R.id.cname);
        cname.setPaintFlags(cname.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        cname.setText(name);
        propertyname = findViewById(R.id.editTextName);

        propertyarea = findViewById(R.id.editTextPropertyArea);
        editTextAddress = findViewById(R.id.editTextAddress);
        imageView = findViewById(R.id.imagesrc);
        permissions.add(CAMERA);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check = "Image";
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
            }
        });
        cirSubmitButton = findViewById(R.id.cirSubmitButton);
        cirSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostProperty();
            }
        });
        getPropertyName();
    }

    private void PostProperty() {

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
            cirSubmitButton.startAnimation();
            try {
                    result = new ClientHelper.POSTProperty().execute(id, pname, parea, img_str, addres).get();
                    if (result.isEmpty()) {
                        result = new ClientHelper.POSTProperty().execute(id, pname, parea, img_str, addres).get();

                    } else {
                        Intent intent = new Intent(getApplicationContext(), ClientPropertyDetail.class);
                        intent.putExtra("ClientID", id);
                        startActivity(intent);
                        cirSubmitButton.revertAnimation();
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                        (this, android.R.layout.select_dialog_item, propertynamelist);
                propertyname.setThreshold(1);//will start working from first character
                propertyname.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
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


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_RESULT) {
                String filePath = getImageFilePath(data);
                ExifInterface ei = null;
                try {
                    ei = new ExifInterface(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);


                if (filePath != null) {
                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);

                    switch (orientation) {

                        case ExifInterface.ORIENTATION_ROTATE_90:
                            selectedImage = rotateImage(selectedImage, 90);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_180:
                            selectedImage = rotateImage(selectedImage, 180);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_270:
                            selectedImage = rotateImage(selectedImage, 270);
                            break;

                        case ExifInterface.ORIENTATION_NORMAL:
                        default:
                            selectedImage = selectedImage;
                    }
                    if (Check == "Image") {
                        imageView.setImageBitmap(selectedImage);
                    } else {
                        //identityImage.setImageBitmap(selectedImage);
                    }
                }
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
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

}

