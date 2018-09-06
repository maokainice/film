package com.guangzhou.film.manageback.wxapi.accessTokenApi;

import com.alibaba.fastjson.JSONObject;
import com.guangzhou.film.manageback.common.AccessTokenContext;
import com.guangzhou.film.manageback.common.Constant;
import com.guangzhou.film.manageback.model.AccessToken;
import com.guangzhou.film.manageback.util.http.HttpsUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AccessTokenService {
    private static final Log logger = LogFactory.getLog(AccessTokenService.class);

    private static String api = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

    private static String APP_ID_VALUE = "wxb6009f02ffd281c1";

    private static String APP_SECRET_VALUE = "91f695e4de15716d8487f4683d1abbc0";

    private static String APP_ID_PARAM = "appid";

    private static String SECRET_PARAM = "secret";

    private static String access_token_url = api + Constant.AND + APP_ID_PARAM + Constant.EQ + APP_ID_VALUE + Constant.AND + SECRET_PARAM + Constant.EQ + APP_SECRET_VALUE;

    /**
     * 调用微信接口获取access_token
     * @return
     */
    public static AccessToken get(){
        Long start = System.currentTimeMillis();
        AccessToken token = new AccessToken();
        try{
            logger.info("正在请求微信接口：" + access_token_url);
            String response = HttpsUtil.requestGet(access_token_url);
            logger.info("微信接口返回结果：" + response);
            JSONObject jsonobject = JSONObject.parseObject(response);
            token = JSONObject.toJavaObject(jsonobject,AccessToken.class);
            AccessTokenContext.set(token);
        }catch (Exception e){
            logger.error(String.format("AccessTokenService error :[%s]",e));
        }
        Long end = System.currentTimeMillis();
        logger.info(String.format("获取token接口耗时：[%s]毫秒",(end - start)));
        return token;
    }

}
