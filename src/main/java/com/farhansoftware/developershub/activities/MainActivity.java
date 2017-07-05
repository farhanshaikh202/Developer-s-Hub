package com.farhansoftware.developershub.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.farhansoftware.developershub.R;
import com.farhansoftware.developershub.adapters.MainScreenPagerAdapter;
import com.farhansoftware.developershub.adapters.WizardPagerAdapter;
import com.farhansoftware.developershub.config.Server;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences pref;
    private TabLayout tabs;
    private ViewPager pager;
    private NavigationView navigationView;
    private FloatingActionButton fab;
    private MainScreenPagerAdapter padapter;
    private String mtitle="Top";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainscreen_toolbar);
        setSupportActionBar(toolbar);


        pref= PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getBoolean("firstlaunch",true)){
            finish();
            startActivity(new Intent(this,wizard.class));

        }
        else if(pref.getBoolean("loginskipped",true)){
            finish();
            startActivity(new Intent(this,Startup.class));
        }

        tabs=(TabLayout)findViewById(R.id.mainscreen_tabs);
        pager=(ViewPager)findViewById(R.id.mainscreen_viewpager);
        tabs.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==0)getSupportActionBar().setTitle(mtitle);
                else getSupportActionBar().setTitle("Categories");
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        padapter=new MainScreenPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(padapter);

        tabs.getTabAt(0).setIcon(R.drawable.ic_main_hot);
        tabs.getTabAt(1).setIcon(R.drawable.ic_main_category);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pref.getBoolean("islogin", false)) {
                    startActivity(new Intent(MainActivity.this, CreatePost.class));
                }else {
                    startActivity(new Intent(MainActivity.this, Startup.class));
                }
            }
        });


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position==0)fab.setVisibility(View.VISIBLE);
                else fab.setVisibility(View.GONE);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setHeaderProfileView();
    }

    private void setHeaderProfileView() {
        View root=navigationView.getHeaderView(0).findViewById(R.id.mainscreen_top);
        if (!pref.getBoolean("islogin",false)) {
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, Startup.class));
                }
            });
        } else {
            //logged in
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(MainActivity.this, MyProfile.class);
                    in.putExtra(Server.PARAM_USER_ID,pref.getString(Server.PARAM_USER_ID,"0"));
                    startActivity(in);
                }
            });

            TextView pName = (TextView) root.findViewById(R.id.mainscreen_user_name);
            TextView pEmail = (TextView) root.findViewById(R.id.mainscreen_user_email);
            CircleImageView pPic = (CircleImageView) root.findViewById(R.id.mainscreen_user_pic);

            Picasso.with(this).load(pref.getString(Server.PARAM_USER_PHOTO,"")).into(pPic);
            pName.setText(pref.getString(Server.PARAM_USERNAME,""));
            pEmail.setText(pref.getString(Server.PARAM_EMAIL,""));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeaderProfileView();
    }

    @Override
    protected void onStart() {//works exit
        overridePendingTransition( 0,android.R.anim.slide_out_right);
        super.onStart();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_top) {
            pager.setCurrentItem(0,true);
            padapter.getMainPostsFragment().externalCall("bylike");
            mtitle="Top";
            getSupportActionBar().setTitle(mtitle);
        } else if (id == R.id.nav_category) {
            pager.setCurrentItem(1,true);
        } else if (id == R.id.nav_trending) {
            pager.setCurrentItem(0,true);
            padapter.getMainPostsFragment().externalCall("byrating");
            mtitle="Trending";
            getSupportActionBar().setTitle(mtitle);
        } else if (id == R.id.nav_latest) {
            pager.setCurrentItem(0,true);
            padapter.getMainPostsFragment().externalCall("bytime");
            mtitle="Latest";
            getSupportActionBar().setTitle(mtitle);
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this,AboutUs.class));
        }else if (id==R.id.nav_manage_cate){
            startActivity(new Intent(MainActivity.this,CategorySelection.class));
        } else if(id==R.id.nav_all_cate){
            padapter.getMainPostsFragment().allCategories();
        }else if(id==R.id.nav_selected_cate){
            padapter.getMainPostsFragment().selectedCategories();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
