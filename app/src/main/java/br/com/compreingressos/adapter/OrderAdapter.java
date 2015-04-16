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

import java.util.ArrayList;

import br.com.compreingressos.R;
import br.com.compreingressos.interfaces.OnItemClickListener;
import br.com.compreingressos.model.Order;

/**
 * Created by luiszacheu on 30/03/15.
 */
public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = "OrderAdapter";

    private ArrayList<Order> mListOrders = new ArrayList<>();
    private Context context;
    public static OnItemClickListener onItemClickListener;
    private static final int TYPE_ITEM = 1;
    private ActionBarActivity activity;

    public OrderAdapter(Context context, ArrayList<Order> orders) {
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
        Log.e(LOG_TAG, "order - " + order.getTituloEspetaculo());

//        ((ViewHolderItem) viewHolder).titiloView.setText(order.getTituloEspetaculo());
        ((ViewHolderItem) viewHolder).dataView.setText(order.getDate().toString());
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
        public TextView titiloView;
        public TextView dataView;
        public TextView numberOrderView;
        public ImageView coverView;

        public ViewHolderItem(View itemView) {
            super(itemView);
            titiloView = (TextView) itemView.findViewById(R.id.txt_titulo);
//            titiloView.setTypeface(CustomTypeFace.setFontLora(itemView.getContext()));

            dataView = (TextView) itemView.findViewById(R.id.txt_data);
//            dataView.setTypeface(CustomTypeFace.setFontLora(itemView.getContext()));

            numberOrderView = (TextView) itemView.findViewById(R.id.txt_order_number);
//            numberOrderView.setTypeface(CustomTypeFace.setFontLora(itemView.getContext()));

            coverView = (ImageView) itemView.findViewById(R.id.img_cover);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, (getPosition()));
                }
            });
        }
    }

}
