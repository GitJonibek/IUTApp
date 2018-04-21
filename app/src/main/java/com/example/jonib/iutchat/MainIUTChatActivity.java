package com.example.jonib.iutchat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jonib.iutchat.adapter.SectionsPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainIUTChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolBar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private DatabaseReference mUserRef;

    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_iutchat);

        mAuth = FirebaseAuth.getInstance();
        mToolBar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.app_name));

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        // Tabs
        mViewPager = findViewById(R.id.main_viewpager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));

    }

    private void sendToStart() {
        Intent startActivity = new Intent(MainIUTChatActivity.this, StartActivity.class);
        startActivity(startActivity);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            sendToStart();
        }else{
            mUserRef.child("online").setValue(true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserRef.child("online").setValue(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int i = item.getItemId();

        if (i == R.id.main_logout_btn) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        else if(i == R.id.main_settings){
            Intent setting = new Intent(MainIUTChatActivity.this, SettingsActivity.class);
            startActivity(setting);
        }
        else if(i == R.id.main_all_users_btn){
            Intent setting = new Intent(MainIUTChatActivity.this, UsersActivity.class);
            startActivity(setting);
        }

        return true;
    }
}
