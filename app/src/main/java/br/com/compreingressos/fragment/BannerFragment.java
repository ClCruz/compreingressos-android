package br.com.compreingressos.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import br.com.compreingressos.R;
import br.com.compreingressos.model.Banner;
import br.com.compreingressos.toolbox.VolleySingleton;
import br.com.compreingressos.widget.CustomNetworkImageView;

/**
 * Created by luiszacheu on 08/04/15.
 */
public class BannerFragment extends Fragment {

    private Banner banner;
    private View view;
    private NetworkImageView imgBanner;

    public static BannerFragment newInstance(Banner banner){
        BannerFragment fragment = new BannerFragment();
        Bundle args = new Bundle();
        args.putSerializable("banner", banner);

        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getArguments().isEmpty() && getArguments().containsKey("banner")) {
            banner = (Banner) getArguments().get("banner");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_visores, container, false);

        imgBanner = (NetworkImageView) rootView.findViewById(R.id.imageView);
        imgBanner.setImageUrl(banner.getImagem(), VolleySingleton.getInstance(getActivity().getApplicationContext()).getImageLoader());


        return rootView;
    }

}
