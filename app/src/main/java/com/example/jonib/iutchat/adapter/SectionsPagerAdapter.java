package com.example.jonib.iutchat.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.jonib.iutchat.fragments.ChatsFragment;
import com.example.jonib.iutchat.fragments.FriendsFragment;
import com.example.jonib.iutchat.fragments.RequestsFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) { super(fm); }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;
            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
                default:
                    return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "REQUESTS";
            case 1:
                return "MESSAGES";
            case 2:
                return "FRIENDS";
            default:
                return null;
        }
    }
}
