package com.atzuche.order.rentercommodity.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsPriceDetailDTO;
import com.atzuche.order.rentercommodity.entity.RenterGoodsPriceDetailEntity;
import com.atzuche.order.rentercommodity.mapper.RenterGoodsPriceDetailMapper;
import com.autoyol.platformcost.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * @Author ZhangBin
 * @Date 2019/12/18 11:00 
 * @Description: 租客商品 - 对外模块提供接口类
 * 
 **/
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
     * @param orderContextDto
     */
    public RenterGoodsDetailDTO setPriceAndGroup(RenterGoodsDetailDTO renterGoodsDetailDTO){
        init(renterGoodsDetailDTO);
        combination(renterGoodsDetailDTO);
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

        //使用revert_time分组
        Map<LocalDate, List<RenterGoodsPriceDetailEntity>> carDayGroupMap = dbGoodsPriceList
                .stream()
                .collect(Collectors.groupingBy(RenterGoodsPriceDetailEntity::getCarDay));

        RenterGoodsPriceDetailEntity dbPriceMaxCarDay = dbGoodsPriceList.get(dbGoodsPriceList.size() - 1);//最后一条
        List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList();

        List<RenterGoodsPriceDetailDTO> newRenterGoodsPriceList = new ArrayList<>();
        if(dbPriceMaxCarDay.getRevertTime().isBefore(revertTime)){//时间延后
            //过滤出revert_time时间最大的一个分组，比数据库中revert_time大的数据用新的，比据库中revert_time小的数据用数据库的，库中的最后一条数据特殊处理
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

                    float HFirst = CommonUtils.getHolidayTopHours(dbPriceMaxCarDay.getRevertTime(), LocalDateTimeUtils.localdateToString(carDay));
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

                    float HFirst = CommonUtils.getHolidayTopHours(dbPriceMaxCarDay.getRevertTime(), LocalDateTimeUtils.localdateToString(carDay));
                }
            });

        }else{//时间提前
            //判断数据库中每个分组的revert_time 找到比数据库中的revert_time相同的
           renterGoodsPriceDetailDTOList.forEach(x->{
               LocalDate carDay = x.getCarDay();
               List<RenterGoodsPriceDetailEntity> dbMapValue = carDayGroupMap.get(carDay);
               dbMapValue.forEach(y->{
                  /* if(){

                   }else{

                   }*/
                   RenterGoodsPriceDetailDTO renterGoods = new RenterGoodsPriceDetailDTO();
                   renterGoods.setCarUnitPrice(y.getCarUnitPrice());
                   renterGoods.setRevertTime(revertTime);
                   renterGoods.setCarDay(y.getCarDay());
                   renterGoods.setCarHourCount(x.getCarHourCount());
                   newRenterGoodsPriceList.add(renterGoods);
               });
            });
        }
        renterGoodsDetailDTO.setRenterGoodsPriceDetailDTOList(newRenterGoodsPriceList);
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
        }

    }

    public static void main(String[] args) {
        LocalDateTime localDateTime1 = LocalDateTime.of(2020,1,7,12,12,0);
        LocalDateTime localDateTime2 = LocalDateTime.of(2020,1,7,12,12,1);
        boolean before = localDateTime1.isBefore(localDateTime2);
        System.out.println(before);

        boolean equal = localDateTime1.isEqual(localDateTime2);
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
        dbGoodsPriceList.add(r);
        dbGoodsPriceList.add(r1);

        Map<LocalDate, RenterGoodsPriceDetailEntity> collect = dbGoodsPriceList.stream().collect(Collectors.toMap(RenterGoodsPriceDetailEntity::getCarDay, x -> x));
        System.out.println(collect);

        System.out.println(JSON.toJSONString(collect.get(LocalDate.of(2020,1,7))));
    }
}
