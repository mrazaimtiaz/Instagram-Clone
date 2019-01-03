package com.example.toshiba.instagramclone.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionsStatePagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragementlist = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragement = new HashMap<>();
    private final HashMap<String, Integer> mFragmentNumbers = new HashMap<>();
    private final HashMap<Integer, String> mFragmentNames = new HashMap<>();

    public SectionsStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragementlist.get(position);
    }

    @Override
    public int getCount() {
        return mFragementlist.size();
    }
    public void addFragment(Fragment fragment,String fragmentName){
        mFragementlist.add(fragment);
        mFragement.put(fragment,mFragementlist.size()-1);
        mFragmentNumbers.put(fragmentName,mFragementlist.size()-1);
        mFragmentNames.put(mFragementlist.size()-1,fragmentName);

    }

    /**
     * return the fragment with name @param
     * @param fragmentName
     * @return
     */
    public Integer getFragmentNumber(String fragmentName){
        if (mFragmentNumbers.containsKey(fragmentName)){
           return mFragmentNumbers.get(fragmentName);
        }
        else {
            return null;
        }

    }

    /**
     * return the fragment with name @param
     * @param fragment
     * @return
     */
    public Integer getFragmentNumber(Fragment fragment){
        if (mFragmentNumbers.containsKey(fragment)){
            return mFragmentNumbers.get(fragment);
        }
        else {
            return null;
        }

    }

    /**
     * return the fragment with name @param
     * @param fragmentNumber
     * @return
     */
    public String getFragmentName(Integer fragmentNumber){
        if (mFragmentNames.containsKey(fragmentNumber)){
            return mFragmentNames.get(fragmentNumber);
        }
        else {
            return null;
        }

    }
}
