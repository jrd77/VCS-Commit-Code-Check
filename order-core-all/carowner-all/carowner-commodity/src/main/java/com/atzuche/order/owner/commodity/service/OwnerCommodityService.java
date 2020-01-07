package com.atzuche.order.owner.commodity.service;

import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsPriceDetailEntity;
import com.atzuche.order.owner.commodity.mapper.OwnerGoodsPriceDetailMapper;
import com.autoyol.platformcost.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OwnerCommodityService {
    @Autowired
    private OwnerGoodsPriceDetailMapper ownerGoodsPriceDetailMapper;
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
    public OwnerGoodsDetailDTO setPriceAndGroup(OwnerGoodsDetailDTO ownerGoodsDetailDTO){
        init(ownerGoodsDetailDTO);
        combination(ownerGoodsDetailDTO);
        return ownerGoodsDetailDTO;
    }

    /**
     * 保存商品信息
     * @param orderContextDto
     */
    public void saveCommodity(OwnerGoodsDetailDTO ownerGoodsDetailDTO){
        ownerGoodsService.save(ownerGoodsDetailDTO);
    }


    //组合
    private void combination(OwnerGoodsDetailDTO ownerGoodsDetailDTO){
        String ownerOrderNo = ownerGoodsDetailDTO.getOwnerOrderNo();
        if(ownerOrderNo == null){
            return;
        }
        LocalDateTime rentTime = ownerGoodsDetailDTO.getRentTime();
        LocalDateTime revertTime = ownerGoodsDetailDTO.getRevertTime();
        LocalDateTime oldRentTime = ownerGoodsDetailDTO.getOldRentTime();
        if(oldRentTime == null && !oldRentTime.isEqual(rentTime)){
            return;
        }

        List<OwnerGoodsPriceDetailEntity> dbGoodsPriceList = ownerGoodsPriceDetailMapper.selectByOwnerOrderNo(ownerOrderNo);
        if(dbGoodsPriceList == null || dbGoodsPriceList.size()<=0){
            return;
        }
        Map<LocalDate, OwnerGoodsPriceDetailEntity> mapPrice = dbGoodsPriceList
                .stream()
                .collect(Collectors.toMap(OwnerGoodsPriceDetailEntity::getCarDay, x -> x));
        OwnerGoodsPriceDetailEntity dbPriceMaxCarDay = dbGoodsPriceList.get(dbGoodsPriceList.size() - 1);
        LocalDateTime oldRevertTime = dbPriceMaxCarDay.getRevertTime();
        List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOList = ownerGoodsDetailDTO.getOwnerGoodsPriceDetailDTOList();
        if(oldRevertTime.isBefore(revertTime)){//时间延后
            OwnerGoodsPriceDetailDTO middle = new OwnerGoodsPriceDetailDTO();
            ownerGoodsPriceDetailDTOList.forEach(x->{
                OwnerGoodsPriceDetailEntity dbPrice = mapPrice.get(x.getCarDay());
                if(dbPriceMaxCarDay.getCarDay().isEqual(x.getCarDay())){  //临界的地方需要拆分
                    float HFirst = CommonUtils.getHolidayTopHours(dbPriceMaxCarDay.getRevertTime(), LocalDateTimeUtils.localdateToString(x.getCarDay()));
                    middle.setRevertTime(revertTime);
                    middle.setCarDay(dbPriceMaxCarDay.getCarDay());
                    middle.setCarUnitPrice(x.getCarUnitPrice());
                    middle.setCarHourCount(HFirst);

                    x.setCarUnitPrice(dbPrice.getCarUnitPrice());
                    x.setCarHourCount(dbPrice.getCarHourCount());
                    x.setRevertTime(dbPrice.getRevertTime());
                }else if(dbPrice != null){//临界之前的值使用数据库的  临界之后使用新生成的一天一价
                    x.setCarUnitPrice(dbPrice.getCarUnitPrice());
                    x.setCarHourCount(dbPrice.getCarHourCount());
                    x.setRevertTime(dbPrice.getRevertTime());
                }
            });
            ownerGoodsPriceDetailDTOList.add(middle);

            //租期不重叠部分 中间部分init中已经处理
            //租期不重叠部分 最后一天 init中已经处理
        }else{//时间提前
            //替换金额即可，使用数据库中的一天一价
            ownerGoodsPriceDetailDTOList.forEach(x->{
                OwnerGoodsPriceDetailEntity dbPrice = mapPrice.get(x.getCarDay());
                x.setCarUnitPrice(dbPrice.getCarUnitPrice());
                x.setRevertTime(revertTime);
            });
        }
    }

    //初始化设置小时数和分组日期
    private void init(OwnerGoodsDetailDTO ownerGoodsDetailDTO){
        LocalDateTime rentTime = ownerGoodsDetailDTO.getRentTime();
        LocalDateTime revertTime = ownerGoodsDetailDTO.getRevertTime();
        List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOList = ownerGoodsDetailDTO.getOwnerGoodsPriceDetailDTOList();
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
