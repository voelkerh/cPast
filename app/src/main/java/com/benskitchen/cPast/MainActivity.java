package com.benskitchen.cPast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import capturingthepast.R;
import com.benskitchen.cPast.domainLogic.RecentCapturesRepository;
import com.benskitchen.cPast.persistence.JsonRecentCapturesStore;
import com.benskitchen.cPast.persistence.RecentCapturesStore;
import com.benskitchen.cPast.ui.fragments.*;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private RecentCapturesRepository recentCapturesRepository;

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        RecentCapturesStore recentCapturesStore = new JsonRecentCapturesStore(this.getApplicationContext());
        this.recentCapturesRepository = new RecentCapturesRepository(recentCapturesStore);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(recentCapturesRepository)).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(recentCapturesRepository)).commit();
                break;
            case R.id.nav_photos:
                openGallery();
                break;
            case R.id.nav_notes:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotesFragment(recentCapturesRepository)).commit();
                break;
            case R.id.nav_help:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpFragment()).commit();
                break;
            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
                break;
            case R.id.nav_exit:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(recentCapturesRepository)).commit();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open gallery", e);
        }
    }
}
