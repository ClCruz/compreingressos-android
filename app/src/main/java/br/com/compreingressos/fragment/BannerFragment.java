package br.com.compreingressos.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

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

    public static BannerFragment newInstance(Banner banner) {
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

        Transformation transformation = new Transformation() {

            @Override
            public Bitmap transform(Bitmap source) {
                int targetWidth = imgBanner.getWidth();

                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            }

            @Override
            public String key() {
                return "transformation" + " desiredWidth";
            }
        };


        Picasso.with(getActivity())
                .load(banner.getImagem())
                .placeholder(getActivity().getResources().getDrawable(R.drawable.placeholder_banner))
                .into(imgBanner, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
//                  progressBar.setVisibility(View.GONE);
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
