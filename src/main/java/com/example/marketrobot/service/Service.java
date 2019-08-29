package com.example.marketrobot.service;

import com.alibaba.fastjson.JSON;
import com.example.marketrobot.config.Config;
import com.example.marketrobot.config.Exchange;
import com.example.marketrobot.entity.OrderVo;
import com.example.marketrobot.util.HttpUtil;
import com.example.marketrobot.util.MD5Util;
import com.example.marketrobot.util.Util;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@org.springframework.stereotype.Service
@EnableScheduling
public class Service {
    private static  final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(Service.class.getName());
    @Autowired
    private Config config;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @PostConstruct
    public void init(){
        Map<String,Exchange> map = config.getExchanges();
        map.entrySet().stream().forEach(e ->{
            Runnable runnable = new Runnable(){
                @Override
                public void run() {
                    try {
                        work(e.getValue());
                    }catch (Exception e){
                    }
                }
            };
            executorService.submit(runnable);
        });
    }

    public void work(Exchange exchange) throws InterruptedException {
        while(true){
            Thread.sleep(exchange.getWaitTimes());
            for(int i =0;i<2;i++){
                OrderVo orderVo =  randomCreateOrder(exchange);
                if((exchange.isStopSell()&&orderVo.getSide().equals(exchange.getSellType()))
                ||(exchange.isStopBuy()&&orderVo.getSide().equals(exchange.getBuyType()))){
                        continue;
                }
                createOrder(orderVo,exchange.getUris().getUriCreateOrder());
            }
        }
    }

    private OrderVo randomCreateOrder(Exchange exchange) {
        int priceScale = exchange.getPriceScale();
        OrderVo orderVo = new OrderVo();
        orderVo.setSymbol(exchange.getSymbol());
        Random random = new Random();
        int randType = random.nextInt(10);
        if (randType>=exchange.getBuyOrderRatio()){
            orderVo.setSide(exchange.getBuyType());
            String buyprice = Util.random(exchange.getBuyBasePrice(),exchange.getBuyfluctuate()).substring(0,priceScale);
            String buyamount =  Util.random(exchange.getBuyamount(),exchange.getAmountFluctuate());
            orderVo.setAmount(buyamount);
            orderVo.setPrice(buyprice);
        }else {
            orderVo.setSide(exchange.getSellType());
            String sellprice = Util.random(exchange.getSellBasePrice(),exchange.getSellfluctuate()).substring(0,priceScale);
            String sellamount = Util.random(exchange.getSellAmount(),exchange.getAmountFluctuate());
            orderVo.setAmount(sellamount);
            orderVo.setPrice(sellprice);
        }
        Random random2 = new Random();
        int rand = random2.nextInt(10);
        if(rand>4&&orderVo.getSide().equals(exchange.getBuyType())){
            orderVo.setSecret_key(exchange.getBuypriKey());
            orderVo.setApi_key(exchange.getBuypubKey());
        }else if (rand<5&&orderVo.getSide().equals(exchange.getBuyType())){
            orderVo.setSecret_key(exchange.getSellpriKey());
            orderVo.setApi_key(exchange.getSellpubKey());
        }else if(rand>4&&orderVo.getSide().equals(exchange.getSellType())){
            orderVo.setSecret_key(exchange.getBuypriKey());
            orderVo.setApi_key(exchange.getBuypubKey());
        } else {
            orderVo.setSecret_key(exchange.getSellpriKey());
            orderVo.setApi_key(exchange.getSellpubKey());
        }
        return orderVo;
    }

    public  void createOrder(OrderVo orderVo,String url){
        long time = System.currentTimeMillis();
        /** 封装需要签名的参数 */
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("symbol", orderVo.getSymbol());
        params.put("type", "1");
        params.put("side", orderVo.getSide());
        params.put("volume", orderVo.getAmount());
        //params.put("price", lastPrice);
        params.put("price", orderVo.getPrice());
        params.put("api_key", orderVo.getApi_key());
        params.put("time", time+"");
        /** 拼接签名字符串，md5签名 */
        StringBuilder result = new StringBuilder();
        Set<Map.Entry<String, String>> entrys = params.entrySet();
        for (Map.Entry<String, String> param : entrys) {
            /** 去掉签名字段 */
            if(param.getKey().equals("sign")){
                continue;
            }
            /** 空参数不参与签名 */
            if(param.getValue()!=null) {
                result.append(param.getKey());
                result.append(param.getValue().toString());
            }
        }
        result.append(orderVo.getSecret_key());
        String sign = MD5Util.getMD5(result.toString());
        params.put("sign", sign);
        ArrayList<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
        params.forEach((k,v) -> basicNameValuePairs.add(new BasicNameValuePair(k, v)));
        String resultJson =HttpUtil.doPost(url, basicNameValuePairs );
        log.info("create order [{}],result [{}]",JSON.toJSONString(params),resultJson);
    }
    /**
     *
     */
    public  void CancelOrder(String orderId,String symbol){
        //String lastPrice =  get_priceByName();
        /** api_key,secret_key */
        String api_key = "f849b7a8d6261791b31748514f553c85";
        String secret_key = "ea02d4edec06282dad2b5f66382cbced";
        long time = new Date().getTime();
        /** 封装需要签名的参数 */
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("symbol", symbol);
        params.put("order_id", orderId);
        params.put("api_key", api_key);
        params.put("time", time+"");
        /** 拼接签名字符串，md5签名 */
        StringBuilder result = new StringBuilder();
        Set<Map.Entry<String, String>> entrys = params.entrySet();
        for (Map.Entry<String, String> param : entrys) {
            /** 去掉签名字段 */
            if(param.getKey().equals("sign")){
                continue;
            }

            /** 空参数不参与签名 */
            if(param.getValue()!=null) {
                result.append(param.getKey());
                result.append(param.getValue().toString());
            }
        }
        result.append(secret_key);
        String sign = MD5Util.getMD5(result.toString());
        params.put("sign", sign);
        ArrayList<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
        params.forEach((k,v) -> basicNameValuePairs.add(new BasicNameValuePair(k, v)));
        System.out.println(JSON.toJSONString(params));
        String resultJson = HttpUtil.doPost("https://openapi.wbfex.com/open/api/cancel_order", basicNameValuePairs );
        System.out.println(resultJson);
    }


}
