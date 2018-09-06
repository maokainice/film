package com.guangzhou.film.manageback.common;

import com.guangzhou.film.manageback.wxapi.accessTokenApi.AccessTokenService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 每隔7000秒调用一次微信AccessToken接口 保证AccessToken可用
 */
public class AccessTokenThread implements Runnable {
    private static final Log logger = LogFactory.getLog(AccessTokenThread.class);

    @Override
    public void run() {
        while (true){
            try{
                logger.info("AccessTokenThread running...");
                AccessTokenContext.set(AccessTokenService.get());
            }catch (Exception e){
                e.printStackTrace();
                logger.error(String.format("AccessTokenThread run Exception :[%s]",e));
            }
            if(AccessTokenContext.get() != null){
                try {
                    logger.info("AccessTokenThread sleeping...");
                    Thread.sleep(7 * 1000 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    logger.error(String.format("AccessTokenThread run Exception :[%s]",e));
                }
            }
        }
    }
}
