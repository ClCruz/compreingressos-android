package br.com.compreingressos.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by zaca on 9/9/15.
 */
public class User {

    @SerializedName("user_id")
    private String userId;

    private String email;

    @SerializedName("php_session")
    private String phpSession;

    private static User ourInstance = new User();

    public static User getInstance() {
        return ourInstance;
    }

    private User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhpSession() {
        return phpSession;
    }

    public void setPhpSession(String phpSession) {
        this.phpSession = phpSession;
    }


    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", phpSession='" + phpSession + '\'' +
                '}';
    }
}
