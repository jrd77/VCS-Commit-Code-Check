package com.atzuche.order.delivery.service.handover;

import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import com.atzuche.order.delivery.mapper.RenterHandoverCarInfoMapper;
import com.atzuche.order.delivery.mapper.RenterHandoverCarRemarkMapper;
import com.atzuche.order.delivery.service.handover.interfaces.IUpdateHandoverCarInfo;
import com.atzuche.order.delivery.vo.handover.req.HandoverCarInfoReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 胡春林
 * 更新租客取还车信息
 */
@Service
public class RenterHandoverCarService implements IUpdateHandoverCarInfo {

    @Resource
    RenterHandoverCarInfoMapper renterHandoverCarInfoMapper;
    @Resource
    RenterHandoverCarRemarkMapper renterHandoverCarRemarkMapper;

    @Autowired
    HandoverCarService handoverCarService;

    /**
     * 更新租客交接车里程油耗数据
     *
     * @param handoverCarInfoReqDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateHandoverCarOilMileageNum(HandoverCarInfoReqDTO handoverCarInfoReqDTO) {
        RenterHandoverCarInfoEntity renterHandoverCarReturnInfoEntity = selectObjectByOrderNo(handoverCarInfoReqDTO.getOrderNo(), RenterHandoverCarTypeEnum.RENTER_TO_RENYUN.getValue());
        renterHandoverCarReturnInfoEntity.setOilNum(Integer.valueOf(handoverCarInfoReqDTO.getRenterReturnOil()));
        renterHandoverCarReturnInfoEntity.setMileageNum(Integer.valueOf(handoverCarInfoReqDTO.getOwnReturnKM()));
        updateRenterHandoverInfoByPrimaryKey(renterHandoverCarReturnInfoEntity);
        RenterHandoverCarInfoEntity renterHandoverCarGetInfoEntity = selectObjectByOrderNo(handoverCarInfoReqDTO.getOrderNo(), RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue());
        renterHandoverCarGetInfoEntity.setOilNum(Integer.valueOf(handoverCarInfoReqDTO.getOwnReturnOil()));
        renterHandoverCarGetInfoEntity.setMileageNum(Integer.valueOf(handoverCarInfoReqDTO.getRenterRetrunKM()));
        updateRenterHandoverInfoByPrimaryKey(renterHandoverCarGetInfoEntity);
    }


    /**
     * 根据订单号获取租客交接车数据
     *
     * @param orderNo
     * @return
     */
    public List<RenterHandoverCarInfoEntity> selectRenterByOrderNo(String orderNo) {
        return renterHandoverCarInfoMapper.selectRenterByOrderNo(orderNo);
    }

    /**
     * 根据订单号和类型获取租客交接车数据
     *
     * @param orderNo
     * @return
     */
    public RenterHandoverCarInfoEntity selectObjectByOrderNo(String orderNo, Integer type) {
        return renterHandoverCarInfoMapper.selectObjectByOrderNo(orderNo, type);
    }

    /**
     * 根据子订单号获取租客交接车数据
     *
     * @param renterOrderNo
     * @return
     */
    public List<RenterHandoverCarInfoEntity> selectRenterHandoverCarByOrderNo(String renterOrderNo) {
        return renterHandoverCarInfoMapper.selectByRenterOrderNo(renterOrderNo);
    }

    /**
     * 根据订单号获取租客备注信息
     *
     * @param orderNo
     * @return
     */
    public List<RenterHandoverCarRemarkEntity> getRenterHandoverRemarkInfo(String orderNo) {
        return renterHandoverCarRemarkMapper.selectObjectByOrderNo(orderNo);
    }

    /**
     * 根据订单号获取租客备注信息
     *
     * @param orderNo
     * @return
     */
    public RenterHandoverCarRemarkEntity selectRenterHandoverRemarkByOrderNoType(String orderNo, Integer type) {
        return renterHandoverCarRemarkMapper.selectObjectByOrderNoType(orderNo, type);
    }

    /**
     * 更新租客交接车信息
     *
     * @param renterHandoverCarInfoEntity
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateRenterHandoverInfoByPrimaryKey(RenterHandoverCarInfoEntity renterHandoverCarInfoEntity) {
        return renterHandoverCarInfoMapper.updateByPrimaryKey(renterHandoverCarInfoEntity);
    }

    /**
     * 根据消息ID查询
     *
     * @param msgId
     * @return
     */
    public String queryObjectByMsgId(String msgId) {
        return renterHandoverCarInfoMapper.queryObjectByMsgId(msgId);
    }

    /**
     * 新增租客配送订单信息
     *
     * @param renterHandoverCarInfoEntity
     */
    public void insertRenterHandoverCar(RenterHandoverCarInfoEntity renterHandoverCarInfoEntity) {
        renterHandoverCarInfoMapper.insertSelective(renterHandoverCarInfoEntity);
    }

    /**
     * 新增租客配送订单地址备注信息
     *
     * @param renterHandoverCarRemarkEntity
     */
    public void insertRenterHandoverCarRemark(RenterHandoverCarRemarkEntity renterHandoverCarRemarkEntity) {
        renterHandoverCarRemarkMapper.insertSelective(renterHandoverCarRemarkEntity);
    }


    /**
     * 新增租客配送订单地址备注信息
     *
     * @param renterHandoverCarRemarkEntity
     */
    public void updateRenterHandoverRemarkByPrimaryKey(RenterHandoverCarRemarkEntity renterHandoverCarRemarkEntity) {
        renterHandoverCarRemarkMapper.updateByPrimaryKeySelective(renterHandoverCarRemarkEntity);
    }

}
