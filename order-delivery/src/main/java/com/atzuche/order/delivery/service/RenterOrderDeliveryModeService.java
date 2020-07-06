package com.atzuche.order.delivery.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atzuche.config.client.api.CityConfigSDK;
import com.atzuche.config.client.api.DefaultConfigContext;
import com.atzuche.config.client.api.SysConfigSDK;
import com.atzuche.config.common.entity.CityEntity;
import com.atzuche.config.common.entity.SysConfigEntity;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.GlobalConstant;
import com.atzuche.order.commons.SectionDeliveryUtils;
import com.atzuche.order.commons.entity.dto.SectionParamDTO;
import com.atzuche.order.commons.entity.dto.SectionProposaltimeDTO;
import com.atzuche.order.commons.entity.dto.TransProgressDTO;
import com.atzuche.order.commons.enums.SrvGetReturnEnum;
import com.atzuche.order.commons.vo.RenterOwnerSummarySectionDeliveryVO;
import com.atzuche.order.commons.vo.res.QuHuanQujianVO;
import com.atzuche.order.commons.vo.res.QuZhiHuanQujianVO;
import com.atzuche.order.commons.vo.res.QuZhiHuanZhunshiVO;
import com.atzuche.order.commons.vo.res.QuhuanZhunshiVO;
import com.atzuche.order.commons.vo.res.SectionDeliveryResultVO;
import com.atzuche.order.commons.vo.res.SectionDeliveryVO;
import com.atzuche.order.commons.vo.res.SummarySectionDeliveryVO;
import com.atzuche.order.commons.vo.res.ZhiquZhihuanVO;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryEntity;
import com.atzuche.order.delivery.entity.RenterOrderDeliveryMode;
import com.atzuche.order.delivery.entity.TransSimpleMode;
import com.atzuche.order.delivery.mapper.RenterOrderDeliveryModeMapper;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RenterOrderDeliveryModeService {

	@Autowired
	private RenterOrderDeliveryModeMapper renterOrderDeliveryModeMapper;
	@Autowired
	private RenterOrderDeliveryService renterOrderDeliveryService;
	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private CityConfigSDK cityConfigSDK;
	@Autowired
	private SysConfigSDK sysConfigSDK;
	
	public RenterOrderDeliveryMode getDeliveryModeByRenterOrderNo(String renterOrderNo) {
		return renterOrderDeliveryModeMapper.getDeliveryModeByRenterOrderNo(renterOrderNo);
	}
	
	public int saveRenterOrderDeliveryMode(RenterOrderDeliveryMode renterOrderDeliveryMode) {
		return renterOrderDeliveryModeMapper.insertSelective(renterOrderDeliveryMode);
	}
	
	/**
	 * 更新建议时间
	 * @param req
	 * @return int
	 */
	public int updateRenterOrderDeliveryMode(SectionProposaltimeDTO req) {
		if (req == null || StringUtils.isBlank(req.getOrderNo())) {
			return 0;
		}
		// 获取有效的租客子订单
    	RenterOrderEntity renterOrderEntity = renterOrderService.getRenterOrderByOrderNoAndIsEffective(req.getOrderNo());
		if (renterOrderEntity == null) {
			return 0;
		}
		RenterOrderDeliveryMode mode = getDeliveryModeByRenterOrderNo(renterOrderEntity.getRenterOrderNo());
    	if (mode == null) {
    		return 0;
    	}
		RenterOrderDeliveryMode renterOrderDeliveryMode = new RenterOrderDeliveryMode();
		renterOrderDeliveryMode.setOrderNo(req.getOrderNo());
		renterOrderDeliveryMode.setRenterOrderNo(renterOrderEntity.getRenterOrderNo());
		renterOrderDeliveryMode.setId(mode.getId());
		if (StringUtils.isNotBlank(req.getOwnerProposalGetTime())) {
			renterOrderDeliveryMode.setOwnerProposalGetTime(DateUtils.parseDate(req.getOwnerProposalGetTime(), DateUtils.DATE_DEFAUTE1));
		}
		if (StringUtils.isNotBlank(req.getOwnerProposalReturnTime())) {
			renterOrderDeliveryMode.setOwnerProposalReturnTime(DateUtils.parseDate(req.getOwnerProposalReturnTime(), DateUtils.DATE_DEFAUTE1));
		}
		if (StringUtils.isNotBlank(req.getRenterProposalGetTime())) {
			renterOrderDeliveryMode.setRenterProposalGetTime(DateUtils.parseDate(req.getRenterProposalGetTime(), DateUtils.DATE_DEFAUTE1));
		}
		if (StringUtils.isNotBlank(req.getRenterProposalReturnTime())) {
			renterOrderDeliveryMode.setRenterProposalReturnTime(DateUtils.parseDate(req.getRenterProposalReturnTime(), DateUtils.DATE_DEFAUTE1));
		}
		return renterOrderDeliveryModeMapper.updateByPrimaryKeySelective(renterOrderDeliveryMode);
	}
	
	
	/**
	 * 获取区间配送信息
	 * @param renterOrderNo
	 * @param rentTime
	 * @param revertTime
	 * @return sectionDeliveryVO
	 */
	public SectionDeliveryVO getRenterSectionDeliveryVO(String renterOrderNo, LocalDateTime rentTime, LocalDateTime revertTime) {
		RenterOrderDeliveryMode mode = getDeliveryModeByRenterOrderNo(renterOrderNo);
		if (mode == null) {
			return null;
		}
		SectionParamDTO sectionParam = new SectionParamDTO();
		BeanUtils.copyProperties(mode, sectionParam);
		sectionParam.setRentTime(rentTime);
		sectionParam.setRevertTime(revertTime);
		SectionDeliveryVO sectionDeliveryVO = SectionDeliveryUtils.getRenterSectionDeliveryVO(sectionParam, DateUtils.DATE_DEFAUTE1);
		if (sectionDeliveryVO != null) {
			sectionDeliveryVO.setDistributionMode(mode.getDistributionMode());
		}
		return sectionDeliveryVO;
	}
	
	
	/**
	 * 获取区间配送信息
	 * @param renterOrderNo
	 * @param rentTime
	 * @param revertTime
	 * @return sectionDeliveryVO
	 */
	public SectionDeliveryVO getOwnerSectionDeliveryVO(String renterOrderNo, LocalDateTime rentTime, LocalDateTime revertTime) {
		RenterOrderDeliveryMode mode = getDeliveryModeByRenterOrderNo(renterOrderNo);
		if (mode == null) {
			return null;
		}
		Integer getCarBeforeTime = 0;
		Integer returnCarAfterTime = 0;
		List<RenterOrderDeliveryEntity> deliveryList = renterOrderDeliveryService.listRenterOrderDeliveryByRenterOrderNo(renterOrderNo);
		Map<Integer, RenterOrderDeliveryEntity> deliveryMap = null;
		if (deliveryList != null && !deliveryList.isEmpty()) {
			deliveryMap = deliveryList.stream().collect(Collectors.toMap(RenterOrderDeliveryEntity::getType, deliver -> {return deliver;}));
		}
		if (deliveryMap != null) {
			RenterOrderDeliveryEntity srvGetDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_GET_TYPE.getCode());
			RenterOrderDeliveryEntity srvReturnDelivery = deliveryMap.get(SrvGetReturnEnum.SRV_RETURN_TYPE.getCode());
			if (srvGetDelivery != null) {
				getCarBeforeTime = srvGetDelivery.getAheadOrDelayTime();
			}
			if (srvReturnDelivery != null) {
				returnCarAfterTime = srvReturnDelivery.getAheadOrDelayTime();
			}
		}
		SectionParamDTO sectionParam = new SectionParamDTO();
		BeanUtils.copyProperties(mode, sectionParam);
		sectionParam.setRentTime(rentTime);
		sectionParam.setRevertTime(revertTime);
		sectionParam.setGetCarBeforeTime(getCarBeforeTime);
		sectionParam.setReturnCarAfterTime(returnCarAfterTime);
		SectionDeliveryVO sectionDeliveryVO = SectionDeliveryUtils.getOwnerSectionDeliveryVO(sectionParam, DateUtils.DATE_DEFAUTE1);
		if (sectionDeliveryVO != null) {
			sectionDeliveryVO.setDistributionMode(mode.getDistributionMode());
		}
		return sectionDeliveryVO;
	}
	
	
	/**
	 * 新增或修改区间配送信息
	 * @param transDeliveryMode
	 * @return RenterOwnerSummarySectionDeliveryVO
	 */
	public RenterOwnerSummarySectionDeliveryVO getSectionDeliveryDetail(TransSimpleMode transSimpleMode, RenterOrderEntity renterOrderEntity, TransProgressDTO initpro) {
		// 数据转换
		RenterOrderDeliveryMode transDeliveryMode = convertTransDeliveryMode(transSimpleMode);
		log.info("新增或修改区间配送信息saveOrUpdateTransDeliveryMode transDeliveryMode=[{}]",transDeliveryMode);
		String renterOrderNo = renterOrderEntity.getRenterOrderNo();
		RenterOrderDeliveryMode mode = getDeliveryModeByRenterOrderNo(renterOrderNo);
		SectionParamDTO sectionParam = new SectionParamDTO();
		sectionParam.setGetCarBeforeTime(transSimpleMode.getGetCarBeforeTime());
		sectionParam.setRentAfterMinutes(transDeliveryMode.getRentAfterMinutes());
		sectionParam.setRentBeforeMinutes(transDeliveryMode.getRentBeforeMinutes());
		sectionParam.setRentTime(transSimpleMode.getRentTime());
		sectionParam.setReturnCarAfterTime(transSimpleMode.getReturnCarAfterTime());
		sectionParam.setRevertAfterMinutes(transDeliveryMode.getRevertAfterMinutes());
		sectionParam.setRevertBeforeMinutes(transDeliveryMode.getRevertBeforeMinutes());
		sectionParam.setRevertTime(transSimpleMode.getRevertTime());
		SectionDeliveryResultVO result = SectionDeliveryUtils.getSectionDeliveryResultVO(sectionParam,  DateUtils.FORMAT_STR_RENYUN);
		if (result == null) {
			return null;
		}
		result.getOwnerSectionDelivery().setAccurateGetSrvUnit(transDeliveryMode.getAccurateGetSrvUnit());
		result.getOwnerSectionDelivery().setAccurateReturnSrvUnit(transDeliveryMode.getAccurateReturnSrvUnit());
		result.getRenterSectionDelivery().setAccurateGetSrvUnit(transDeliveryMode.getAccurateGetSrvUnit());
		result.getRenterSectionDelivery().setAccurateReturnSrvUnit(transDeliveryMode.getAccurateReturnSrvUnit());
		
		// 取还车区间配送
		Map<Integer,QuHuanQujianVO> quHuanQujianMap = SectionDeliveryUtils.listQuHuanQujianVO(result, sectionParam, initpro).stream().collect(Collectors.toMap(QuHuanQujianVO::getMemType, curObj -> curObj));
		handProposalTime(mode, quHuanQujianMap);
		// 取还车准时达
		Map<Integer,QuhuanZhunshiVO> quhuanZhunshiMap = SectionDeliveryUtils.listQuhuanZhunshiVO(result, sectionParam, initpro).stream().collect(Collectors.toMap(QuhuanZhunshiVO::getMemType, curObj -> curObj));
		TransProgressDTO pro = getQuZhiHuanTP(initpro, renterOrderEntity);
		// 取车服务-自还-区间配送
		Map<Integer,QuZhiHuanQujianVO> quZhiHuanQujianMap = SectionDeliveryUtils.listQuZhiHuanQujianVO(result, sectionParam, pro).stream().collect(Collectors.toMap(QuZhiHuanQujianVO::getMemType, curObj -> curObj));
		handProposalTimeZihuan(mode, quZhiHuanQujianMap);
		// 取车服务-自还-准时达
		Map<Integer,QuZhiHuanZhunshiVO> quZhiHuanZhunshiMap = SectionDeliveryUtils.listQuZhiHuanZhunshiVO(result, sectionParam, pro).stream().collect(Collectors.toMap(QuZhiHuanZhunshiVO::getMemType, curObj -> curObj));
		// 自取自还
		TransProgressDTO zzpro = getZhiQuZhiHuanTP(renterOrderEntity);
		Map<Integer,ZhiquZhihuanVO> zhiquZhihuanMap = SectionDeliveryUtils.listZhiquZhihuanVO(result, zzpro).stream().collect(Collectors.toMap(ZhiquZhihuanVO::getMemType, curObj -> curObj));
		RenterOwnerSummarySectionDeliveryVO renterOwnerSummarySectionDeliveryVO = new RenterOwnerSummarySectionDeliveryVO();
		SummarySectionDeliveryVO renter = new SummarySectionDeliveryVO();
		renter.setQuHuanQujianVO(quHuanQujianMap.get(SectionDeliveryUtils.RENTER));
		renter.setQuhuanZhunshiVO(quhuanZhunshiMap.get(SectionDeliveryUtils.RENTER));
		renter.setQuZhiHuanQujianVO(quZhiHuanQujianMap.get(SectionDeliveryUtils.RENTER));
		renter.setQuZhiHuanZhunshiVO(quZhiHuanZhunshiMap.get(SectionDeliveryUtils.RENTER));
		renter.setZhiquZhihuanVO(zhiquZhihuanMap.get(SectionDeliveryUtils.RENTER));
		SummarySectionDeliveryVO owner = new SummarySectionDeliveryVO();
		owner.setQuHuanQujianVO(quHuanQujianMap.get(SectionDeliveryUtils.OWNER));
		owner.setQuhuanZhunshiVO(quhuanZhunshiMap.get(SectionDeliveryUtils.OWNER));
		owner.setQuZhiHuanQujianVO(quZhiHuanQujianMap.get(SectionDeliveryUtils.OWNER));
		owner.setQuZhiHuanZhunshiVO(quZhiHuanZhunshiMap.get(SectionDeliveryUtils.OWNER));
		owner.setZhiquZhihuanVO(zhiquZhihuanMap.get(SectionDeliveryUtils.OWNER));
		renterOwnerSummarySectionDeliveryVO.setRenter(renter);
		renterOwnerSummarySectionDeliveryVO.setOwner(owner);
		if (mode != null) {
			renterOwnerSummarySectionDeliveryVO.setDistributionMode(mode.getDistributionMode());
		} else {
			renterOwnerSummarySectionDeliveryVO.setDistributionMode(0);
		}
		renterOwnerSummarySectionDeliveryVO.setAccurateGetSrvUnit(transDeliveryMode.getAccurateGetSrvUnit());
		renterOwnerSummarySectionDeliveryVO.setAccurateReturnSrvUnit(transDeliveryMode.getAccurateReturnSrvUnit());
		return renterOwnerSummarySectionDeliveryVO;
	}
	
	
	public TransProgressDTO getQuZhiHuanTP(TransProgressDTO initpro, RenterOrderEntity transRealTimeVO) {
		String ownerRentTime = initpro == null ? null:initpro.getOwnerRentTime();
		String renterRentTime = initpro == null ? null:initpro.getRenterRentTime();
		LocalDateTime realRevertTime = transRealTimeVO == null ? null:transRealTimeVO.getActRevertTime();
		String realRevertDateTime = realRevertTime == null ? null:DateUtils.formate(realRevertTime, DateUtils.FORMAT_STR_RENYUN);
		TransProgressDTO pro = new TransProgressDTO(ownerRentTime, renterRentTime, realRevertDateTime, realRevertDateTime);
		return pro;
	}
	
	public TransProgressDTO getZhiQuZhiHuanTP(RenterOrderEntity transRealTimeVO) {
		LocalDateTime realRentTime = transRealTimeVO == null ? null:transRealTimeVO.getActRentTime();
		LocalDateTime realRevertTime = transRealTimeVO == null ? null:transRealTimeVO.getActRevertTime();
		String realRentDateTime = realRentTime == null ? null:DateUtils.formate(realRentTime, DateUtils.FORMAT_STR_RENYUN);
		String realRevertDateTime = realRevertTime == null ? null:DateUtils.formate(realRevertTime, DateUtils.FORMAT_STR_RENYUN);
		TransProgressDTO pro = new TransProgressDTO(realRentDateTime, realRentDateTime, realRevertDateTime, realRevertDateTime);
		return pro;
	}
	
	
	/**
	 * 数据转换
	 * @param transSimpleMode
	 * @return RenterOrderDeliveryMode
	 */
	public RenterOrderDeliveryMode convertTransDeliveryMode(TransSimpleMode transSimpleMode) {
		log.info("新增或修改区间配送信息 convertTransDeliveryMode transSimpleMode=[{}]",transSimpleMode);
		if (transSimpleMode == null) {
			return null;
		}
        log.info("config-从城市配置中获取区间配置,cityCode=[{}]", transSimpleMode.getCityCode());
        CityEntity configByCityCode = cityConfigSDK.getConfigByCityCode(new DefaultConfigContext(),Integer.valueOf(transSimpleMode.getCityCode()));
        log.info("config-从城市配置中获取区间配置,configByCityCode=[{}]", JSON.toJSONString(configByCityCode));
        RenterOrderDeliveryMode mode = new RenterOrderDeliveryMode();
        BeanUtils.copyProperties(configByCityCode, mode);
        List<SysConfigEntity> sysConfigSDKConfig = sysConfigSDK.getConfig(new DefaultConfigContext());
        List<SysConfigEntity> sysConfigEntityList = Optional.ofNullable(sysConfigSDKConfig)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(x -> GlobalConstant.GET_RETURN_ACCURATE_SRV.equals(x.getAppType()))
                .collect(Collectors.toList());
        SysConfigEntity sysGetAccurateAmt = sysConfigEntityList.stream().filter(x -> GlobalConstant.ACCURATE_GET_SRV_UNIT.equals(x.getItemKey())).findFirst().get();
        log.info("config-从配置中获取精准取车服务费单价sysGetAccurateAmt=[{}]",JSON.toJSONString(sysGetAccurateAmt));
        Integer getAccurateCost = (sysGetAccurateAmt==null||sysGetAccurateAmt.getItemValue()==null) ? null : Integer.valueOf(sysGetAccurateAmt.getItemValue());
        SysConfigEntity sysReturnAccurateAmt = sysConfigEntityList.stream().filter(x -> GlobalConstant.ACCURATE_RETURN_SRV_UNIT.equals(x.getItemKey())).findFirst().get();
        log.info("config-从配置中获取精准还车服务费单价sysReturnAccurateAmt=[{}]",JSON.toJSONString(sysReturnAccurateAmt));
        Integer returnAccurateCost = (sysReturnAccurateAmt==null||sysReturnAccurateAmt.getItemValue()==null) ? 30 : Integer.valueOf(sysReturnAccurateAmt.getItemValue());
        mode.setId(null);
        //mode.setOrderNo(transSimpleMode.getOrderNo());
        //mode.setRenterOrderNo(transSimpleMode.getRenterOrderNo());
        mode.setAccurateGetSrvUnit(getAccurateCost);
        mode.setAccurateReturnSrvUnit(returnAccurateCost);
		return mode;
	}
	
	public void handProposalTime(RenterOrderDeliveryMode mode, Map<Integer,QuHuanQujianVO> quHuanQujianMap) {
		if (mode == null || quHuanQujianMap == null) {
			return;
		}
		for (Map.Entry<Integer, QuHuanQujianVO> entry : quHuanQujianMap.entrySet()) {  
			QuHuanQujianVO result = entry.getValue();
			if (result == null) {
				continue;
			}
			if (mode.getRenterProposalGetTime() != null && SectionDeliveryUtils.RENTER.equals(result.getMemType())) {
				result.setDefaultRentTime(DateUtils.formate(mode.getRenterProposalGetTime(), DateUtils.FORMAT_STR_RENYUN));
			}
			if (mode.getRenterProposalReturnTime() != null && SectionDeliveryUtils.RENTER.equals(result.getMemType())) {
				result.setDefaultRevertTime(DateUtils.formate(mode.getRenterProposalReturnTime(), DateUtils.FORMAT_STR_RENYUN));
			}
			if (mode.getOwnerProposalGetTime() != null && SectionDeliveryUtils.OWNER.equals(result.getMemType())) {
				result.setDefaultRentTime(DateUtils.formate(mode.getOwnerProposalGetTime(), DateUtils.FORMAT_STR_RENYUN));
			}
			if (mode.getOwnerProposalReturnTime() != null && SectionDeliveryUtils.OWNER.equals(result.getMemType())) {
				result.setDefaultRevertTime(DateUtils.formate(mode.getOwnerProposalReturnTime(), DateUtils.FORMAT_STR_RENYUN));
			}
		} 
	}
	
	
	public void handProposalTimeZihuan(RenterOrderDeliveryMode mode, Map<Integer,QuZhiHuanQujianVO> quZhiHuanQujianMap) {
		if (mode == null || quZhiHuanQujianMap == null) {
			return;
		}
		for (Map.Entry<Integer, QuZhiHuanQujianVO> entry : quZhiHuanQujianMap.entrySet()) {  
			QuZhiHuanQujianVO result = entry.getValue();
			if (result == null) {
				continue;
			}
			if (mode.getRenterProposalGetTime() != null && SectionDeliveryUtils.RENTER.equals(result.getMemType())) {
				result.setDefaultRentTime(DateUtils.formate(mode.getRenterProposalGetTime(), DateUtils.FORMAT_STR_RENYUN));
			}
			if (mode.getOwnerProposalGetTime() != null && SectionDeliveryUtils.OWNER.equals(result.getMemType())) {
				result.setDefaultRentTime(DateUtils.formate(mode.getOwnerProposalGetTime(), DateUtils.FORMAT_STR_RENYUN));
			}
		} 
	}
}
