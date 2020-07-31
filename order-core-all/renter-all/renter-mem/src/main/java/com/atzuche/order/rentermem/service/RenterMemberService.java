package com.atzuche.order.rentermem.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.CategoryEnum;
import com.atzuche.order.commons.enums.MemberFlagEnum;
import com.atzuche.order.commons.enums.RightTypeEnum;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import com.atzuche.order.rentermem.entity.RenterMemberRightEntity;
import com.atzuche.order.rentermem.mapper.RenterMemberMapper;
import com.atzuche.order.rentermem.mapper.RenterMemberRightMapper;
import com.atzuche.order.rentermem.utils.RenterMemUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * 租客端会员概览表
 *
 * @author ZhangBin
 * @date 2019-12-14 17:27:28
 */
@Slf4j
@Service
public class RenterMemberService{

    private static Logger logger = LoggerFactory.getLogger(RenterMemberService.class);

    @Autowired
    private RenterMemberMapper renterMemberMapper;
    @Autowired
    private RenterMemberRightMapper renterMemberRightMapper;


    /*
     * @Author ZhangBin
     * @Date 2020/4/17 10:45
     * @Description: 是否是企业用户 true:是 false：不是
     * renterOrderNo: 租客子订单号
     **/
    public boolean isEnterpriseUserOrder(String renterOrderNo){
        List<RenterMemberRightEntity> renterMemberRightEntityList = getRenterMemRightByRenterOrderNo(renterOrderNo);
        List<RenterMemberRightDTO> renterMemberRightDTOS = new ArrayList<>();
        renterMemberRightEntityList.forEach(x->{
            RenterMemberRightDTO renterMemberRightDto = new RenterMemberRightDTO();
            BeanUtils.copyProperties(x,renterMemberRightDto);
            renterMemberRightDTOS.add(renterMemberRightDto);
        });
        RenterMemberRightDTO renterMemberRightDTO = RenterMemUtils.filterRight(renterMemberRightDTOS, RightTypeEnum.MEMBER_FLAG, Arrays.asList(MemberFlagEnum.QYYH,MemberFlagEnum.QYXYYH), "1");
        if(renterMemberRightDTO == null){
            return false;
        }
        return true;
    }
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
        log.info("RenterMemberService.save renterMemberDto={}", JSON.toJSONString(renterMemberDto));
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
            if(RightTypeEnum.MEMBER_FLAG.getCode() == x.getRightType()
                    && (MemberFlagEnum.QYYH.getRightCode().equals(x.getRightCode()) || MemberFlagEnum.QYXYYH.getRightCode().equals(x.getRightCode()))
                    && renterMemberDto.getOrderCategory()!=null
                    && !(renterMemberDto.getOrderCategory().equals(CategoryEnum.ORDINARY.getCode()) || renterMemberDto.getOrderCategory().equals(CategoryEnum.LONG_ORDER.getCode()))
                ){
                log.info("非短租或者长租，企业用户权益为0 renterMemberDto={}",JSON.toJSONString(renterMemberDto));
                renterMemberRightEntity.setRightValue("0");
            }
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

    public List<RenterMemberRightEntity> getRenterMemRightByRenterOrderNo(String renterOrderNo){
        List<RenterMemberRightEntity> dbList = renterMemberRightMapper.selectByRenterOrderNo(renterOrderNo);
        return dbList;
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

    public RenterMemberEntity getRenterMemberByOrderNo(String orderNo) {
    	return renterMemberMapper.getRenterMemberByOrderNo(orderNo);
    }
    
    public String queryRenterPhoneByOrderNo(String orderNo) {
        return renterMemberMapper.queryRenterPhoneByOrderNo(orderNo);
    }

    public RenterMemberEntity getRenterMemberByMemberNo(String memNo) {
        return renterMemberMapper.getRenterMemberByMemberNo(memNo);
    }
}
