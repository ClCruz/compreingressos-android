package br.com.compreingressos.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import br.com.compreingressos.R;
import br.com.compreingressos.adapter.PageViewAdapter;
import br.com.compreingressos.interfaces.BannerListener;
import br.com.compreingressos.model.Banner;

/**
 * Created by luiszacheu on 08/04/15.
 */
public class MainBannerFragment extends Fragment implements BannerListener {

    PageViewAdapter mAdapter;
    ViewPager mPager;
    CirclePageIndicator mIndicator;

    private Handler handler = new Handler();
    private Runnable runnable;
    int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_banner, container, false);

        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator) rootView.findViewById(R.id.indicator);


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPager.getAdapter() == null) {
            mAdapter = new PageViewAdapter(getChildFragmentManager(), initListDummy());
            mPager.setAdapter(mAdapter);

        }

        mIndicator.setViewPager(mPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    public ArrayList<Banner> initListDummy(){
        ArrayList<Banner> banners =  new ArrayList<>();
        for (int i = 0; i < 5 ; i++) {
            Banner banner = new Banner("teste", "teste");

            banners.add(banner);
        }

        return banners;
    }


    @Override
    public void updateBannerAdapter(ArrayList<Banner> listBanner) {
        mAdapter.setListBanners(listBanner);
        mAdapter.notifyDataSetChanged();

        if (handler != null) {
            slideShow();
            handler.postDelayed(runnable, 5000);
        }

        Log.e("------------- >>" , "Atualizou os banners!!!");
    }

    public void slideShow() {
        runnable = new Runnable() {
            public void run() {
                if (position >= mAdapter.getCount()) {
                    position = 0;
                } else {
                    position = position + 1;
                }
                mPager.setCurrentItem(position, true);
                handler.postDelayed(this, 5000);
            }
        };

    }
}
