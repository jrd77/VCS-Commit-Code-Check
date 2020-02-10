package com.atzuche.order.transport.interfaces;

import com.atzuche.order.commons.vo.res.delivery.RenterOrderDeliveryRepVO;
import com.autoyol.commons.web.ResponseData;

import java.util.List;

/**
 * @author 胡春林
 * 配送相关费用接口
 */
public interface IDeliveryCarFee<E> {

    /**
     * 获取费用数据
     * @param orderNo
     * @return
     */
    E getDeliveryCarFee(String orderNo,List<RenterOrderDeliveryRepVO> renterOrderDeliveryRepVOList);

    /**
     * 取车相关费用
     *
     * @return
     */
    Double getCarFee();

    /**
     * 还车相关费用
     *
     * @return
     */
    Double returnCarFee();

    /**
     * 远程获取数据
     * @return
     */
    ResponseData<?> getDeliveryCarFeeFromTargetFeign(Object serializable);
}
