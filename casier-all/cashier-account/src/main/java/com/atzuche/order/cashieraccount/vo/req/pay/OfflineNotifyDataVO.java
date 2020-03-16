package com.atzuche.order.cashieraccount.vo.req.pay;

import com.autoyol.autopay.gateway.vo.req.NotifyDataVo;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/11 6:03 下午
 **/
public class OfflineNotifyDataVO extends NotifyDataVo {
    private String payChannel;

    /**
     * 线上支付或者线下、虚拟支付（0-线上支付，1-线下支付，2-虚拟支付）
     */
    private int payLine;

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public int getPayLine() {
        return payLine;
    }

    public void setPayLine(int payLine) {
        this.payLine = payLine;
    }

    @Override
    public String toString() {
        return "OfflineNotifyDataVO{" +
                "payChannel='" + payChannel + '\'' +
                ", payLine=" + payLine + ","+super.toString()+
                '}';
    }
}
