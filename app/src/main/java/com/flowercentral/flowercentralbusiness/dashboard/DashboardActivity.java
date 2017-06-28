package com.flowercentral.flowercentralbusiness.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.flowercentral.flowercentralbusiness.feedback.FeedbackFragment;
import com.flowercentral.flowercentralbusiness.help.HelpFragment;
import com.flowercentral.flowercentralbusiness.login.ui.LauncherActivity;
import com.flowercentral.flowercentralbusiness.order.OrderFragment;
import com.flowercentral.flowercentralbusiness.profile.ProfileFragment;
import com.flowercentral.flowercentralbusiness.sales.SalesDashboardFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.flowercentral.flowercentralbusiness.preference.UserPreference.deleteProfileInformation;

/**
 *
 */
public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view_left)
    NavigationView mNavigationViewLeft;

    private ActionBar mActionBar;

    /**
     * @param savedInstanceState savedInstanceState
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

        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        mDrawer.setDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationViewLeft.setNavigationItemSelectedListener(this);

        //Show the Order navigation option by default.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_content_wrapper, OrderFragment.newInstance()).commit();
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
     * @param item item
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

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
