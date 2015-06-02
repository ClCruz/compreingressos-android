package br.com.compreingressos.adapter;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.compreingressos.R;
import br.com.compreingressos.interfaces.OnItemClickListener;
import br.com.compreingressos.model.Order;
import br.com.compreingressos.utils.CustomTypeFace;

/**
 * Created by luiszacheu on 30/03/15.
 */
public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = "OrderAdapter";

    private List<Order> mListOrders = Collections.emptyList();
    private Context context;
    public static OnItemClickListener onItemClickListener;
    private static final int TYPE_ITEM = 1;
    private ActionBarActivity activity;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");

    public OrderAdapter(Context context, List<Order> orders) {
        this.mListOrders = orders;
        this.context = context;
        activity  = (ActionBarActivity) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_order, viewGroup, false);


        return new ViewHolderItem(v);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        Order order = getItem(position);
        ((ViewHolderItem) viewHolder).tituloView.setText(order.getTituloEspetaculo());
        ((ViewHolderItem) viewHolder).dataView.setText(sdf.format(order.getDate()));
        ((ViewHolderItem) viewHolder).numberOrderView.setText(order.getNumber());

    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    private Order getItem(int position) {
        return mListOrders.get(position);
    }

    @Override
    public int getItemCount() {
        return (mListOrders == null ? 0 : mListOrders.size()) ;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        OrderAdapter.onItemClickListener = onItemClickListener;
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        public TextView tituloView;
        public TextView dataView;
        public TextView numberOrderView;
        public ImageView coverView;

        public ViewHolderItem(View itemView) {
            super(itemView);
            tituloView = (TextView) itemView.findViewById(R.id.txt_espetaculo);
            tituloView.setTypeface(CustomTypeFace.setFontLora(itemView.getContext()));

            dataView = (TextView) itemView.findViewById(R.id.txt_data);
            dataView.setTypeface(CustomTypeFace.setFontLora(itemView.getContext()));

            numberOrderView = (TextView) itemView.findViewById(R.id.txt_order_number);
            numberOrderView.setTypeface(CustomTypeFace.setFontLora(itemView.getContext()));

            coverView = (ImageView) itemView.findViewById(R.id.img_cover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, (getPosition()));
                }
            });
        }
    }

    public void updateList(List<Order> orders) {
        mListOrders = orders;
        notifyDataSetChanged();
    }


}
