package com.atzuche.order.coreapi.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.atzuche.order.rentercommodity.entity.RenterGoodsPriceDetailEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atzuche.order.coreapi.entity.bo.RentAmtResultBO;
import com.atzuche.order.coreapi.entity.bo.RenterGoodsPriceBO;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.rentercommodity.service.RenterGoodsPriceDetailService;
import com.atzuche.order.coreapi.entity.bo.RenterModifyGetReturnTimeBO;
import com.atzuche.order.rentercommodity.entity.RenterGoodsPriiceDetailEntity;
import com.atzuche.order.rentercommodity.service.RenterGoodsPriiceDetailService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.platformcost.RenterFeeCalculatorUtils;
import com.autoyol.platformcost.model.CarPriceOfDay;
import com.autoyol.platformcost.model.FeeResult;

@Service
public class ModifyOrderComposeService {

	@Autowired
	private RenterOrderService renterOrderService;
	@Autowired
	private RenterGoodsPriceDetailService renterGoodsPriiceDetailService;
	

	/**
	 * 计算租金获取租金对象
	 * @param orderNo
	 * @param configHours
	 * @param updateRentTime
	 * @param updateRevertTime
	 * @param carPriceOfDayList
	 * @return RentAmtResultBO
	 */
	public RentAmtResultBO getRentAmtFeeResult(Long orderNo, Integer configHours, LocalDateTime updateRentTime, LocalDateTime updateRevertTime, List<CarPriceOfDay> carPriceOfDayList) {
		List<RenterGoodsPriceBO> listRenterOrderPrice = listRenterOrderPrice(orderNo);
		if (listRenterOrderPrice == null || listRenterOrderPrice.isEmpty()) {
			return null;
		}
		RenterGoodsPriceBO lastRenterGoodsPriceBO = null;
		List<CarPriceOfDay> carPriceOfDayAfterList = null;
		Integer rentAmt = 0;
		for (RenterGoodsPriceBO renterGoodsPriceBO:listRenterOrderPrice) {
			if (lastRenterGoodsPriceBO == null) {
				lastRenterGoodsPriceBO = renterGoodsPriceBO;
			}
			// 组织开始结束时间
			RenterModifyGetReturnTimeBO getReturnTimeBO = handGetReturnTime(renterGoodsPriceBO.getExpRentStartTime(), renterGoodsPriceBO.getExpRentEndTime(),
					lastRenterGoodsPriceBO.getExpRentStartTime(), lastRenterGoodsPriceBO.getExpRentEndTime(),
					updateRentTime, updateRevertTime);
			if (getReturnTimeBO == null) {
				break;
			}
			// 日期价格列表转化
			List<RenterGoodsPriiceDetailEntity> renterGoodsPriceList = renterGoodsPriceBO.getRenterGoodsPriceList();
			carPriceOfDayAfterList = handCarPriceOfDayList(renterGoodsPriceList, getReturnTimeBO.getRevertTime());
			// 去重
			carPriceOfDayAfterList = distinctCarPriceOfDayList(carPriceOfDayAfterList);
			FeeResult curFeeResult = RenterFeeCalculatorUtils.calRentAmt(getReturnTimeBO.getRentTime(), getReturnTimeBO.getRevertTime(), configHours, carPriceOfDayAfterList);
			rentAmt += curFeeResult.getTotalFee();
			lastRenterGoodsPriceBO = renterGoodsPriceBO;
		}
		RenterModifyGetReturnTimeBO getReturnTimeAfterBO = handGetReturnTime(lastRenterGoodsPriceBO.getExpRentStartTime(), lastRenterGoodsPriceBO.getExpRentEndTime(), updateRentTime, updateRevertTime);
		if (getReturnTimeAfterBO != null) {
			carPriceOfDayAfterList = carPriceOfDayList;
			if (lastRenterGoodsPriceBO.getExpRentStartTime().isEqual(updateRentTime)) {
				// 修改了起租时间，租金按新的来重新算，未修改起租时间按分段算
				carPriceOfDayAfterList = handCarPriceOfDayList(lastRenterGoodsPriceBO.getRenterGoodsPriceList(), carPriceOfDayList, getReturnTimeAfterBO.getRentTime(), getReturnTimeAfterBO.getRevertTime());
			}
			// 去重
			carPriceOfDayAfterList = distinctCarPriceOfDayList(carPriceOfDayAfterList);
			FeeResult curFeeResult = RenterFeeCalculatorUtils.calRentAmt(getReturnTimeAfterBO.getRentTime(), getReturnTimeAfterBO.getRevertTime(), configHours, carPriceOfDayAfterList);
			rentAmt += curFeeResult.getTotalFee();
		}

		RentAmtResultBO rentAmtResultBO = new RentAmtResultBO();
		rentAmtResultBO.setCarPriceOfDayList(carPriceOfDayAfterList);
		rentAmtResultBO.setTotalFee(rentAmt);
		return rentAmtResultBO;
	}

	/**
	 * 获取已同意的租客子订单列表含商品价格列表
	 * @param orderNo 车主订单号
	 * @return List<RenterGoodsPriceBO>
	 */
	public List<RenterGoodsPriceBO> listRenterOrderPrice(Long orderNo) {
		// 获取已同意的租客子单
		List<RenterOrderEntity> renterOrderList = renterOrderService.listAgreeRenterOrderByOrderNo(orderNo);
		if (renterOrderList == null || renterOrderList.isEmpty()) {
			return null;
		}
		// 最后一条索引
		Integer lastIndex = renterOrderList.size() - 1;
		// 最后一条起租时间
		LocalDateTime lastRentTime = renterOrderList.get(lastIndex).getExpRentStartTime();
		List<RenterOrderEntity> renterOrderAfterList = new ArrayList<RenterOrderEntity>();
		for (int i=lastIndex; i>=0; i--) {
			LocalDateTime expRentStartTime = renterOrderList.get(i).getExpRentStartTime();
			if (lastRentTime != null && expRentStartTime != null && !lastRentTime.isEqual(expRentStartTime)) {
				break;
			}
			renterOrderAfterList.add(renterOrderList.get(i));
		}
		RenterOrderEntity lastRenterOrderEntity = renterOrderAfterList.get(0);
		RenterOrderEntity curRenterOrderEntity = null;
		List<RenterOrderEntity> renterOrderThenList = new ArrayList<RenterOrderEntity>();
		for (int i=0; i<renterOrderAfterList.size(); i++) {
			curRenterOrderEntity = renterOrderAfterList.get(i);
			LocalDateTime lastStartTime = lastRenterOrderEntity.getExpRentStartTime();
			LocalDateTime lastEndTime = lastRenterOrderEntity.getExpRentEndTime();
			LocalDateTime curStartTime = curRenterOrderEntity.getExpRentStartTime();
			LocalDateTime curEndTime = curRenterOrderEntity.getExpRentEndTime();
			// 顺便做了一个去重
            if ((i > 0 && curStartTime.isEqual(lastStartTime) && curEndTime.isEqual(lastEndTime))
                    || curEndTime.isAfter(lastEndTime)) {
                continue;
            } else {
            	renterOrderThenList.add(curRenterOrderEntity);
            	lastRenterOrderEntity = curRenterOrderEntity;
            }
		}
		// 获取租客价格列表
		List<RenterGoodsPriceDetailEntity> renterGoodsPriceAllList = renterGoodsPriiceDetailService.listRenterGoodsPriceByOrderNo(orderNo);
		List<RenterGoodsPriceBO> renterGoodsPriceBOList = renterOrderList.stream().map(renterOrder -> getRenterOrderPrice(renterOrder,renterGoodsPriceAllList)).collect(Collectors.toList());
		List<RenterGoodsPriiceDetailEntity> renterGoodsPriceAllList = renterGoodsPriiceDetailService.listRenterGoodsPriceByOrderNo(orderNo);
		if (renterGoodsPriceAllList == null) {
			return null;
		}
		List<RenterGoodsPriceBO> renterGoodsPriceBOList = renterOrderThenList.stream().map(renterOrder -> getRenterOrderPrice(renterOrder,renterGoodsPriceAllList)).collect(Collectors.toList());
		renterGoodsPriceBOList = renterGoodsPriceBOList.stream().sorted(Comparator.comparing(RenterGoodsPriceBO::getId)).collect(Collectors.toList());
		return renterGoodsPriceBOList;
	}
	
	public RenterGoodsPriceBO getRenterOrderPrice(RenterOrderEntity renterOrder, List<RenterGoodsPriceDetailEntity> renterGoodsPriceAllList) {
	/**
	 * 获取租客子订单和商品价格列表
	 * @param renterOrder
	 * @param renterGoodsPriceAllList
	 * @return RenterGoodsPriceBO
	 */
	public RenterGoodsPriceBO getRenterOrderPrice(RenterOrderEntity renterOrder, List<RenterGoodsPriiceDetailEntity> renterGoodsPriceAllList) {
		if (renterOrder == null) {
			return null;
		}
		RenterGoodsPriceBO renterGoodsPriceBO = new RenterGoodsPriceBO();
		renterGoodsPriceBO.setId(renterOrder.getId());
		renterGoodsPriceBO.setExpRentStartTime(renterOrder.getExpRentStartTime());
		renterGoodsPriceBO.setExpRentEndTime(renterOrder.getExpRentEndTime());
		renterGoodsPriceBO.setOrderNo(renterOrder.getOrderNo());
		renterGoodsPriceBO.setRenterOrderNo(renterOrder.getRenterOrderNo());
		if (renterGoodsPriceAllList == null || renterGoodsPriceAllList.isEmpty()) {
			return null;
		}
		List<RenterGoodsPriiceDetailEntity> renterGoodsPriceList = new ArrayList<>();
		for (RenterGoodsPriiceDetailEntity renterGoodsPrice:renterGoodsPriceAllList) {
			if (renterOrder.getRenterOrderNo() != null && renterOrder.getRenterOrderNo().equals(renterGoodsPrice.getRenterOrderNo())) {
				renterGoodsPriceList.add(renterGoodsPrice);
			}
		}
		renterGoodsPriceBO.setRenterGoodsPriceList(renterGoodsPriceList);
		return renterGoodsPriceBO;
	}


	/**
	 * 计算使用当前修改记录价格计算的取还车时间
	 * @param orderRentTime    修改记录当前取车时间
	 * @param orderRevertTime  修改记录当前还车时间
	 * @param lastRentTime     订单上次修改记录的取车时间，第一条取本条
	 * @param lastRevertTime   订单上次修改记录的还车时间，第一条取本条
	 * @param updateRentTime   修改后的取车时间
	 * @param updateRevertTime 修改后的还车时间
	 * @return RenterModifyGetReturnTimeBO
	 */
	private RenterModifyGetReturnTimeBO handGetReturnTime(LocalDateTime orderRentTime, LocalDateTime orderRevertTime,
			LocalDateTime lastRentTime, LocalDateTime lastRevertTime,
			LocalDateTime updateRentTime, LocalDateTime updateRevertTime) {
		RenterModifyGetReturnTimeBO renterModifyGetReturnTimeBO = null;
		// 修改取车时间,修改后的租期按车主设置的最新价格计算,这里只计算修改记录的，直接返回
		if (!lastRentTime.isEqual(updateRentTime)) {
			return renterModifyGetReturnTimeBO;
		} else if (orderRevertTime.isEqual(lastRevertTime)) {
			// 计算修改时间第一段租金
			if (updateRevertTime.isAfter(orderRevertTime)) {
				// 还车时间延后
				renterModifyGetReturnTimeBO = new RenterModifyGetReturnTimeBO(orderRentTime, orderRevertTime);
			} else {
				// 还车时间提前
				renterModifyGetReturnTimeBO = new RenterModifyGetReturnTimeBO(orderRentTime, updateRevertTime);
			}
		} else if ((updateRevertTime.isAfter(orderRevertTime) || updateRevertTime.isEqual(orderRevertTime))
				&& orderRevertTime.isAfter(lastRevertTime)) {
			// 还车时间延后,计算后续修改订单的每一段
			renterModifyGetReturnTimeBO = new RenterModifyGetReturnTimeBO(lastRevertTime, orderRevertTime);
		} else if (lastRevertTime.isBefore(updateRevertTime) && updateRevertTime.isBefore(orderRevertTime)) {
			// 还车时间提前，并且命中当前修改记录的时间段
			renterModifyGetReturnTimeBO = new RenterModifyGetReturnTimeBO(lastRevertTime, updateRevertTime);
		}
		return renterModifyGetReturnTimeBO;
	}

	/**
	 * 计算使用车主设置的最新价格计算的取还车时间
	 * 这里只需要传最后一条修改记录的取还车时间
	 *
	 * @param orderRentTime
	 * @param orderRevertTime
	 * @param updateRentTime
	 * @param updateRevertTime
	 * @return RenterModifyGetReturnTimeBO
	 */
	private RenterModifyGetReturnTimeBO handGetReturnTime(LocalDateTime orderRentTime, LocalDateTime orderRevertTime,
			LocalDateTime updateRentTime, LocalDateTime updateRevertTime) {
		RenterModifyGetReturnTimeBO renterModifyGetReturnTimeBO = null;
		if (!orderRentTime.isEqual(updateRentTime)) {
			// 修改取车时间,修改后的租期按车主设置的最新价格计算
			renterModifyGetReturnTimeBO = new RenterModifyGetReturnTimeBO(updateRentTime, updateRevertTime);
		} else if (updateRevertTime.isAfter(orderRevertTime)) {
			// 还车时间延后
			renterModifyGetReturnTimeBO = new RenterModifyGetReturnTimeBO(orderRevertTime, updateRevertTime);
		}
		return renterModifyGetReturnTimeBO;
	}

	/**
	 * 组织日期价格
	 * @param renterGoodsPriceList
	 * @param endTime
	 * @return List<CarPriceOfDay>
	 */
	public List<CarPriceOfDay> handCarPriceOfDayList(List<RenterGoodsPriiceDetailEntity> renterGoodsPriceList, LocalDateTime endTime) {
		if (renterGoodsPriceList == null || renterGoodsPriceList.isEmpty()) {
			return null;
		}
		LocalDate endDate = endTime.toLocalDate();
		renterGoodsPriceList = renterGoodsPriceList.stream().filter(rgp -> {
			return rgp.getCarDay() != null && (rgp.getCarDay().isBefore(endDate) || rgp.getCarDay().isEqual(endDate));
		}).collect(Collectors.toList());
		return renterGoodsPriceList.stream().map(renterGp -> {
			CarPriceOfDay carPriceOfDay = new CarPriceOfDay();
			carPriceOfDay.setCurDate(renterGp.getCarDay());
			carPriceOfDay.setDayPrice(renterGp.getCarUnitPrice());
			return carPriceOfDay;
		}).collect(Collectors.toList());
	}


	/**
	 * 组织日期价格
	 * @param renterGoodsPriceList
	 * @param carPriceOfDayList
	 * @param startTime
	 * @param endTime
	 * @return List<CarPriceOfDay>
	 */
	public List<CarPriceOfDay> handCarPriceOfDayList(List<RenterGoodsPriiceDetailEntity> renterGoodsPriceList, List<CarPriceOfDay> carPriceOfDayList, LocalDateTime startTime, LocalDateTime endTime) {
		if (renterGoodsPriceList == null || renterGoodsPriceList.isEmpty()) {
			return null;
		}
		if (carPriceOfDayList == null || carPriceOfDayList.isEmpty()) {
			return null;
		}
		List<CarPriceOfDay> carPriceOfDayFist = handCarPriceOfDayList(renterGoodsPriceList, endTime);
		carPriceOfDayFist = carPriceOfDayFist.stream().filter(carPrice -> {
			return carPrice.getCurDate() != null && !carPrice.getCurDate().isEqual(startTime.toLocalDate());
		}).collect(Collectors.toList());
		carPriceOfDayFist.addAll(carPriceOfDayList);
		return carPriceOfDayFist;
	}

	/**
	 * 日期价格列表去重
	 * @param carPriceOfDayList
	 * @return List<CarPriceOfDay>
	 */
	public List<CarPriceOfDay> distinctCarPriceOfDayList(List<CarPriceOfDay> carPriceOfDayList) {
		if (carPriceOfDayList == null || carPriceOfDayList.isEmpty()) {
			return null;
		}
		return carPriceOfDayList.stream().collect(Collectors.collectingAndThen(
	                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CarPriceOfDay::getCurDate))), ArrayList::new));
	}
}
