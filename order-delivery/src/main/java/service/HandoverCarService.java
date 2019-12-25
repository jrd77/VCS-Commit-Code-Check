package service;

import enums.ServiceTypeEnum;
import enums.UserTypeEnum;
import lombok.extern.slf4j.Slf4j;
import mapper.DelegationCarAdminMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vo.CarBO;
import vo.HandoverCarVO;
import vo.OrderInfoVO;

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
    MemberService memberService;

    @Autowired
    OrderService orderService;

    /**
     * 向租客和车主发送交接车信息
     */
    public void handlerHandoverCarStepByTransInfo(HandoverCarVO handoverCarVO){

        if(StringUtils.isBlank(handoverCarVO.getProId()) || !handoverCarVO.isUserType())
        {
            return;
        }
        //todo 根据订单号查询订单获取车辆号 租客 车主 来源等数据 Trans trans = transProgressService.queryMemInfo(orderNo);
        OrderInfoVO orderInfoVO = new OrderInfoVO();
        int memNo;
        int userType = Integer.valueOf(handoverCarVO.getUserType());
        memNo = userType == 1 ? orderInfoVO.getRenterNo() : orderInfoVO.getOwnerNo();
        int proId = Integer.valueOf(handoverCarVO.getProId());
        String orderNoStr = String.valueOf(handoverCarVO.getOrderNo());
        if(memNo != 0 && (proId == 2 || proId == 6)
                && !("202".equals(orderInfoVO.getSource())
                || "400".equals(orderInfoVO.getSource())
                || "401".equals(orderInfoVO.getSource())))
        {
            if(userType == UserTypeEnum.RENTER_TYPE.getValue().intValue())
            {
                if(orderInfoVO.isGreaterThanZero()) {
                    log.info("租客订单号:renterOrderNo：{}",orderInfoVO.getRenterOrderNo());
                    orderNoStr = orderInfoVO.getRenterOrderNo().toString();
                }
                log.info("renterTransInfo: orderNo={}, renterOrderNo={}", handoverCarVO.getOrderNo(), orderInfoVO.getRenterOrderNo());
                if(handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                    sendImMsgThirdService.sendImMsg9798("97", String.valueOf(memNo), orderNoStr, false, "", handoverCarVO.getHeadName(), handoverCarVO.getHeadPhone());
                } else if(handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())){
                    sendImMsgThirdService.sendImMsg9798("98", String.valueOf(memNo), orderNoStr, false, "",handoverCarVO.getHeadName(), handoverCarVO.getHeadPhone());
                }
            }
            else if(userType == UserTypeEnum.OWNER_TYPE.getValue().intValue())
            {
                String toMemNo = String.valueOf(memNo);
                CarBO carInfo=carService.getCarInfoByCarNo(orderInfoVO.getCarNo());
                int ownerType=carInfo.getOwnerType();
                if(ownerType==35) {
                    //查询代管车管理员
                    Long delegatMobile=delegationCarAdminMapper.getAdminMobileByCarNo(orderInfoVO.getCarNo());
                    if(delegatMobile!=null){ toMemNo = memberService.getMemNoByMobile(delegatMobile.toString()).toString();
                    }
                }
                if(handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                    sendImMsgThirdService.sendImMsg130("130", toMemNo,String.valueOf(orderInfoVO.getOrderNo()), true,String.valueOf(orderInfoVO.getCarNo()),  handoverCarVO.getHeadName(), handoverCarVO.getHeadPhone());
                } else if(handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())){
                    sendImMsgThirdService.sendImMsg131("131", toMemNo,String.valueOf(orderInfoVO.getOrderNo()), true, String.valueOf(orderInfoVO.getCarNo()), handoverCarVO.getHeadName(), handoverCarVO.getHeadPhone());
                }

            }
        }

        if (memNo != 0 && ((proId==4 && (handoverCarVO.getDescription().equals("车辆已交给租客") || handoverCarVO.getDescription().equals("已同租客交接完成"))) ||
                (proId==8 && (handoverCarVO.getDescription().equals("车辆已交还给车主") || handoverCarVO.getDescription().equals("已同车主交接完成"))))
                && !("202".equals(orderInfoVO.getSource()) || "400".equals(orderInfoVO.getSource()) || "401".equals(orderInfoVO.getSource()))) {
            if (userType == UserTypeEnum.RENTER_TYPE.getValue().intValue()) {
                if(orderInfoVO.isGreaterThanZero()) {
                    log.info("租客订单号:renterOrderNo：{}",orderInfoVO.getRenterOrderNo());
                    orderNoStr = orderInfoVO.getRenterOrderNo().toString();
                }
                if(handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                    log.info("take car sendImMsg94 userType 1");
                    sendImMsgThirdService.sendImMsg94(String.valueOf(memNo), orderNoStr, "true", false, "");
                } else if(handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())) {
                    log.info("back car sendImMsg273 userType 1");
                    sendImMsgThirdService.sendImMsg273(String.valueOf(memNo), orderNoStr, "true", false, "");
                }
            } else if (userType == UserTypeEnum.OWNER_TYPE.getValue().intValue()) {
                if(handoverCarVO.getServiceType().equals(ServiceTypeEnum.TAKE_TYPE.getValue())) {
                    log.info("take car sendImMsg94 userType 2");
                    sendImMsgThirdService.sendImMsg94(String.valueOf(memNo), orderNoStr, "false", true, String.valueOf(orderInfoVO.getCarNo()));
                } else if(handoverCarVO.getServiceType().equals(ServiceTypeEnum.BACK_TYPE.getValue())) {
                    log.info("back car sendImMsg273 userType 2");
                    sendImMsgThirdService.sendImMsg273(String.valueOf(memNo), orderNoStr, "false", true, String.valueOf(orderInfoVO.getCarNo()));
                }
            }
        }

    }

}
