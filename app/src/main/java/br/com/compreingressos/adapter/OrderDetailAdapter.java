package br.com.compreingressos.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.glxn.qrgen.android.QRCode;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.compreingressos.DetailHistoryOrderActivity;
import br.com.compreingressos.R;
import br.com.compreingressos.fragment.MainBannerFragment;
import br.com.compreingressos.helper.PassWalletHelper;
import br.com.compreingressos.interfaces.OnItemClickListener;
import br.com.compreingressos.model.Banner;
import br.com.compreingressos.model.Genero;
import br.com.compreingressos.model.Ingresso;
import br.com.compreingressos.model.Order;
import br.com.compreingressos.utils.CustomTypeFace;

/**
 * Created by luiszacheu on 30/03/15.
 */
public class OrderDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = "OrderDetailAdapter";

    private Order order;
    private List<Ingresso> mListIngressos = new ArrayList<>();
    private Context context;
    public static OnItemClickListener onItemClickListener;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ActionBarActivity activity;
    private SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");

    public OrderDetailAdapter(Context context, Order order) {
        this.order = order;
        this.mListIngressos = order.getIngressos();
        this.context = context;

        activity  = (ActionBarActivity) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_HEADER){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.header_order, viewGroup, false);


            return new ViewHolderHeader(v);

        }else if (viewType == TYPE_ITEM){
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_order_tickets, viewGroup, false);
            return new ViewHolderItem(v);
        }

        throw new RuntimeException("não foi encontrado o tipo " + viewType + " + tem certeza que esta usando o tipo correto!");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ViewHolderHeader){
            ((ViewHolderHeader) viewHolder).espetaculoView.setText(order.getTituloEspetaculo());
            ((ViewHolderHeader) viewHolder).dataView.setText(sdf.format(order.getDate()) + " ás " + order.getHorarioEspetaculo());
            ((ViewHolderHeader) viewHolder).enderecoView.setText(order.getEnderecoEspetaculo());
        }else if(viewHolder instanceof ViewHolderItem){
            Ingresso ingresso = getItem(position);
            ((ViewHolderItem) viewHolder).setorView.setText(ingresso.getType());
            ((ViewHolderItem) viewHolder).cadeiraView.setText(ingresso.getLocal());

            Bitmap myBitmap = QRCode.from(ingresso.getQrcode()).bitmap();
            ((ViewHolderItem) viewHolder).qrcodeView.setImageBitmap(myBitmap);

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

    private Ingresso getItem(int position) {
        return mListIngressos.get(position - 1);
    }

    @Override
    public int getItemCount() {
        return (mListIngressos == null ? 0 : mListIngressos.size()) +1 ;
    }

    public interface OnItemClickListener{
        public void onClickListener(View v, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener onItemClickListener){
        OrderDetailAdapter.onItemClickListener = onItemClickListener;
    }

    public class ViewHolderItem extends RecyclerView.ViewHolder {
        public TextView setorView;
        public TextView cadeiraView;
        public ImageView qrcodeView;
        public ImageButton passwallet;

        public ViewHolderItem(View itemView) {
            super(itemView);
            setorView = (TextView) itemView.findViewById(R.id.txt_setor);
            cadeiraView = (TextView) itemView.findViewById(R.id.txt_cadeira);

            qrcodeView = (ImageView) itemView.findViewById(R.id.img_qrcode);

            passwallet = (ImageButton) itemView.findViewById(R.id.passwallet);
            passwallet.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    PassWalletHelper.launchPassWallet(context, Uri.parse("http://mpassbook.herokuapp.com/passes/0054741128200000100146.pkpass"), true);
                }
            });

        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder{
        public TextView espetaculoView;
        public TextView dataView;
        public TextView enderecoView;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            espetaculoView = (TextView) itemView.findViewById(R.id.txt_espetaculo);
            dataView = (TextView) itemView.findViewById(R.id.txt_data);
            enderecoView = (TextView) itemView.findViewById(R.id.txt_endereco);

        }
    }



}
