package br.com.compreingressos.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import br.com.compreingressos.model.Ingresso;

/**
 * Created by luiszacheu on 15/04/15.
 */
public class IngressoDao extends BaseDaoImpl<Ingresso, Integer> {

    public IngressoDao(ConnectionSource connectionSource) throws SQLException {
        super(Ingresso.class);
        setConnectionSource(connectionSource);
        initialize();
    }
}
