package br.com.compreingressos.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import br.com.compreingressos.R;
import br.com.compreingressos.adapter.PageViewAdapter;
import br.com.compreingressos.interfaces.BannerListener;
import br.com.compreingressos.model.Banner;
import br.com.compreingressos.utils.ConnectionUtils;

/**
 * Created by luiszacheu on 08/04/15.
 */
public class MainBannerFragment extends Fragment implements BannerListener {

    PageViewAdapter mAdapter;
    ViewPager mPager;
    CirclePageIndicator mIndicator;
    FrameLayout viewBanner;

    private Handler handler = new Handler();
    private Runnable runnable;
    int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_banner, container, false);


        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator) rootView.findViewById(R.id.indicator);
        viewBanner = (FrameLayout) rootView.findViewById(R.id.view_banner);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ConnectionUtils.isInternetOn(getActivity())){
            if (mPager.getAdapter() == null) {
                mAdapter = new PageViewAdapter(getChildFragmentManager(), initList());
                mPager.setAdapter(mAdapter);
            }

            mIndicator.setViewPager(mPager);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPager.getAdapter() == null) {
            if (handler != null) {
                handler.removeCallbacks(runnable);
                handler = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPager.getAdapter() == null) {
            if (handler != null) {
                handler.removeCallbacks(runnable);
                handler = null;
            }
        }
    }

    public ArrayList<Banner> initList(){
        ArrayList<Banner> banners =  new ArrayList<>();
        Banner banner = new Banner("empty", "empty");
        banners.add(banner);

        return banners;
    }


    @Override
    public void updateBannerAdapter(ArrayList<Banner> listBanner) {
        mAdapter.setListBanners(listBanner);
        mAdapter.notifyDataSetChanged();

        if (listBanner.size() > 0){
            viewBanner.setVisibility(View.VISIBLE);
        }

        if (handler != null) {
            slideShow();
            handler.postDelayed(runnable, 5000);
        }

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
