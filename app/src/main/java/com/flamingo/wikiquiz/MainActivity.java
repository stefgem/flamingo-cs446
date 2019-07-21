package com.flamingo.wikiquiz;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import static androidx.navigation.Navigation.findNavController;

public class MainActivity extends AppCompatActivity {

    static final long EXIT_DURATION = 100L;
    static final long ENTER_DURATION = 200L;

    BottomNavigationView bottomNavView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavView = findViewById(R.id.bottom_nav_view);

        // bind the NavController to the BottomNavigationView
        NavController navController
                = findNavController(this, R.id.mainNavigationFragment);


        navController.addOnDestinationChangedListener
                (new NavController.OnDestinationChangedListener() {
                    @Override
                    public void onDestinationChanged(@NonNull NavController controller
                            , @NonNull NavDestination destination, @Nullable Bundle arguments) {


                        // if you want a fragment to be fullscreen, just add it to this list
                        final List<Integer> fullscreenFragmentList = new ArrayList<>();
                        fullscreenFragmentList.add(R.id.questionFragment);
                       // fullscreenFragmentList.add(R.id.endQuizFragment);

                        if (fullscreenFragmentList.contains(destination.getId())) {
                            hideBottomNavBar();
                            getSupportActionBar().hide();
                        } else {
                            showBottomNavBar();
                            getSupportActionBar().show();
                        }
                    }
                });

        NavigationUI.setupWithNavController(bottomNavView, navController);
    }

    // ensure that selecting a bottom nav icon is handled by the NavController
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        NavController navController
//                = findNavController(this, R.id.mainNavigationFragment);
//
//        return NavigationUI.onNavDestinationSelected(item, navController) ||
//                super.onOptionsItemSelected(item);
//    }
//
//    // make the Overview back button work with Navigation, instead of always just exiting the app
//    @Override
//    public boolean onSupportNavigateUp() {
//        return findNavController(findViewById(R.id.mainNavigationFragment)).navigateUp();
//    }

    private void hideBottomNavBar() {
        if (bottomNavView.getVisibility() == View.VISIBLE
                && bottomNavView.getAlpha() == 1f) {

            bottomNavView.animate().alpha(0f)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            bottomNavView.setVisibility(View.GONE);
                        }
                    })
                    .setDuration(EXIT_DURATION);
        }
    }

    private void showBottomNavBar() {
        bottomNavView.setVisibility(View.VISIBLE);
        bottomNavView.animate().alpha(1f).setDuration(ENTER_DURATION);
    }
}
