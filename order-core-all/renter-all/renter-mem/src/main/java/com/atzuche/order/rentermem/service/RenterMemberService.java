package com.atzuche.order.rentermem.service;

import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberRightDTO;
import com.atzuche.order.commons.enums.MemberFlagEnum;
import com.atzuche.order.commons.enums.RightTypeEnum;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import com.atzuche.order.rentermem.entity.RenterMemberRightEntity;
import com.atzuche.order.rentermem.mapper.RenterMemberMapper;
import com.atzuche.order.rentermem.mapper.RenterMemberRightMapper;
import com.atzuche.order.rentermem.utils.RenterMemUtils;
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
        RenterMemberRightEntity renterMemberRightEntity = RenterMemUtils.filterRight(renterMemberRightEntityList, RightTypeEnum.MEMBER_FLAG, MemberFlagEnum.QYYH, "1");
        return Objects.nonNull(renterMemberRightEntity);
    }
    /**
     * 保存租客用户信息
     */
    public void save(RenterMemberDTO renterMemberDto){
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


}
