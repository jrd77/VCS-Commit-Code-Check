package com.atzuche.order.owner.commodity.service;

import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OrderContextDto;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDto;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDto;
import com.atzuche.order.commons.entity.request.SubmitOrderReq;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsPriceDetailEntity;
import com.atzuche.order.owner.commodity.mapper.OwnerGoodsPriceDetailMapper;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.autoyol.platformcost.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommodityService {
    @Autowired
    private OwnerGoodsPriceDetailMapper ownerGoodsPriceDetailMapper;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OwnerGoodsService ownerGoodsService;


    /**
     * 获取租客价格列表
     * @param orderNo 主订单号
     * @return List<OwnerGoodsPriiceDetailEntity>
     */
    public List<OwnerGoodsPriceDetailEntity> listOwnerGoodsPriceByOrderNo(Long orderNo) {
        return null;
    }

    /**
     * 通过车主子单号获取一天一价列表
     * @param ownerOrderNo
     * @return
     */
    public List<OwnerGoodsPriceDetailEntity> getOwnerGoodsPriceListByOwnerOrderNo(String ownerOrderNo){
       return ownerGoodsPriceDetailMapper.selectByOwnerOrderNo(ownerOrderNo);
    }

    /**
     *
     * @param ownerOrderno 车主子订单号
     * @param isNeedPrice 是否需要价格信息 true-需要  false-不需要
     * @return 通过子订单号获取车主商品信息
     */
    public OwnerGoodsDetailDto getOwnerGoodsDetail(String ownerOrderno,boolean isNeedPrice){
       return ownerGoodsService.getOwnerGoodsDetail(ownerOrderno,isNeedPrice);
    }

    /**
     * 计算小时数 设置分组日期
     * @param orderContextDto
     */
    public void setPriceAndGroup(OrderContextDto orderContextDto){
        init(orderContextDto);
        combination(orderContextDto);
    }

    /**
     * 保存商品信息
     * @param orderContextDto
     */
    public void saveCommodity(OrderContextDto orderContextDto){
        ownerGoodsService.save(orderContextDto);
    }


    //组合
    private void combination(OrderContextDto orderContextDto){
        OrderEntity orderEntity = orderService.getParentOrderDetailByOrderNo(orderContextDto.getOrderNo());
        if(orderEntity == null || orderEntity.getOwnerOrderNo() == null){//没有订单
            return;
        }
        SubmitOrderReq submitOrderReq = orderContextDto.getSubmitOrderReq();
        LocalDateTime rentTime = submitOrderReq.getRentTime();
        LocalDateTime revertTime = submitOrderReq.getRevertTime();
        String ownerOrderNo = orderEntity.getOwnerOrderNo();

        List<OwnerGoodsPriceDetailEntity> dbGoodsPriceList = ownerGoodsPriceDetailMapper.selectByOwnerOrderNo(ownerOrderNo);
        LocalDate carDayRent = dbGoodsPriceList.get(0).getCarDay();
        LocalDate carDayRevert = dbGoodsPriceList.get(dbGoodsPriceList.size()-1).getCarDay();

        if(!carDayRent.isEqual(rentTime.toLocalDate())){
            return;
        } if(carDayRevert.isBefore(revertTime.toLocalDate())){//时间延后
            dbGoodsPriceList.get(dbGoodsPriceList.size()-1).setCarHourCount(24F);
            List<OwnerGoodsPriceDetailDto> ownerGoodsPriceDetailDtoList = orderContextDto.getOwnerGoodsDetailDto().getOwnerGoodsPriceDetailDtoList();
            //租期重叠部分- 使用数据库的 价格/小时数
            for (int i = 0;i<dbGoodsPriceList.size();i++){
                OwnerGoodsPriceDetailDto ownerGoodsPriceDetailDto = ownerGoodsPriceDetailDtoList.get(i);
                ownerGoodsPriceDetailDto.setCarUnitPrice(dbGoodsPriceList.get(i).getCarUnitPrice());
                ownerGoodsPriceDetailDto.setRevertTime(dbGoodsPriceList.get(i).getRevertTime());
                if(i == (dbGoodsPriceList.size()-1)){
                    ownerGoodsPriceDetailDto.setCarHourCount(24F);
                    continue;
                }
                ownerGoodsPriceDetailDto.setCarHourCount(dbGoodsPriceList.get(i).getCarHourCount());
            }
            //租期不重叠部分 中间部分init中已经处理
            //租期不重叠部分 最后一天 init中已经处理
        }else{//时间提前
            List<OwnerGoodsPriceDetailDto> ownerGoodsPriceDetailDtoList = orderContextDto.getOwnerGoodsDetailDto().getOwnerGoodsPriceDetailDtoList();
            //租期重叠部分- 使用数据库的 价格/小时数
            for (int i = 0;i<dbGoodsPriceList.size();i++){
                OwnerGoodsPriceDetailDto ownerGoodsPriceDetailDto = ownerGoodsPriceDetailDtoList.get(i);
                ownerGoodsPriceDetailDto.setCarUnitPrice(dbGoodsPriceList.get(i).getCarUnitPrice());
                ownerGoodsPriceDetailDto.setRevertTime(dbGoodsPriceList.get(i).getRevertTime());
                if(i == (dbGoodsPriceList.size()-1)){//最后一天不使用数据库的小时数
                    continue;
                }
                ownerGoodsPriceDetailDto.setCarHourCount(dbGoodsPriceList.get(i).getCarHourCount());
            }
        }
    }

    //初始化设置小时数和分组日期
    private void init(OrderContextDto orderContextDto){
        SubmitOrderReq submitOrderReq = orderContextDto.getSubmitOrderReq();
        LocalDateTime rentTime = submitOrderReq.getRentTime();
        LocalDateTime revertTime = submitOrderReq.getRevertTime();

        OwnerGoodsDetailDto ownerGoodsDetailDto = orderContextDto.getOwnerGoodsDetailDto();
        List<OwnerGoodsPriceDetailDto> ownerGoodsPriceDetailDtoList = ownerGoodsDetailDto.getOwnerGoodsPriceDetailDtoList();


        if (ownerGoodsPriceDetailDtoList.size() == 1) {//一天的情况
            float holidayTopHours = CommonUtils.getHolidayTopHours(rentTime, LocalDateTimeUtils.localdateToString(revertTime.toLocalDate()));
            OwnerGoodsPriceDetailDto ownerGoodsPriceDetailDto = ownerGoodsPriceDetailDtoList.get(0);
            ownerGoodsPriceDetailDto.setRevertTime(revertTime);
            ownerGoodsPriceDetailDto.setCarHourCount(holidayTopHours);
        } else { // 含2天的情况。
            // 第一天
            LocalDate carDay = ownerGoodsPriceDetailDtoList.get(0).getCarDay();
            float HFirst = CommonUtils.getHolidayTopHours(rentTime, LocalDateTimeUtils.localdateToString(carDay));
            OwnerGoodsPriceDetailDto ownerGoodsPriceFirst = ownerGoodsPriceDetailDtoList.get(0);
            ownerGoodsPriceFirst.setRevertTime(revertTime);
            ownerGoodsPriceFirst.setCarHourCount(HFirst);

            // 中间
            if (ownerGoodsPriceDetailDtoList.size() > 2) {
                for (int i = 1; i < ownerGoodsPriceDetailDtoList.size() - 1; i++) {
                    OwnerGoodsPriceDetailDto ownerGoodsPrice = ownerGoodsPriceDetailDtoList.get(i);
                    ownerGoodsPrice.setRevertTime(revertTime);
                    ownerGoodsPrice.setCarHourCount(24F);
                }
            }
            // 最后一天
            OwnerGoodsPriceDetailDto ownerGoodsPriceDetailDto = ownerGoodsPriceDetailDtoList.get(ownerGoodsPriceDetailDtoList.size() - 1);
            LocalDate dateLast = ownerGoodsPriceDetailDto.getCarDay();
            float HLast = CommonUtils.getHolidayFootHours(revertTime, LocalDateTimeUtils.localdateToString(dateLast));
            ownerGoodsPriceDetailDto.setRevertTime(revertTime);
            ownerGoodsPriceDetailDto.setCarHourCount(HLast);
        }
    }

}
