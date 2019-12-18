package com.atzuche.order.owner.mem.service;

import com.atzuche.order.commons.entity.dto.OrderContextDto;
import com.atzuche.order.commons.entity.dto.OwnerMemberDto;
import com.atzuche.order.commons.entity.dto.OwnerMemberRightDto;
import com.atzuche.order.owner.mem.entity.OwnerMemberEntity;
import com.atzuche.order.owner.mem.entity.OwnerMemberRightEntity;
import com.atzuche.order.owner.mem.mapper.OwnerMemberMapper;
import com.atzuche.order.owner.mem.mapper.OwnerMemberRightMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 车主会员概览表
 *
 * @author ZhangBin
 * @date 2019-12-18 16:15:16
 */
@Service
public class OwnerMemberService{
    @Autowired
    private OwnerMemberMapper ownerMemberMapper;
    @Autowired
    private OwnerMemberRightMapper ownerMemberRightMapper;

    /**
     * 保存租客用户信息
     */
    public void save(OrderContextDto orderContextDto){
        OwnerMemberDto ownerMemberDto = orderContextDto.getOwnerMemberDto();
        OwnerMemberEntity ownerMemberEntity = new OwnerMemberEntity();
        BeanUtils.copyProperties(ownerMemberDto,ownerMemberEntity);
        ownerMemberMapper.insert(ownerMemberEntity);

        List<OwnerMemberRightEntity> list = new ArrayList<>();
        ownerMemberDto.getOwnerMemberRightDtoList().forEach(x->{
            OwnerMemberRightEntity ownerMemberRightEntity = new OwnerMemberRightEntity();
            BeanUtils.copyProperties(x,ownerMemberRightEntity);
            list.add(ownerMemberRightEntity);
        });
        ownerMemberRightMapper.insertList(list);
    }

    /**
     * 通过会员号获取会员信息
     * @param ownerOrderNo 子订单号
     * @param isNeedRight 是否需要权益值信息
     */
    public OwnerMemberDto selectownerMemberByMemNo(String ownerOrderNo,boolean isNeedRight){
        OwnerMemberDto ownerMemberDto = new OwnerMemberDto();
        OwnerMemberEntity ownerMemberEntity = ownerMemberMapper.selectByOwnerOrderNo(ownerOrderNo);
        BeanUtils.copyProperties(ownerMemberEntity,ownerMemberDto);
        if(!isNeedRight){
            return ownerMemberDto;
        }
        List<OwnerMemberRightEntity> dbList = ownerMemberRightMapper.selectByOwnerOrderNo(ownerOrderNo);
        List<OwnerMemberRightDto> list = new ArrayList<>();
        dbList.forEach(x->{
            OwnerMemberRightDto ownerMemberRightDto = new OwnerMemberRightDto();
            BeanUtils.copyProperties(x,ownerMemberRightDto);
            list.add(ownerMemberRightDto);
        });
        ownerMemberDto.setOwnerMemberRightDtoList(list);
        return ownerMemberDto;
    }


}
