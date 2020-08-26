package com.atzuche.order.owner.commodity.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerGoodsPriceDetailDTO;
import com.atzuche.order.commons.exceptions.PriceDayGroupException;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsPriceDetailEntity;
import com.atzuche.order.owner.commodity.mapper.OwnerGoodsPriceDetailMapper;
import com.autoyol.platformcost.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("setPriceAndGroup.ownerGoodsDetailDTO={}",JSON.toJSONString(ownerGoodsDetailDTO));
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
    public void combination(OwnerGoodsDetailDTO ownerGoodsDetailDTO){
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

        OwnerGoodsPriceDetailEntity dbPriceMaxCarDay = dbGoodsPriceList.get(dbGoodsPriceList.size() - 1);//最后一条
        List<OwnerGoodsPriceDetailDTO> newOwnerGoodsPriceList = new ArrayList<>();
        if(oldRentTime.isEqual(rentTime) && revertTime.isEqual(dbPriceMaxCarDay.getRevertTime())){
            dbGoodsPriceList.stream().forEach(y->{
                OwnerGoodsPriceDetailDTO ownerGoods = new OwnerGoodsPriceDetailDTO();
                ownerGoods.setCarHourCount(y.getCarHourCount());
                ownerGoods.setCarDay(y.getCarDay());
                ownerGoods.setCarUnitPrice(y.getCarUnitPrice());
                ownerGoods.setRevertTime(y.getRevertTime());
                newOwnerGoodsPriceList.add(ownerGoods);
            });
            ownerGoodsDetailDTO.setOwnerGoodsPriceDetailDTOList(newOwnerGoodsPriceList);
            return;
        }


        if(dbPriceMaxCarDay.getRevertTime().isBefore(revertTime)){//时间延后
            List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOList = ownerGoodsDetailDTO.getOwnerGoodsPriceDetailDTOList();
            //过滤出revert_time时间最大的一个分组，比数据库中revert_time大的数据用新的，比据库中revert_time小的数据用数据库的，库中的最后一条数据特殊处理
            //使用carday分组
            Map<LocalDate, List<OwnerGoodsPriceDetailEntity>> carDayGroupMap = dbGoodsPriceList
                    .stream()
                    .collect(Collectors.groupingBy(OwnerGoodsPriceDetailEntity::getCarDay));
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
                    float HFirst = 0;
                    if(x.isMaxDay()){
                        HFirst = CommonUtils.getTotalHoursByRentAveragePrice(dbPriceMaxCarDay.getRevertTime(),x.getRevertTime());
                    }else{
                        HFirst = CommonUtils.getHolidayTopHours(dbPriceMaxCarDay.getRevertTime(), LocalDateTimeUtils.localdateToString(carDay));
                    }
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
            ownerGoodsDetailDTO.setOwnerGoodsPriceDetailDTOList(newOwnerGoodsPriceList);
        }else{//时间提前
            Map<LocalDateTime, List<OwnerGoodsPriceDetailEntity>> dbRevertTimeGroup = dbGoodsPriceList
                    .stream()
                    .filter(x->(x.getCarDay().isBefore(revertTime.toLocalDate()) || x.getCarDay().isEqual(revertTime.toLocalDate())))
                    .collect(Collectors.groupingBy(OwnerGoodsPriceDetailEntity::getRevertTime));
            //判断new_revert_time属于哪一个dbRevertTimeGroup分组，移除不需要的分组
            LocalDateTime belongGroup = belongGroup(dbRevertTimeGroup,revertTime);
            //判断new_revert_time跟哪一个一天一价是临界点，移除当前组不需要的数据
            List<OwnerGoodsPriceDetailDTO> filterList = criticalCarDay(dbRevertTimeGroup,revertTime,belongGroup);
            //将dbRevertTimeGroup分组剩下的数据转化为集合

            newOwnerGoodsPriceList.addAll(filterList);
            ownerGoodsDetailDTO.setOwnerGoodsPriceDetailDTOList(newOwnerGoodsPriceList);
        }
    }

    /*
     * revertTime属于哪一个分组段内
     * */
    public static LocalDateTime belongGroup(Map<LocalDateTime, List<OwnerGoodsPriceDetailEntity>> dbRevertTimeGroup,LocalDateTime revertTime){
        List<LocalDateTime> dbGoodsPricekeyList = dbRevertTimeGroup.keySet().stream().sorted((x,y)->x.compareTo(y)).collect(Collectors.toList());
        long revertTimeMilli = revertTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        long closestValue  = Long.MAX_VALUE;
        LocalDateTime closest = null;
        for(LocalDateTime item : dbGoodsPricekeyList){
            long itemMilli = item.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long diffValue = itemMilli - revertTimeMilli;
            if(diffValue >= 0 && diffValue <= closestValue){
                closestValue = diffValue;
                closest = item;
            }
        }
        if(closest == null){
            throw new PriceDayGroupException();
        }
        for(LocalDateTime item : dbGoodsPricekeyList){
            long itemMilli = item.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long diffValue = itemMilli - revertTimeMilli;
            if(diffValue > 0 && !item.isEqual(closest)){
                dbRevertTimeGroup.remove(item);
            }
        }
        return closest;
    }

    /**
     * 查找组内的临界点，删除不需要的数据
     * @param dbRevertTimeGroup
     * @param revertTime
     */
    public static List<OwnerGoodsPriceDetailDTO> criticalCarDay(Map<LocalDateTime, List<OwnerGoodsPriceDetailEntity>> dbRevertTimeGroup,LocalDateTime revertTime,LocalDateTime belongGroup ){
        List<OwnerGoodsPriceDetailEntity> belongGroupPriceList = dbRevertTimeGroup.get(belongGroup);
        if(belongGroupPriceList == null || belongGroupPriceList.size() <= 0){
            throw new PriceDayGroupException();
        }
        List<OwnerGoodsPriceDetailDTO> ownerGoodsPriceDetailDTOS = dbRevertTimeGroupToList(dbRevertTimeGroup);
        LocalDate revertTimeToLocalDate = revertTime.toLocalDate();
        for(Iterator<OwnerGoodsPriceDetailEntity> iterator=belongGroupPriceList.iterator();iterator.hasNext();){
            OwnerGoodsPriceDetailEntity next = iterator.next();
            LocalDate carDay = next.getCarDay();
            next.setRevertTime(revertTime);
            if(carDay.isAfter(revertTimeToLocalDate)){
                iterator.remove();
            }else if(carDay.isEqual(revertTimeToLocalDate)){//临界点
                double carHourCount = ownerGoodsPriceDetailDTOS.stream()
                        .filter(x -> x.getCarDay().equals(carDay))
                        .collect(Collectors.summingDouble(OwnerGoodsPriceDetailDTO::getCarHourCount));
                float carHourCountF = (float)carHourCount;
                carHourCountF = carHourCountF - next.getCarHourCount();
                float Hlast = CommonUtils.getHolidayFootHours(revertTime, LocalDateTimeUtils.localdateToString(revertTime.toLocalDate()));
                next.setCarHourCount(Hlast - carHourCountF);
            }
        }
        return dbRevertTimeGroupToList(dbRevertTimeGroup);
    }

    /*
     * map 转化为 集合
     * */
    private static List<OwnerGoodsPriceDetailDTO> dbRevertTimeGroupToList(Map<LocalDateTime, List<OwnerGoodsPriceDetailEntity>> dbRevertTimeGroup) {
        List<OwnerGoodsPriceDetailDTO> newOwnerGoodsPriceList = new ArrayList<>();
        dbRevertTimeGroup.forEach((k,v)->{
            v.forEach(x->{
                OwnerGoodsPriceDetailDTO ownerGoods = new OwnerGoodsPriceDetailDTO();
                ownerGoods.setCarHourCount(x.getCarHourCount());
                ownerGoods.setCarDay(x.getCarDay());
                ownerGoods.setCarUnitPrice(x.getCarUnitPrice());
                ownerGoods.setRevertTime(x.getRevertTime());
                newOwnerGoodsPriceList.add(ownerGoods);
            });
        });
        return newOwnerGoodsPriceList;
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
            ownerGoodsPriceDetailDto.setMaxDay(true);
        }
    }
}
