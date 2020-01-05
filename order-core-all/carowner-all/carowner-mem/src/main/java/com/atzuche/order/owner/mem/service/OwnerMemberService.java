package com.atzuche.order.owner.mem.service;

import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberRightDTO;
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
     * 保存车主用户信息
     */
    public void save(OwnerMemberDTO ownerMemberDto){
        OwnerMemberEntity ownerMemberEntity = new OwnerMemberEntity();
        BeanUtils.copyProperties(ownerMemberDto,ownerMemberEntity);
        ownerMemberMapper.insert(ownerMemberEntity);

        List<OwnerMemberRightEntity> list = new ArrayList<>();
        ownerMemberDto.getOwnerMemberRightDTOList().forEach(x->{
            OwnerMemberRightEntity ownerMemberRightEntity = new OwnerMemberRightEntity();
            BeanUtils.copyProperties(x,ownerMemberRightEntity);
            ownerMemberRightEntity.setOrderNo(ownerMemberDto.getOrderNo());
            ownerMemberRightEntity.setOwnerOrderNo(ownerMemberDto.getOwnerOrderNo());
            ownerMemberRightEntity.setMemNo(ownerMemberDto.getMemNo());
            list.add(ownerMemberRightEntity);
        });
        ownerMemberRightMapper.insertList(list);
    }

    /**
     * 通过会员号获取会员信息
     * @param ownerOrderNo 子订单号
     * @param isNeedRight 是否需要权益值信息
     */
    public OwnerMemberDTO selectownerMemberByMemNo(String ownerOrderNo, boolean isNeedRight){
        OwnerMemberDTO ownerMemberDto = new OwnerMemberDTO();
        OwnerMemberEntity ownerMemberEntity = ownerMemberMapper.selectByOwnerOrderNo(ownerOrderNo);
        BeanUtils.copyProperties(ownerMemberEntity,ownerMemberDto);
        if(!isNeedRight){
            return ownerMemberDto;
        }
        List<OwnerMemberRightEntity> dbList = ownerMemberRightMapper.selectByOwnerOrderNo(ownerOrderNo);
        List<OwnerMemberRightDTO> list = new ArrayList<>();
        dbList.forEach(x->{
            OwnerMemberRightDTO ownerMemberRightDto = new OwnerMemberRightDTO();
            BeanUtils.copyProperties(x,ownerMemberRightDto);
            list.add(ownerMemberRightDto);
        });
        ownerMemberDto.setOwnerMemberRightDTOList(list);
        return ownerMemberDto;
    }


    public String getOwnerNoByOrderNo(String orderNo) {
        return ownerMemberMapper.getOwnerNoByOrderNo(orderNo);
    }

    public List<OwnerMemberEntity> queryMemNoAndPhoneByOrderList(List<String> orderNos) {
        return ownerMemberMapper.queryMemNoAndPhoneByOrderList(orderNos);
    }
}
