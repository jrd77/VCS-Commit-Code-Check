package com.atzuche.order.owner.commodity.service;

import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OrderContextDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.atzuche.order.commons.entity.request.SubmitOrderReqVO;
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
    public OwnerGoodsDetailDTO getOwnerGoodsDetail(String ownerOrderno, boolean isNeedPrice){
       return ownerGoodsService.getOwnerGoodsDetail(ownerOrderno,isNeedPrice);
    }

    /**
     * 计算小时数 设置分组日期
     * @param orderContextDto
     */
    public void setPriceAndGroup(OrderContextDTO orderContextDto){
        init(orderContextDto);
        combination(orderContextDto);
    }

    /**
     * 保存商品信息
     * @param orderContextDto
     */
    public void saveCommodity(OrderContextDTO orderContextDto){
        ownerGoodsService.save(orderContextDto);
    }


    //组合
    private void combination(OrderContextDTO orderContextDto){
//        OrderEntity orderEntity = orderService.getParentOrderDetailByOrderNo(orderContextDto.getOrderNo());
//        if(orderEntity == null || orderEntity.getOwnerOrderNo() == null){//没有订单
//            return;
//        }
        SubmitOrderReqVO submitOrderReqVO = orderContextDto.getSubmitOrderReqVO();
        LocalDateTime rentTime = submitOrderReqVO.getRentTime();
        LocalDateTime revertTime = submitOrderReqVO.getRevertTime();
//        String ownerOrderNo = orderEntity.getOwnerOrderNo();
        String ownerOrderNo = null;

        List<OwnerGoodsPriceDetailEntity> dbGoodsPriceList = ownerGoodsPriceDetailMapper.selectByOwnerOrderNo(ownerOrderNo);
        LocalDate carDayRent = dbGoodsPriceList.get(0).getCarDay();
        LocalDate carDayRevert = dbGoodsPriceList.get(dbGoodsPriceList.size()-1).getCarDay();

        if(!carDayRent.isEqual(rentTime.toLocalDate())){
            return;
        } if(carDayRevert.isBefore(revertTime.toLocalDate())){//时间延后
            dbGoodsPriceList.get(dbGoodsPriceList.size()-1).setCarHourCount(24F);
            List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOList = orderContextDto.getOwnerGoodsDetailDto().getOwnerGoodsPriceDetailDTOList();
            //租期重叠部分- 使用数据库的 价格/小时数
            for (int i = 0;i<dbGoodsPriceList.size();i++){
                OwnerGoodsPriceDetailDTO ownerGoodsPriceDetailDto = ownerGoodsPriceDetailDTOList.get(i);
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
            List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOList = orderContextDto.getOwnerGoodsDetailDto().getOwnerGoodsPriceDetailDTOList();
            //租期重叠部分- 使用数据库的 价格/小时数
            for (int i = 0;i<dbGoodsPriceList.size();i++){
                OwnerGoodsPriceDetailDTO ownerGoodsPriceDetailDto = ownerGoodsPriceDetailDTOList.get(i);
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
    private void init(OrderContextDTO orderContextDto){
        SubmitOrderReqVO submitOrderReqVO = orderContextDto.getSubmitOrderReqVO();
        LocalDateTime rentTime = submitOrderReqVO.getRentTime();
        LocalDateTime revertTime = submitOrderReqVO.getRevertTime();

        OwnerGoodsDetailDTO ownerGoodsDetailDto = orderContextDto.getOwnerGoodsDetailDto();
        List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOList = ownerGoodsDetailDto.getOwnerGoodsPriceDetailDTOList();


        if (ownerGoodsPriceDetailDTOList.size() == 1) {//一天的情况
            float holidayTopHours = CommonUtils.getHolidayTopHours(rentTime, LocalDateTimeUtils.localdateToString(revertTime.toLocalDate()));
            OwnerGoodsPriceDetailDTO ownerGoodsPriceDetailDto = ownerGoodsPriceDetailDTOList.get(0);
            ownerGoodsPriceDetailDto.setRevertTime(revertTime);
            ownerGoodsPriceDetailDto.setCarHourCount(holidayTopHours);
        } else { // 含2天的情况。
            // 第一天
            LocalDate carDay = ownerGoodsPriceDetailDTOList.get(0).getCarDay();
            float HFirst = CommonUtils.getHolidayTopHours(rentTime, LocalDateTimeUtils.localdateToString(carDay));
            OwnerGoodsPriceDetailDTO ownerGoodsPriceFirst = ownerGoodsPriceDetailDTOList.get(0);
            ownerGoodsPriceFirst.setRevertTime(revertTime);
            ownerGoodsPriceFirst.setCarHourCount(HFirst);

            // 中间
            if (ownerGoodsPriceDetailDTOList.size() > 2) {
                for (int i = 1; i < ownerGoodsPriceDetailDTOList.size() - 1; i++) {
                    OwnerGoodsPriceDetailDTO ownerGoodsPrice = ownerGoodsPriceDetailDTOList.get(i);
                    ownerGoodsPrice.setRevertTime(revertTime);
                    ownerGoodsPrice.setCarHourCount(24F);
                }
            }
            // 最后一天
            OwnerGoodsPriceDetailDTO ownerGoodsPriceDetailDto = ownerGoodsPriceDetailDTOList.get(ownerGoodsPriceDetailDTOList.size() - 1);
            LocalDate dateLast = ownerGoodsPriceDetailDto.getCarDay();
            float HLast = CommonUtils.getHolidayFootHours(revertTime, LocalDateTimeUtils.localdateToString(dateLast));
            ownerGoodsPriceDetailDto.setRevertTime(revertTime);
            ownerGoodsPriceDetailDto.setCarHourCount(HLast);
        }
    }

}
