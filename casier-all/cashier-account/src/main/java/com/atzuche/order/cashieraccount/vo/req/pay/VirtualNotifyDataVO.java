package com.atzuche.order.cashieraccount.vo.req.pay;

import com.atzuche.order.commons.vo.NotifyDataDTO;
import com.autoyol.autopay.gateway.vo.req.NotifyDataVo;
import org.springframework.beans.BeanUtils;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/3/11 4:05 下午
 **/
public class VirtualNotifyDataVO extends NotifyDataVo {
    /**
     * 线上支付或者线下、虚拟支付（0-线上支付，1-线下支付，2-虚拟支付）
     */
    private int payLine;
    /**
     * 虚拟支付对应的成本账号
     */
    private String virtualAccountNo;
    /**
     * 线下支付的渠道名称
     */
    private String payChannel;

    public int getPayLine() {
        return payLine;
    }

    public void setPayLine(int payLine) {
        this.payLine = payLine;
    }

    public String getVirtualAccountNo() {
        return virtualAccountNo;
    }

    public void setVirtualAccountNo(String virtualAccountNo) {
        this.virtualAccountNo = virtualAccountNo;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }


    public static void main(String[] args) {
        VirtualNotifyDataVO virtualNotifyDataVO = new VirtualNotifyDataVO();
        virtualNotifyDataVO.setPayLine(1);
        virtualNotifyDataVO.setMemNo("22");
        test(virtualNotifyDataVO);

    }

    public static void test(NotifyDataVo notifyDataVo){
        NotifyDataDTO notifyDataDTO = new NotifyDataDTO();
        BeanUtils.copyProperties(notifyDataVo,notifyDataDTO);

        System.out.println(notifyDataDTO);
    }
}
