package br.com.compreingressos.adapter;

/**
 * Created by luiszacheu on 08/04/15.
 */


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;

import br.com.compreingressos.fragment.BannerFragment;
import br.com.compreingressos.model.Banner;

public class PageViewAdapter extends FragmentStatePagerAdapter {

    private static final String LOG_TAG = PageViewAdapter.class.getSimpleName();

    private List<Banner> mListBanners = Collections.emptyList();

    public PageViewAdapter(FragmentManager fm, ArrayList<Banner> listBanners) {
        super(fm);
        this.mListBanners = listBanners;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return BannerFragment.newInstance(mListBanners.get(position % mListBanners.size()));
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
            return null;
        }
    }

    public void setListBanners(ArrayList<Banner> listBanners){
        this.mListBanners = listBanners;
    }

    @Override
    public int getCount() {
        return mListBanners.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }


    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        FragmentManager manager = ((Fragment) object).getFragmentManager();
//        FragmentTransaction trans = manager.beginTransaction();
//        trans.remove((Fragment) object);
//        trans.commit();
        super.destroyItem(container, position, object);
    }
}