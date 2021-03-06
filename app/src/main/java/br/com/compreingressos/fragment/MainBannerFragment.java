package br.com.compreingressos.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import br.com.compreingressos.R;
import br.com.compreingressos.adapter.PageViewAdapter;
import br.com.compreingressos.interfaces.BannerListener;
import br.com.compreingressos.model.Banner;
import br.com.compreingressos.utils.AndroidUtils;
import br.com.compreingressos.utils.ConnectionUtils;

/**
 * Created by luiszacheu on 08/04/15.
 */
public class MainBannerFragment extends Fragment implements BannerListener {

    private static final String LOG_TAG = MainBannerFragment.class.getSimpleName();

    private PageViewAdapter mAdapter;
    private ViewPager mPager;
    private CirclePageIndicator mIndicator;
    private FrameLayout viewBanner;
    private ImageView placeholderView;

    private Handler handler = new Handler();
    private Runnable runnable;
    int position = 0;
    public ProgressBar progressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_banner, container, false);


        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mIndicator = (CirclePageIndicator) rootView.findViewById(R.id.indicator);
        viewBanner = (FrameLayout) rootView.findViewById(R.id.view_banner);
        placeholderView = (ImageView) rootView.findViewById(R.id.placeholder);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ConnectionUtils.isInternetOn(getActivity())){
            if (mPager.getAdapter() == null) {
                mAdapter = new PageViewAdapter(getChildFragmentManager(), initList());
                mPager.setAdapter(mAdapter);
                mIndicator.setViewPager(mPager);
            }
        }else{
            progressBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacks(runnable);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", position);
        super.onSaveInstanceState(outState);
    }



    public ArrayList<Banner> initList(){
        ArrayList<Banner> banners =  new ArrayList<>();
        Banner banner = new Banner("empty", "empty");
        banners.add(banner);
        return banners;
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void updateBannerAdapter(ArrayList<Banner> listBanner) {
        if (listBanner != null){
            if (mAdapter != null){
                mAdapter.setListBanners(listBanner);
                mAdapter.notifyDataSetChanged();

                if (listBanner.size() > 0){
                    viewBanner.setVisibility(View.VISIBLE);
                    placeholderView.setVisibility(View.GONE);
                    if (AndroidUtils.isHoneyCombOrNewer())
                        placeholderView.animate().alpha(1.0f);
                }

                if (handler != null) {
                    slideShow();
                    handler.postDelayed(runnable, 5000);
                }
            }


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
