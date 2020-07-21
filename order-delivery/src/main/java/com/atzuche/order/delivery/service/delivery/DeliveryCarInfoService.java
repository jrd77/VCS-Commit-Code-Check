package com.atzuche.order.delivery.service.delivery;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.alibaba.fastjson.JSONObject;
import com.atzuche.order.accountownercost.entity.AccountOwnerCostSettleDetailEntity;
import com.atzuche.order.accountownercost.service.notservice.AccountOwnerCostSettleDetailNoTService;
import com.atzuche.order.accountownercost.util.AccountOwnerCostSettleUtil;
import com.atzuche.order.accountplatorm.entity.AccountPlatformProfitDetailEntity;
import com.atzuche.order.accountplatorm.service.notservice.AccountPlatformProfitDetailNotService;
import com.atzuche.order.accountplatorm.util.AccountPlatformProfitUtil;
import com.atzuche.order.commons.enums.CarOwnerTypeEnum;
import com.atzuche.order.commons.enums.account.SettleStatusEnum;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.parentorder.entity.OrderStatusEntity;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.atzuche.order.transport.service.GetReturnCarCostService;
import com.atzuche.order.transport.vo.GetReturnResponseVO;
import com.autoyol.doc.annotation.AutoDocProperty;
import com.autoyol.platformcost.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnCarCostReqDto;
import com.atzuche.order.commons.entity.dto.GetReturnCarOverCostReqDto;
import com.atzuche.order.commons.entity.dto.MileageAmtDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.delivery.common.delivery.TranSportService;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.handover.HandoverCarService;
import com.atzuche.order.delivery.service.handover.OwnerHandoverCarService;
import com.atzuche.order.delivery.service.handover.RenterHandoverCarService;
import com.atzuche.order.delivery.utils.CommonUtil;
import com.atzuche.order.delivery.utils.DateUtils;
import com.atzuche.order.delivery.vo.delivery.rep.DeliveryCarVO;
import com.atzuche.order.delivery.vo.delivery.rep.GetHandoverCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.ReturnHandoverCarDTO;
import com.atzuche.order.delivery.vo.delivery.req.DeliveryCarRepVO;
import com.atzuche.order.rentercommodity.service.RenterCommodityService;
import com.atzuche.order.transport.service.GetReturnCarCostProxyService;
import com.atzuche.order.transport.service.TranSportProxyService;
import com.atzuche.order.transport.vo.GetReturnCostDTO;
import com.atzuche.order.transport.vo.GetReturnOverCostDTO;
import com.autoyol.platformcost.OwnerFeeCalculatorUtils;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 胡春林
 * 配送信息服务
 */
@Service
@Slf4j
public class DeliveryCarInfoService {

    @Autowired
    DeliveryCarService deliveryCarService;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    HandoverCarService handoverCarService;
    @Autowired
    DeliveryCarInfoPriceService deliveryCarInfoPriceService;
    @Autowired
    RenterHandoverCarService renterHandoverCarService;
    @Autowired
    OwnerHandoverCarService ownerHandoverCarService;
    @Autowired
    TranSportService tranSportService;
    @Autowired
    TranSportProxyService tranSportProxyService;
    @Autowired
    GetReturnCarCostProxyService getReturnCarCostProxyService;
    @Autowired
    RenterCommodityService renterCommodityService;
    @Autowired
    GetReturnCarCostService getReturnCarCostService;
    @Value("${auto.cost.configHours}")
    private Integer configHours;
    @Autowired
    RenterOrderService renterOrderService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private AccountOwnerCostSettleDetailNoTService accountOwnerCostSettleDetailNoTService;
    @Autowired
    private AccountPlatformProfitDetailNotService accountPlatformProfitDetailNotService;

    /**
     * 获取配送相关信息（待优化）
     * @param deliveryCarDTO
     * @return
     */
    public DeliveryCarVO findDeliveryListByOrderNo(String renterOrderNo,DeliveryCarRepVO deliveryCarDTO, OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO, Integer carEngineType,int carType,RenterGoodsDetailDTO renterGoodsDetailDTO) {
        List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities = renterHandoverCarService.selectRenterByOrderNo(deliveryCarDTO.getOrderNo());
        List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities = ownerHandoverCarService.selectOwnerByOrderNo(deliveryCarDTO.getOrderNo());
        List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList = renterOrderDeliveryService.selectByRenterOrderNo(renterOrderNo);
        DeliveryCarVO deliveryCarVO = createDeliveryCarVOParams(ownerGetAndReturnCarDTO, renterHandoverCarInfoEntities, ownerHandoverCarInfoEntities, renterOrderDeliveryEntityList, carEngineType, carType, renterGoodsDetailDTO);

        return deliveryCarVO;
    }


    /**
     * 构造结构体(待优化)
     * @param renterHandoverCarInfoEntities
     * @param ownerHandoverCarInfoEntities
     * @return
     */
    public DeliveryCarVO createDeliveryCarVOParams(OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO,
                                                   List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities,
                                                   List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities,
                                                   List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList,
                                                   Integer carEngineType,
                                                   Integer carType,
                                                   RenterGoodsDetailDTO renterGoodsDetailDTO
                                                   ) {
        DeliveryCarVO deliveryCarVO = new DeliveryCarVO();

        GetReturnCarCostReqDto getReturnCarCostReqDto = new GetReturnCarCostReqDto();
        for (RenterOrderDeliveryEntity renterOrderDeliveryEntity : renterOrderDeliveryEntityList) {
            if (null == renterOrderDeliveryEntity) {
                continue;
            }
            if (renterOrderDeliveryEntity.getType().intValue() == 1) {
                getReturnCarCostReqDto.setIsGetCarCost(true);
                deliveryCarVO.setIsGetCar(renterOrderDeliveryEntity.getIsNotifyRenyun());
            } else { //还车
                getReturnCarCostReqDto.setIsReturnCarCost(true);
                deliveryCarVO.setIsReturnCar(renterOrderDeliveryEntity.getIsNotifyRenyun());
            }
            getReturnCarCostReqDto.setCityCode(Integer.valueOf(renterOrderDeliveryEntity.getCityCode()));
            getReturnCarCostReqDto.setIsPackageOrder(false);
            createGetHandoverCar(getReturnCarCostReqDto,deliveryCarVO, renterOrderDeliveryEntity,carType,renterGoodsDetailDTO);
        }
        getReturnCarCostReqDto.setCarShowLon(renterGoodsDetailDTO.getCarShowLon());
        getReturnCarCostReqDto.setCarShowLat(renterGoodsDetailDTO.getCarShowLat());
        GetReturnCostDTO getReturnCostDTO = getReturnCarCostProxyService.getReturnCarCost(getReturnCarCostReqDto);
        GetReturnResponseVO getReturnResponseVO = getReturnCostDTO.getGetReturnResponseVO();
        if(Objects.nonNull(getReturnCostDTO)) {
//            double getCarCost = getReturnResponseVO.getGetFee()+Double.valueOf(StringUtils.isNotEmpty(getReturnResponseVO.getGetTimePeriodUpPrice()) == true ? getReturnResponseVO.getGetTimePeriodUpPrice() : "0")+Double.valueOf(StringUtils.isNotEmpty(getReturnResponseVO.getGetCicrleUpPrice()) == true ? getReturnResponseVO.getGetCicrleUpPrice() : "0")+
//                    Double.valueOf(StringUtils.isNotEmpty(getReturnResponseVO.getGetDistanceUpPrice()) == true ? getReturnResponseVO.getGetDistanceUpPrice() : "0");
//            double returnCarCost = getReturnResponseVO.getReturnFee()+Double.valueOf(StringUtils.isNotEmpty(getReturnResponseVO.getReturnTimePeriodUpPrice()) == true ? getReturnResponseVO.getReturnTimePeriodUpPrice() : "0")+Double.valueOf(StringUtils.isNotEmpty(getReturnResponseVO.getReturnCicrleUpPrice()) == true ? getReturnResponseVO.getReturnCicrleUpPrice() : "0")+
//                    Double.valueOf(StringUtils.isNotEmpty(getReturnResponseVO.getReturnDistanceUpPrice()) == true ? getReturnResponseVO.getReturnDistanceUpPrice() : "0");
//            deliveryCarVO.getGetHandoverCarDTO().setGetCarCrash(String.valueOf((new Double(getCarCost)).intValue()));
//            deliveryCarVO.getReturnHandoverCarDTO().setReturnCarCrash(String.valueOf(new Double(returnCarCost).intValue()));
            deliveryCarVO.getGetHandoverCarDTO().setGetCarCrash(String.valueOf(getReturnResponseVO.getGetFee()));
            deliveryCarVO.getReturnHandoverCarDTO().setReturnCarCrash(String.valueOf(getReturnResponseVO.getReturnFee()));

        }
        //取车时的所在城市
        RenterOrderDeliveryEntity renterOrderDelivery = renterOrderDeliveryEntityList.stream().filter(r->r.getType() == 1).findFirst().get();
        String cityCode = renterOrderDelivery.getCityCode();
        String  tenancy = CommonUtils.getRentDays(renterOrderDelivery.getRentTime(), renterOrderDelivery.getRevertTime(), configHours).toString();
        ownerGetAndReturnCarDTO.setZuQi(tenancy);
        deliveryCarVO = createDeliveryCarInfo(renterOrderDelivery,ownerGetAndReturnCarDTO, deliveryCarVO, ownerHandoverCarInfoEntities, renterHandoverCarInfoEntities, carEngineType, cityCode, renterGoodsDetailDTO);
        return deliveryCarVO;
    }

    /**
     * 组装取送车信息
     *
     * @param renterOrderDeliveryEntity
     * @return
     */
    public void createGetHandoverCar(GetReturnCarCostReqDto getReturnCarCostReqDto,DeliveryCarVO deliveryCarVO,
                                     RenterOrderDeliveryEntity renterOrderDeliveryEntity,
                                     Integer carType,
                                     RenterGoodsDetailDTO renterGoodsDetailDTO) {

        //获取超运能
        GetReturnCarOverCostReqDto getReturnCarOverCostReqDto = new GetReturnCarOverCostReqDto();
        getReturnCarOverCostReqDto.setCityCode(Integer.valueOf(renterOrderDeliveryEntity.getCityCode()));
        getReturnCarOverCostReqDto.setOrderType(1);
        getReturnCarOverCostReqDto.setIsGetCarCost(true);
        getReturnCarOverCostReqDto.setIsReturnCarCost(true);
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setStartTime(renterOrderDeliveryEntity.getRentTime());
        costBaseDTO.setEndTime(renterOrderDeliveryEntity.getRevertTime());
        costBaseDTO.setOrderNo(renterOrderDeliveryEntity.getOrderNo());
        costBaseDTO.setRenterOrderNo(renterOrderDeliveryEntity.getRenterOrderNo());
        getReturnCarOverCostReqDto.setCostBaseDTO(costBaseDTO);
        getReturnCarCostReqDto.setCostBaseDTO(costBaseDTO);
        String isGetOverTransport;
        String isReturnOverTransport;
        String chaoYunNengAddCrashStr = "";
        String returnChaoYunNengAddCrashStr = "";
        try {
            GetReturnOverCostDTO getReturnOverCostDTO = tranSportProxyService.getGetReturnOverCost(getReturnCarOverCostReqDto);
            isGetOverTransport = getReturnOverCostDTO.getGetReturnOverTransportDTO().getIsGetOverTransport() == true ? "1" : "0";
            isReturnOverTransport = getReturnOverCostDTO.getGetReturnOverTransportDTO().getIsReturnOverTransport() == true ? "1" : "0";
            int chaoYunNengAddCrash = getReturnOverCostDTO.getGetReturnOverTransportDTO().getGetOverTransportFee();
            int returnChaoYunNengAddCrash =  getReturnOverCostDTO.getGetReturnOverTransportDTO().getReturnOverTransportFee();
            returnChaoYunNengAddCrashStr = String.valueOf(returnChaoYunNengAddCrash);
            chaoYunNengAddCrashStr = String.valueOf(chaoYunNengAddCrash);
        } catch (Exception e) {
            log.error("获取超运能异常，给默认值,cause:{}", e);
            isGetOverTransport = "0";
            isReturnOverTransport = "0";
        }
        if (renterOrderDeliveryEntity.getType() == 1 && renterOrderDeliveryEntity.getStatus() != 0) {
            GetHandoverCarDTO getHandoverCarDTO = new GetHandoverCarDTO();
            getHandoverCarDTO = getHandoverCarInfo(getHandoverCarDTO, renterOrderDeliveryEntity, carType);
            try {
                getHandoverCarDTO.setRenterRealGetAddrReamrk(renterOrderDeliveryEntity.getRenterRealGetReturnRemark());
                getHandoverCarDTO.setOwnRealGetRemark(renterOrderDeliveryEntity.getOwnerRealGetReturnRemark());
            } catch (Exception e) {
                log.error("备注获取失败",e);
            }
            getReturnCarCostReqDto.setSrvGetLat(renterOrderDeliveryEntity.getRenterGetReturnAddrLat());
            getReturnCarCostReqDto.setSrvGetLon(renterOrderDeliveryEntity.getRenterGetReturnAddrLon());
            getHandoverCarDTO.setIsChaoYunNeng(isGetOverTransport);
            getHandoverCarDTO.setChaoYunNengAddCrash(chaoYunNengAddCrashStr);
            getHandoverCarDTO.setOwnDefaultGetCarAddr(renterGoodsDetailDTO.getCarRealAddr());
            getHandoverCarDTO.setOwnDefaultGetCarLat(renterGoodsDetailDTO.getCarRealLat());
            getHandoverCarDTO.setOwnRealReturnLng(renterGoodsDetailDTO.getCarRealLon());
            deliveryCarVO.setGetHandoverCarDTO(getHandoverCarDTO);
        } else if (renterOrderDeliveryEntity.getType() == 2 && renterOrderDeliveryEntity.getStatus() != 0) {
            ReturnHandoverCarDTO returnHandoverCarDTO = new ReturnHandoverCarDTO();
            returnHandoverCarDTO = returnHandoverCarInfo(returnHandoverCarDTO, renterOrderDeliveryEntity, carType);
            try {
                returnHandoverCarDTO.setRenterRealGetRemark(renterOrderDeliveryEntity.getRenterRealGetReturnRemark());
                returnHandoverCarDTO.setOwnerRealGetAddrReamrk(renterOrderDeliveryEntity.getOwnerRealGetReturnRemark());
            } catch (Exception e) {
                log.error("备注获取失败",e);
            }
            getReturnCarCostReqDto.setSrvReturnLat(renterOrderDeliveryEntity.getRenterGetReturnAddrLat());
            getReturnCarCostReqDto.setSrvReturnLon(renterOrderDeliveryEntity.getRenterGetReturnAddrLon());
            returnHandoverCarDTO.setIsChaoYunNeng(isReturnOverTransport);
            returnHandoverCarDTO.setChaoYunNengAddCrash(returnChaoYunNengAddCrashStr);
            returnHandoverCarDTO.setOwnDefaultReturnCarAddr(renterGoodsDetailDTO.getCarRealAddr());
            returnHandoverCarDTO.setOwnDefaultReturnCarLat(renterGoodsDetailDTO.getCarRealLat());
            returnHandoverCarDTO.setOwnDefaultReturnCarLng(renterGoodsDetailDTO.getCarRealLon());
            deliveryCarVO.setReturnHandoverCarDTO(returnHandoverCarDTO);
        }
    }

    /**
     * 构造最终数据
     * @param deliveryCarVO
     * @param ownerHandoverCarInfoEntities
     * @param renterHandoverCarInfoEntities
     * @return
     */
    public DeliveryCarVO createDeliveryCarInfo(RenterOrderDeliveryEntity renterOrderDelivery,OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO, DeliveryCarVO deliveryCarVO, List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities, List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities,
                                              Integer carEngineType,String cityCode,RenterGoodsDetailDTO renterGoodsDetailDTO) {
        RenterGetAndReturnCarDTO renterGetAndReturnCarDTO = RenterGetAndReturnCarDTO.builder().build();
        int oilTotalCalibration = renterGoodsDetailDTO.getOilTotalCalibration() == null ? 16 : renterGoodsDetailDTO.getOilTotalCalibration();
        //车主取送信息
        ownerGetAndReturnCarDTO = deliveryCarInfoPriceService.createOwnerGetAndReturnCarDTO(ownerGetAndReturnCarDTO, ownerHandoverCarInfoEntities,carEngineType,cityCode,2,oilTotalCalibration);

        OwnerGetAndReturnCarDTO ownerGetAndReturnCarDO = OwnerGetAndReturnCarDTO.builder().build();
        BeanUtils.copyProperties(ownerGetAndReturnCarDTO,ownerGetAndReturnCarDO);
        //租客取送信息
        List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoList = Lists.newArrayList();
        for(RenterHandoverCarInfoEntity renterGetAndReturnCar : renterHandoverCarInfoEntities )
        {
            OwnerHandoverCarInfoEntity ownerGetAndReturnCar = new OwnerHandoverCarInfoEntity();
            BeanUtils.copyProperties(renterGetAndReturnCar,ownerGetAndReturnCar);
            ownerHandoverCarInfoList.add(ownerGetAndReturnCar);
        }
        ownerHandoverCarInfoEntities = CommonUtil.copyList(ownerHandoverCarInfoList);
        OwnerGetAndReturnCarDTO getAndReturnCarDTO = deliveryCarInfoPriceService.createOwnerGetAndReturnCarDTO(ownerGetAndReturnCarDO,ownerHandoverCarInfoEntities,carEngineType,cityCode,1,oilTotalCalibration);
        int renterOverMileageAmt = getDeliveryCarOverMileageAmt(renterOrderDelivery,getAndReturnCarDTO, renterGoodsDetailDTO);
        getAndReturnCarDTO.setOverKNCrash(String.valueOf(Math.abs(renterOverMileageAmt)));
        BeanUtils.copyProperties(getAndReturnCarDTO, renterGetAndReturnCarDTO);

        //需求变更 http://jira.autozuche.com/browse/BASIC-1180
        String orderNo = renterGoodsDetailDTO.getOrderNo();
        ownerGetAndReturnCarDTO.setCarOwnerType(renterGoodsDetailDTO.getCarOwnerType());
        OrderStatusEntity orderStatusEntity = orderStatusService.getByOrderNo(orderNo);
        if(orderStatusEntity!=null && orderStatusEntity.getCarDepositSettleStatus()== SettleStatusEnum.SETTLED.getCode()){//已结算
            //非代管 、非短租托管
            ownerGetAndReturnCarDTO.setOverKNCrash("0");
            ownerGetAndReturnCarDTO.setOilDifferenceCrash("0");
            ownerGetAndReturnCarDTO.setPlatFormOilServiceCharge("0");
            ownerGetAndReturnCarDTO.setCarOwnerAllOilCrash("0");
            ownerGetAndReturnCarDTO.setOverKNCrash("0");

            int rentOilDiffAmt = Integer.valueOf(renterGetAndReturnCarDTO.getOilDifferenceCrash()==null?"0":renterGetAndReturnCarDTO.getOilDifferenceCrash());
            if(ownerGetAndReturnCarDTO.getCarOwnerType()!=null && (ownerGetAndReturnCarDTO.getCarOwnerType().equals(CarOwnerTypeEnum.DZTGC.getCode())
                            || ownerGetAndReturnCarDTO.getCarOwnerType().equals(CarOwnerTypeEnum.DGCL.getCode()))){//短租脱管车或者代官车
                List<AccountPlatformProfitDetailEntity> platformProfitDetails = accountPlatformProfitDetailNotService.getPlatformProfitDetails(orderNo);
                //代管车、短租托管车超里程费用
                AccountPlatformProfitDetailEntity proxyOverKN = AccountPlatformProfitUtil.filterBySourceCode(OwnerCashCodeEnum.MILEAGE_COST_OWNER_PROXY, platformProfitDetails);
                AccountPlatformProfitDetailEntity carOwnerOil = AccountPlatformProfitUtil.filterBySourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST_PROXY, platformProfitDetails);
                AccountPlatformProfitDetailEntity carOilServiceCharge = AccountPlatformProfitUtil.filterBySourceCode(OwnerCashCodeEnum.OWNER_PLANT_OIL_SERVICE_FEE_PROXY, platformProfitDetails);
                int oilDiffAmt = carOwnerOil.getAmt() != null ? carOwnerOil.getAmt() : 0;
                int oilServiceAmt = carOilServiceCharge.getAmt() != null ? carOilServiceCharge.getAmt() : 0;
                //超里程
                ownerGetAndReturnCarDTO.setProxyOverKNCrash(proxyOverKN.getAmt()!=null?String.valueOf(proxyOverKN.getAmt()):"0");
                //车主总油费
                ownerGetAndReturnCarDTO.setCarOwnerOilCrash(String.valueOf(oilDiffAmt + oilServiceAmt));
                //平台加油服务费
                ownerGetAndReturnCarDTO.setCarOilServiceCharge(String.valueOf(oilServiceAmt));
                //油量差价
                ownerGetAndReturnCarDTO.setCarOilDifferenceCrash(String.valueOf(oilDiffAmt));
                //取还车邮费补贴
                ownerGetAndReturnCarDTO.setGetReturnCarOilSubsidy(String.valueOf(oilDiffAmt - rentOilDiffAmt));
            }else{
                List<AccountOwnerCostSettleDetailEntity> accountOwnerCostSettleDetailList = accountOwnerCostSettleDetailNoTService.getAccountOwnerCostSettleDetailsByOrderNo(orderNo);
                AccountOwnerCostSettleDetailEntity proxyOverKNC = AccountOwnerCostSettleUtil.filterBySourceCode(OwnerCashCodeEnum.MILEAGE_COST_OWNER, accountOwnerCostSettleDetailList);
                AccountOwnerCostSettleDetailEntity proxyOil = AccountOwnerCostSettleUtil.filterBySourceCode(OwnerCashCodeEnum.ACCOUNT_OWNER_SETTLE_OIL_COST, accountOwnerCostSettleDetailList);
                AccountOwnerCostSettleDetailEntity carOilServiceCharge = AccountOwnerCostSettleUtil.filterBySourceCode(OwnerCashCodeEnum.OWNER_PLANT_OIL_SERVICE_FEE, accountOwnerCostSettleDetailList);
                int oilDiffAmt = proxyOil.getAmt() != null ? proxyOil.getAmt() : 0;
                int oilServiceAmt = carOilServiceCharge.getAmt() != null ? carOilServiceCharge.getAmt() : 0;
                //超里程费用
                ownerGetAndReturnCarDTO.setOverKNCrash(proxyOverKNC.getAmt()!=null?String.valueOf(proxyOverKNC.getAmt()):"0");
                //车主总油费
                ownerGetAndReturnCarDTO.setCarOwnerAllOilCrash(String.valueOf(oilDiffAmt + oilServiceAmt));
                //平台加油服务费
                ownerGetAndReturnCarDTO.setPlatFormOilServiceCharge(String.valueOf(oilServiceAmt));
                //油量差价
                ownerGetAndReturnCarDTO.setOilDifferenceCrash(String.valueOf(oilDiffAmt));
            }


        }else{
            //超里程费用
            int overMileageAmt = getDeliveryCarOverMileageAmt(renterOrderDelivery,ownerGetAndReturnCarDTO, renterGoodsDetailDTO);
            ownerGetAndReturnCarDTO.setOverKNCrash(String.valueOf(Math.abs(overMileageAmt)));
            //车主总油耗
            String oilDifferenceCrashs = ownerGetAndReturnCarDTO.getOilDifferenceCrash();
            try {
                ownerGetAndReturnCarDTO.setCarOwnerAllOilCrash(String.valueOf(Integer.valueOf(oilDifferenceCrashs.contains("元") ? oilDifferenceCrashs.replace("元", "") : oilDifferenceCrashs) + Integer.valueOf(ownerGetAndReturnCarDTO.getPlatFormOilServiceCharge().contains("元") ? ownerGetAndReturnCarDTO.getPlatFormOilServiceCharge().replace("元", "") : ownerGetAndReturnCarDTO.getPlatFormOilServiceCharge())));
            }catch (Exception e)
            {
                log.info("获取车主总油耗出错",e);
            }
            //车主平台加油服务费
            try {
                int platFormOilServiceCharge = -deliveryCarInfoPriceService.getOwnerPlatFormOilServiceChargeByOrderNo(oilTotalCalibration, orderNo);
                ownerGetAndReturnCarDTO.setPlatFormOilServiceCharge(String.valueOf(platFormOilServiceCharge));
            } catch (Exception e) {
                log.info("获取平台加邮费出错,ownerGetAndReturnCarDTO:[]", JSONObject.toJSONString(ownerGetAndReturnCarDTO));
                ownerGetAndReturnCarDTO.setPlatFormOilServiceCharge("0");
            }
        }

        deliveryCarVO.setOwnerGetAndReturnCarDTO(ownerGetAndReturnCarDTO);
        deliveryCarVO.setRenterGetAndReturnCarDTO(renterGetAndReturnCarDTO);
        return deliveryCarVO;
    }




    /**
     * 获取取车相关数据
     * @return
     */
    public GetHandoverCarDTO getHandoverCarInfo(GetHandoverCarDTO getHandoverCarDTO, RenterOrderDeliveryEntity renterOrderDeliveryEntity, Integer carType) {
        getHandoverCarDTO.setChaoYunNengAddCrash("0");
        getHandoverCarDTO.setGetCarCrash(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvGetAmt(carType, renterOrderDeliveryEntity.getType())));
        getHandoverCarDTO.setGetCarKM(String.valueOf(Math.round(deliveryCarInfoPriceService.getDistanceKM(renterOrderDeliveryEntity))));
        getHandoverCarDTO.setOwnerGetCarCrash(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvGetAmt(carType, renterOrderDeliveryEntity.getType())));
        getHandoverCarDTO.setRenterRealGetAddr(renterOrderDeliveryEntity.getRenterGetReturnAddr());
        getHandoverCarDTO.setRenterRealGetLat(renterOrderDeliveryEntity.getRenterGetReturnAddrLat());
        getHandoverCarDTO.setRenterRealGetLng(renterOrderDeliveryEntity.getRenterGetReturnAddrLon());
        //车辆地址
        getHandoverCarDTO.setOwnDefaultGetCarAddr(renterOrderDeliveryEntity.getOwnerGetReturnAddr());
        getHandoverCarDTO.setOwnDefaultGetCarLat(renterOrderDeliveryEntity.getOwnerGetReturnAddrLat());
        getHandoverCarDTO.setOwnDefaultGetCarLng(renterOrderDeliveryEntity.getOwnerGetReturnAddrLon());
        getHandoverCarDTO.setOwnRealReturnAddr(renterOrderDeliveryEntity.getOwnerGetReturnAddr());
        getHandoverCarDTO.setOwnRealReturnLat(renterOrderDeliveryEntity.getOwnerGetReturnAddrLat());
        getHandoverCarDTO.setOwnRealReturnLng(renterOrderDeliveryEntity.getOwnerGetReturnAddrLon());
        LocalDateTime rentTime = renterOrderDeliveryEntity.getRentTime();
        rentTime = rentTime.minusMinutes(renterOrderDeliveryEntity.getAheadOrDelayTime());
        getHandoverCarDTO.setRentTime(DateUtils.formate(rentTime, DateUtils.DATE_DEFAUTE_4) + "," + renterOrderDeliveryEntity.getAheadOrDelayTime());
        return getHandoverCarDTO;
    }

    /**
     * 获取还车相关数据
     * @return
     */
    public ReturnHandoverCarDTO returnHandoverCarInfo(ReturnHandoverCarDTO returnHandoverCarDTO, RenterOrderDeliveryEntity renterOrderDeliveryEntity, Integer carType) {
        returnHandoverCarDTO.setChaoYunNengAddCrash("0");
        returnHandoverCarDTO.setReturnCarCrash(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvReturnAmt(carType, renterOrderDeliveryEntity.getType())));
        returnHandoverCarDTO.setReturnCarKM(String.valueOf(Math.round(deliveryCarInfoPriceService.getDistanceKM(renterOrderDeliveryEntity))));
        returnHandoverCarDTO.setOwnerReturnCarCrash(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvReturnAmt(carType, renterOrderDeliveryEntity.getType())));
        returnHandoverCarDTO.setRenterRealReturnAddr(renterOrderDeliveryEntity.getRenterGetReturnAddr());
        returnHandoverCarDTO.setRenterRealReturnLat(renterOrderDeliveryEntity.getRenterGetReturnAddrLat());
        returnHandoverCarDTO.setRenterRealReturnLng(renterOrderDeliveryEntity.getRenterGetReturnAddrLon());
        returnHandoverCarDTO.setOwnDefaultReturnCarAddr(renterOrderDeliveryEntity.getOwnerGetReturnAddr());
        returnHandoverCarDTO.setOwnerRealGetAddr(renterOrderDeliveryEntity.getOwnerGetReturnAddr());
        returnHandoverCarDTO.setOwnerRealGetLat(renterOrderDeliveryEntity.getOwnerGetReturnAddrLat());
        returnHandoverCarDTO.setOwnerRealGetLng(renterOrderDeliveryEntity.getOwnerGetReturnAddrLon());
        LocalDateTime revertTime = renterOrderDeliveryEntity.getRevertTime();
        revertTime = revertTime.plusMinutes(renterOrderDeliveryEntity.getAheadOrDelayTime());
        returnHandoverCarDTO.setRentTime(DateUtils.formate(revertTime, DateUtils.DATE_DEFAUTE_4) + "," + renterOrderDeliveryEntity.getAheadOrDelayTime());
        return  returnHandoverCarDTO;
    }


    /**
     * 获取超历程数据
     */
    public Integer getDeliveryCarOverMileageAmt(RenterOrderDeliveryEntity renterOrderDelivery,OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO,RenterGoodsDetailDTO renterGoodsDetailDTO) {
        try {
            MileageAmtDTO mileageAmtDTO = new MileageAmtDTO();
            if(renterGoodsDetailDTO != null){
                mileageAmtDTO.setCarOwnerType(renterGoodsDetailDTO.getCarOwnerType()==null?null:renterGoodsDetailDTO.getCarOwnerType());
                mileageAmtDTO.setDayMileage(renterGoodsDetailDTO.getCarDayMileage()==null?null:renterGoodsDetailDTO.getCarDayMileage());
                mileageAmtDTO.setGuideDayPrice(renterGoodsDetailDTO.getCarGuideDayPrice()==null?null:renterGoodsDetailDTO.getCarGuideDayPrice());
            }
            if(ownerGetAndReturnCarDTO != null){
                mileageAmtDTO.setGetmileage(ownerGetAndReturnCarDTO.getGetKM()==null?null:Integer.valueOf(ownerGetAndReturnCarDTO.getGetKM()));
                mileageAmtDTO.setReturnMileage(ownerGetAndReturnCarDTO.getReturnKM()==null?null:Integer.valueOf(ownerGetAndReturnCarDTO.getReturnKM()));
            }
            CostBaseDTO costBaseDTO = new CostBaseDTO();
            if(renterOrderDelivery != null){
                costBaseDTO.setStartTime(renterOrderDelivery.getRentTime());
                costBaseDTO.setEndTime(renterOrderDelivery.getRevertTime());
                costBaseDTO.setOrderNo(renterOrderDelivery.getOrderNo());
            }
            mileageAmtDTO.setCostBaseDTO(costBaseDTO);
            return deliveryCarInfoPriceService.getMileageAmtEntity(mileageAmtDTO).getTotalFee();
        } catch (Exception e) {
            log.error("获取超里程失败原因:", e);
            return 0;
        }

    }



}
