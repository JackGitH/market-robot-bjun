package com.example.marketrobot.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.marketrobot.config.Config;
import com.example.marketrobot.config.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

@RestController
public class AdminController {
    private Logger LOG = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private Config config;
    @Resource
    private ContextRefresher contextRefresher;
    @GetMapping("/admin/refresh")
    public String refresh(){
        Set<String> s = contextRefresher.refresh();
        LOG.debug("change config key is {}",s);
        return show();
    }
    @GetMapping("/admin/show")
    public String show() {
        JSONObject res = new JSONObject();
        res.put("biz", JSONObject.toJSONString(config));
        return res.toJSONString();
    }
    @GetMapping("/")
    public String hello() {
        return "hello world";
    }

    @GetMapping("/admin/stopBuy")
    public String stopBuy(Exchange exchange){
        if(exchange.getWebsite()==null){
            return "err params :Website is null ";
        }
        if(exchange.getCoinName()==null){
            return "err params :Symbol is null ";
        }
        Map<String,Exchange> exchangeMap = config.getExchanges();
        String key = exchange.getWebsite()+"-"+exchange.getCoinName();
        Exchange oldExchange = exchangeMap.get(key);
        if(oldExchange==null){
            return "err params :"+JSONObject.toJSONString(exchange);
        }
        oldExchange.setStopBuy(true);
        return show();
    }
    @GetMapping("/admin/stopSell")
    public String stopSell(Exchange exchange){
        if(exchange.getWebsite()==null){
            return "err params :Website is null ";
        }
        if(exchange.getCoinName()==null){
            return "err params :Symbol is null ";
        }
        Map<String,Exchange> exchangeMap = config.getExchanges();
        String key = exchange.getWebsite()+"-"+exchange.getCoinName();
        Exchange oldExchange = exchangeMap.get(key);
        if(oldExchange==null){
            return "err params :"+JSONObject.toJSONString(exchange);
        }
        oldExchange.setStopSell(true);
        return show();
    }

    //todo 操作拉升
}
