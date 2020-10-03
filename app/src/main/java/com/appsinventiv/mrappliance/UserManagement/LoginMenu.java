package com.appsinventiv.mrappliance.UserManagement;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appsinventiv.mrappliance.Models.FacebookProfileModel;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.appsinventiv.mrappliance.Activities.MainActivity;
import com.appsinventiv.mrappliance.Models.User;
import com.appsinventiv.mrappliance.R;
import com.appsinventiv.mrappliance.Utils.CommonUtils;
import com.appsinventiv.mrappliance.Utils.PrefManager;
import com.appsinventiv.mrappliance.Utils.SharedPrefs;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;


import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;

public class LoginMenu extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    Button login;
    TextView signUp;
    CircleImageView google, fb;
    private PrefManager prefManager;
    LinearLayout skip;
    GoogleApiClient apiClient;
    public static GoogleSignInAccount account;
    HashMap<String, User> map = new HashMap<>();
    DatabaseReference mDatabase;
    LoginButton facebook;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private boolean isLoggedIn;
    public static FacebookProfileModel profile;
    //    LoginManager manager;
    EditText username, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        apiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
        fb = findViewById(R.id.fb);
        facebook = findViewById(R.id.facebook);
        skip = findViewById(R.id.skip);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);
        google = findViewById(R.id.google);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.appsinventiv.mrappliance", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//        isLoggedIn = accessToken != null && !accessToken.isExpired();
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));


//        facebook.setReadPermissions("email", "public_profile");
        facebook.setPermissions("email", "public_profile");


        mCallbackManager = CallbackManager.Factory.create();


        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AccessToken accessToken = AccessToken.getCurrentAccessToken();
//                isLoggedIn = accessToken != null && !accessToken.isExpired();
                LoginManager.getInstance().logInWithReadPermissions(LoginMenu.this, Arrays.asList("email", "public_profile"));
                facebook.performClick();
            }
        });


        facebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                Log.d(TAG, "facebook:onSuccess:" + loginResult);
//                handleFacebookAccessToken(loginResult.getAccessToken());

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    LoginMenu.account = null;

                                    String email = object.getString("email");
                                    String firstName = object.getString("name").split(" ")[0];
                                    String lastName = object.getString("name").split(" ")[1];
                                    String id = object.getString("id");
                                    profile = new FacebookProfileModel(id, firstName, lastName, email);
//                                    social = true;

//                                    loginUser(firstName+lastName);
                                    String userOd = profile.getFirstName() + profile.getLastName();
                                    if (map.containsKey(userOd)) {
                                        loginUser(userOd);
                                        LoginManager.getInstance().logOut();
                                    } else {
                                        Intent i = new Intent(LoginMenu.this, Register.class);
                                        i.putExtra("userId", profile.getFirstName() + profile.getLastName());
                                        i.putExtra("email", profile.getId());
                                        startActivity(i);
                                        LoginManager.getInstance().logOut();
                                    }


                                } catch (Exception e) {
                                    CommonUtils.showToast(e.getMessage());
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();

//                profile = Profile.getCurrentProfile();
//
//                String userOd = profile.getFirstName() + profile.getLastName();
//                if (map.containsKey(userOd)) {
//                    loginUser(userOd);
//                    LoginManager.getInstance().logOut();
//                } else {
//                    Intent i = new Intent(LoginMenu.this, Register.class);
//                    i.putExtra("userId", profile.getFirstName() + profile.getLastName());
//                    i.putExtra("email", profile.getId());
//                    startActivity(i);
//                    LoginManager.getInstance().logOut();
//                }
            }

            @Override
            public void onCancel() {
//                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
//                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                CommonUtils.showToast(error.getMessage());
//                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
//                updateUI(null);
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(LoginMenu.this, Login.class));
                if ((username.getText().toString().trim().isEmpty())) {
                    username.setError("Enter username");
                } else if ((password.getText().toString().trim().isEmpty())) {
                    password.setError("Enter password");
                } else {
                    checkUser();
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginMenu.this, Register.class));
            }
        });


        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefs.setIsSkipped("true");
                startActivity(new Intent(LoginMenu.this, MainActivity.class));
                finish();
            }
        });
        getAllUsersFromDB();

    }

    private void checkUser() {
        if (map.containsKey(username.getText().toString())) {
            if (map.get(username.getText().toString()).getPassword().equalsIgnoreCase(password.getText().toString())) {
                loginUser(username.getText().toString());
            } else {
                CommonUtils.showToast("Wrong password");
            }
        } else {
            CommonUtils.showToast("User does not exist\nPlease signup");
        }
    }

    private void getAllUsersFromDB() {
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    map.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {

                            map.put(snapshot.getKey(), user);


                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void signin() {
        Intent i = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(i, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(googleSignInResult);
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d(TAG, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
//        showProgressDialog();
        // [END_EXCLUDE]

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String userOd = profile.getFirstName() + profile.getLastName();
//                            CommonUtils.showToast(task.getResult().getUser().getUid());
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                            if (!isLoggedIn) {
                            LoginManager.getInstance().logOut();
                            AccessToken.setCurrentAccessToken(null);
                            mAuth.signOut();
                            if (map.containsKey(userOd)) {
                                loginUser(userOd);
                            } else {
                                Intent i = new Intent(LoginMenu.this, Register.class);
                                i.putExtra("userId", profile.getFirstName() + profile.getLastName());
                                i.putExtra("email", profile.getId());
                                startActivity(i);
                            }

                        }

//                        } else {
//                            // If sign in fails, display a message to the user.
////                            Log.w(TAG, "signInWithCredential:failure", task.getException());
////                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
////                                    Toast.LENGTH_SHORT).show();
////                            updateUI(null);
//                            CommonUtils.showToast("Error");
//                        }

                        // [START_EXCLUDE]
//                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }


    private void handleResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            account = googleSignInResult.getSignInAccount();
//            e_fullname.setText(account.getDisplayName());

            String userId = account.getEmail().replace("@", "").replace(".", "");
            String email = account.getEmail();
            Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {

                }
            });
            if (map.containsKey(userId)) {
                loginUser(userId);
            } else {
                Intent i = new Intent(LoginMenu.this, Register.class);
                i.putExtra("userId", userId);
                i.putExtra("email", email);
                startActivity(i);

            }
//                loginUser(account.getEmail().replace("@", "").replace(".", ""));
//                Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//
//                    }
//                });
//            } else {
//                Auth.GoogleSignInApi.signOut(apiClient).setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//
//                    }
//                });
//            }
//            CommonUtils.showToast(account.getDisplayName() +"    "+ account.getEmail()+account.get);
        }
    }

    private void loginUser(String userId) {
        SharedPrefs.setUser(map.get(userId));
        SharedPrefs.setIsLoggedIn("yes");
        CommonUtils.showToast("Logged in successfully");

        if (!map.get(userId).isNumberVerified()) {
            Intent i = new Intent(LoginMenu.this, MobileVerification.class);

            startActivity(i);
        } else {
            prefManager.setFirstTimeLaunch(false);

            Intent i = new Intent(LoginMenu.this, MainActivity.class);

            startActivity(i);

            finish();
        }

    }


    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);

        Intent i = new Intent(LoginMenu.this, MainActivity.class);

        startActivity(i);

        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
