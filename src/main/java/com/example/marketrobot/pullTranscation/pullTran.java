package com.example.marketrobot.pullTranscation;

import com.example.marketrobot.entity.OrderVo;
import com.example.marketrobot.service.Service;
import com.example.marketrobot.util.MD5Util;
import com.example.marketrobot.util.Util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jack on 2019/9/2.
 */
public class pullTran {
    // todo   运行前停止服务器脚本  保持手机勿扰模式
    public static void main(String[] args) throws ParseException, InterruptedException {
        boolean swf =  true;// 开关 负责控制哪一个账户买卖
       String  baseAmount = "1000"; // 基础数量
       int baseAmountRound = 50; // 数量浮动
        String beginDate = "2019-09-04 14:40:00";// 拉升开始时间   todo
        String endDate = "2019-09-04 14:50:00";//  拉升结束时间
        String changePrice  = "";// 变动过程中的价格

        String url = "http://47.56.43.220:8090/openApi/tradeApi/addEntrust"; // todo

        int baseTime   = 10000  ;// 每隔10秒 发一次订单
        int times = 60;// todo 此参数需要手动计算 10秒一次 一分钟六次  一共拉升10分钟  一共60次
        String timesprice = "0.012";// todo  此参数需要手动计算 最终价格-初始价格 / 次数

        String coinName  = "ETH"; // todo
        String payCoinName = "USDT";


       String basePrice  = "0.0671"; // 基础价格
       String glonalPrice  = "0.137"; //目标价格

        String userId_1  ="1309724"; // todo
        String md5Key_1  = "ETH"; // todo
        String secret_1  = "987321lv";// 密码 // todo

        String userId_2  ="1309724"; // todo
        String md5Key_2  = "ETH"; // todo
        String secret_2  = "987321lv";// 密码 // todo

        String sign = "";// 签名
        String  secret = "";//  密码
        String userId = "";// 用户id
        String md5Key = "";// 签名


        String type = "1"; // 1  买   2  卖

        changePrice  = basePrice;// 临时价格
        String pricefinal = "";// 最终价格





        while(true){
            long nowdata = new Date().getTime();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date bdate =  df.parse(beginDate);
            Date edate  = df.parse(endDate);
            if(nowdata>= bdate.getTime()) {
                if (swf) {
                    // 切换第一个账号卖
                    secret = secret_1;
                    userId = userId_1;
                    md5Key = md5Key_1;
                    type = "2";
                    StringBuilder result = new StringBuilder();
                    result.append(userId + coinName + secret + payCoinName + md5Key);
                    sign = MD5Util.getMD5(result.toString());
                    swf = false;
                } else {
                    // 切换第二个账号买
                    secret = secret_2;
                    userId = userId_2;
                    md5Key = md5Key_2;
                    type = "1";
                    StringBuilder result = new StringBuilder();
                    result.append(userId + coinName + secret + payCoinName + md5Key);
                    sign = MD5Util.getMD5(result.toString());
                    swf = true;
                }


                String amount = Util.random(baseAmount, baseAmountRound);
                // bdate.getTime();
                if (nowdata >= edate.getTime()) {
                    pricefinal = glonalPrice;// 等于最终价格
                    System.out.println("已经到达最终价格！！！！");
                    System.out.println("当前changprice是：" + changePrice);
                    System.out.println("pricefinal：" + pricefinal);
                } else {
                    BigDecimal basePriceBig = new BigDecimal(changePrice);
                    BigDecimal timesPriceBig = new BigDecimal(timesprice);
                    BigDecimal finalBig = timesPriceBig.add(basePriceBig);
                    changePrice = finalBig.toString();
                    pricefinal = Util.randomPrice(changePrice);// todo  此处的价格变动可以去手动调整小一点
                    System.out.println("当前changprice是：" + changePrice);
                    System.out.println("pricefinal：" + pricefinal);
                }

                    OrderVo orderVo = new OrderVo();
                    orderVo.setCoinName(coinName);
                    orderVo.setPayCoinName(payCoinName);
                    orderVo.setAmount(amount);
                    orderVo.setPrice(pricefinal);
                    orderVo.setType(type);
                    orderVo.setCipherText(sign);
                    orderVo.setSecret(secret);
                    orderVo.setUserId(userId);
                    Service service = new Service();
                    service.createOrder(orderVo, url);
                    Thread.sleep(baseTime);// 休眠多长时间发一次

            }

        }


    }
}
