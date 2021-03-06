package com.flowercentral.flowercentralbusiness.dashboard;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.databinding.ActivityDashboardBinding;
import com.flowercentral.flowercentralbusiness.databinding.LayoutAppToolbarBinding;
import com.flowercentral.flowercentralbusiness.feedback.FeedbackFragment;
import com.flowercentral.flowercentralbusiness.help.HelpFragment;
import com.flowercentral.flowercentralbusiness.login.ui.LauncherActivity;
import com.flowercentral.flowercentralbusiness.order.OrderFragment;
import com.flowercentral.flowercentralbusiness.profile.ProfileFragment;
import com.flowercentral.flowercentralbusiness.sales.SalesDashboardFragment;

import static com.flowercentral.flowercentralbusiness.preference.UserPreference.deleteProfileInformation;

/**
 * Application dashboard, which has container to hold
 * Order, Sales details, Profile, Feedback's, Help
 */
public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityDashboardBinding mBinder;

    /**
     * Set up the dashboard view, navigation drawer and toolbar.
     * Load order's details as default content of dashboard.
     *
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        LayoutAppToolbarBinding toolbarBinder = mBinder.dashboard.ltToolbar;
        setSupportActionBar(toolbarBinder.toolbar);

        if (toolbarBinder.toolbar != null) {
            setSupportActionBar(toolbarBinder.toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }

        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(
                this, mBinder.drawerLayout, toolbarBinder.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        mBinder.drawerLayout.setDrawerListener(mToggle);
        mToggle.syncState();

        mBinder.navViewLeft.setNavigationItemSelectedListener(this);

        //Show the Order navigation option by default.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_content_wrapper, OrderFragment.newInstance()).commit();
    }

    /**
     * On back pressed, if navigation drawer is opened then close it.
     * Or else finish the dashboard.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Handles the navigation item click.
     * The corresponding views loaded on each selection.
     *
     * @param item item which is being clicked.
     * @return boolean
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (id) {
            case R.id.nav_item_order:
                transaction.replace(R.id.nav_content_wrapper, OrderFragment.newInstance()).commit();
                break;

            case R.id.nav_item_sales_dashboard:
                transaction.replace(R.id.nav_content_wrapper, SalesDashboardFragment.newInstance()).commit();
                break;

            case R.id.nav_item_profile:
                transaction.replace(R.id.nav_content_wrapper, ProfileFragment.newInstance()).commit();
                break;

            case R.id.nav_item_feedback:
                transaction.replace(R.id.nav_content_wrapper, FeedbackFragment.newInstance()).commit();
                break;

            case R.id.nav_item_help:
                transaction.replace(R.id.nav_content_wrapper, HelpFragment.newInstance()).commit();
                break;

            case R.id.nav_item_logout:
                deleteProfileInformation(this);
                Intent mIntent = new Intent(DashboardActivity.this, LauncherActivity.class);
                startActivity(mIntent);
                finish();
                break;
        }

        mBinder.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
