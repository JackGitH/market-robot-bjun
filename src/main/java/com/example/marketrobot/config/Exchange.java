package com.example.marketrobot.config;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
public class Exchange {
    private String website;
    private String userIdbuy;
    private String secretbuy;
    private String MD5Keybuy;
    private String userIdsell;
    private String secretsell;
    private String MD5Keysell;
    private long waitTimes;
    private int buyfluctuate;
    private int sellfluctuate;
    private String sellBasePrice;
    private String buyBasePrice;
    private int priceScale;
    private int amountFluctuate;
    private String buyamount;
    private String sellAmount;
    private int buyOrderRatio;
    private Uris uris;
    private String coinName;
    private String payCoinIdName;

    private String buyType;
    private String sellType;
    private String clearTime;

    private boolean stopBuy;
    private boolean stopSell;

    public int getBuyfluctuate() {
        return buyfluctuate;
    }

    public int getBuyOrderRatio() {
        return buyOrderRatio;
    }

    public int getPriceScale() {
        return priceScale;
    }

    public int getSellfluctuate() {
        return sellfluctuate;
    }

    public long getWaitTimes() {
        return waitTimes;
    }

    public int getAmountFluctuate() {
        return amountFluctuate;
    }

    public String getBuyamount() {
        return buyamount;
    }



    public String getSellAmount() {
        return sellAmount;
    }

    public String getBuyType() {
        return buyType;
    }



    public Uris getUris() {
        return uris;
    }

    public void setAmountFluctuate(int amountFluctuate) {
        this.amountFluctuate = amountFluctuate;
    }

    public void setBuyamount(String buyamount) {
        this.buyamount = buyamount;
    }

    public void setBuyOrderRatio(int buyOrderRatio) {
        this.buyOrderRatio = buyOrderRatio;
    }

    public void setBuyfluctuate(int buyfluctuate) {
        this.buyfluctuate = buyfluctuate;
    }



    public void setPriceScale(int priceScale) {
        this.priceScale = priceScale;
    }



    public void setWaitTimes(long waitTimes) {
        this.waitTimes = waitTimes;
    }

    public void setSellfluctuate(int sellfluctuate) {
        this.sellfluctuate = sellfluctuate;
    }

    public void setSellAmount(String sellAmount) {
        this.sellAmount = sellAmount;
    }

    public void setUris(Uris uris) {
        this.uris = uris;
    }



    public void setBuyType(String buyType) {
        this.buyType = buyType;
    }

    public String getSellType() {
        return sellType;
    }

    public void setSellType(String sellType) {
        this.sellType = sellType;
    }

    public String getSellBasePrice() {
        return sellBasePrice;
    }

    public void setSellBasePrice(String sellBasePrice) {
        this.sellBasePrice = sellBasePrice;
    }

    public String getBuyBasePrice() {
        return buyBasePrice;
    }

    public void setBuyBasePrice(String buyBasePrice) {
        this.buyBasePrice = buyBasePrice;
    }

    public boolean isStopBuy() {
        return stopBuy;
    }

    public void setStopBuy(boolean stopBuy) {
        this.stopBuy = stopBuy;
    }

    public boolean isStopSell() {
        return stopSell;
    }

    public void setStopSell(boolean stopSell) {
        this.stopSell = stopSell;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }




    public String getUserIdbuy() {
        return userIdbuy;
    }

    public void setUserIdbuy(String userIdbuy) {
        this.userIdbuy = userIdbuy;
    }

    public String getSecretbuy() {
        return secretbuy;
    }

    public void setSecretbuy(String secretbuy) {
        this.secretbuy = secretbuy;
    }

    public String getMD5Keybuy() {
        return MD5Keybuy;
    }

    public void setMD5Keybuy(String MD5Keybuy) {
        this.MD5Keybuy = MD5Keybuy;
    }

    public String getUserIdsell() {
        return userIdsell;
    }

    public void setUserIdsell(String userIdsell) {
        this.userIdsell = userIdsell;
    }

    public String getSecretsell() {
        return secretsell;
    }

    public void setSecretsell(String secretsell) {
        this.secretsell = secretsell;
    }

    public String getMD5Keysell() {
        return MD5Keysell;
    }

    public void setMD5Keysell(String MD5Keysell) {
        this.MD5Keysell = MD5Keysell;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getPayCoinIdName() {
        return payCoinIdName;
    }

    public void setPayCoinIdName(String payCoinIdName) {
        this.payCoinIdName = payCoinIdName;
    }

    public String getClearTime() {
        return clearTime;
    }

    public void setClearTime(String clearTime) {
        this.clearTime = clearTime;
    }
}
