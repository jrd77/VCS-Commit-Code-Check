package com.atzuche.order.rentermem.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import com.atzuche.order.rentermem.entity.RenterMemberRightEntity;
import com.atzuche.order.rentermem.mapper.RenterMemberMapper;
import com.atzuche.order.rentermem.mapper.RenterMemberRightMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 租客端会员概览表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:27:28
 */
@Service
public class RenterMemberService{

    private static Logger logger = LoggerFactory.getLogger(RenterMemberService.class);

    @Autowired
    private RenterMemberMapper renterMemberMapper;
    @Autowired
    private RenterMemberRightMapper renterMemberRightMapper;


    /**
     * 保存租客会员信息
     *
     * @param orderNo 订单号
     * @param renterOrderNo 租客订单号
     * @param renterMemberDto 租客会员信息(不包含订单号)
     */
    public void save(String orderNo, String renterOrderNo, RenterMemberDTO renterMemberDto) {
        logger.info("Save renter member detail.param is,orderNo:[{}],renterOrderNo:[{}],renterMemberDto:[{}]",
                orderNo, renterOrderNo, JSON.toJSONString(renterMemberDto));
        if(Objects.isNull(renterMemberDto)) {
            return ;
        }
        renterMemberDto.setOrderNo(orderNo);
        renterMemberDto.setRenterOrderNo(renterOrderNo);
        save(renterMemberDto);
    }

    /**
     * 保存租客会员信息
     *
     * @param renterMemberDto 租客会员信息(包含订单号)
     */
    public void save(RenterMemberDTO renterMemberDto){
        logger.info("Save renter member detail.param is,renterMemberDto:[{}]", JSON.toJSONString(renterMemberDto));

        RenterMemberEntity renterMemberEntity = new RenterMemberEntity();
        BeanUtils.copyProperties(renterMemberDto,renterMemberEntity);
        renterMemberMapper.insert(renterMemberEntity);

        List<RenterMemberRightEntity> list = new ArrayList<>();
        renterMemberDto.getRenterMemberRightDTOList().forEach(x->{
            RenterMemberRightEntity renterMemberRightEntity = new RenterMemberRightEntity();
            BeanUtils.copyProperties(x,renterMemberRightEntity);
            renterMemberRightEntity.setOrderNo(renterMemberDto.getOrderNo());
            renterMemberRightEntity.setRenterOrderNo(renterMemberDto.getRenterOrderNo());
            renterMemberRightEntity.setMemNo(renterMemberDto.getMemNo());
            list.add(renterMemberRightEntity);
        });
        renterMemberRightMapper.insertList(list);
    }

    /**
     * 通过会员号获取会员信息
     * @param renterOrderNo 子订单号
     * @param isNeedRight 是否需要权益值信息
     */
    public RenterMemberDTO selectrenterMemberByRenterOrderNo(String renterOrderNo, boolean isNeedRight){
        RenterMemberDTO renterMemberDto = new RenterMemberDTO();
        RenterMemberEntity renterMemberEntity = renterMemberMapper.selectByRenterOrderNo(renterOrderNo);
        if(renterMemberEntity != null){
            BeanUtils.copyProperties(renterMemberEntity,renterMemberDto);
        }
        if(!isNeedRight){
            return renterMemberDto;
        }
        List<RenterMemberRightEntity> dbList = renterMemberRightMapper.selectByRenterOrderNo(renterOrderNo);
        List<RenterMemberRightDTO> list = new ArrayList<>();
        dbList.forEach(x->{
            RenterMemberRightDTO renterMemberRightDto = new RenterMemberRightDTO();
            BeanUtils.copyProperties(x,renterMemberRightDto);
            list.add(renterMemberRightDto);
        });
        renterMemberDto.setRenterMemberRightDTOList(list);
        return renterMemberDto;
    }

    public String getRenterNoByOrderNo(String orderNo) {
        return renterMemberMapper.getRenterNoByOrderNo(orderNo);
    }

    public List<RenterMemberEntity> queryMemNoAndPhoneByOrderList(List<String> orderNos) {
        return renterMemberMapper.queryMemNoAndPhoneByOrderList(orderNos);
    }

    public RenterMemberEntity queryRenterInfoByOrderNoAndRenterNo(String orderNo, String renterNo) {
        return renterMemberMapper.queryRenterInfoByOrderNoAndRenterNo(orderNo,renterNo);
    }


}
