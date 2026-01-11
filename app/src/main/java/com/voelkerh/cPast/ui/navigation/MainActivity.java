package com.voelkerh.cPast.ui.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.navigation.NavigationView;
import com.voelkerh.cPast.R;
import com.voelkerh.cPast.ui.about.AboutFragment;
import com.voelkerh.cPast.ui.help.HelpFragment;
import com.voelkerh.cPast.ui.home.HomeFragment;
import com.voelkerh.cPast.ui.notes.NotesFragment;

/**
 * Main activity hosting the main navigation flow of the application.
 *
 * <p>This activity provides a drawer-based navigation.
 * It serves as central container for the UI fragments (home, notes, help, about) and calls the gallery app (photos).</p>
 *
 * <p>Navigation actions are handled via a {@link NavigationView}.</p>
 *
 * <p>This activity does not handle any business logic.</p>
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout drawerLayout;

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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (!isHomeFragmentVisible()) {
                    loadFragment(new HomeFragment());
                    navigationView.setCheckedItem(R.id.nav_home);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        if (itemId == R.id.nav_home || itemId == R.id.nav_exit) {
            loadFragment(new HomeFragment());
        } else if (itemId == R.id.nav_photos) {
            openGallery();
        } else if (itemId == R.id.nav_notes) {
            loadFragment(new NotesFragment());
        } else if (itemId == R.id.nav_help) {
            loadFragment(new HelpFragment());
        } else if (itemId == R.id.nav_about) {
            loadFragment(new AboutFragment());
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private boolean isHomeFragmentVisible() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return currentFragment instanceof HomeFragment;
    }

    private void openGallery() {
        try {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setType("image/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open gallery: ", e);
        }
    }

}
