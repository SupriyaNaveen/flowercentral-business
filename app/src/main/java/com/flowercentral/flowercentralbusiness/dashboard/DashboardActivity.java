package com.flowercentral.flowercentralbusiness.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.flowercentral.flowercentralbusiness.R;
import com.flowercentral.flowercentralbusiness.login.ui.LauncherActivity;
import com.flowercentral.flowercentralbusiness.order.OrderFragment;
import com.flowercentral.flowercentralbusiness.preference.UserPreference;
import com.flowercentral.flowercentralbusiness.profile.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 17-05-2017.
 */

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    private ActionBarDrawerToggle mToggle;

    @BindView(R.id.nav_view_left)
    NavigationView mNavigationViewLeft;

    private ActionBar mActionBar;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mActionBar = getSupportActionBar();
            if (mActionBar != null) {
                mActionBar.setHomeButtonEnabled(true);
                mActionBar.setDisplayHomeAsUpEnabled(true);
                mActionBar.setDisplayShowHomeEnabled(true);
            }
        }

        mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationViewLeft.setNavigationItemSelectedListener(this);

        //Show the Order navigation option by default.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_content_wrapper, OrderFragment.newInstance()).commit();
        mActionBar.setSubtitle(getString(R.string.nav_item_order));
    }

    /**
     *
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
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (id) {
            case R.id.nav_item_order:
                transaction.replace(R.id.nav_content_wrapper, OrderFragment.newInstance()).commit();
                mActionBar.setSubtitle(getString(R.string.nav_item_order));
                break;

            case R.id.nav_item_sales_dashboard:
                transaction.replace(R.id.nav_content_wrapper, SalesDashboardFragment.newInstance()).commit();
                mActionBar.setSubtitle(getString(R.string.nav_item_sales_dashboard));
                break;

            case R.id.nav_item_profile:
                transaction.replace(R.id.nav_content_wrapper, ProfileFragment.newInstance()).commit();
                mActionBar.setSubtitle(getString(R.string.nav_item_profile));
                break;

            case R.id.nav_item_feedback:
                transaction.replace(R.id.nav_content_wrapper, FeedbackFragment.newInstance()).commit();
                mActionBar.setSubtitle(getString(R.string.nav_item_feedback));
                break;

            case R.id.nav_item_help:
                transaction.replace(R.id.nav_content_wrapper, HelpFragment.newInstance()).commit();
                mActionBar.setSubtitle(getString(R.string.nav_item_help));
                break;

            case R.id.nav_item_logout:
                UserPreference.deleteProfileInformation();
                Intent mIntent = new Intent(DashboardActivity.this, LauncherActivity.class);
                startActivity(mIntent);
                finish();
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
