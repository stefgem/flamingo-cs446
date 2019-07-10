package com.flamingo.wikiquiz;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind the NavController to the BottomNavigationView
        NavController navController
                = findNavController(this, R.id.mainNavigationFragment);

        BottomNavigationView bottomNavView = findViewById(R.id.bottom_nav_view);

        NavigationUI.setupWithNavController(bottomNavView, navController);
    }


    // ensure that selecting a bottom nav icon is handled by the NavController
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController
                = findNavController(this, R.id.mainNavigationFragment);

        return NavigationUI.onNavDestinationSelected(item, navController) ||
                super.onOptionsItemSelected(item);
    }

    // make the Overview back button work with Navigation, instead of always just exiting the app
    @Override
    public boolean onSupportNavigateUp() {
        return findNavController(findViewById(R.id.mainNavigationFragment)).navigateUp();
    }
}
