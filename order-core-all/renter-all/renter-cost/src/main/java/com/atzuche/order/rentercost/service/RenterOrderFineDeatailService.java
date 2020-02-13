package com.atzuche.order.rentercost.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.client.api.CityConfigSDK;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.client.api.HolidaySettingSDK;
import com.atzuche.config.client.api.SysConfigSDK;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.common.entity.HolidaySettingEntity;
import com.atzuche.config.common.entity.SysConfigEntity;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.CancelFineAmtDTO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.GetReturnAddressInfoDTO;
import com.atzuche.order.commons.enums.FineSubsidyCodeEnum;
import com.atzuche.order.commons.enums.FineSubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.dto.GetReturnFineDTO;
import com.atzuche.order.rentercost.exception.RenterCostParameterException;
import com.atzuche.order.rentercost.mapper.RenterOrderFineDeatailMapper;
import com.autoyol.platformcost.CommonUtils;
import com.dianping.cat.Cat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 租客订单罚金明细表
 *
 */
@Service
@Slf4j
public class RenterOrderFineDeatailService{

    private static Logger logger = LoggerFactory.getLogger(RenterOrderFineDeatailService.class);

    @Autowired
    private RenterOrderFineDeatailMapper renterOrderFineDeatailMapper;
    @Autowired
    private CityConfigSDK cityConfigSDK;
    @Autowired
    private SysConfigSDK sysConfigSDK;
    @Autowired
    private HolidaySettingSDK holidaySettingSDK;

    /**
     * 获取罚金明细列表
     * @param orderNo 主订单号
     * @param renterOrderNo 租客订单号
     * @return List<RenterOrderFineDeatailEntity>
     */
    public List<RenterOrderFineDeatailEntity> listRenterOrderFineDeatail(String orderNo, String renterOrderNo) {
    	return renterOrderFineDeatailMapper.listRenterOrderFineDeatail(orderNo, renterOrderNo);
    }

    /**
     * 租客罚金列表  与上面的方法重复。去掉
     */
//    public List<RenterOrderFineDeatailEntity> queryRentOrderFineDetail(String orderNo,String renterOrderNo) {
//    	List<RenterOrderFineDeatailEntity> lst = renterOrderFineDeatailMapper.listRenterOrderFineDeatail(orderNo, renterOrderNo);
//    	return lst;
//    }
    
	/**
	 * 获取某个子单的罚金总额
	 * @param orderNo
	 * @param renterNo
	 * @return
	 */
	public int getTotalRenterOrderFineAmt(String orderNo, String renterOrderNo){
		List<RenterOrderFineDeatailEntity> renterOrderFineDeatailEntities =  listRenterOrderFineDeatail(orderNo,renterOrderNo);
		int totalAmt=0;
		for(RenterOrderFineDeatailEntity renterOrderFineDeatailEntity:renterOrderFineDeatailEntities){
			if(renterOrderFineDeatailEntity.getFineAmount()!=null) {
				totalAmt = totalAmt + renterOrderFineDeatailEntity.getFineAmount();
			}
		}
		return totalAmt;
	}
    
    /**
     * 保存罚金记录
     * @param renterOrderFineDeatailEntity 罚金明细
     * @return Integer
     */
    public Integer saveRenterOrderFineDeatail(RenterOrderFineDeatailEntity renterOrderFineDeatailEntity) {
        if(null == renterOrderFineDeatailEntity) {
            logger.warn("Not fund renterr order fine data.");
            return 0;
        }
    	return renterOrderFineDeatailMapper.saveRenterOrderFineDeatail(renterOrderFineDeatailEntity);
    }
    
    /**
     * 批量保存罚金记录
     * @param entityList 罚金明细列表
     * @return Integer
     */
    public Integer saveRenterOrderFineDeatailBatch(List<RenterOrderFineDeatailEntity> entityList) {
    	if (entityList == null || entityList.isEmpty()) {
    		return 1;
    	}
    	return renterOrderFineDeatailMapper.saveRenterOrderFineDeatailBatch(entityList);
    }
    
    /**
     * 车主同意后删除取还车违约金，而后转入全局的违约金中
     * @param id
     * @param remark
     * @return
     */
    public Integer deleteGetReturnFineAfterAgree(Integer id, String remark) {
    	return renterOrderFineDeatailMapper.deleteGetReturnFineAfterAgree(id, remark);
    }
    
    
    /**
	 * 计算取还车违约金
	 * @param initInfo 原始数据
	 * @param updateInfo 修改数据
	 * @return List<RenterOrderFineDeatailEntity>
	 */
	public List<RenterOrderFineDeatailEntity> calculateGetOrReturnFineAmt(GetReturnAddressInfoDTO initInfo,GetReturnAddressInfoDTO updateInfo,Integer cityCode) {
		log.info("calculateGetOrReturnFineAmt initInfo=[{}],updateInfo=[{}]",initInfo,updateInfo);
		if(initInfo == null) {
			log.error("calculateGetOrReturnFineAmt 计算取还车违约金initInfo对象为空");
			Cat.logError("计算取还车违约金initInfo对象为空", new RenterCostParameterException());
			return null;
		}
		if(updateInfo == null) {
			log.error("calculateGetOrReturnFineAmt 计算取还车违约金updateInfo对象为空");
			Cat.logError("计算取还车违约金updateInfo对象为空", new RenterCostParameterException());
			return null;
		}
		CostBaseDTO initBase = initInfo.getCostBaseDTO();
		CostBaseDTO updateBase = updateInfo.getCostBaseDTO();
        log.info("config-从城市配置中获取beforeTransTimeSpan,cityCode=[{}]", cityCode);
        CityEntity configByCityCode = cityConfigSDK.getConfigByCityCode(new DefaultConfigContext(),cityCode);
        log.info("config-从城市配置中获取beforeTransTimeSpan,configByCityCode=[{}]", JSON.toJSONString(configByCityCode));
        Integer beforeTransTimeSpan = configByCityCode.getBeforeTransTimeSpan()==null?CommonUtils.BEFORE_TRANS_TIME_SPAN:configByCityCode.getBeforeTransTimeSpan();

        List<SysConfigEntity> sysConfigSDKConfig = sysConfigSDK.getConfig(new DefaultConfigContext());
        List<SysConfigEntity> sysConfigEntityList = Optional.ofNullable(sysConfigSDKConfig)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> GlobalConstant.GET_RETURN_FINE_AMT.equals(x.getAppType()))
                .collect(Collectors.toList());

        SysConfigEntity sysGetFineAmt = sysConfigEntityList.stream().filter(x -> GlobalConstant.GET_FINE_AMT.equals(x.getItemKey())).findFirst().get();
        log.info("config-从配置中获取取车违约金getFineAmt=[{}]",JSON.toJSONString(sysGetFineAmt));
        Integer getCost = (sysGetFineAmt==null||sysGetFineAmt.getItemValue()==null) ? CommonUtils.MODIFY_GET_FINE_AMT : Integer.valueOf(sysGetFineAmt.getItemValue());

        SysConfigEntity sysReturnFineAmt = sysConfigEntityList.stream().filter(x -> GlobalConstant.RETURN_FINE_AMT.equals(x.getItemKey())).findFirst().get();
		log.info("config-从配置中获取还车违约金returnFineAmt=[{}]",JSON.toJSONString(sysReturnFineAmt));
        Integer returnCost = (sysReturnFineAmt==null||sysReturnFineAmt.getItemValue()==null) ? CommonUtils.MODIFY_RETURN_FINE_AMT : Integer.valueOf(sysReturnFineAmt.getItemValue());;
		//取车开关
		Integer getFlag = initInfo.getGetFlag();
		//还车开关
		Integer returnFlag = initInfo.getReturnFlag();
		//原始信息
		LocalDateTime initGetTime = initBase.getStartTime();
		LocalDateTime initReturnTime = initBase.getEndTime();
		String initGetLon = initInfo.getGetLon();
		String initReturnLon = initInfo.getReturnLon();
		String initGetLat = initInfo.getGetLat();
		String initReturnLat = initInfo.getReturnLat();

		//当前修改的信息
		LocalDateTime getTime = updateBase.getStartTime();
		LocalDateTime returnTime = updateBase.getEndTime();
		String getLon = updateInfo.getGetLon();
		String returnLon = updateInfo.getReturnLon();
		String getLat = updateInfo.getGetLat();
		String returnLat = updateInfo.getReturnLat();
		List<RenterOrderFineDeatailEntity> entityList = new ArrayList<RenterOrderFineDeatailEntity>();

		//取车服务违约金
		Integer getFineAmt = 0;
		//还车服务违约金
		Integer returnFineAmt = 0;
		//当前时间
		LocalDateTime nowTime = LocalDateTime.now();
		//当前时间加x小时
		LocalDateTime beforeHour = nowTime.plusHours(beforeTransTimeSpan);
		if (getFlag != null && getFlag == 1) {
			// 获取取车违约金
			GetReturnFineDTO init = new GetReturnFineDTO(initGetTime, initGetLon, initGetLat);
			GetReturnFineDTO upd = new GetReturnFineDTO(getTime, getLon, getLat);
			getFineAmt = getGetOrReturnFineAmt(init, upd, beforeHour, getCost);
			RenterOrderFineDeatailEntity fineEntity = fineDataConvert(initBase, getFineAmt, FineSubsidyCodeEnum.PLATFORM, FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.MODIFY_GET_FINE);
			if (fineEntity != null) {
				entityList.add(fineEntity);
			}
		}
		if (returnFlag != null && returnFlag == 1) {
			// 获取还车违约金
			GetReturnFineDTO init = new GetReturnFineDTO(initReturnTime, initReturnLon, initReturnLat);
			GetReturnFineDTO upd = new GetReturnFineDTO(returnTime, returnLon, returnLat);
			returnFineAmt = getGetOrReturnFineAmt(init, upd, beforeHour, returnCost);
			RenterOrderFineDeatailEntity fineEntity = fineDataConvert(initBase, returnFineAmt, FineSubsidyCodeEnum.PLATFORM, FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.MODIFY_RETURN_FINE);
			if (fineEntity != null) {
				entityList.add(fineEntity);
			}
		}
		return entityList;
	}
	
	
	/**
	 * 计算取还车违约金
	 * @param init 修改前订单信息
	 * @param upd 修改后订单信息
	 * @param beforeHour 当前时间加上配置小时数
	 * @param configFine 配置违约金金额
	 * @return Integer
	 */
	public Integer getGetOrReturnFineAmt(GetReturnFineDTO init, GetReturnFineDTO upd, LocalDateTime beforeHour, Integer configFine) {
		// 修改前订单信息
		LocalDateTime initTime = init.getCurTime();
		String initLon = init.getLon();
		String initLat = init.getLat();
		// 修改后订单信息
		LocalDateTime updTime = upd.getCurTime();
		String updLon = upd.getLon();
		String updLat = upd.getLat();
		Integer fineAmt = 0;
		//计算取车违约金
		if (initTime != null && updTime != null && !initTime.isEqual(updTime)) {
			//修改了时间
			if (beforeHour.isAfter(initTime)) {
				//收取违约金
				fineAmt = configFine;
			}
		}
		if (initTime != null && beforeHour.isAfter(initTime)) {
			if (StringUtils.isNotBlank(initLon) && StringUtils.isNotBlank(updLon)) {
				BigDecimal bigInitLon = new BigDecimal(initLon);
				BigDecimal bigLon = new BigDecimal(updLon);
				if (bigInitLon.compareTo(bigLon) != 0) {
					//修改了地址
					//收取违约金
					fineAmt = configFine;
				}
			}
			if (StringUtils.isNotBlank(initLat) && StringUtils.isNotBlank(updLat)) {
				BigDecimal bigInitLat = new BigDecimal(initLat);
				BigDecimal bigLat = new BigDecimal(updLat);
				if (bigInitLat.compareTo(bigLat) != 0) {
					//修改了地址
					//收取违约金
					fineAmt = configFine;
				}
			}
		}
		return fineAmt;
	}
	
	
	/**
	 * 计算提前还车违约金
	 * @param costBaseDTO 基础信息
	 * @param initRentAmt 修改前租金
	 * @param updRentAmt 修改后租金
	 * @param initRevertTime 修改前还车时间
	 * @return RenterOrderFineDeatailEntity
	 */
    public RenterOrderFineDeatailEntity calcFineAmt(CostBaseDTO costBaseDTO, Integer initRentAmt, Integer updRentAmt, LocalDateTime initRevertTime) {
    	log.info("calcFineAmt costBaseDTO=[{}],initRentAmt=[{}],updRentAmt=[{}]",costBaseDTO,initRentAmt,updRentAmt);
    	if (costBaseDTO == null) {
        	return null;
        }
    	int fineAmt = 0;
        if (initRentAmt == null || updRentAmt == null || initRentAmt < 0 || updRentAmt >= initRentAmt) {
            return null;
        }
        LocalDateTime rentTime = costBaseDTO.getStartTime();
        LocalDateTime revertTime = costBaseDTO.getEndTime();
        LocalDateTime nowTime = LocalDateTime.now();
        if (rentTime == null || rentTime.isAfter(nowTime)) {
            return null;
        }
        if (revertTime == null || initRevertTime == null || revertTime.isEqual(initRevertTime) || revertTime.isAfter(initRevertTime)) {
        	return null;
        }
        int rentDate = Integer.valueOf(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(rentTime)).substring(0,8));
        int revertDate = Integer.valueOf(String.valueOf(LocalDateTimeUtils.localDateTimeToLong(revertTime)).substring(0,8));

        List<HolidaySettingEntity> holidayConfigList = holidaySettingSDK.getConfig(new DefaultConfigContext());
        log.info("config-获取节假日配置信息holidayConfigList=[{}]",JSON.toJSONString(holidayConfigList));
        boolean rentTimeInHolidayFlag = Optional.ofNullable(holidayConfigList).orElseGet(ArrayList::new).stream().anyMatch(x -> {
            int realStartDate = x.getRealStartDate();
            int realEndDate = x.getRealEndDate();
            if ((rentDate >= realStartDate && rentDate <= realEndDate)) {
                return true;
            }
            if (revertDate >= realStartDate && revertDate <= realEndDate) {
                return true;
            }
            return false;
        });
        log.info("判断是否包含节假日rentTimeInHolidayFlag=[{}]",rentTimeInHolidayFlag);
        if (rentTimeInHolidayFlag) {
            fineAmt = Math.round((initRentAmt - updRentAmt) * CommonUtils.FINE_AMT_RATIO_BIG);
        } else {
            fineAmt = Math.round((initRentAmt - updRentAmt) * CommonUtils.FINE_AMT_RATIO_SMALL);
        }
        RenterOrderFineDeatailEntity entity = fineDataConvert(costBaseDTO, fineAmt, FineSubsidyCodeEnum.PLATFORM, FineSubsidySourceCodeEnum.RENTER, FineTypeEnum.MODIFY_ADVANCE);
        return entity;
    }
    
    
    /**
               * 计算取消订单违约金
     * @param cancelFineAmtDTO 参数对象
     * @return RenterOrderFineDeatailEntity
     */
    public int calCancelFine(CancelFineAmtDTO cancelFineAmtDTO) {
    	log.info("calCancelFine 计算取消订单违约金cancelFineAmtDTO[{}]", cancelFineAmtDTO);
    	if (cancelFineAmtDTO == null) {
    		return 0;
    	}
    	CostBaseDTO costBaseDTO = cancelFineAmtDTO.getCostBaseDTO();
    	LocalDateTime rentTime = costBaseDTO.getStartTime();
    	LocalDateTime cancelTime = cancelFineAmtDTO.getCancelTime();
    	if (rentTime == null || cancelTime == null) {
    		return 0;
    	}
    	Integer rentAmt = cancelFineAmtDTO.getRentAmt();
    	if (rentAmt == null) {
    		return 0;
    	}
    	int penalty = 0;
    	// 提前下单时间，单位小时
        Integer beforeTransTimeSpan = CommonUtils.BEFORE_TRANS_TIME_SPAN;
        // 当前时间加x小时
		LocalDateTime beforeHour = cancelTime.plusHours(beforeTransTimeSpan);
		// 交易开始前提前4小时（含）以上不收取违约罚金/租客下普通订单且车辆类型为“代管车\无人代管车\短租托管车”时，取消规则按“交易开始前免费取消
		if (beforeHour.isBefore(rentTime) || beforeHour.isEqual(rentTime) || CommonUtils.isEscrowCar(cancelFineAmtDTO.getOwnerType())) {

		} else {
			//交易开始前4小时内，收取订单租金的30%作为违约罚金
			penalty = (int) (rentAmt*CommonUtils.CANCEL_FINE_RATIO);
		}
		// 交易开始后取消交易，收取订单租金的100%作为违约罚金
		if (cancelTime.isAfter(rentTime) || cancelTime.isEqual(rentTime)) {
			penalty = rentAmt;
		}
		//违约罚金上限为500元
		if (penalty > CommonUtils.CANCEL_FINE_LIMIT) {
            penalty = CommonUtils.CANCEL_FINE_LIMIT;
        }
		return penalty;
    }
	
	
	/**
	 * 罚金数据转化
	 * @param costBaseDTO 基础信息
	 * @param fineAmt 罚金金额
	 * @param code 罚金补贴方编码枚举
	 * @param source 罚金来源编码枚举
	 * @param type 罚金类型枚举
	 * @return RenterOrderFineDeatailEntity
	 */
	public RenterOrderFineDeatailEntity fineDataConvert(CostBaseDTO costBaseDTO, Integer fineAmt, FineSubsidyCodeEnum code, FineSubsidySourceCodeEnum source, FineTypeEnum type) {
		if (fineAmt == null || fineAmt == 0) {
			return null;
		}
		RenterOrderFineDeatailEntity fineEntity = new RenterOrderFineDeatailEntity();
		// 罚金负数
		fineEntity.setFineAmount(-fineAmt);
		fineEntity.setFineSubsidyCode(code.getFineSubsidyCode());
		fineEntity.setFineSubsidyDesc(code.getFineSubsidyDesc());
		fineEntity.setFineSubsidySourceCode(source.getFineSubsidySourceCode());
		fineEntity.setFineSubsidySourceDesc(source.getFineSubsidySourceDesc());
		fineEntity.setFineType(type.getFineType());
		fineEntity.setFineTypeDesc(type.getFineTypeDesc());
		fineEntity.setMemNo(costBaseDTO.getMemNo());
		fineEntity.setOrderNo(costBaseDTO.getOrderNo());
		fineEntity.setRenterOrderNo(costBaseDTO.getRenterOrderNo());
		return fineEntity;
	}

    public List<RenterOrderFineDeatailEntity> getRenterOrderFineDeatailByOwnerOrderNo(String renterOrderNo) {
        return renterOrderFineDeatailMapper.getRenterOrderFineDeatailByOwnerOrderNo(renterOrderNo);
    }
}
