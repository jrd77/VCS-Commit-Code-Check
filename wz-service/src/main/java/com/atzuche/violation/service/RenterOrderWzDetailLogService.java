package com.atzuche.violation.service;

import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.entity.dto.RenterOrderWzDetailLogDTO;
import com.atzuche.order.commons.entity.dto.RenterOrderWzDetailLogList;
import com.atzuche.order.commons.entity.wz.RenterOrderWzDetailLogEntity;

import com.atzuche.violation.common.PageParam;
import com.atzuche.violation.mapper.RenterOrderWzDetailLogMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RenterOrderWzDetailLogService {
    @Autowired
    private RenterOrderWzDetailLogMapper renterOrderWzDetailLogMapper;

    /*
     * @Author ZhangBin
     * @Date 2020/6/8 14:43
     * @Description: 添加数据
     *
     **/
    public int insert(RenterOrderWzDetailLogEntity renterOrderWzDetailLogEntity){
        return renterOrderWzDetailLogMapper.insertSelective(renterOrderWzDetailLogEntity);
    }

    /*
     * @Author ZhangBin
     * @Date 2020/6/9 10:52
     * @Description: 分页查询
     *
     **/
    public RenterOrderWzDetailLogList queryList(PageParam pageParam) {
        PageHelper.startPage(pageParam.getPageNumber(),pageParam.getPageSize());
        List<RenterOrderWzDetailLogEntity> renterOrderWzDetailLogEntityList = renterOrderWzDetailLogMapper.queryList();
        PageInfo<RenterOrderWzDetailLogEntity> pageInfo = new PageInfo<>(renterOrderWzDetailLogEntityList);
        List<RenterOrderWzDetailLogEntity> list = pageInfo.getList();
        List<RenterOrderWzDetailLogDTO> renterOrderWzDetailLogDTOS = new ArrayList<>();
        Optional.ofNullable(list).orElseGet(ArrayList::new).stream().forEach(x->{
            RenterOrderWzDetailLogDTO renterOrderWzDetailLogDTO = new RenterOrderWzDetailLogDTO();
            BeanUtils.copyProperties(x,renterOrderWzDetailLogDTO);
            LocalDateTime updateTime = x.getUpdateTime();
            renterOrderWzDetailLogDTO.setUpdateTimeStr(LocalDateTimeUtils.localdateToString(updateTime,LocalDateTimeUtils.DEFAULT_PATTERN));
            renterOrderWzDetailLogDTOS.add(renterOrderWzDetailLogDTO);
        });
        RenterOrderWzDetailLogList renterOrderWzDetailLogList = new RenterOrderWzDetailLogList();
        renterOrderWzDetailLogList.setRenterOrderWzDetailLogDTOS(renterOrderWzDetailLogDTOS);
        renterOrderWzDetailLogList.setCount(pageInfo.getTotal());
        renterOrderWzDetailLogList.setTotalPage(pageInfo.getPages());
        return renterOrderWzDetailLogList;
    }
}
