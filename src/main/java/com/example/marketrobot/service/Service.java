package com.example.marketrobot.service;

import com.alibaba.fastjson.JSON;
import com.example.marketrobot.config.Config;
import com.example.marketrobot.config.Exchange;
import com.example.marketrobot.entity.OrderVo;
import com.example.marketrobot.util.HttpUtil;
import com.example.marketrobot.util.MD5Util;
import com.example.marketrobot.util.Util;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    private static String accountType0 = "0"; //取消账号0
    private static String accountType1 = "1"; // 取消账号1

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
        String accountType = "0"; // 0  取消其中一个账号的所有订单   1  取消另一个账号的所有订单
        Date beginTime=new Date();
        SimpleDateFormat simtime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time_id=simtime.format(beginTime);
        boolean radioChange = true;// 清空哪个账号的开关

        while(true){
            Thread.sleep(exchange.getWaitTimes());

            Date dd=new Date();
            String current_time=simtime.format(dd);
            //比较两个时间的差距 大于几个小时 用于每隔几小时取消某个账号的订单
            if(prepareTime(current_time,time_id,exchange.getClearTime())){
                CancelOrder("",exchange,accountType);
                time_id = current_time;
                if (radioChange){
                    accountType = "0";
                    radioChange = false;
                }else {
                    accountType = "1";
                    radioChange = true;
                }

            }
            for(int i =0;i<2;i++){
                OrderVo orderVo =  randomCreateOrder(exchange);
                if((exchange.isStopSell()&&orderVo.getType().equals(exchange.getSellType()))
                ||(exchange.isStopBuy()&&orderVo.getType().equals(exchange.getBuyType()))){
                        continue;
                }
                createOrder(orderVo,exchange.getUris().getUriCreateOrder());
            }
        }
    }

    private OrderVo randomCreateOrder(Exchange exchange) {
        int priceScale = exchange.getPriceScale();
        OrderVo orderVo = new OrderVo();
        orderVo.setCoinName(exchange.getCoinName());
        orderVo.setPayCoinName(exchange.getPayCoinIdName());
        Random random = new Random();
        int randType = random.nextInt(10);
        if (randType>=exchange.getBuyOrderRatio()){
            orderVo.setType(exchange.getBuyType());
            String buyprice = Util.random(exchange.getBuyBasePrice(),exchange.getBuyfluctuate()).substring(0,priceScale);
            String buyamount =  Util.random(exchange.getBuyamount(),exchange.getAmountFluctuate());
            orderVo.setAmount(buyamount);
            orderVo.setPrice(buyprice);

        }else {
            orderVo.setType(exchange.getSellType());
            String sellprice = Util.random(exchange.getSellBasePrice(),exchange.getSellfluctuate()).substring(0,priceScale);
            String sellamount = Util.random(exchange.getSellAmount(),exchange.getAmountFluctuate());
            orderVo.setAmount(sellamount);
            orderVo.setPrice(sellprice);
            //orderVo.setSecret(exchange.getSecretsell());
        }
        Random random2 = new Random();
        int rand = random2.nextInt(10);
        if(rand>4&&orderVo.getType().equals(exchange.getBuyType())){
            orderVo.setUserId(exchange.getUserIdbuy());
            orderVo.setSecret(exchange.getSecretbuy());
            /** 拼接签名字符串，md5签名 */
            StringBuilder result = new StringBuilder();
            result.append(orderVo.getUserId()+orderVo.getCoinName()+orderVo.getSecret()+orderVo.getPayCoinName()+exchange.getMD5Keybuy());
            String sign = MD5Util.getMD5(result.toString());
            orderVo.setCipherText(sign);
        }else if (rand<5&&orderVo.getType().equals(exchange.getBuyType())){
            orderVo.setUserId(exchange.getUserIdsell());
            orderVo.setSecret(exchange.getSecretsell());
            /** 拼接签名字符串，md5签名 */
            StringBuilder result = new StringBuilder();
            result.append(orderVo.getUserId()+orderVo.getCoinName()+orderVo.getSecret()+orderVo.getPayCoinName()+exchange.getMD5Keysell());
            String sign = MD5Util.getMD5(result.toString());
            orderVo.setCipherText(sign);
        }else if(rand>4&&orderVo.getType().equals(exchange.getSellType())){
            orderVo.setUserId(exchange.getUserIdbuy());
            orderVo.setSecret(exchange.getSecretbuy());
            /** 拼接签名字符串，md5签名 */
            StringBuilder result = new StringBuilder();
            result.append(orderVo.getUserId()+orderVo.getCoinName()+orderVo.getSecret()+orderVo.getPayCoinName()+exchange.getMD5Keybuy());
            String sign = MD5Util.getMD5(result.toString());
            orderVo.setCipherText(sign);
        } else {
            orderVo.setUserId(exchange.getUserIdsell());
            orderVo.setSecret(exchange.getSecretsell());
            /** 拼接签名字符串，md5签名 */
            StringBuilder result = new StringBuilder();
            result.append(orderVo.getUserId()+orderVo.getCoinName()+orderVo.getSecret()+orderVo.getPayCoinName()+exchange.getMD5Keysell());
            String sign = MD5Util.getMD5(result.toString());
            orderVo.setCipherText(sign);
        }
        return orderVo;
    }

    public  void createOrder(OrderVo orderVo,String url){
        long time = System.currentTimeMillis();
        /** 封装需要签名的参数 */
        TreeMap<String, String> params = new TreeMap<String, String>();
        params.put("coinName", orderVo.getCoinName());

        params.put("payCoinName", orderVo.getPayCoinName());


        params.put("amount", orderVo.getAmount());
        //params.put("price", lastPrice);
        params.put("price", orderVo.getPrice());
        params.put("type", orderVo.getType());
        params.put("secret", orderVo.getSecret());
        params.put("userId", orderVo.getUserId());

        params.put("cipherText", orderVo.getCipherText());
        ArrayList<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
        params.forEach((k,v) -> basicNameValuePairs.add(new BasicNameValuePair(k, v)));
        //log.info("create order url [{}]",url);
        String resultJson =HttpUtil.doPost(url, basicNameValuePairs );
        log.info("create order [{}],result [{}]",JSON.toJSONString(params),resultJson);
    }
    /**
     *
     */
    public  void CancelOrder(String orderId,Exchange exchange,String accountType){
        if (orderId== ""){
            // 取消所有订购单的参数
            String type;//0:取消买单和卖 1:取消买单  2:取消卖单
            String cipherText;
            String userId = "";
            String isAll; // True:取消所有交易  False:取消指定交易对
            String coinName;
            String payCoinName;
            if(accountType.equals(accountType0)){
                type = "0";
                userId = exchange.getUserIdbuy();
                isAll = "false";
                coinName = exchange.getCoinName();
                payCoinName = exchange.getPayCoinIdName();
                StringBuilder result = new StringBuilder();
                result.append(userId+exchange.getMD5Keybuy());
                String sign = MD5Util.getMD5(result.toString());
                cipherText = sign;
            }else {
                type = "0";
                userId = exchange.getUserIdsell();
                isAll = "false";
                coinName = exchange.getCoinName();
                payCoinName = exchange.getPayCoinIdName();
                StringBuilder result = new StringBuilder();
                result.append(userId+exchange.getMD5Keysell());
                String sign = MD5Util.getMD5(result.toString());
                cipherText = sign;
            }

                TreeMap<String, String> params = new TreeMap<String, String>();
                params.put("type",type);
                params.put("cipherText",cipherText);
                params.put("userId",userId);
                params.put("isAll",isAll);
                params.put("coinName",coinName);
                params.put("payCoinName",payCoinName);

                ArrayList<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
                params.forEach((k,v) -> basicNameValuePairs.add(new BasicNameValuePair(k, v)));

                String resultJson = HttpUtil.doPost(exchange.getUris().getGetUriCancelOrderAll(), basicNameValuePairs );
                log.info("cancel all order params[{}], userid [{}], reult[{}]",JSON.toJSONString(params),exchange.getUserIdbuy(),resultJson);

        }else {
            // todo 还未使用  单个取消
            String entrustId= orderId;
            String userId = exchange.getUserIdbuy();

            StringBuilder result = new StringBuilder();
            result.append(userId+exchange.getMD5Keybuy());
            String sign = MD5Util.getMD5(result.toString());
            String cipherText  = sign;

            TreeMap<String, String> params = new TreeMap<String, String>();
            params.put("entrustId",entrustId);
            params.put("cipherText",cipherText);
            params.put("userId",userId);

            ArrayList<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
            params.forEach((k,v) -> basicNameValuePairs.add(new BasicNameValuePair(k, v)));
            System.out.println(JSON.toJSONString(params));

            String resultJson = HttpUtil.doPost(exchange.getUris().getUriCancelOrder(), basicNameValuePairs );
            log.info("cancel all order userid [{}]",exchange.getUserIdbuy());
            System.out.println(resultJson);

        }

    }





    //两个时间的差距
    public static boolean prepareTime(String currentTime,String indenTime,String clearTime) {
        boolean prep = false;
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long current;
        try {
            current = sim.parse(currentTime).getTime();
            long inden = sim.parse(indenTime).getTime();
            //int h = (int) ((current - inden) / 3600000);

            BigDecimal bi1 = new BigDecimal(current-inden);
            BigDecimal bi2 = new BigDecimal(3600000);
            Double biclear = Double.valueOf(clearTime);
            BigDecimal h = bi1.divide(bi2, 4, RoundingMode.HALF_UP);
            double hfinal = h.doubleValue();
            if (hfinal > biclear) {
                prep = true;
            } else {
                prep = false;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return prep;
    }


}
