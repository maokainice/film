package com.guangzhou.film.manageback.common;

import java.io.Serializable;

/**
 * 返回值包装类
 * @param <T>
 */
public class Response<T> implements Serializable {

    private static final long serialVersionUID = -1026644456438375097L;

    private Status status;
    private Msg msg;
    private T data;


    public static enum Status{
        SUCCESS,FAIL
    }

    public static enum Msg{

        USER_ISNOT_EXIST("0001","用户不存在"),
        PWD_ERROR("0002","密码错误");

        private String code;
        private String message;

        Msg(String code,String message){
            this.code = code;
            this.message = message;
        }

    }

}
