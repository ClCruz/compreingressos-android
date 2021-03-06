package br.com.compreingressos.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Created by zaca on 9/8/15.
 */
public class SessionManager {

    static final int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "UserSessionPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_ID = "userId";

    SharedPreferences pref;
    Editor editor;
    Context mContext;

    public SessionManager(Context mContext) {
        this.mContext = mContext;
        pref =  mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //Metodo para armazenar os dados de sessão
    public void createLoginSession(String email, String userId) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USER_ID, userId);
        editor.commit();
    }

    //Metodo que verifica se a sessão
    public boolean checkLogin() {
        if (!this.isLoggedIn()) {
            return false;
        } else {
            return true;
        }
    }

    //Metodo que retorna um hash com os detalhe do usuárui logado
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));

        return user;
    }

    //Metodo responsavel por fazer o logout do user atual.
    public boolean logoutUser() {
        editor.clear();
        editor.commit();

        return true;
    }

    //Metodo retorna atual situação da Sessão
    private boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
