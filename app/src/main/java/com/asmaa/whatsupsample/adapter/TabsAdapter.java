package com.asmaa.whatsupsample.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.asmaa.whatsupsample.views.ChatFragment;
import com.asmaa.whatsupsample.views.ContactsFragment;
import com.asmaa.whatsupsample.views.GroupFragment;

public class TabsAdapter extends FragmentPagerAdapter {
    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment  fragment = null;
        switch (position)
        {
            case 0:
                fragment = new ChatFragment();
                break;
            case 1:
                fragment = new GroupFragment();
                break;
            case 2:
                fragment=new ContactsFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position)
    {
        String title =null;
        switch (position){
            case 0 :
                title =  "Chats";
                break;
            case 1 :
                title = "Groups";
                break;
            case 2 :
                title= "Contacts";
                break;
        }
        return title;
    }
}
