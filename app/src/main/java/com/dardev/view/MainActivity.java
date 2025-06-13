package com.dardev.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import com.dardev.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Fragment fragment = null;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


      View decorView = getWindow().getDecorView();
        int uiOptions =  View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        Drawable background = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.gradient_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setBackgroundDrawable(background);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        fragment = new Home();
                        switchfragment(fragment);
                        break;

                    case R.id.cart:
                        Intent cartIntent = new Intent(MainActivity.this, CartActivity.class);
                        startActivity(cartIntent);
                        break;

                    case R.id.shopping_bag:
                        Intent wishlistIntent = new Intent(MainActivity.this, MyWishlistActivity.class);
                        startActivity(wishlistIntent);
                        break;

//                    case R.id.message:
//                        break;

                    case R.id.user:
                        Intent myAccountIntent = new Intent(MainActivity.this, UserActivity.class);
                        startActivity(myAccountIntent);
                        break;
                }
                return false;
            }
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.home); // change to whichever id should be default
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
//            case R.id.all_categories:
//                all_category();
//                break;
            case R.id.orders:
                my_orders();
                break;
            case R.id.cart:
                Intent cartIntent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(cartIntent);
                break;
            case R.id.wishlist:
                Intent wishlistIntent = new Intent(MainActivity.this, MyWishlistActivity.class);
                startActivity(wishlistIntent);
                break;
            case R.id.account:
                Intent myAccountIntent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(myAccountIntent);
                break;
            case R.id.notifications:
                Intent notificationsIntent = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(notificationsIntent);
                break;
//            case R.id.privacy_policy:
//                break;
//            case R.id.legal:
//                break;
//            case R.id.report:
//                break;
//            case R.id.rate:
//                break;
//            case R.id.share:
//                break;
//            case R.id.logout:
//                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    void cart() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    void all_category() {
        Intent intent = new Intent(this, AllCategoryActivity.class);
        startActivity(intent);
    }

    void my_orders() {
        Intent intent = new Intent(this, MyOrdersActivity.class);
        startActivity(intent);
    }

    void switchfragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.framelayout, fragment).commit();
    }
}