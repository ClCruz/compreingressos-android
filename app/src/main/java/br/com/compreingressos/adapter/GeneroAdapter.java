package br.com.compreingressos.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import br.com.compreingressos.R;
import br.com.compreingressos.fragment.MainBannerFragment;
import br.com.compreingressos.model.Banner;
import br.com.compreingressos.model.Genero;
import br.com.compreingressos.utils.AndroidUtils;
import br.com.compreingressos.utils.CustomTypeFace;

/**
 * Created by luiszacheu on 30/03/15.
 */
public class GeneroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = "GeneroAdapter";

    private ArrayList<Genero> mListGeneros = new ArrayList<>();
    private ArrayList<Banner> mListBanners = new ArrayList<>();
    private Context context;
    public static OnItemClickListener onItemClickListener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ActionBarActivity activity;


    public GeneroAdapter(Context context, ArrayList<Genero> generos, ArrayList<Banner> banners) {
        this.mListGeneros = generos;
        this.context = context;
        this.mListBanners = banners;
        activity  = (ActionBarActivity) context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_generos, viewGroup, false);
            MainBannerFragment  fragment = new MainBannerFragment();

            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();

            fragmentTransaction.add(R.id.view, fragment, "header");
            fragmentTransaction.commit();

            return new ViewHolderHeader(v);

        }else if (viewType == TYPE_ITEM){


            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_genero, viewGroup, false);

            DisplayMetrics dm = context.getResources().getDisplayMetrics();


            return new ViewHolderItem(v);
        }

        throw new RuntimeException("não foi encontrado o tipo " + viewType + " + tem certeza que esta usando o tipo correto!");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ViewHolderItem){
            Genero genero = getItem(position);
            ((ViewHolderItem) viewHolder).nomeView.setText(genero.getNome());
            ((ViewHolderItem) viewHolder).coverView.setBackgroundResource(genero.getCover());
//            ((ViewHolderItem) viewHolder).coverView.setPadding(0, 10, 0, 0);

        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    public void updateBanners(ArrayList<Banner> banners){
        try {
            ((MainBannerFragment) activity.getSupportFragmentManager().findFragmentByTag("header")).updateBannerAdapter(banners);
        }catch (NullPointerException e){
            Crashlytics.logException(e);
        }



    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private Genero getItem(int position) {
        return mListGeneros.get(position - 1);
    }

    @Override
    public int getItemCount() {
        return (mListGeneros == null ? 0 : mListGeneros.size()) +1 ;
    }

    public interface OnItemClickListener{
        public void onClickListener(View v, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener onItemClickListener){
        GeneroAdapter.onItemClickListener = onItemClickListener;
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        public TextView nomeView;
        public ImageView coverView;

        public ViewHolderItem(View itemView) {
            super(itemView);
            nomeView = (TextView) itemView.findViewById(R.id.txt_nome);
            nomeView.setTypeface(CustomTypeFace.setFontLora(itemView.getContext()));

            coverView = (ImageView) itemView.findViewById(R.id.img_cover);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClickListener(v, getPosition() - 1); //Foi inserido o (-1) pois a lista foi somado +1 para termos um item para adicionar o cabeçalho.
                }
            });
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder{

        public ViewHolderHeader(View itemView) {
            super(itemView);
        }
    }
}
