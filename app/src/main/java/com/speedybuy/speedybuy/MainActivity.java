package com.speedybuy.speedybuy;


import static com.speedybuy.speedybuy.DBqueries.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.speedybuy.speedybuy.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends  AppCompatActivity
                implements NavigationView.OnNavigationItemSelectedListener{


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 1;
    private static final int ORDERS_FRAGMENT = 2;
    private static final int ACCOUNT_FRAGMENT = 3;
    private static final int WISHLIST_FRAGMENT = 4;
    private static final int REWARDS_FRAGMENT = 5;
    private static final int ONLINE_HELP = 6;
    public static Boolean showCart = false;
    public static Activity mainActivity;
    public static boolean resetMainActivity = false;

    private FrameLayout frameLayout;
    private ImageView actionBarLogo;
    private int currentFragment = -1;
    private NavigationView navigationView;

    private Window window;
    private Toolbar toolbar;
    private Dialog signInDialog;
    private FirebaseUser currentUser;

    private TextView badgeCount;

    private int scrollFlags;
    private AppBarLayout.LayoutParams params;
    private CircleImageView profileView;
    private TextView fullname,email;
    private ImageView addProfileIcon;

    public static DrawerLayout drawer;
    private boolean setSignUpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = (Toolbar) findViewById(R.id.toolbarn);
//         actionBarLogo = findViewById(R.id.actionbar_logo);
        setSupportActionBar(binding.appBarMain.toolbar);
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

//        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        //scrollFlags = params.getScrollFlags();

        drawer = findViewById(R.id.drawer_layout);

        DrawerLayout drawer = binding.drawerLayout;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        navigationView.setNavigationItemSelectedListener(this);

        /*NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_n);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);*/

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        frameLayout = findViewById(R.id.main_framelayout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        profileView = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.main_profile_image);
        fullname = (TextView) navigationView.getHeaderView(0).findViewById(R.id.main_fullname);
        email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.main_email);
        addProfileIcon = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.add_profile);

        if (showCart) {
            mainActivity = this;
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            gotoFragment("My Cart", new MyCartFragment(), -2);
        } else {
           ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, binding.appBarMain.toolbar, R.string.navigation_drawer_open , R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            setFragment(new HomeFragment(), HOME_FRAGMENT);
        }

        signInDialog = new Dialog(MainActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.ok_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.cancel_btn);
        Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);

            }
        });
        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = true;
                startActivity(registerIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
        }else{
            if(DBqueries.email == null) {
                FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DBqueries.fullname = task.getResult().getString("fullname");
                                    DBqueries.email = task.getResult().getString("email");
                                    DBqueries.profile = task.getResult().getString("profile");

                                    fullname.setText(DBqueries.fullname);
                                    email.setText(DBqueries.email);
                                        // Do something

                                        if (DBqueries.profile.equals("")) {
                                            addProfileIcon.setVisibility(View.VISIBLE);
                                        } else {
                                            addProfileIcon.setVisibility(View.INVISIBLE);
                                            Glide.with(MainActivity.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.profile)).into(profileView);
                                        }

                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else {

                fullname.setText(DBqueries.fullname);
                email.setText(DBqueries.email);
                if (DBqueries.profile.equals("")) {
                    profileView.setImageResource(R.drawable.profile);
                    //addProfileIcon.setVisibility(View.VISIBLE);
                } else {
                    if (addProfileIcon != null) {
                        addProfileIcon.setVisibility(View.VISIBLE);
                        Glide.with(MainActivity.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.drawable.profile)).into(profileView);
                    }
                }
            }
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
        }
        if (resetMainActivity){
            resetMainActivity = false;
            actionBarLogo.setVisibility(View.INVISIBLE);
            setFragment(new HomeFragment(), HOME_FRAGMENT);
            navigationView.getMenu().getItem(0).setCheckable(true);
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DBqueries.checkNotification(true,null);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            if (currentFragment == HOME_FRAGMENT){
                currentFragment = -1;
                super.onBackPressed();
            }else {
                if (showCart){
                    mainActivity = null;
                    showCart = false;
                    finish();
                }else {
                    //   actionBarLogo.setVisibility(View.VISIBLE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setCheckable(true);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (currentFragment == HOME_FRAGMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);

            /*MenuItem cartItem = menu.findItem(R.id.main_cart_icon);
            cartItem.setActionView(R.layout.badge_layout);
                ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
                badgeIcon.setImageResource(R.drawable.shopping_white);
                badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);
                if (currentUser != null){

                    if (DBqueries.cartList.size() == 0) {
                        DBqueries.loadCartList(MainActivity.this, new Dialog(MainActivity.this), false,badgeCount,new TextView(MainActivity.this));
                    }else{
                        badgeCount.setVisibility(View.VISIBLE);
                        if (DBqueries.cartList.size() < 99) {
                            badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                        }else{
                            badgeCount.setText("99");
                        }
                    }
                }

                cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentUser == null) {
                            signInDialog.show();
                        }else {
                            gotoFragment("My cart", new MyCartFragment(), CART_FRAGMENT);
                        }
                    }
                });*/

            MenuItem notifyItem = menu.findItem(R.id.main_notification_icon);
            notifyItem.setActionView(R.layout.badge_layout);
            ImageView notifyIcon = notifyItem.getActionView().findViewById(R.id.badge_icon);
            notifyIcon.setImageResource(R.drawable.notification);
            TextView notifyCount = notifyItem.getActionView().findViewById(R.id.badge_count);
            if (currentUser != null) {
                DBqueries.checkNotification(false,notifyCount);
            }

            notifyItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        Intent notificationIntent = new Intent(MainActivity.this, NotificationActivity.class);
                        startActivity(notificationIntent);
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.main_search_icon) {
           Intent searchIntent = new Intent(this, SearchActivity.class);
           startActivity(searchIntent);
            return true;
        }else if (id == R.id.main_notification_icon){
           Intent notificationIntent = new Intent(this, NotificationActivity.class);
           startActivity(notificationIntent);
            return true;
        } /*else if (id == R.id.main_cart_icon){
            if (currentUser == null) {
                signInDialog.show();
            }else {
                gotoFragment("My cart", new MyCartFragment(), CART_FRAGMENT);
            }
            return true;
        }*/else if (id == android.R.id.home){
            if (showCart){
                mainActivity = null;
                showCart = false;
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoFragment(String title,Fragment fragment,int fragmentNo) {
//        actionBarLogo.setVisibility(View.GONE);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
            setFragment(fragment,fragmentNo);
        if (fragmentNo != CART_FRAGMENT || showCart) {
            navigationView.getMenu().getItem(4).setChecked(false);
//           params.setScrollFlags(0);
        }else {
          //  params.setScrollFlags(scrollFlags);
        }
    }
   
MenuItem menuItem;
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_n);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer =(DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        menuItem = item;

        if (currentUser != null){
            drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    int id = menuItem.getItemId();
                    if (id == R.id.nav_speedybuy) {
                       // actionBarLogo.setVisibility(View.VISIBLE);
                        invalidateOptionsMenu();
                        setFragment(new HomeFragment(), HOME_FRAGMENT);
                    } else if (id == R.id.nav_favorite) {
                        gotoFragment("My Wishlist", new MyWishlistFragment(), WISHLIST_FRAGMENT);
                    } /*else if (id == R.id.nav_my_cart) {
                        gotoFragment("My Cart", new MyCartFragment(), CART_FRAGMENT);
                    } */else if (id == R.id.nav_online_help) {
//                        Intent regisIntent = new Intent(MainActivity.this, HelpActivity.class);
//                        startActivity(regisIntent);
//                        finish();
                        gotoFragment("Online Help", new OnlineHelpFragment(),ONLINE_HELP);
                    } else if (id == R.id.nav_my_account) {
                        gotoFragment("My Account", new MyAccountFragment(), ACCOUNT_FRAGMENT);
                    } /*else if (id == R.id.nav_my_order) {
                        gotoFragment("My Order", new MyOrdersFragment(), ORDERS_FRAGMENT);
                    } else if (id == R.id.nav_my_wishlist) {

                    } else if (id == R.id.nav_reword) {
                        gotoFragment("My Rewards", new MyRewardsFragment(), REWARDS_FRAGMENT);
                    } */else if (id == R.id.nav_sign_out) {
                        FirebaseAuth.getInstance().signOut();
                        DBqueries.clearData();
                        Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(registerIntent);
                        finish();
                    }
                    drawer.removeDrawerListener(this);
                }
            });
            return true;
        }else{
            signInDialog.show();
            return false;
        }
    }


    private void setFragment(Fragment fragment,int fragmentNo) {
        if (fragmentNo != currentFragment) {
            if (fragmentNo == REWARDS_FRAGMENT){
                window.setStatusBarColor(Color.parseColor("#5B04B1"));
                toolbar.setBackgroundColor(Color.parseColor("#5B04B1"));
            }else {
                window.setStatusBarColor(getResources().getColor(R.color.teal_200));
            }
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }

}