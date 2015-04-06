package br.com.compreingressos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.compreingressos.R;
import br.com.compreingressos.model.Genero;
import br.com.compreingressos.utils.CustomTypeFace;

/**
 * Created by luiszacheu on 30/03/15.
 */
public class GeneroRVArapter extends RecyclerView.Adapter<GeneroRVArapter.ViewHolder> {

    private List<Genero> generos;
    private Context context;
    OnItemClickListener mItemClickListener;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView nomeView;
        public ImageView coverView;

        public ViewHolder(View itemView) {
            super(itemView);
            nomeView = (TextView) itemView.findViewById(R.id.txt_nome);
            nomeView.setTypeface(CustomTypeFace.setFontLora(itemView.getContext()));
            coverView = (ImageView) itemView.findViewById(R.id.img_cover);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null){
                mItemClickListener.onClickListener(v, getPosition());
            }
        }
    }

    public GeneroRVArapter(Context context, List<Genero> generos) {
        this.generos = generos;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_genero, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.nomeView.setText(generos.get(position).getNome());
        viewHolder.coverView.setBackgroundResource(generos.get(position).getCover());
        viewHolder.coverView.setPadding(0, 10, 0, 0);

    }

    @Override
    public int getItemCount() {
        return generos.size();
    }

    public interface OnItemClickListener{
        public void onClickListener(View v, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener){
        this.mItemClickListener = itemClickListener;
    }


}
