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
import android.support.v4.view.ViewPager;
import android.view.View;

import br.com.compreingressos.fragment.BannerFragment;
import br.com.compreingressos.model.Banner;

public class PageViewAdapter extends FragmentStatePagerAdapter {

    private static final String LOG_TAG = PageViewAdapter.class.getSimpleName();

    private int mCount;
    private List<Banner> mListBanners = Collections.emptyList();
    private FragmentManager fm = null;

    public PageViewAdapter(FragmentManager fm, ArrayList<Banner> listBanners) {
        super(fm);
        this.mListBanners = listBanners;
        this.mCount = listBanners.size();
        this.fm = fm;

    }

    @Override
    public Fragment getItem(int position) {
        try {
            return BannerFragment.newInstance(mListBanners.get(position % mListBanners.size()));
//            fm.beginTransaction().commitAllowingStateLoss();

        } catch (Exception e) {
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

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }


}