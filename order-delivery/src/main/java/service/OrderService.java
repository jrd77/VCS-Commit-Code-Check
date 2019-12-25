package service;

import org.springframework.stereotype.Service;

@Service
public class OrderService {

    /**
     * 获取订单图片数据
     * @param orderNo
     * @return
     */
    public String selectOrderCoverPic(String orderNo){return null;}

    public Integer getPlatformMessageByEvent(Integer event ){return null;}

    /**
     * 根据订单号返回套餐ID
     * @param orderNo
     * @return
     */
    public Long getPackageOrderNoByOrderNo(long orderNo){return null;}
}
