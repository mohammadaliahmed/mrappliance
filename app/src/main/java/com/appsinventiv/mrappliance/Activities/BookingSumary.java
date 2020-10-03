package com.appsinventiv.mrappliance.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appsinventiv.mrappliance.Activities.ChatManagement.LiveChat;
import com.appsinventiv.mrappliance.Adapters.PickedPicturesAdapter;
import com.appsinventiv.mrappliance.Adapters.ServicesBookedAdapter;
import com.appsinventiv.mrappliance.R;
import com.appsinventiv.mrappliance.Services.ChooseServiceOptions;
import com.appsinventiv.mrappliance.Services.ListOfSubServices;
import com.appsinventiv.mrappliance.Utils.CommonUtils;
import com.appsinventiv.mrappliance.Utils.CompressImage;
import com.appsinventiv.mrappliance.Utils.ConnectivityManager;
import com.appsinventiv.mrappliance.Utils.Constants;
import com.appsinventiv.mrappliance.Utils.GifSizeFilter;
import com.appsinventiv.mrappliance.Utils.Glide4Engine;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookingSumary extends AppCompatActivity {

    RecyclerView recyclerview;
    private static final int REQUEST_CODE_CHOOSE = 23;

    ServicesBookedAdapter adapter;
    TextView date, time, buildingType;
    RelativeLayout next;
    TextView serviceType;
    ImageView back;
    LinearLayout pickPictures;
    StorageReference mStorageRef;

    List<Uri> mSelected = new ArrayList<>();
    ArrayList<String> imageUrl = new ArrayList<>();
    public static ArrayList<String> uploadUrlList = new ArrayList();

    PickedPicturesAdapter picsAdapter;
    RecyclerView pickedPictures;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_summary);
        pickPictures = findViewById(R.id.pickPictures);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ServicesBookedAdapter(this, ListOfSubServices.orderList);
        recyclerview.setAdapter(adapter);

        getPermissions();
        back = findViewById(R.id.back);
        next = findViewById(R.id.next);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        buildingType = findViewById(R.id.buildingType);
        serviceType = findViewById(R.id.serviceType);
        serviceType.setText(ListOfSubServices.parentServiceModel.getName());

        date.setText(ChooseServiceOptions.daySelected.replace("\n", " "));
        time.setText(ChooseServiceOptions.timeSelected);
        buildingType.setText(ChooseServiceOptions.buildingType);

        pickedPictures = findViewById(R.id.pickedPictures);
        pickedPictures.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ConnectivityManager.isNetworkConnected(BookingSumary.this)) {
                    startActivity(new Intent(BookingSumary.this, ChooseAddress.class));
                    for (Uri img : mSelected) {

                        imageUrl.add(CompressImage.compressImage("" + img, BookingSumary.this));
                    }
                    for (String img : imageUrl) {
                        putPictures(img);
                    }
                } else {
                    CommonUtils.showToast("Please check internet connection");
                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        pickPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mSelected.clear();
                imageUrl.clear();
                initMatisse();
            }
        });


    }

    private void initMatisse() {
        Matisse.from(this)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .maxSelectable(5)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE && data != null) {
            mSelected = Matisse.obtainResult(data);
            picsAdapter = new PickedPicturesAdapter(BookingSumary.this, mSelected, new PickedPicturesAdapter.PickCallbacks() {
                @Override
                public void onDelete(int position) {
                    mSelected.remove(position);
                    picsAdapter.setItemList(mSelected);
                }
            });
            pickedPictures.setAdapter(picsAdapter);


//            for (Uri img : mSelected) {

//                imageUrl.add(CompressImage.compressImage("" + img, this));
//            }
//            for (String img : imageUrl) {
//                putPictures(img);
//            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    public void putPictures(String path) {
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

        final Uri file = Uri.fromFile(new File(path));

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child("Photos").child(imgName);

        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                uploadUrlList.add("" + downloadUrl);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        CommonUtils.showToast(exception.getMessage() + "");

                    }
                });


    }
    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
