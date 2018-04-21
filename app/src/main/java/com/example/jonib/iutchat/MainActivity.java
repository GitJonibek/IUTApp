package com.example.jonib.iutchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jonib.iutchat.adapter.ViewPagerAdapter;
import com.example.jonib.iutchat.fragments.AboutFragment;
import com.example.jonib.iutchat.fragments.EventsFragment;
import com.example.jonib.iutchat.fragments.HomeFragment;
import com.example.jonib.iutchat.fragments.MapsFragment;
import com.example.jonib.iutchat.fragments.NotificationFragment;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    private ViewPager viewPager;

    HomeFragment homeFragment;
    EventsFragment eventsFragment;
    NotificationFragment notificationFragment;
    MapsFragment mapsFragment;
    AboutFragment aboutFragment;

    MenuItem prevMenuItem;
    Toolbar toolbar;

    // Navigation drawer
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        switch (menuItem.getItemId()){
                            case R.id.nav_achievements:
                                Toast.makeText( MainActivity.this, "Achievements", Toast.LENGTH_SHORT ).show();
                                return true;
                            case R.id.nav_photos:
                                Toast.makeText( MainActivity.this, "Photos", Toast.LENGTH_SHORT ).show();
                                return true;
                            case R.id.nav_videos:
                                Toast.makeText( MainActivity.this, "Videos", Toast.LENGTH_SHORT ).show();
                                return true;
                            case R.id.nav_eclass:
                                Toast.makeText( MainActivity.this, "E-Class", Toast.LENGTH_SHORT ).show();
                                return true;
                            case R.id.nav_timetable:
                                Toast.makeText( MainActivity.this, "Timetable", Toast.LENGTH_SHORT ).show();
                                return true;
                            case R.id.nav_inha_mail:
                                Toast.makeText( MainActivity.this, "INHA Mail", Toast.LENGTH_SHORT ).show();
                                return true;
                            case R.id.nav_student_ins:
                                Toast.makeText( MainActivity.this, "Student INS", Toast.LENGTH_SHORT ).show();
                                return true;
                            case R.id.nav_online_print:
                                Toast.makeText( MainActivity.this, "Online printing", Toast.LENGTH_SHORT ).show();
                                return true;
                            case R.id.nav_chat:
                                Intent mainChat = new Intent(MainActivity.this, MainIUTChatActivity.class);
                                startActivity(mainChat);
                                return true;
                            case R.id.nav_faq:
                                Toast.makeText( MainActivity.this, "IUT Chat", Toast.LENGTH_SHORT ).show();
                                return true;
                            case R.id.nav_apply:
                                Toast.makeText( MainActivity.this, "IUT Chat", Toast.LENGTH_SHORT ).show();
                                return true;
                        }
                        return true;
                    }
                });

        bottomNavigationView.setOnNavigationItemSelectedListener( new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_events:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.action_notifications:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.action_maps:
                                viewPager.setCurrentItem(3);
                                break;
                            case R.id.action_about_iut:
                                viewPager.setCurrentItem(4);
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }

                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);
    }

    public void initComponents(){
        viewPager = findViewById(R.id.viewpager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        homeFragment = new HomeFragment();
        eventsFragment = new EventsFragment();
        notificationFragment = new NotificationFragment();
        mapsFragment = new MapsFragment();
        aboutFragment = new AboutFragment();

        adapter.addFragment(homeFragment);
        adapter.addFragment(eventsFragment);
        adapter.addFragment(notificationFragment);
        adapter.addFragment(mapsFragment);
        adapter.addFragment(aboutFragment);

        viewPager.setAdapter(adapter);
    }

}