package com.atzuche.order.rentermem.service;

import com.atzuche.order.commons.entity.dto.OrderContextDto;
import com.atzuche.order.commons.entity.dto.RenterMemberDto;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDto;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import com.atzuche.order.rentermem.entity.RenterMemberRightEntity;
import com.atzuche.order.rentermem.mapper.RenterMemberRightMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.atzuche.order.rentermem.mapper.RenterMemberMapper;

import java.util.ArrayList;
import java.util.List;


/**
 * 租客端会员概览表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:27:28
 */
@Service
public class RenterMemberService{
    @Autowired
    private RenterMemberMapper renterMemberMapper;
    @Autowired
    private RenterMemberRightMapper renterMemberRightMapper;

    /**
     * 保存租客用户信息
     */
    public void save(OrderContextDto orderContextDto){
        RenterMemberDto renterMemberDto = orderContextDto.getRenterMemberDto();
        RenterMemberEntity renterMemberEntity = new RenterMemberEntity();
        BeanUtils.copyProperties(renterMemberDto,renterMemberEntity);
        renterMemberMapper.insert(renterMemberEntity);

        List<RenterMemberRightEntity> list = new ArrayList<>();
        renterMemberDto.getRenterMemberRightDtoList().forEach(x->{
            RenterMemberRightEntity renterMemberRightEntity = new RenterMemberRightEntity();
            BeanUtils.copyProperties(x,renterMemberRightEntity);
            list.add(renterMemberRightEntity);
        });
        renterMemberRightMapper.insertList(list);
    }

    /**
     * 通过会员号获取会员信息
     * @param renterOrderNo 子订单号
     * @param isNeedRight 是否需要权益值信息
     */
    public RenterMemberDto selectrenterMemberByMemNo(String renterOrderNo,boolean isNeedRight){
        RenterMemberDto renterMemberDto = new RenterMemberDto();
        RenterMemberEntity renterMemberEntity = renterMemberMapper.selectByRenterOrderNo(renterOrderNo);
        BeanUtils.copyProperties(renterMemberEntity,renterMemberDto);
        if(!isNeedRight){
            return renterMemberDto;
        }
        List<RenterMemberRightEntity> dbList = renterMemberRightMapper.selectByRenterOrderNo(renterOrderNo);
        List<RenterMemberRightDto> list = new ArrayList<>();
        dbList.forEach(x->{
            RenterMemberRightDto renterMemberRightDto = new RenterMemberRightDto();
            BeanUtils.copyProperties(x,renterMemberRightDto);
            list.add(renterMemberRightDto);
        });
        renterMemberDto.setRenterMemberRightDtoList(list);
        return renterMemberDto;
    }

}
