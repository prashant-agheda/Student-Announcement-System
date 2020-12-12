package com.example.studentannouncementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.studentannouncementsystem.fragments.EditProfileFragment;
import com.example.studentannouncementsystem.fragments.HomeFragment;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomepageActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_header_main);

        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_edit_profile)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_sign_out) {
                    View view = findViewById(R.id.nav_sign_out);
                    String message = "Signed Out Successfully";
                    int duration = Snackbar.LENGTH_SHORT;
                    showSnackBar(view, message, duration);

                    Intent profileIntent = new Intent(HomepageActivity.this, MainActivity.class);
                    startActivity(profileIntent);
                    // FacebookSdk.sdkInitialize(getApplicationContext());
                    LoginManager.getInstance().logOut();
                    mGoogleSignInClient.signOut();
                    finish();
                } else if (id == R.id.nav_edit_profile) {
                    View view = findViewById(R.id.nav_edit_profile);
                    String message = "Edit Profile";
                    int duration = Snackbar.LENGTH_SHORT;
                    showSnackBar(view, message, duration);

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragmentContainer, new EditProfileFragment());
                    ft.commit();
                    drawer.close();
                } else if (id == R.id.nav_home) {
                    View view = findViewById(R.id.nav_sign_out);
                    String message = "Home";
                    int duration = Snackbar.LENGTH_SHORT;
                    showSnackBar(view, message, duration);

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragmentContainer, new HomeFragment());
                    ft.commit();
                    drawer.close();
                }

                return true;
            }
        });
    }

    public void showSnackBar(View view, String message, int duration)
    {
        Snackbar.make(view, message, duration).show();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}