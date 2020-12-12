package com.example.studentannouncementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // A Constant for detecting the login intent result
    private static final int RC_SIGN_IN = 26;
    private static final String TAG = "Prashant Agheda";

    // Creating a GoogleSignInClient object
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    // Add Firebase Auth Object
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private TextView mUserName;
    private ImageView mProfilePicture;
    private LoginButton fbLoginButton;
    private SignInButton googleSigninButton;
    private Button signOutButton;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());

        fbLoginButton = findViewById(R.id.fb_login_button);
        fbLoginButton.setReadPermissions("email", "public_profile");
        googleSigninButton = findViewById(R.id.google_sign_in_button);

        // Configure Google Sign In and Add GoogleSignInOptions object
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        // Getting the GoogleSignInClient object from GoogleSignIn class
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        fbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "onSuccess" + loginResult, Toast.LENGTH_SHORT).show();
                handleFacebookToken(loginResult.getAccessToken());
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    passUserData(user);
                    updateUI();
                } else {
                    passUserData(null);
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "onError" + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Auth State Condition Checks
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    passUserData(user);
                } else {
                    passUserData(null);
                }
            }
        };
    }

    // onStart() - Gets executed when the App is Started
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(authStateListener);
    }

    // Function to Sign In
    private void signIn() {
        mGoogleSignInClient.signOut();

        // Getting the google sign in intent
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        // Starting the activity for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookToken(AccessToken token) {
        Toast.makeText(MainActivity.this, "Handle Facebook Token" + token, Toast.LENGTH_SHORT).show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Sign In With Credential Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    passUserData(user);
                } else {
                    Toast.makeText(MainActivity.this, "Sign In With Credential Failed" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Authenticating with firebase
                assert account != null;
                FireBaseGoogleAuthentication(account);

            } catch (ApiException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                FireBaseGoogleAuthentication(null);
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this, "Google Sign In Successful", Toast.LENGTH_SHORT).show();
            assert account != null;
            FireBaseGoogleAuthentication(account);
        } catch (ApiException e) {
            Toast.makeText(MainActivity.this, "Google Sign In Failed", Toast.LENGTH_SHORT).show();
            FireBaseGoogleAuthentication(null);
        }
    }

    private void FireBaseGoogleAuthentication(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Google Login Successful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    passUserData(user);
                    updateUI();
                } else {
                    Toast.makeText(MainActivity.this, "Google Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Adding Username Data to Firebase
    private void passUserData(FirebaseUser user) {
        if (user != null) {
            String userName = user.getDisplayName().toString();
            HashMap<String, Object> map = new HashMap<>();
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
            mDatabaseRef.child("Name").setValue(userName);
        }
    }

    // Update the UI Accordingly
    public void updateUI() {
        Intent intent = new Intent(MainActivity.this, HomepageActivity.class);
        startActivity(intent);
    }

    // onStop() - Gets executed when the App is Stopped
    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(authStateListener);
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
    }
}