package br.com.compreingressos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import br.com.compreingressos.R;
import br.com.compreingressos.model.Genero;
import br.com.compreingressos.utils.CustomTypeFace;

/**
 * Created by luiszacheu on 19/03/15.
 */
public class GeneroAdapter extends BaseAdapter {

    private List<Genero> generos = Collections.emptyList();
    private final Context context;

    public GeneroAdapter(Context context, List<Genero> generos) {
        this.generos = generos;
        this.context = context;
    }

    @Override
    public int getCount() {
        return generos.size();
    }

    @Override
    public Genero getItem(int position) {
        return generos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView nomeView;
        ImageView coverView;

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_genero, parent, false);
            nomeView = (TextView) convertView.findViewById(R.id.txt_nome);
            coverView = (ImageView) convertView.findViewById(R.id.img_cover);
            convertView.setTag(new ViewHolder(nomeView, coverView));
        }else{
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            nomeView = viewHolder.nomeView;
            coverView = viewHolder.coverView;
        }

        Genero genero = getItem(position);
        nomeView.setText(genero.getNome());
        nomeView.setTypeface(CustomTypeFace.setFontLora(context));
        coverView.setBackgroundResource(genero.getCover());


        return convertView;
    }

    private static class ViewHolder{
        public final TextView nomeView;
        public final ImageView coverView;

        public ViewHolder(TextView nomeView, ImageView coverView){
            this.nomeView = nomeView;
            this.coverView = coverView;
        }
    }
}
