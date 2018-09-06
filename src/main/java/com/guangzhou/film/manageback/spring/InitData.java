package com.guangzhou.film.manageback.spring;

import com.guangzhou.film.manageback.common.AccessTokenThread;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * 容器初始化后执行
 * 运行获取access_token的线程
 */
@Component
public class InitData implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(contextRefreshedEvent.getApplicationContext().getParent() == null){
            new Thread(new AccessTokenThread()).start();
        }
    }
}
