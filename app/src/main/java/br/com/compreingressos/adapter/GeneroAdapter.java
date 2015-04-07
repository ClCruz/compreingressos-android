package br.com.compreingressos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.compreingressos.R;
import br.com.compreingressos.model.Genero;
import br.com.compreingressos.utils.CustomTypeFace;

/**
 * Created by luiszacheu on 30/03/15.
 */
public class GeneroAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = "GeneroAdapter";

    private ArrayList<Genero> mListGeneros;
    private Context context;
    public static OnItemClickListener onItemClickListener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    public GeneroAdapter(Context context, ArrayList<Genero> generos) {
        this.mListGeneros = generos;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_generos, viewGroup, false);
            return new ViewHolderHeader(v);

        }else if (viewType == TYPE_ITEM){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_genero, viewGroup, false);
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
            ((ViewHolderItem) viewHolder).coverView.setPadding(0, 10, 0, 0);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
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
