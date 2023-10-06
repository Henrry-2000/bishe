package com.yhq.bishe.constant;

public interface BookingConstant {
    /**
     * 已退款
     */
    int REFUND = 3;
    /**
     * 取消订单（未支付）
     */
    int CANCEL = 2;
    /***
     * 支付成功
     */
    int PAID = 1;
    /**
     * 待支付
     */
    int WAITING = 0;

    /**
     * 包场
     */
    int SINGLE = 0;
    /**
     * 散客
     */
    int GROUP = 1;



}
