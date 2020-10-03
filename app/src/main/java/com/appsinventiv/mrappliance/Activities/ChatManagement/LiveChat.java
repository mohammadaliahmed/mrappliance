package com.appsinventiv.mrappliance.Activities.ChatManagement;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.appsinventiv.mrappliance.Models.AdminModel;
import com.appsinventiv.mrappliance.Models.ChatModel;
import com.appsinventiv.mrappliance.R;
import com.appsinventiv.mrappliance.Utils.CommonUtils;
import com.appsinventiv.mrappliance.Utils.CompressImage;
import com.appsinventiv.mrappliance.Utils.Constants;
import com.appsinventiv.mrappliance.Utils.GifSizeFilter;
import com.appsinventiv.mrappliance.Utils.Glide4Engine;
import com.appsinventiv.mrappliance.Utils.NotificationAsync;
import com.appsinventiv.mrappliance.Utils.NotificationObserver;
import com.appsinventiv.mrappliance.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LiveChat extends AppCompatActivity implements NotificationObserver {

    DatabaseReference mDatabase;
    EditText message;
    FloatingActionButton send;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ChatAdapter adapter;
    ArrayList<ChatModel> chatModelArrayList = new ArrayList<>();
    int soundId;
    SoundPool sp;
    String adminFcmKey;
    boolean noData = true;
    String foneNumber;
    ImageView back;


    RelativeLayout attachArea;
    ImageView attach;
    ImageView pick, document;
    boolean isAttachAreaVisible = false;
    private static final int REQUEST_CODE_FILE = 25;

    List<Uri> mSelected = new ArrayList<>();
    ArrayList<String> imageUrl = new ArrayList<>();
    StorageReference mStorageRef;
    private static final int REQUEST_CODE_CHOOSE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getPermissions();


        mDatabase = FirebaseDatabase.getInstance().getReference();
        send = findViewById(R.id.send);
        back = findViewById(R.id.back);
        message = findViewById(R.id.message);

        attach = findViewById(R.id.attach);
        attachArea = findViewById(R.id.attachArea);

        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(LiveChat.this, R.raw.tick_sound, 1);
        pick = findViewById(R.id.pick);
        document = findViewById(R.id.document);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachArea.setVisibility(View.GONE);
                isAttachAreaVisible = false;
                openFile(REQUEST_CODE_FILE);
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAttachAreaVisible) {
                    attachArea.setVisibility(View.GONE);
                    isAttachAreaVisible = false;
                } else {
                    attachArea.setVisibility(View.VISIBLE);
                    isAttachAreaVisible = true;
                }
            }
        });

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attachArea.setVisibility(View.GONE);
                isAttachAreaVisible = false;

                mSelected.clear();
                imageUrl.clear();
                initMatisse();
            }
        });


    }

    private void initMatisse() {
        Matisse.from(LiveChat.this)
                .choose(MimeType.ofImage(), false)
                .countable(true)
                .maxSelectable(10)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new Glide4Engine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    private void openFile(Integer CODE) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("*/*");
        startActivityForResult(i, CODE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getAdminDetails();
        getMessagesFromServer();
        readAllMessages();
    }

    private void getAdminDetails() {
        mDatabase.child("Settings").child("AdminNumber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    foneNumber = dataSnapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    AdminModel model = dataSnapshot.getValue(AdminModel.class);
                    if (model != null) {
//                        LiveChat.this.setTitle(model.getId());
                        adminFcmKey = model.getFcmKey();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readAllMessages() {
        mDatabase.child("Chats").child(SharedPrefs.getUser().getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel chatModel = snapshot.getValue(ChatModel.class);
                        if (chatModel != null) {
                            if (!chatModel.getUsername().equals(SharedPrefs.getUser().getUsername())) {
                                mDatabase.child("Chats").child(SharedPrefs.getUser().getUsername()).child(chatModel.getId()).child("status").setValue("read");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMessagesFromServer() {
        recyclerView = findViewById(R.id.chats);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(LiveChat.this, chatModelArrayList);
        recyclerView.setAdapter(adapter);

        mDatabase.child("Chats").child(SharedPrefs.getUser().getUsername()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    noData = false;
                    chatModelArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ChatModel model = snapshot.getValue(ChatModel.class);
                        if (model != null) {
                            chatModelArrayList.add(model);
                            recyclerView.scrollToPosition(chatModelArrayList.size() - 1);
                            adapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    noData = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    recyclerView.scrollToPosition(chatModelArrayList.size() - 1);
                }

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (message.getText().length() == 0) {
                    message.setError("Cant send empty message");
                } else {
                    sendMessageToServer(Constants.MESSAGE_TYPE_TEXT, "", "");
                }

            }
        });

    }

    private void sendMessageToServer(final String type, final String url, String extension) {

        final String msg = message.getText().toString();
        message.setText(null);
        final String key = mDatabase.push().getKey();


        mDatabase.child("Chats").child(SharedPrefs.getUser().getUsername()).child(key)
                .setValue(new ChatModel(key,
                        msg,
                        SharedPrefs.getUser().getUsername(),
                        System.currentTimeMillis(),
                        "sending",
                        SharedPrefs.getUser().getUsername(),
                        SharedPrefs.getUser().getFullName(),
                        type.equals(Constants.MESSAGE_TYPE_IMAGE) ? url : "",
                        type.equals(Constants.MESSAGE_TYPE_DOCUMENT) ? url : "",
                        "." + extension,
                        type


                )).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {


                sp.play(soundId, 1, 1, 0, 0, 1);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatModelArrayList.size() - 1);

                mDatabase.child("Chats").child(SharedPrefs.getUser().getUsername()).child(key).child("status").setValue("sent");


                NotificationAsync notificationAsync = new NotificationAsync(LiveChat.this);
                String NotificationTitle = "New message from " + SharedPrefs.getUser().getFullName();
                String NotificationMessage = "";
                if (type.equals(Constants.MESSAGE_TYPE_TEXT)) {
                    NotificationMessage = SharedPrefs.getUser().getUsername() + ": " + msg;
                } else if (type.equals(Constants.MESSAGE_TYPE_IMAGE)) {
                    NotificationMessage = SharedPrefs.getUser().getUsername() + ": \uD83D\uDCF7 Image";
                } else if (type.equals(Constants.MESSAGE_TYPE_AUDIO)) {
                    NotificationMessage = SharedPrefs.getUser().getUsername() + ": \uD83C\uDFB5 Audio";
                } else if (type.equals(Constants.MESSAGE_TYPE_DOCUMENT)) {
                    NotificationMessage = SharedPrefs.getUser().getUsername() + ": \uD83D\uDCC4 Document";
                }
                notificationAsync.execute("ali", adminFcmKey, NotificationTitle, NotificationMessage, "Chat", key);
//                if (noData) {
//                    CommonUtils.sendMessage(foneNumber, "New chat message received on app");
//
//                } else {
//
//                }

            }
        });


    }

    public void putDocument(final Uri path) {
        String imgName = Long.toHexString(Double.doubleToLongBits(Math.random()));

//        Uri file = Uri.fromFile(new File(path));

        mStorageRef = FirebaseStorage.getInstance().getReference();

        StorageReference riversRef = mStorageRef.child("Documents").child(imgName);

        riversRef.putFile(path)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    @SuppressWarnings("VisibleForTests")
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {


                                sendMessageToServer(Constants.MESSAGE_TYPE_DOCUMENT, "" + uri, getMimeType(LiveChat.this, path));
                                String k = mDatabase.push().getKey();
//                                mDatabase.child("Documents").child(k).setValue(new MediaModel(k, Constants.MESSAGE_TYPE_DOCUMENT, "" + uri, System.currentTimeMillis()));

//
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
//                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                sendMessageToServer(Constants.MESSAGE_TYPE_IMAGE, "" + downloadUrl, getMimeType(LiveChat.this, file));
//                        mDatabase.child("Media").push().setValue()
                                String k = mDatabase.push().getKey();
//
                            }
                        });

//                            mDatabase.child("Images").child(k).setValue(new MediaModel(k, Constants.MESSAGE_TYPE_IMAGE, "" + downloadUrl, System.currentTimeMillis()));

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

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE && data != null) {
            mSelected = Matisse.obtainResult(data);
            for (Uri img : mSelected) {

                imageUrl.add(CompressImage.compressImage("" + img, this));
            }
            for (String img : imageUrl) {
                putPictures(img);
            }
        }

        if (requestCode == REQUEST_CODE_FILE && data != null) {
            Uri Fpath = data.getData();
            putDocument(Fpath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSuccess(String chatId) {
        mDatabase.child("Chats").child(SharedPrefs.getUser().getUsername()).child(chatId).child("status").setValue("delivered");
    }

    @Override
    public void onFailure() {

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
