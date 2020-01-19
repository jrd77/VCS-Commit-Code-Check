package com.atzuche.order.delivery.service.delivery;

import com.atzuche.config.client.api.OilAverageCostConfigSDK;
import com.atzuche.config.common.api.ConfigContext;
import com.atzuche.config.common.entity.OilAverageCostEntity;
import com.atzuche.order.commons.StringUtil;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.MileageAmtDTO;
import com.atzuche.order.commons.enums.cashcode.OwnerCashCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.res.delivery.DistributionCostVO;
import com.atzuche.order.commons.vo.res.ownercosts.OwnerOrderPurchaseDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderCostDetailEntity;
import com.atzuche.order.delivery.common.DeliveryErrorCode;
import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import com.atzuche.order.delivery.exception.DeliveryOrderException;
import com.atzuche.order.delivery.service.RenterOrderDeliveryService;
import com.atzuche.order.delivery.service.handover.OwnerHandoverCarService;
import com.atzuche.order.delivery.service.handover.RenterHandoverCarService;
import com.atzuche.order.delivery.utils.MathUtil;
import com.atzuche.order.delivery.vo.delivery.DeliveryOilCostVO;
import com.atzuche.order.delivery.vo.delivery.rep.OwnerGetAndReturnCarDTO;
import com.atzuche.order.delivery.vo.delivery.rep.RenterGetAndReturnCarDTO;
import com.autoyol.platformcost.CommonUtils;
import com.autoyol.platformcost.OwnerFeeCalculatorUtils;
import com.autoyol.platformcost.RenterFeeCalculatorUtils;
import com.autoyol.platformcost.model.FeeResult;
import com.dianping.cat.Cat;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * @author 胡春林
 */
@Service
@Slf4j
public class DeliveryCarInfoPriceService {

    @Autowired
    OilAverageCostConfigSDK oilAverageCostConfigSDK;
    @Autowired
    RenterOrderDeliveryService renterOrderDeliveryService;
    @Autowired
    OwnerHandoverCarService ownerHandoverCarService;
    @Autowired
    RenterHandoverCarService renterHandoverCarService;

    @Value("${auto.cost.configHours}")
    private Integer configHours;


    /**
     * 根据类型获取所在城市的油价
     */
    public Double getOilPriceByCityCodeAndType(Integer cityCode, Integer type) {

        //目前获取到的EngineType全部为0  没有区分 所以暂时写死
        List<OilAverageCostEntity> oilAverageCostEntityList = oilAverageCostConfigSDK.getConfig(DeliveryCarInfoConfigContext.builder().build());
        OilAverageCostEntity oilAverageCostEntity = oilAverageCostEntityList.stream().filter(r -> r.getCityCode() == cityCode.intValue() && r.getEngineType() == 0).findFirst().get();
        if (Objects.isNull(oilAverageCostEntity)) {
            oilAverageCostEntity = oilAverageCostEntityList.stream().filter(r -> r.getCityCode() == 0 && r.getEngineType() == 0).findFirst().get();
        }
        if (Objects.isNull(oilAverageCostEntity)) {
            throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有找到对应的城市油价");
        }
        int molecule = oilAverageCostEntity.getMolecule();
        int denominator = oilAverageCostEntity.getDenominator();
        if (denominator == 0) {
           return  0d;
        } else {
            return MathUtil.div(molecule, denominator, 2);
        }
    }


    /**
     * 根据订单号获取油费
     * 燃料类型 1：92号汽油、2：95号汽油、3：0号柴油、4：纯电动、5: 98号汽油
     * @param orderNo
     * @return
     */
    public DeliveryOilCostVO getOilCostByRenterOrderNo(String orderNo, Integer carEngineType) {
        try {
            List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoEntities = ownerHandoverCarService.selectOwnerByOrderNo(orderNo);
            List<RenterHandoverCarInfoEntity> renterHandoverCarInfoEntities = renterHandoverCarService.selectRenterByOrderNo(orderNo);
            List<RenterOrderDeliveryEntity> renterOrderDeliveryEntityList = renterOrderDeliveryService.findRenterOrderListByOrderNo(orderNo);
            if (CollectionUtils.isEmpty(renterOrderDeliveryEntityList)) {
                throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有找到该笔配送订单");
            }
            //取车时的所在城市
            RenterOrderDeliveryEntity renterOrderDelivery = renterOrderDeliveryEntityList.stream().filter(r -> r.getType() == 1).findFirst().get();
            String cityCode = renterOrderDelivery.getCityCode();
            if (StringUtils.isBlank(cityCode)) {
                throw new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(), "没有找到cityCode");
            }
            OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO = OwnerGetAndReturnCarDTO.builder().build();
            RenterGetAndReturnCarDTO renterGetAndReturnCarDTO = RenterGetAndReturnCarDTO.builder().build();
            //车主取送信息
            ownerGetAndReturnCarDTO = createOwnerGetAndReturnCarDTO(ownerGetAndReturnCarDTO, ownerHandoverCarInfoEntities, carEngineType, cityCode);
            OwnerGetAndReturnCarDTO ownerGetAndReturnCarDO = OwnerGetAndReturnCarDTO.builder().build();
            BeanUtils.copyProperties(ownerGetAndReturnCarDTO, ownerGetAndReturnCarDO);
            //租客取送信息
            List<OwnerHandoverCarInfoEntity> ownerHandoverCarInfoList = Lists.newArrayList();
            for (RenterHandoverCarInfoEntity renterGetAndReturnCar : renterHandoverCarInfoEntities) {
                OwnerHandoverCarInfoEntity ownerGetAndReturnCar = new OwnerHandoverCarInfoEntity();
                BeanUtils.copyProperties(renterGetAndReturnCar, ownerGetAndReturnCar);
                ownerHandoverCarInfoList.add(ownerGetAndReturnCar);
            }
            ownerGetAndReturnCarDO = createOwnerGetAndReturnCarDTO(ownerGetAndReturnCarDO, ownerHandoverCarInfoList, carEngineType, cityCode);
            BeanUtils.copyProperties(ownerGetAndReturnCarDO, renterGetAndReturnCarDTO);
            return DeliveryOilCostVO.builder().ownerGetAndReturnCarDTO(ownerGetAndReturnCarDTO).renterGetAndReturnCarDTO(renterGetAndReturnCarDTO).build();
        } catch (DeliveryOrderException e) {
            log.info("获取油费等数据发生业务异常：{}", e.getErrorMsg());
        } catch (Exception ex) {
            log.info("获取油费等数据发生异常：{}", ex.getMessage());
            Cat.logError("获取油费等数据发生异常",ex);
        }
        return DeliveryOilCostVO.builder().build();
    }

    /**
     * 获取取/还车实际距离
     *
     * @param renterOrderDeliveryEntity
     * @return
     */
    public Double getDistanceKM(RenterOrderDeliveryEntity renterOrderDeliveryEntity) {
        try {
            double carLat = Double.valueOf(renterOrderDeliveryEntity.getRenterGetReturnAddrLat()).doubleValue();
            double carLng = Double.valueOf(renterOrderDeliveryEntity.getRenterGetReturnAddrLon()).doubleValue();
            double originCarLon = Double.valueOf(renterOrderDeliveryEntity.getOwnerGetReturnAddrLat()).doubleValue();
            double originCarLat = Double.valueOf(renterOrderDeliveryEntity.getOwnerGetReturnAddrLon()).doubleValue();
            return CommonUtils.calcDistance(carLat, carLng, originCarLon, originCarLat);
        } catch (Exception e) {
            log.info("换算取还车距离失败，cause:{}", e.getMessage());
            return 0d;
        }
    }

    /**
     * 获取车主租客取送车信息
     *
     * @param ownerGetAndReturnCarDTO
     * @param HandoverCarInfoEntities
     * @return
     */
    public OwnerGetAndReturnCarDTO createOwnerGetAndReturnCarDTO(OwnerGetAndReturnCarDTO ownerGetAndReturnCarDTO, List<OwnerHandoverCarInfoEntity> HandoverCarInfoEntities,Integer carEngineType,String cityCode) {
        for (OwnerHandoverCarInfoEntity ownerHandoverCarInfoEntity : HandoverCarInfoEntities) {
            if (Objects.isNull(ownerHandoverCarInfoEntity.getType())) {
                continue;
            }
            if (ownerHandoverCarInfoEntity.getType().intValue() == RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().intValue()) {
                ownerGetAndReturnCarDTO.setGetCarOil(String.valueOf(ownerHandoverCarInfoEntity.getOilNum()));
                ownerGetAndReturnCarDTO.setGetKM(String.valueOf(ownerHandoverCarInfoEntity.getMileageNum()));
                ownerGetAndReturnCarDTO.setRealGetTime(String.valueOf(ownerHandoverCarInfoEntity.getRealReturnTime()));
            } else {
                ownerGetAndReturnCarDTO.setReturnCarOil(String.valueOf(ownerHandoverCarInfoEntity.getOilNum()));
                ownerGetAndReturnCarDTO.setReturnKM(String.valueOf(ownerHandoverCarInfoEntity.getMileageNum()));
                ownerGetAndReturnCarDTO.setRealReturnTime(String.valueOf(ownerHandoverCarInfoEntity.getRealReturnTime()));
            }
        }
        //行驶里程
        try {
            String ownerDrivingKM = String.valueOf(Math.abs(Integer.valueOf(ownerGetAndReturnCarDTO.getGetKM())) - Math.abs(Integer.valueOf(ownerGetAndReturnCarDTO.getReturnKM())));
            int oilDifference = Math.abs(Integer.valueOf(ownerGetAndReturnCarDTO.getGetCarOil())) - Math.abs(Integer.valueOf(ownerGetAndReturnCarDTO.getReturnCarOil()));
            ownerGetAndReturnCarDTO.setDrivingKM(ownerDrivingKM);
            ownerGetAndReturnCarDTO.setOilDifference(String.valueOf(oilDifference));
            ownerGetAndReturnCarDTO.setOilDifferenceCrash(String.valueOf(MathUtil.mul(oilDifference,getOilPriceByCityCodeAndType(Integer.valueOf(cityCode), carEngineType))));
        }catch (Exception e)
        {
            log.error("设置参数失败,目前没有值");
        }
        return ownerGetAndReturnCarDTO;
    }


    /**
     * 获取车主取车还车费用
     * @param carType
     * @return
     */
    public DistributionCostVO getDistributionCost(Integer carType) {
        DistributionCostVO distributionCostVO = DistributionCostVO.builder().build();
        //取车费用
        distributionCostVO.setGetAmt(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvGetAmt(carType, 1)));
        //还车费用
        distributionCostVO.setReturnAmt(String.valueOf(OwnerFeeCalculatorUtils.calOwnerSrvReturnAmt(carType, 2)));
        return distributionCostVO;
    }


    /**
     * 获取超里程费用
     * @param mileageAmtDTO
     * @return RenterOrderCostDetailEntity
     */
    public FeeResult getMileageAmtEntity(MileageAmtDTO mileageAmtDTO) {
        FeeResult feeResult = new FeeResult();
        feeResult.setUnitCount(1.0);
        log.info("getMileageAmtEntity mileageAmtDTO=[{}]",mileageAmtDTO);
        if (mileageAmtDTO == null) {
            log.error("getMileageAmtEntity 获取超里程费用mileageAmtDTO对象为空");
            Cat.logError("获取超里程费用mileageAmtDTO对象为空", new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(),"获取超里程费用mileageAmtDTO对象为空"));
            feeResult.setTotalFee(0);
            feeResult.setUnitPrice(0);
            return feeResult;

        }
        CostBaseDTO costBaseDTO = mileageAmtDTO.getCostBaseDTO();
        if (costBaseDTO == null) {
            log.error("getMileageAmtEntity 获取超里程费用mileageAmtDTO.costBaseDTO对象为空");
            Cat.logError("获取超里程费用mileageAmtDTO.costBaseDTO对象为空", new DeliveryOrderException(DeliveryErrorCode.DELIVERY_PARAMS_ERROR.getValue(),"获取超里程费用mileageAmtDTO对象为空"));
            feeResult.setTotalFee(0);
            feeResult.setUnitPrice(0);
            return feeResult;
        }
        Integer mileageAmt = RenterFeeCalculatorUtils.calMileageAmt(mileageAmtDTO.getDayMileage(), mileageAmtDTO.getGuideDayPrice(),
                mileageAmtDTO.getGetmileage(), mileageAmtDTO.getReturnMileage(), costBaseDTO.getStartTime(), costBaseDTO.getEndTime(), configHours);
        feeResult.setTotalFee(mileageAmt);
        feeResult.setUnitPrice(mileageAmt);
        return feeResult;
    }


    /**
     * 车主端平台服务费
     * @param rentAmt 租金
     * @param serviceProportion 服务费比例
     * @return OwnerOrderPurchaseDetailEntity
     */
    public FeeResult getServiceExpense(Integer rentAmt, Integer serviceProportion) {
        Integer serviceExpense = OwnerFeeCalculatorUtils.calServiceExpense(rentAmt, serviceProportion);
        FeeResult feeResult = new FeeResult(serviceExpense, 1.0, serviceExpense);
        return feeResult;
    }

    /**
     * 设置预环境
     */
    @Builder
    private static class DeliveryCarInfoConfigContext implements ConfigContext {

        @Override
        public boolean preConfig() {
            return false;
        }
    }
}
