package br.com.compreingressos.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.QRCodeDecoderMetaData;

import net.glxn.qrgen.android.QRCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.compreingressos.R;
import br.com.compreingressos.helper.OrderHelper;
import br.com.compreingressos.helper.PassWalletHelper;
import br.com.compreingressos.model.Ingresso;
import br.com.compreingressos.model.Order;
import br.com.compreingressos.toolbox.VolleySingleton;

/**
 * Created by luiszacheu on 30/03/15.
 */
public class OrderDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = "OrderDetailAdapter";

    private Order order;
    private List<Ingresso> mListIngressos = new ArrayList<>();
    private Context context;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ActionBarActivity activity;
    private SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM");
    private RequestQueue requestQueue;

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

            AztecWriter write = new AztecWriter();

            BitMatrix bitMatrix  = write.encode(ingresso.getQrcode(), BarcodeFormat.AZTEC, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();

            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            ((ViewHolderItem) viewHolder).qrcodeView.setImageBitmap(bmp);

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
                    Ingresso ingresso  =  order.getIngressos().get(getPosition()-1);

                    requestQueue = VolleySingleton.getInstance(context).getRequestQueue();

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, context.getString(R.string.url_mpassbook) + "generate.json", OrderHelper.createJsonPeerTicket(order, ingresso), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String strNamePkPassresponse = response.getJSONArray("passes").get(0).toString();
                                PassWalletHelper.launchPassWallet(context, Uri.parse( context.getString(R.string.url_mpassbook) + strNamePkPassresponse), true);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, context.getString(R.string.message_erro_envio_passwallet), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    requestQueue.add(jsonObjectRequest);
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
