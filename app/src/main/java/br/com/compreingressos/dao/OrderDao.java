package br.com.compreingressos.dao;

import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import br.com.compreingressos.model.Espetaculo;
import br.com.compreingressos.model.Ingresso;
import br.com.compreingressos.model.Order;

/**
 * Created by luiszacheu on 15/04/15.
 */
public class OrderDao extends BaseDaoImpl<Order, Integer> {

    private static final String LOG_TAG = "OrderDao";
    private IngressoDao ingressoDao;
    private EspetaculoDao espetaculoDao;

    public OrderDao(ConnectionSource connectionSource) throws SQLException{
        super(Order.class);
        setConnectionSource(connectionSource);
        initialize();
    }

    @Override
    public int create(Order data) throws SQLException {
        espetaculoDao =  new EspetaculoDao(getConnectionSource());
        ingressoDao = new IngressoDao(getConnectionSource());

        Order order = data;
        order.setTituloEspetaculo(data.getEspetaculo().getTitulo());
        order.setEnderecoEspetaculo(data.getEspetaculo().getEndereco());
        order.setNomeTeatroEspetaculo(data.getEspetaculo().getTeatro());
        order.setHorarioEspetaculo(data.getEspetaculo().getHorario());


        int result = super.create(order);

        if (result == 1 ){
//            Espetaculo espetaculo = data.getEspetaculo();
//            espetaculo.setOrder(data);
//            int espetaculoResult = espetaculoDao.create(espetaculo);
//            Log.e(LOG_TAG, "espetaculoResult - " + espetaculoResult + " title " + espetaculo.getTitulo());

            for (Ingresso ingresso : data.getIngressos()){
                ingresso.setOrder(data);
                int x = ingressoDao.create(ingresso);
                Log.e(LOG_TAG, "x ------- > - " + x);
            }
        }

        return result;
    }



}
