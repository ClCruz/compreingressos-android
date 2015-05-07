package br.com.compreingressos.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import br.com.compreingressos.utils.AndroidUtils;

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
            ((ViewHolderHeader) viewHolder).teatroView.setText(order.getNomeTeatroEspetaculo());
            ((ViewHolderHeader) viewHolder).mesView.setText(new SimpleDateFormat("MMM").format(order.getDate()).toUpperCase());
            ((ViewHolderHeader) viewHolder).diaNumeroView.setText(new SimpleDateFormat("dd").format(order.getDate()));
            ((ViewHolderHeader) viewHolder).diaView.setText(new SimpleDateFormat("EEE").format(order.getDate()).toUpperCase());
            ((ViewHolderHeader) viewHolder).enderecoView.setText(order.getEnderecoEspetaculo());
            ((ViewHolderHeader) viewHolder).horarioView.setText(order.getHorarioEspetaculo());
        }else if(viewHolder instanceof ViewHolderItem){
            Ingresso ingresso = getItem(position);
            ((ViewHolderItem) viewHolder).setorView.setText(ingresso.getType());
            ((ViewHolderItem) viewHolder).cadeiraView.setText(ingresso.getLocal());
            ((ViewHolderItem) viewHolder).valorView.setText("R$ " + ingresso.getPrice());

            DisplayMetrics dm = context.getResources().getDisplayMetrics();

            AztecWriter write = new AztecWriter();

            BitMatrix bitMatrix  = write.encode(ingresso.getQrcode(), BarcodeFormat.AZTEC, AndroidUtils.getDPI(128, dm), AndroidUtils.getDPI(128 ,dm));
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
        public TextView valorView;
        public ImageView qrcodeView;
        public ImageButton passwallet;

        public ViewHolderItem(View itemView) {
            super(itemView);
            setorView = (TextView) itemView.findViewById(R.id.txt_setor);
            cadeiraView = (TextView) itemView.findViewById(R.id.txt_cadeira);
            valorView = (TextView) itemView.findViewById(R.id.txt_valor);

            qrcodeView = (ImageView) itemView.findViewById(R.id.img_qrcode);

            passwallet = (ImageButton) itemView.findViewById(R.id.passwallet);
            passwallet.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    final ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Aguarde...");
                    progressDialog.show();

                    Ingresso ingresso  =  order.getIngressos().get(getPosition()-1);

                    requestQueue = VolleySingleton.getInstance(context).getRequestQueue();

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, context.getString(R.string.url_mpassbook) + "generate.json", OrderHelper.createJsonPeerTicket(order, ingresso), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                progressDialog.dismiss();
                                String strNamePkPassresponse = response.getJSONArray("passes").get(0).toString();
                                PassWalletHelper.launchPassWallet(context, Uri.parse( context.getString(R.string.url_mpassbook) + strNamePkPassresponse), true);

                            } catch (JSONException e) {
                                progressDialog.dismiss();
                                e.printStackTrace();
                                Toast.makeText(context, context.getString(R.string.message_erro_envio_passwallet), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                }
            });
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder{
        public TextView espetaculoView;
        public TextView teatroView;
        public TextView mesView;
        public TextView diaNumeroView;
        public TextView diaView;
        public TextView enderecoView;
        public TextView horarioView;

        public ViewHolderHeader(View itemView) {
            super(itemView);
            espetaculoView = (TextView) itemView.findViewById(R.id.txt_espetaculo);
            teatroView = (TextView) itemView.findViewById(R.id.txt_teatro);
            mesView = (TextView) itemView.findViewById(R.id.txt_mes);
            diaNumeroView = (TextView) itemView.findViewById(R.id.txt_dia_numero);
            diaView = (TextView) itemView.findViewById(R.id.txt_dia);
            enderecoView = (TextView) itemView.findViewById(R.id.txt_endereco);
            horarioView = (TextView) itemView.findViewById(R.id.txt_horario);

        }
    }


}
