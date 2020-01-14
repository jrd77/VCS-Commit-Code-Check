package com.atzuche.order.photo.common;

import java.io.Serializable;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/1/2 3:55 下午
 **/
public class AdminUser implements Serializable {
    private String authId;
    private String authName;


    public AdminUser(String authId, String authName) {
        this.authId = authId;
        this.authName = authName;
    }

    public String getAuthId() {
        return authId;
    }



    public String getAuthName() {
        return authName;
    }



    @Override
    public String toString() {
        return "AdminUser{" +
                "authId='" + authId + '\'' +
                ", authName='" + authName + '\'' +
                '}';
    }
}
