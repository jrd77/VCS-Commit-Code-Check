package com.atzuche.order.rentercommodity.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.commons.exceptions.PriceDayGroupException;
import com.atzuche.order.rentercommodity.entity.RenterGoodsPriceDetailEntity;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsPriceDetailMapper;
import com.autoyol.platformcost.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/*
 * @Author ZhangBin
 * @Date 2019/12/18 11:00 
 * @Description: 租客商品 - 对外模块提供接口类
 * 
 **/
@Slf4j
@Service
public class RenterCommodityService {
    @Autowired
    private RenterGoodsPriceDetailMapper renterGoodsPriceDetailMapper;
    @Autowired
    private RenterGoodsService renterGoodsService;

    /**
     * 通过租客子单号获取一天一价列表
     * @param renterOrderNo
     * @return
     */
    public List<RenterGoodsPriceDetailEntity> getRenterGoodsPriceListByRenterOrderNo(String renterOrderNo){
        return renterGoodsPriceDetailMapper.selectByRenterOrderNo(renterOrderNo);
    }

    /**
     *
     * @param renterOrderno 租客子订单号
     * @param isNeedPrice 是否需要价格信息 true-需要  false-不需要
     * @return 通过子订单号获取租客商品信息
     */
    public RenterGoodsDetailDTO getRenterGoodsDetail(String renterOrderno, boolean isNeedPrice){
        return renterGoodsService.getRenterGoodsDetail(renterOrderno,isNeedPrice);
    }

    /**
     * 计算小时数 设置分组日期
     * @param
     */
    public RenterGoodsDetailDTO setPriceAndGroup(RenterGoodsDetailDTO renterGoodsDetailDTO){
        init(renterGoodsDetailDTO);
        combination(renterGoodsDetailDTO);
        log.info("combination-renterGoodsDetailDTO.renterGoodsPriceDetailDTOList={}",JSON.toJSONString(renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList()));
        return renterGoodsDetailDTO;
    }

    //组合
    public void combination(RenterGoodsDetailDTO renterGoodsDetailDTO){
        String renterOrderNo = renterGoodsDetailDTO.getRenterOrderNo();
        if(renterOrderNo == null){
            return;
        }
        LocalDateTime rentTime = renterGoodsDetailDTO.getRentTime();
        LocalDateTime revertTime = renterGoodsDetailDTO.getRevertTime();
        LocalDateTime oldRentTime = renterGoodsDetailDTO.getOldRentTime();
        if(oldRentTime == null || !oldRentTime.isEqual(rentTime)){
            return;
        }

        List<RenterGoodsPriceDetailEntity> dbGoodsPriceList = renterGoodsPriceDetailMapper.selectByRenterOrderNo(renterOrderNo);
        if(dbGoodsPriceList == null || dbGoodsPriceList.size()<=0){
            return;
        }

        RenterGoodsPriceDetailEntity dbPriceMaxCarDay = dbGoodsPriceList.get(dbGoodsPriceList.size() - 1);//最后一条
        List<RenterGoodsPriceDetailDTO> newRenterGoodsPriceList = new ArrayList<>();
        if(oldRentTime.isEqual(rentTime) && revertTime.isEqual(dbPriceMaxCarDay.getRevertTime())){
            dbGoodsPriceList.stream().forEach(y->{
                RenterGoodsPriceDetailDTO renterGoods = new RenterGoodsPriceDetailDTO();
                renterGoods.setCarHourCount(y.getCarHourCount());
                renterGoods.setCarDay(y.getCarDay());
                renterGoods.setCarUnitPrice(y.getCarUnitPrice());
                renterGoods.setRevertTime(y.getRevertTime());
                newRenterGoodsPriceList.add(renterGoods);
            });
            renterGoodsDetailDTO.setRenterGoodsPriceDetailDTOList(newRenterGoodsPriceList);
            return;
        }


        if(dbPriceMaxCarDay.getRevertTime().isBefore(revertTime)){//时间延后
            List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList();
            //过滤出revert_time时间最大的一个分组，比数据库中revert_time大的数据用新的，比据库中revert_time小的数据用数据库的，库中的最后一条数据特殊处理
            //使用carday分组
            Map<LocalDate, List<RenterGoodsPriceDetailEntity>> carDayGroupMap = dbGoodsPriceList
                    .stream()
                    .collect(Collectors.groupingBy(RenterGoodsPriceDetailEntity::getCarDay));
            renterGoodsPriceDetailDTOList.forEach(x->{
                LocalDate carDay = x.getCarDay();
                List<RenterGoodsPriceDetailEntity> dbMapValue = carDayGroupMap.get(carDay);
                if(carDay.isEqual(dbPriceMaxCarDay.getCarDay())){//重复的这一天，可能数据重复，需要特别处理
                    dbMapValue.forEach(y->{
                        RenterGoodsPriceDetailDTO renterGoods = new RenterGoodsPriceDetailDTO();
                        renterGoods.setCarHourCount(y.getCarHourCount());
                        renterGoods.setCarDay(y.getCarDay());
                        renterGoods.setCarUnitPrice(y.getCarUnitPrice());
                        renterGoods.setRevertTime(y.getRevertTime());
                        newRenterGoodsPriceList.add(renterGoods);
                    });
                    float HFirst = 0;
                    if(x.isMaxDay()){
                        HFirst = CommonUtils.getTotalHoursByRentAveragePrice(dbPriceMaxCarDay.getRevertTime(),x.getRevertTime());
                    }else{
                        HFirst = CommonUtils.getHolidayTopHours(dbPriceMaxCarDay.getRevertTime(), LocalDateTimeUtils.localdateToString(carDay));
                    }
                    RenterGoodsPriceDetailDTO renterGoods = new RenterGoodsPriceDetailDTO();
                    renterGoods.setCarHourCount(HFirst);
                    renterGoods.setCarDay(x.getCarDay());
                    renterGoods.setCarUnitPrice(x.getCarUnitPrice());
                    renterGoods.setRevertTime(x.getRevertTime());
                    newRenterGoodsPriceList.add(renterGoods);
                }else if(carDay.isBefore(dbPriceMaxCarDay.getCarDay())){//取数据库中的数据
                    dbMapValue.forEach(y->{
                        RenterGoodsPriceDetailDTO renterGoods = new RenterGoodsPriceDetailDTO();
                        renterGoods.setCarHourCount(y.getCarHourCount());
                        renterGoods.setCarDay(y.getCarDay());
                        renterGoods.setCarUnitPrice(y.getCarUnitPrice());
                        renterGoods.setRevertTime(y.getRevertTime());
                        newRenterGoodsPriceList.add(renterGoods);
                    });
                }else{//取最新的数据
                    RenterGoodsPriceDetailDTO renterGoods = new RenterGoodsPriceDetailDTO();
                    renterGoods.setCarHourCount(x.getCarHourCount());
                    renterGoods.setCarDay(x.getCarDay());
                    renterGoods.setCarUnitPrice(x.getCarUnitPrice());
                    renterGoods.setRevertTime(x.getRevertTime());
                    newRenterGoodsPriceList.add(renterGoods);
                }
            });
            renterGoodsDetailDTO.setRenterGoodsPriceDetailDTOList(newRenterGoodsPriceList);
        }else{//时间提前
            Map<LocalDateTime, List<RenterGoodsPriceDetailEntity>> dbRevertTimeGroup = dbGoodsPriceList
                    .stream()
                    .filter(x->(x.getCarDay().isBefore(revertTime.toLocalDate()) || x.getCarDay().isEqual(revertTime.toLocalDate())))
                    .collect(Collectors.groupingBy(RenterGoodsPriceDetailEntity::getRevertTime));
            //判断new_revert_time属于哪一个dbRevertTimeGroup分组，移除不需要的分组
            LocalDateTime belongGroup = belongGroup(dbRevertTimeGroup,revertTime);
            //判断new_revert_time跟哪一个一天一价是临界点，移除当前组不需要的数据
            List<RenterGoodsPriceDetailDTO> filterList = criticalCarDay(dbRevertTimeGroup,revertTime,belongGroup);
            /*filterList = filterList.stream()
                    .sorted((y,x)->y.getRevertTime().compareTo(x.getRevertTime()))
                    .sorted((y,x)->y.getCarDay().compareTo(x.getCarDay()))
                    .collect(Collectors.toList());*/
            newRenterGoodsPriceList.addAll(filterList);
            log.info("一天一价列表实践提前filterList={}",JSON.toJSONString(filterList));
            renterGoodsDetailDTO.setRenterGoodsPriceDetailDTOList(newRenterGoodsPriceList);
        }
    }



    /*
    * revertTime属于哪一个分组段内
    * */
    public static LocalDateTime belongGroup(Map<LocalDateTime, List<RenterGoodsPriceDetailEntity>> dbRevertTimeGroup,LocalDateTime revertTime){
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
    public static List<RenterGoodsPriceDetailDTO> criticalCarDay(Map<LocalDateTime, List<RenterGoodsPriceDetailEntity>> dbRevertTimeGroup,LocalDateTime revertTime,LocalDateTime belongGroup ){
        List<RenterGoodsPriceDetailEntity> belongGroupPriceList = dbRevertTimeGroup.get(belongGroup);
        if(belongGroupPriceList == null || belongGroupPriceList.size() <= 0){
            throw new PriceDayGroupException();
        }
        List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOS = dbRevertTimeGroupToList(dbRevertTimeGroup);
        LocalDate revertTimeToLocalDate = revertTime.toLocalDate();
        for(Iterator<RenterGoodsPriceDetailEntity> iterator=belongGroupPriceList.iterator();iterator.hasNext();){
            RenterGoodsPriceDetailEntity next = iterator.next();
            LocalDate carDay = next.getCarDay();
            next.setRevertTime(revertTime);
            if(carDay.isAfter(revertTimeToLocalDate)){
                iterator.remove();
            }else if(carDay.isEqual(revertTimeToLocalDate)){//临界点
                double carHourCount = renterGoodsPriceDetailDTOS.stream()
                        .filter(x -> x.getCarDay().equals(carDay))
                        .collect(Collectors.summingDouble(RenterGoodsPriceDetailDTO::getCarHourCount));
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
    private static List<RenterGoodsPriceDetailDTO> dbRevertTimeGroupToList(Map<LocalDateTime, List<RenterGoodsPriceDetailEntity>> dbRevertTimeGroup) {
        List<RenterGoodsPriceDetailDTO> newRenterGoodsPriceList = new ArrayList<>();
        dbRevertTimeGroup.forEach((k,v)->{
            v.forEach(x->{
                RenterGoodsPriceDetailDTO renterGoods = new RenterGoodsPriceDetailDTO();
                renterGoods.setCarHourCount(x.getCarHourCount());
                renterGoods.setCarDay(x.getCarDay());
                renterGoods.setCarUnitPrice(x.getCarUnitPrice());
                renterGoods.setRevertTime(x.getRevertTime());
                newRenterGoodsPriceList.add(renterGoods);
            });
        });
        return newRenterGoodsPriceList;
    }



    //初始化设置小时数和分组日期
    private void init(RenterGoodsDetailDTO renterGoodsDetailDTO){
        LocalDateTime rentTime = renterGoodsDetailDTO.getRentTime();
        LocalDateTime revertTime = renterGoodsDetailDTO.getRevertTime();
        List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList();
        if (renterGoodsPriceDetailDTOList.size() == 1) {//一天的情况
            float holidayTopHours = CommonUtils.getHolidayTopHours(rentTime, LocalDateTimeUtils.localdateToString(revertTime.toLocalDate()));
            RenterGoodsPriceDetailDTO renterGoodsPriceDetailDto = renterGoodsPriceDetailDTOList.get(0);
            renterGoodsPriceDetailDto.setRevertTime(revertTime);
            renterGoodsPriceDetailDto.setCarHourCount(holidayTopHours);
        } else { // 含2天的情况。
            // 第一天
            LocalDate carDay = renterGoodsPriceDetailDTOList.get(0).getCarDay();
            float HFirst = CommonUtils.getHolidayTopHours(rentTime, LocalDateTimeUtils.localdateToString(carDay));
            RenterGoodsPriceDetailDTO renterGoodsPriceFirst = renterGoodsPriceDetailDTOList.get(0);
            renterGoodsPriceFirst.setRevertTime(revertTime);
            renterGoodsPriceFirst.setCarHourCount(HFirst);

            // 中间
            if (renterGoodsPriceDetailDTOList.size() > 2) {
                for (int i = 1; i < renterGoodsPriceDetailDTOList.size() - 1; i++) {
                    RenterGoodsPriceDetailDTO renterGoodsPrice = renterGoodsPriceDetailDTOList.get(i);
                    renterGoodsPrice.setRevertTime(revertTime);
                    renterGoodsPrice.setCarHourCount(24F);
                }
            }
            // 最后一天
            RenterGoodsPriceDetailDTO renterGoodsPriceDetailDto = renterGoodsPriceDetailDTOList.get(renterGoodsPriceDetailDTOList.size() - 1);
            LocalDate dateLast = renterGoodsPriceDetailDto.getCarDay();
            float HLast = CommonUtils.getHolidayFootHours(revertTime, LocalDateTimeUtils.localdateToString(dateLast));
            renterGoodsPriceDetailDto.setRevertTime(revertTime);
            renterGoodsPriceDetailDto.setCarHourCount(HLast);
            renterGoodsPriceDetailDto.setMaxDay(true);
        }
    }

    public static void main(String[] args) {
        LocalDateTime localDateTime1 = LocalDateTime.of(2020,1,7,10,0,0);
        LocalDateTime localDateTime2 = LocalDateTime.of(2020,1,7,12,0,0);
        LocalDateTime localDateTime3 = LocalDateTime.of(2020,1,8,10,0,0);
        LocalDateTime localDateTime4 = LocalDateTime.of(2020,1,9,10,0,0);
        //System.out.println(localDateTime1.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        //System.out.println(localDateTime2.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        //List<LocalDateTime> list = Arrays.asList(localDateTime1,localDateTime2,localDateTime3,localDateTime4);

        //LocalDateTime localDateTime = belongGroup(list, LocalDateTime.of(2020, 1, 9, 11, 0, 0));
        //System.out.println(localDateTime);
        //boolean before = localDateTime1.isBefore(localDateTime2);
       //System.out.println(before);

        /*boolean equal = localDateTime1.isEqual(localDateTime2);
        System.out.println(equal);

        boolean after = localDateTime1.isAfter(localDateTime2);
        System.out.println(after);
        List<RenterGoodsPriceDetailEntity> dbGoodsPriceList = new ArrayList<>();
        RenterGoodsPriceDetailEntity r = new RenterGoodsPriceDetailEntity();
        r.setCarHourCount(1f);
        r.setCarDay(LocalDate.of(2020,1,7));
        r.setCarUnitPrice(100);
        RenterGoodsPriceDetailEntity r1 = new RenterGoodsPriceDetailEntity();
        r1.setCarHourCount(1f);
        r1.setCarDay(LocalDate.of(2020,1,8));
        r1.setCarUnitPrice(200);

        RenterGoodsPriceDetailEntity r2 = new RenterGoodsPriceDetailEntity();
        r2.setCarHourCount(1f);
        r2.setCarDay(LocalDate.of(2020,1,6));
        r2.setCarUnitPrice(200);

        dbGoodsPriceList.add(r);
        dbGoodsPriceList.add(r1);
        dbGoodsPriceList.add(r2);

        List<RenterGoodsPriceDetailEntity> collect = dbGoodsPriceList.stream()
                .sorted((x, y) -> x.getCarDay().compareTo(y.getCarDay()))
                .collect(Collectors.toList());
        System.out.println(JSON.toJSONString(collect));
*/

        RenterGoodsPriceDetailEntity renterGoodsPriceDetailEntity = new RenterGoodsPriceDetailEntity();
        renterGoodsPriceDetailEntity.setCarUnitPrice(11);

        List<RenterGoodsPriceDetailEntity> list = Arrays.asList(renterGoodsPriceDetailEntity);

        for(Iterator<RenterGoodsPriceDetailEntity> iterator=list.iterator();iterator.hasNext();){
            iterator.remove();
            iterator.next().setCarUnitPrice(22);
        }

        System.out.println(JSON.toJSONString(list));
    }


}
