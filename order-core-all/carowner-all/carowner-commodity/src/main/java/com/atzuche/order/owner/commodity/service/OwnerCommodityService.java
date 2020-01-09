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
import java.util.ArrayList;
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
        if(oldRentTime == null || !oldRentTime.isEqual(rentTime)){
            return;
        }


        List<OwnerGoodsPriceDetailEntity> dbGoodsPriceList = ownerGoodsPriceDetailMapper.selectByOwnerOrderNo(ownerOrderNo);
        if(dbGoodsPriceList == null || dbGoodsPriceList.size()<=0){
            return;
        }

        //使用revert_time分组
        Map<LocalDate, List<OwnerGoodsPriceDetailEntity>> carDayGroupMap = dbGoodsPriceList
                .stream()
                .collect(Collectors.groupingBy(OwnerGoodsPriceDetailEntity::getCarDay));

        OwnerGoodsPriceDetailEntity dbPriceMaxCarDay = dbGoodsPriceList.get(dbGoodsPriceList.size() - 1);//最后一条
        if(oldRentTime.isEqual(rentTime) && revertTime.isEqual(dbPriceMaxCarDay.getRevertTime())){
            return;
        }
        List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOList = ownerGoodsDetailDTO.getOwnerGoodsPriceDetailDTOList();

        List<OwnerGoodsPriceDetailDTO> newOwnerGoodsPriceList = new ArrayList<>();
        if(dbPriceMaxCarDay.getRevertTime().isBefore(revertTime)){//时间延后
            //过滤出revert_time时间最大的一个分组，比数据库中revert_time大的数据用新的，比据库中revert_time小的数据用数据库的，库中的最后一条数据特殊处理
            ownerGoodsPriceDetailDTOList.forEach(x->{
                LocalDate carDay = x.getCarDay();
                List<OwnerGoodsPriceDetailEntity> dbMapValue = carDayGroupMap.get(carDay);
                if(carDay.isEqual(dbPriceMaxCarDay.getCarDay())){//重复的这一天，可能数据重复，需要特别处理
                    dbMapValue.forEach(y->{
                        OwnerGoodsPriceDetailDTO ownerGoods = new OwnerGoodsPriceDetailDTO();
                        ownerGoods.setCarHourCount(y.getCarHourCount());
                        ownerGoods.setCarDay(y.getCarDay());
                        ownerGoods.setCarUnitPrice(y.getCarUnitPrice());
                        ownerGoods.setRevertTime(y.getRevertTime());
                        newOwnerGoodsPriceList.add(ownerGoods);
                    });

                    float HFirst = CommonUtils.getHolidayTopHours(dbPriceMaxCarDay.getRevertTime(), LocalDateTimeUtils.localdateToString(carDay));
                    OwnerGoodsPriceDetailDTO ownerGoods = new OwnerGoodsPriceDetailDTO();
                    ownerGoods.setCarHourCount(HFirst);
                    ownerGoods.setCarDay(x.getCarDay());
                    ownerGoods.setCarUnitPrice(x.getCarUnitPrice());
                    ownerGoods.setRevertTime(x.getRevertTime());
                    newOwnerGoodsPriceList.add(ownerGoods);
                }else if(carDay.isBefore(dbPriceMaxCarDay.getCarDay())){//取数据库中的数据
                    dbMapValue.forEach(y->{
                        OwnerGoodsPriceDetailDTO ownerGoods = new OwnerGoodsPriceDetailDTO();
                        ownerGoods.setCarHourCount(y.getCarHourCount());
                        ownerGoods.setCarDay(y.getCarDay());
                        ownerGoods.setCarUnitPrice(y.getCarUnitPrice());
                        ownerGoods.setRevertTime(y.getRevertTime());
                        newOwnerGoodsPriceList.add(ownerGoods);
                    });
                }else{//取最新的数据
                    OwnerGoodsPriceDetailDTO ownerGoods = new OwnerGoodsPriceDetailDTO();
                    ownerGoods.setCarHourCount(x.getCarHourCount());
                    ownerGoods.setCarDay(x.getCarDay());
                    ownerGoods.setCarUnitPrice(x.getCarUnitPrice());
                    ownerGoods.setRevertTime(x.getRevertTime());
                    newOwnerGoodsPriceList.add(ownerGoods);
                }
            });

        }else{//时间提前
            //判断数据库中每个分组的revert_time 找到比数据库中的revert_time相同的
            Map<LocalDateTime, List<OwnerGoodsPriceDetailEntity>> dbRevertTimeGroup = dbGoodsPriceList
                    .stream()
                    .filter(x->(x.getCarDay().isBefore(revertTime.toLocalDate()) || x.getCarDay().isEqual(revertTime.toLocalDate())))
                    .collect(Collectors.groupingBy(OwnerGoodsPriceDetailEntity::getRevertTime));
            OwnerGoodsPriceDetailEntity lastGroup = dbGoodsPriceList.stream()
                    .filter(x->(x.getCarDay().isBefore(revertTime.toLocalDate()) || x.getCarDay().isEqual(revertTime.toLocalDate())))
                    .sorted((x, y) -> y.getCarDay().compareTo(x.getCarDay())).findFirst().get();
            dbRevertTimeGroup.forEach((k,v)->{
                if(lastGroup.getRevertTime().isEqual(k)){//最后的一段
                    List<OwnerGoodsPriceDetailEntity> renterGoodsPriceDetailList = dbRevertTimeGroup.get(lastGroup.getRevertTime());
                    renterGoodsPriceDetailList.stream().forEach(x->{
                        if(x.getCarDay().isEqual(revertTime.toLocalDate())){
                            float Hlast = CommonUtils.getHolidayFootHours(rentTime, LocalDateTimeUtils.localdateToString(rentTime.toLocalDate()));
                            OwnerGoodsPriceDetailDTO renterGoods = new OwnerGoodsPriceDetailDTO();
                            renterGoods.setCarHourCount(Hlast);
                            renterGoods.setCarDay(x.getCarDay());
                            renterGoods.setCarUnitPrice(x.getCarUnitPrice());
                            renterGoods.setRevertTime(revertTime);
                            newOwnerGoodsPriceList.add(renterGoods);
                        }else{
                            OwnerGoodsPriceDetailDTO renterGoods = new OwnerGoodsPriceDetailDTO();
                            renterGoods.setCarHourCount(x.getCarHourCount());
                            renterGoods.setCarDay(x.getCarDay());
                            renterGoods.setCarUnitPrice(x.getCarUnitPrice());
                            renterGoods.setRevertTime(revertTime);
                            newOwnerGoodsPriceList.add(renterGoods);
                        }
                    });
                }else{//其他的
                    v.forEach(x->{
                        OwnerGoodsPriceDetailDTO renterGoods = new OwnerGoodsPriceDetailDTO();
                        renterGoods.setCarHourCount(x.getCarHourCount());
                        renterGoods.setCarDay(x.getCarDay());
                        renterGoods.setCarUnitPrice(x.getCarUnitPrice());
                        renterGoods.setRevertTime(x.getRevertTime());
                        newOwnerGoodsPriceList.add(renterGoods);
                    });
                }
            });
        }
        ownerGoodsDetailDTO.setOwnerGoodsPriceDetailDTOList(newOwnerGoodsPriceList);
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
