package com.example.marketrobot.entity;

public class OrderVo {
    private String coinName;// 交易币种名称
    private String payCoinName;// 支付币种名称
    private String amount;// 数量
    private String price;// 委托价格
    private String type;// 交易类型 1 买 2 卖
    private String cipherText;// 秘钥
    private String secret;// 交易密码
    private String userId;// 用户id

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getPayCoinName() {
        return payCoinName;
    }

    public void setPayCoinName(String payCoinName) {
        this.payCoinName = payCoinName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        this.cipherText = cipherText;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
