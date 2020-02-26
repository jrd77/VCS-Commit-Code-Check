package com.atzuche.order.renterwz.service;

import com.atzuche.order.renterwz.entity.WzQueryDayConfEntity;
import com.atzuche.order.renterwz.mapper.WzQueryDayConfMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WzQueryDayConfService
 *
 * @author shisong
 * @date 2020/1/3
 */
@Service
public class WzQueryDayConfService {

    @Resource
    private WzQueryDayConfMapper wzQueryDayConfMapper;

    /**
     * 违章查询多少天的订单
     */
    @Value("${com.autoyol.tv.query.TVQueryDays}")
    private Integer TV_QUERY_DAYS;

    public List<WzQueryDayConfEntity> queryAll() {
        return wzQueryDayConfMapper.queryList();
    }

    public Map<Integer, List<Integer>> convertCityByList(List<WzQueryDayConfEntity> list) {
        Map<Integer, List<Integer>> map = new HashMap<>(16);
        if (list == null || list.size() == 0) {
            map.put(TV_QUERY_DAYS, null);
            return map;
        }
        for (WzQueryDayConfEntity illegalQueryDayConf : list) {
            //查询的天数 15,30
            Integer key = illegalQueryDayConf.getIllegalQueryDay();
            List<Integer> city = map.get(key);
            if (city == null || city.size() == 0) {
                city = new ArrayList<>();
                map.put(key, city);
            }
            city.add(illegalQueryDayConf.getCityCode());
        }
        return map;
    }

    /**
     * 组装数据：map{查询日期：城市代码列表}
     * @param list
     * @return
     */
    public Map<Integer, List<Integer>> convertTranQueryDayByList(List<WzQueryDayConfEntity> list){
        Map<Integer, List<Integer>> map = new HashMap<>(16);
        if (list == null || list.size() == 0) {
            map.put(TV_QUERY_DAYS, null);
            return map;
        }
        for (WzQueryDayConfEntity illegalQueryDayConf : list) {
            //18,33天
            Integer key = illegalQueryDayConf.getTransProcessDay();
            List<Integer> city = map.get(key);
            if (city == null || city.size() == 0) {
                city = new ArrayList<Integer>();
                map.put(key, city);
            }
            city.add(illegalQueryDayConf.getCityCode());
        }
        return map;
    }
}
