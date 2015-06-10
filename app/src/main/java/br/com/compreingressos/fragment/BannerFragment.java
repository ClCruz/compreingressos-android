package br.com.compreingressos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import br.com.compreingressos.CompreIngressosActivity;
import br.com.compreingressos.R;
import br.com.compreingressos.model.Banner;

/**
 * Created by luiszacheu on 08/04/15.
 */
public class BannerFragment extends Fragment {

    private Banner banner;
    private View view;
    private ImageView imgBanner;
    private ProgressBar progressBar;

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

        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        imgBanner = (ImageView) rootView.findViewById(R.id.imageView);

        Picasso.with(getActivity())
                .load(banner.getImagem())
                .into(imgBanner, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                    }
                });

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CompreIngressosActivity.class);
                intent.putExtra("u", banner.getUrl());
                intent.putExtra("titulo_espetaculo", "Destaque");
                startActivity(intent);
            }
        });


        return rootView;
    }

}
