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
        if(oldRentTime == null && !oldRentTime.isEqual(rentTime)){
            return;
        }

        List<RenterGoodsPriceDetailEntity> dbGoodsPriceList = renterGoodsPriceDetailMapper.selectByRenterOrderNo(renterOrderNo);
        if(dbGoodsPriceList == null || dbGoodsPriceList.size()<=0){
            return;
        }
        Map<LocalDate, RenterGoodsPriceDetailEntity> mapPrice = dbGoodsPriceList
                .stream()
                .collect(Collectors.toMap(RenterGoodsPriceDetailEntity::getCarDay, x -> x));
        RenterGoodsPriceDetailEntity dbPriceMaxCarDay = dbGoodsPriceList.get(dbGoodsPriceList.size() - 1);
        LocalDateTime oldRevertTime = dbPriceMaxCarDay.getRevertTime();
        List<RenterGoodsPriceDetailDTO> renterGoodsPriceDetailDTOList = renterGoodsDetailDTO.getRenterGoodsPriceDetailDTOList();
        if(oldRevertTime.isBefore(revertTime)){//时间延后
            RenterGoodsPriceDetailDTO middle = new RenterGoodsPriceDetailDTO();
            renterGoodsPriceDetailDTOList.forEach(x->{
                RenterGoodsPriceDetailEntity dbPrice = mapPrice.get(x.getCarDay());
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
            renterGoodsPriceDetailDTOList.add(middle);

            //租期不重叠部分 中间部分init中已经处理
            //租期不重叠部分 最后一天 init中已经处理
        }else{//时间提前
            //替换金额即可，使用数据库中的一天一价
            renterGoodsPriceDetailDTOList.forEach(x->{
                RenterGoodsPriceDetailEntity dbPrice = mapPrice.get(x.getCarDay());
                x.setCarUnitPrice(dbPrice.getCarUnitPrice());
            });
        }
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
