package com.atzuche.order.rentercommodity.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OrderContextDto;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDto;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDto;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDto;
import com.atzuche.order.commons.entity.request.SubmitOrderReq;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.mapper.OrderMapper;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercommodity.entity.RenterGoodsPriceDetailEntity;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsPriceDetailMapper;
import com.autoyol.platformcost.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 商品概览价格明细表
 *
 * @author ZhangBin
 * @date 2019-12-11 18:06:32
 */
@Service
public class RenterGoodsPriceDetailService {
    @Autowired
    private RenterGoodsPriceDetailMapper renterGoodsPriceDetailMapper;
    @Autowired
    private OrderService orderService;
    /**
     * 获取租客价格列表
     * @param orderNo 主订单号
     * @return List<RenterGoodsPriiceDetailEntity>
     */
    public List<RenterGoodsPriceDetailEntity> listRenterGoodsPriceByOrderNo(Long orderNo) {
    	return null;
    }


    public void getPriceAndGroup(OrderContextDto orderContextDto){
        init(orderContextDto);
        combination(orderContextDto);
    }


    //组合
    public void combination(OrderContextDto orderContextDto){
        OrderEntity orderEntity = orderService.getParentOrderDetailByOrderNo(orderContextDto.getOrderNo());
        if(orderEntity == null || orderEntity.getRenterOrderNo() == null){//没有订单
            return;
        }
        SubmitOrderReq submitOrderReq = orderContextDto.getSubmitOrderReq();
        LocalDateTime rentTime = submitOrderReq.getRentTime();
        LocalDateTime revertTime = submitOrderReq.getRevertTime();
        String renterOrderNo = orderEntity.getRenterOrderNo();

        List<RenterGoodsPriceDetailEntity> dbGoodsPriceList = renterGoodsPriceDetailMapper.selectByRenterOrderNo(renterOrderNo);
        LocalDate carDayRent = dbGoodsPriceList.get(0).getCarDay();
        LocalDate carDayRevert = dbGoodsPriceList.get(dbGoodsPriceList.size()-1).getCarDay();

        if(!carDayRent.isEqual(rentTime.toLocalDate())){
            return;
        } if(carDayRevert.isBefore(revertTime.toLocalDate())){//时间延后
            dbGoodsPriceList.get(dbGoodsPriceList.size()-1).setCarHourCount(24F);
            List<RenterGoodsPriceDetailDto> renterGoodsPriceDetailDtoList = orderContextDto.getRenterGoodsDetailDto().getRenterGoodsPriceDetailDtoList();
            //租期重叠部分- 使用数据库的 价格/小时数
            for (int i = 0;i<dbGoodsPriceList.size();i++){
                RenterGoodsPriceDetailDto renterGoodsPriceDetailDto = renterGoodsPriceDetailDtoList.get(i);
                renterGoodsPriceDetailDto.setCarUnitPrice(dbGoodsPriceList.get(i).getCarUnitPrice());
                renterGoodsPriceDetailDto.setRevertTime(dbGoodsPriceList.get(i).getRevertTime());
                if(i == (dbGoodsPriceList.size()-1)){
                    renterGoodsPriceDetailDto.setCarHourCount(24F);
                    continue;
                }
                renterGoodsPriceDetailDto.setCarHourCount(dbGoodsPriceList.get(i).getCarHourCount());
            }
            //租期不重叠部分 中间部分init中已经处理
            //租期不重叠部分 最后一天 init中已经处理
        }else{//时间提前
            List<RenterGoodsPriceDetailDto> renterGoodsPriceDetailDtoList = orderContextDto.getRenterGoodsDetailDto().getRenterGoodsPriceDetailDtoList();
            //租期重叠部分- 使用数据库的 价格/小时数
            for (int i = 0;i<dbGoodsPriceList.size();i++){
                RenterGoodsPriceDetailDto renterGoodsPriceDetailDto = renterGoodsPriceDetailDtoList.get(i);
                renterGoodsPriceDetailDto.setCarUnitPrice(dbGoodsPriceList.get(i).getCarUnitPrice());
                renterGoodsPriceDetailDto.setRevertTime(dbGoodsPriceList.get(i).getRevertTime());
                if(i == (dbGoodsPriceList.size()-1)){//最后一天不使用数据库的小时数
                    continue;
                }
                renterGoodsPriceDetailDto.setCarHourCount(dbGoodsPriceList.get(i).getCarHourCount());
            }
        }
    }

    //初始化设置小时数和分组日期
    public void init(OrderContextDto orderContextDto){
        SubmitOrderReq submitOrderReq = orderContextDto.getSubmitOrderReq();
        LocalDateTime rentTime = submitOrderReq.getRentTime();
        LocalDateTime revertTime = submitOrderReq.getRevertTime();

        RenterGoodsDetailDto renterGoodsDetailDto = orderContextDto.getRenterGoodsDetailDto();
        List<RenterGoodsPriceDetailDto> renterGoodsPriceDetailDtoList = renterGoodsDetailDto.getRenterGoodsPriceDetailDtoList();


        if (renterGoodsPriceDetailDtoList.size() == 1) {//一天的情况
            float holidayTopHours = CommonUtils.getHolidayTopHours(rentTime, LocalDateTimeUtils.localdateToString(revertTime.toLocalDate()));
            RenterGoodsPriceDetailDto renterGoodsPriceDetailDto = renterGoodsPriceDetailDtoList.get(0);
            renterGoodsPriceDetailDto.setRevertTime(revertTime);
            renterGoodsPriceDetailDto.setCarHourCount(holidayTopHours);
        } else { // 含2天的情况。
            // 第一天
            LocalDate carDay = renterGoodsPriceDetailDtoList.get(0).getCarDay();
            float HFirst = CommonUtils.getHolidayTopHours(rentTime, LocalDateTimeUtils.localdateToString(carDay));
            RenterGoodsPriceDetailDto renterGoodsPriceFirst = renterGoodsPriceDetailDtoList.get(0);
            renterGoodsPriceFirst.setRevertTime(revertTime);
            renterGoodsPriceFirst.setCarHourCount(HFirst);

            // 中间
            if (renterGoodsPriceDetailDtoList.size() > 2) {
                for (int i = 1; i < renterGoodsPriceDetailDtoList.size() - 1; i++) {
                    RenterGoodsPriceDetailDto renterGoodsPrice = renterGoodsPriceDetailDtoList.get(i);
                    renterGoodsPrice.setRevertTime(revertTime);
                    renterGoodsPrice.setCarHourCount(24F);
                }
            }
            // 最后一天
            RenterGoodsPriceDetailDto renterGoodsPriceDetailDto = renterGoodsPriceDetailDtoList.get(renterGoodsPriceDetailDtoList.size() - 1);
            LocalDate dateLast = renterGoodsPriceDetailDto.getCarDay();
            float HLast = CommonUtils.getHolidayFootHours(revertTime, LocalDateTimeUtils.localdateToString(dateLast));
            renterGoodsPriceDetailDto.setRevertTime(revertTime);
            renterGoodsPriceDetailDto.setCarHourCount(HLast);
        }

    }


}
