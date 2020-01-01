package com.atzuche.order.delivery.service.handover;

import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.OwnerHandoverCarRemarkEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import com.atzuche.order.delivery.enums.HandoverCarTypeEnum;
import com.atzuche.order.delivery.enums.ServiceTypeEnum;
import com.atzuche.order.delivery.enums.UserTypeEnum;
import com.atzuche.order.delivery.exception.HandoverCarOrderException;
import com.atzuche.order.delivery.mapper.*;
import com.atzuche.order.delivery.service.CarService;
import com.atzuche.order.delivery.service.DeliveryMemberService;
import com.atzuche.order.delivery.service.DeliveryOrderService;
import com.atzuche.order.delivery.service.SendImMsgThirdService;
import com.atzuche.order.delivery.vo.CarBO;
import com.atzuche.order.delivery.vo.HandoverCarRenYunVO;
import com.atzuche.order.delivery.vo.OrderInfoVO;
import com.atzuche.order.delivery.vo.handover.HandoverCarVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author 胡春林
 * 交接车服务
 */
@Service
@Slf4j
public class HandoverCarService {

    @Autowired
    SendImMsgThirdService sendImMsgThirdService;

    @Autowired
    CarService carService;

    @Autowired
    DelegationCarAdminMapper delegationCarAdminMapper;

    @Autowired
    DeliveryMemberService deliveryMemberService;

    @Autowired
    DeliveryOrderService deliveryOrderService;
    @Resource
    RenterHandoverCarInfoMapper renterHandoverCarInfoMapper;
    @Resource
    RenterHandoverCarRemarkMapper renterHandoverCarRemarkMapper;
    @Resource
    OwnerHandoverCarInfoMapper ownerHandoverCarInfoMapper;
    @Resource
    OwnerHandoverCarRemarkMapper ownerHandoverCarRemarkMapper;

    /**
     * 向租客和车主发送交接车信息
     */
    public void handlerHandoverCarStepByTransInfo(HandoverCarRenYunVO handoverCarVO) {

        if (StringUtils.isBlank(handoverCarVO.getProId()) || !handoverCarVO.isUserType()) {
            return;
        }
        //todo 根据订单号查询订单获取车辆号 租客 车主 来源等数据 Trans trans = transProgressService.queryMemInfo(orderNo);
        OrderInfoVO orderInfoVO = new OrderInfoVO();
        int memNo;
        int userType = Integer.valueOf(handoverCarVO.getUserType());
        memNo = userType == 1 ? orderInfoVO.getRenterNo() : orderInfoVO.getOwnerNo();
        int proId = Integer.valueOf(handoverCarVO.getProId());
        String orderNoStr = String.valueOf(handoverCarVO.getOrderNo());
        if (memNo != 0 && (proId == 2 || proId == 6)
                && !("202".equals(orderInfoVO.getSource())
                || "400".equals(orderInfoVO.getSource())
                || "401".equals(orderInfoVO.getSource()))) {
            if (userType == UserTypeEnum.RENTER_TYPE.getValue().intValue()) {
                if (orderInfoVO.isGreaterThanZero()) {
                    log.info("租客订单号:renterOrderNo：{}", orderInfoVO.getRenterOrderNo());
                    orderNoStr = orderInfoVO.getRenterOrderNo().toString();
                }
                log.info("renterTransInfo: orderNo={}, renterOrderNo={}", handoverCarVO.getOrderNo(), orderInfoVO.getRenterOrderNo());
                if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                    sendImMsgThirdService.sendImMsg9798("97", String.valueOf(memNo), orderNoStr, false, "", handoverCarVO.getHeadName(), handoverCarVO.getHeadPhone());
                } else if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())) {
                    sendImMsgThirdService.sendImMsg9798("98", String.valueOf(memNo), orderNoStr, false, "", handoverCarVO.getHeadName(), handoverCarVO.getHeadPhone());
                }
            } else if (userType == UserTypeEnum.OWNER_TYPE.getValue().intValue()) {
                String toMemNo = String.valueOf(memNo);
                CarBO carInfo = carService.getCarInfoByCarNo(orderInfoVO.getCarNo());
                int ownerType = carInfo.getOwnerType();
                if (ownerType == 35) {
                    //查询代管车管理员
                    Long delegatMobile = delegationCarAdminMapper.getAdminMobileByCarNo(orderInfoVO.getCarNo());
                    if (delegatMobile != null) {
                        toMemNo = deliveryMemberService.getMemNoByMobile(delegatMobile.toString()).toString();
                    }
                }
                if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                    sendImMsgThirdService.sendImMsg130("130", toMemNo, String.valueOf(orderInfoVO.getOrderNo()), true, String.valueOf(orderInfoVO.getCarNo()), handoverCarVO.getHeadName(), handoverCarVO.getHeadPhone());
                } else if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())) {
                    sendImMsgThirdService.sendImMsg131("131", toMemNo, String.valueOf(orderInfoVO.getOrderNo()), true, String.valueOf(orderInfoVO.getCarNo()), handoverCarVO.getHeadName(), handoverCarVO.getHeadPhone());
                }
            }
        }

        if (memNo != 0 && ((proId == 4 && (handoverCarVO.getDescription().equals("车辆已交给租客") || handoverCarVO.getDescription().equals("已同租客交接完成"))) ||
                (proId == 8 && (handoverCarVO.getDescription().equals("车辆已交还给车主") || handoverCarVO.getDescription().equals("已同车主交接完成"))))
                && !("202".equals(orderInfoVO.getSource()) || "400".equals(orderInfoVO.getSource()) || "401".equals(orderInfoVO.getSource()))) {
            if (userType == UserTypeEnum.RENTER_TYPE.getValue().intValue()) {
                if (orderInfoVO.isGreaterThanZero()) {
                    log.info("租客订单号:renterOrderNo：{}", orderInfoVO.getRenterOrderNo());
                    orderNoStr = orderInfoVO.getRenterOrderNo().toString();
                }
                if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                    log.info("take car sendImMsg94 userType 1");
                    sendImMsgThirdService.sendImMsg94(String.valueOf(memNo), orderNoStr, "true", false, "");
                } else if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())) {
                    log.info("back car sendImMsg273 userType 1");
                    sendImMsgThirdService.sendImMsg273(String.valueOf(memNo), orderNoStr, "true", false, "");
                }
            } else if (userType == UserTypeEnum.OWNER_TYPE.getValue().intValue()) {
                if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                    log.info("take car sendImMsg94 userType 2");
                    sendImMsgThirdService.sendImMsg94(String.valueOf(memNo), orderNoStr, "false", true, String.valueOf(orderInfoVO.getCarNo()));
                } else if (handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())) {
                    log.info("back car sendImMsg273 userType 2");
                    sendImMsgThirdService.sendImMsg273(String.valueOf(memNo), orderNoStr, "false", true, String.valueOf(orderInfoVO.getCarNo()));
                }
            }
        }
    }

    /**
     * 新增交接车数据
     *
     * @param handoverCarVO
     */
    public void addHandoverCarInfo(HandoverCarVO handoverCarVO) {
        if (Objects.isNull(handoverCarVO) || handoverCarVO.getHandoverCarInfoDTO().getType() == null) {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        int type = handoverCarVO.getHandoverCarInfoDTO().getType().intValue();
        //向租客交车
        if (type == HandoverCarTypeEnum.OWNER_TO_RENTER.getValue().intValue() || type == HandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue()) {

            RenterHandoverCarInfoEntity renterHandoverCarInfoEntity = new RenterHandoverCarInfoEntity();
            BeanUtils.copyProperties(handoverCarVO.getHandoverCarInfoDTO(), renterHandoverCarInfoEntity);
            renterHandoverCarInfoMapper.insertSelective(renterHandoverCarInfoEntity);
            if (handoverCarVO.getHandoverCarRemarkDTO() != null) {
                RenterHandoverCarRemarkEntity renterHandoverCarRemarkEntity = new RenterHandoverCarRemarkEntity();
                BeanUtils.copyProperties(handoverCarVO.getHandoverCarRemarkDTO(), renterHandoverCarRemarkEntity);
                renterHandoverCarRemarkMapper.insertSelective(renterHandoverCarRemarkEntity);
            }
        } else if (type == HandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().intValue() || type == HandoverCarTypeEnum.RENTER_TO_OWNER.getValue().intValue()) {
            OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity = new OwnerHandoverCarInfoEntity();
            BeanUtils.copyProperties(handoverCarVO.getHandoverCarInfoDTO(), ownerHandoverCarInfoEntity);
            ownerHandoverCarInfoMapper.insertSelective(ownerHandoverCarInfoEntity);
            if (handoverCarVO.getHandoverCarRemarkDTO() != null) {
                OwnerHandoverCarRemarkEntity ownerHandoverCarRemarkEntity = new OwnerHandoverCarRemarkEntity();
                BeanUtils.copyProperties(handoverCarVO.getHandoverCarRemarkDTO(), ownerHandoverCarRemarkEntity);
                ownerHandoverCarRemarkMapper.insertSelective(ownerHandoverCarRemarkEntity);
            }
        } else {
            log.info("没有找到合适的交车类型，handoverCarVO:{}", handoverCarVO.toString());
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有找到合适的交车类型");
        }
    }

    /**
     * 根据消息ID获取
     * @param msgId
     * @return
     */
    public String getHandoverCarInfoByMsgId(String msgId) {
        if (StringUtils.isBlank(msgId)) {
            throw new HandoverCarOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR);
        }
        String handoverMsgId = renterHandoverCarInfoMapper.queryObjectByMsgId(msgId);
        if (StringUtils.isBlank(handoverMsgId)) {
            handoverMsgId = ownerHandoverCarInfoMapper.queryObjectByMsgId(msgId);
        }
        return handoverMsgId;
    }


}
