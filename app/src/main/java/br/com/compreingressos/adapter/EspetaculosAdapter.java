package br.com.compreingressos.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import br.com.compreingressos.R;
import br.com.compreingressos.interfaces.OnItemClickListener;
import br.com.compreingressos.model.Espetaculo;
import br.com.compreingressos.toolbox.VolleySingleton;
import br.com.compreingressos.utils.CustomTypeFace;


/**
 * Created by luiszacheu on 30/03/15.
 */
public class EspetaculosAdapter extends RecyclerView.Adapter<EspetaculosAdapter.ViewHolder> {

    private static final String LOG_TAG = "EspetaculosAdapter";

    private ArrayList<Espetaculo> mListEspetaculos;
    public static OnItemClickListener onItemClickListener;
    private Context context;


    public EspetaculosAdapter(Context context, ArrayList<Espetaculo> espetaculos) {
        this.mListEspetaculos = espetaculos;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        EspetaculosAdapter.onItemClickListener = onItemClickListener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_espetaculo, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Espetaculo espetaculo = mListEspetaculos.get(position);
        viewHolder.tituloView.setText(espetaculo.getTitulo());
        viewHolder.teatroView.setText(espetaculo.getTeatro());
        viewHolder.localView.setText(espetaculo.getCidade() + " - " + espetaculo.getEstado());
        viewHolder.generoView.setText(espetaculo.getGenero());

        viewHolder.miniaturaView.setImageUrl(espetaculo.getMiniatura(), VolleySingleton.getInstance(context).getImageLoader());


    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return (mListEspetaculos == null ? 0 : mListEspetaculos.size());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tituloView;
        public TextView teatroView;
        public TextView localView;
        public TextView generoView;
        public NetworkImageView miniaturaView;

        public ViewHolder(View itemView) {
            super(itemView);

            tituloView = (TextView) itemView.findViewById(R.id.txt_titulo);
            tituloView.setTypeface(CustomTypeFace.setFontLora(itemView.getContext()));

            teatroView = (TextView) itemView.findViewById(R.id.txt_teatro);
            teatroView.setTypeface(CustomTypeFace.setFontLora(itemView.getContext()));

            localView = (TextView) itemView.findViewById(R.id.txt_local);
            localView.setTypeface(CustomTypeFace.setFontLora(itemView.getContext()));

            generoView = (TextView) itemView.findViewById(R.id.txt_genero);
            miniaturaView = (NetworkImageView) itemView.findViewById(R.id.img_miniatura);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, (getPosition()));
                }
            });
        }




    }
}
