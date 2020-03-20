package com.atzuche.order.delivery.service.handover;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.commons.vo.req.handover.req.HandoverCarInfoReqDTO;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.OwnerHandoverCarRemarkEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import com.atzuche.order.delivery.mapper.OwnerHandoverCarInfoMapper;
import com.atzuche.order.delivery.mapper.OwnerHandoverCarRemarkMapper;
import com.atzuche.order.delivery.service.handover.interfaces.IUpdateHandoverCarInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author 胡春林
 * 车主端交接车信息
 */
@Slf4j
@Service
public class OwnerHandoverCarService implements IUpdateHandoverCarInfo {

    @Resource
    OwnerHandoverCarInfoMapper ownerHandoverCarInfoMapper;
    @Resource
    OwnerHandoverCarRemarkMapper ownerHandoverCarRemarkMapper;

    /**
     * 更新车主端交接车里程油耗数据
     *
     * @param handoverCarInfoReqDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateHandoverCarOilMileageNum(HandoverCarInfoReqDTO handoverCarInfoReqDTO) {

        List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntityList = selectOwnerByOrderNo(handoverCarInfoReqDTO.getOrderNo());
        ownerHandoverCarInfoEntityList.stream().forEach(r -> {
            if (r.getType().intValue() == RenterHandoverCarTypeEnum.OWNER_TO_RENTER.getValue() || r.getType().intValue() == RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue()) {
                if (StringUtils.isNotBlank(handoverCarInfoReqDTO.getRenterReturnOil())) {
                    r.setOilNum(Integer.valueOf(handoverCarInfoReqDTO.getRenterReturnOil()));
                }
                if (StringUtils.isNotBlank(handoverCarInfoReqDTO.getRenterRetrunKM())) {
                    r.setMileageNum(Integer.valueOf(handoverCarInfoReqDTO.getRenterRetrunKM()));
                }
            } else if (r.getType().intValue() == RenterHandoverCarTypeEnum.RENTER_TO_OWNER.getValue() || r.getType().intValue() == RenterHandoverCarTypeEnum.RENTER_TO_RENYUN.getValue()) {
                if (StringUtils.isNotBlank(handoverCarInfoReqDTO.getOwnReturnOil())) {
                    r.setOilNum(Integer.valueOf(handoverCarInfoReqDTO.getOwnReturnOil()));
                }
                if (StringUtils.isNotBlank(handoverCarInfoReqDTO.getOwnReturnKM())) {
                    r.setMileageNum(Integer.valueOf(handoverCarInfoReqDTO.getOwnReturnKM()));
                }
            }
            log.info("车主更新油耗里程--->>>>:handoverCarInfoReqDTO:[{}],r-->>[{}]",handoverCarInfoReqDTO.toString(), JSONObject.toJSONString(r));
            updateOwnerHandoverInfoByPrimaryKey(r);
        });
    }


    /**
     * 获取車主交接车数据
     *
     * @param orderNo
     * @return
     */
    public List<OwnerHandoverCarInfoEntity> selectOwnerByOrderNo(String orderNo) {
        return ownerHandoverCarInfoMapper.selectOwnerByOrderNo(orderNo);
    }

    /**
     * 根据订单号和类型获取车主交接车数据
     *
     * @param orderNo
     * @return
     */
    public OwnerHandoverCarInfoEntity selectObjectByOrderNo(String orderNo, Integer type) {
        return ownerHandoverCarInfoMapper.selectObjectByOrderNo(orderNo, type);
    }

    /**
     * 根据子订单号获取车主交接车数据
     *
     * @param renterOrderNo
     * @return
     */
    public List<OwnerHandoverCarInfoEntity> selectOwnerHandoverCarByOrderNo(String renterOrderNo) {
        return ownerHandoverCarInfoMapper.selectObjectByOwnerOrderNo(renterOrderNo);
    }

    /**
     * 获取车主备注信息
     *
     * @param orderNo
     * @return
     */
    public List<OwnerHandoverCarRemarkEntity> getOwnerHandoverRemarkInfo(String orderNo) {
        return ownerHandoverCarRemarkMapper.selectObjectByOrderNo(orderNo);
    }

    /**
     * 更新车主交接车信息
     *
     * @param ownerHandoverCarInfoEntity
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer updateOwnerHandoverInfoByPrimaryKey(OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity) {
        return ownerHandoverCarInfoMapper.updateByPrimaryKeySelective(ownerHandoverCarInfoEntity);
    }

    /**
     * 根据消息ID查询
     *
     * @param msgId
     * @return
     */
    public String queryObjectByMsgId(String msgId) {
        return ownerHandoverCarInfoMapper.queryObjectByMsgId(msgId);
    }

    /**
     * 新增车主配送订单信息
     *
     * @param ownerHandoverCarInfoEntity
     */
    public void insertOwnerHandoverCarInfo(OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity) {
        ownerHandoverCarInfoMapper.insertSelective(ownerHandoverCarInfoEntity);
    }

    /**
     * 新增车主配送订单地址信息
     *
     * @param ownerHandoverCarRemarkEntity
     */
    public void insertOwnerHandoverCarRemark(OwnerHandoverCarRemarkEntity ownerHandoverCarRemarkEntity) {
        ownerHandoverCarRemarkMapper.insertSelective(ownerHandoverCarRemarkEntity);
    }

    /**
     * 更新车主配送订单地址信息
     *
     * @param ownerHandoverCarRemarkEntity
     */
    public void updateOwnerHandoverRemarkByPrimaryKey(OwnerHandoverCarRemarkEntity ownerHandoverCarRemarkEntity) {
        ownerHandoverCarRemarkMapper.updateByPrimaryKeySelective(ownerHandoverCarRemarkEntity);
    }

    /**
     * 根据订单号获取车主备注信息
     *
     * @param orderNo
     * @return
     */
    public OwnerHandoverCarRemarkEntity selectOwnerHandoverRemarkByOrderNoType(String orderNo, Integer type) {
        return ownerHandoverCarRemarkMapper.selectObjectByOrderNoType(orderNo, type);
    }

    public OwnerHandoverCarInfoEntity selectByRenterOrderNoAndType(String renterOrderNo, Integer type) {
        return ownerHandoverCarInfoMapper.selectByOwnerOrderNoAndType(renterOrderNo, type);
    }
    /**
     * 根据流程ID获取
     * @param orderNo
     * @return
     */
    public List<OwnerHandoverCarRemarkEntity> selectProIdByOrderNo(String orderNo) {
        return ownerHandoverCarRemarkMapper.selectProIdByOrderNo(orderNo);
    }

}
