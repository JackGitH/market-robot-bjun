package com.example.marketrobot.firstTranscation;

import com.example.marketrobot.entity.OrderVo;
import com.example.marketrobot.service.Service;
import com.example.marketrobot.util.MD5Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jack on 2019/9/2.
 * 到时间 发送第一笔交易的方法  用的手机信号 则拒绝所有来电
 */
public class firstTran {
    // todo  运行前 保持手机勿扰模式
    public static void main(String[] args) throws ParseException, InterruptedException {
        // todo  前提手动挂一个卖单   这里是下买单 标todo的都是需要改的
        boolean swithc = true; // 发完第一笔交易以后停止的开关
        String beginDate = "2019-09-02 19:05:00";// 开始第一笔订单的时间   todo
        String url = "http://47.56.43.220:8090/openApi/tradeApi/addEntrust"; // todo
        String md5Key = "UL1SfRaJbZEARgoX";  // todo

        String userId  ="1309724"; // todo
        String coinName  = "ETH"; // todo
        String secret  = "987321lv";// 密码 // todo

        String payCoinName = "USDT";
        String amount = "1"; // todo
        String price = "195"; // todo
        String type = "1"; // 1  买   2  卖

        StringBuilder result = new StringBuilder();
        result.append(userId+coinName+secret+payCoinName+md5Key);
        String sign = MD5Util.getMD5(result.toString());


        while(true){
            long nowdata = new Date().getTime();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date tdate =  df.parse(beginDate);
            tdate.getTime();
           // if(nowdata>= tdate.getTime() && swithc){
            if(nowdata>= tdate.getTime()){
                OrderVo orderVo = new OrderVo();
                orderVo.setCoinName(coinName);
                orderVo.setPayCoinName(payCoinName);
                orderVo.setAmount(amount);
                orderVo.setPrice(price);
                orderVo.setType(type);
                orderVo.setCipherText(sign);
                orderVo.setSecret(secret);
                orderVo.setUserId(userId);
                Service service  = new Service();
                service.createOrder(orderVo,url);
               // swithc = false;
                System.out.println("定时任务成功");
                Thread.sleep(1000);
            }

        }
    }
}
