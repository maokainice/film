package com.guangzhou.film.manageback.common;

import com.guangzhou.film.manageback.model.AccessToken;
import com.guangzhou.film.manageback.wxapi.accessTokenApi.AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;


public class AccessTokenContext {


    private static AccessToken accessToken = null;

    public static AccessToken get(){
        if(accessToken != null){
            return accessToken;
        }
        return null;
    }

    public static void set(AccessToken accessToken){
        AccessTokenContext.accessToken = accessToken;
    }


}
