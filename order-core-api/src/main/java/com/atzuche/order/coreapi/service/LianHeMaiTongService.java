package com.atzuche.order.coreapi.service;

import com.atzuche.order.commons.enums.ErrorCode;
import com.atzuche.order.commons.exceptions.LianHeMaiTongMemberException;
import com.atzuche.order.commons.vo.LianHeMaiTongMemberVO;
import com.atzuche.order.owner.commodity.entity.OwnerGoodsEntity;
import com.atzuche.order.owner.commodity.service.OwnerGoodsService;
import com.atzuche.order.owner.mem.entity.OwnerMemberEntity;
import com.atzuche.order.owner.mem.service.OwnerMemberService;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.parentorder.service.OrderSourceStatService;
import com.atzuche.order.parentorder.service.OrderStatusService;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.rentermem.entity.RenterMemberEntity;
import com.atzuche.order.rentermem.service.RenterMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class LianHeMaiTongService {
    @Autowired
    private OwnerGoodsService ownerGoodsService;
    @Autowired
    private OwnerMemberService ownerMemberService;
    @Autowired
    private RenterMemberService renterMemberService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private OrderSourceStatService orderSourceStatService;

    public LianHeMaiTongMemberVO getMemberInfo(String phone, String memNo, String platNum) {
        LianHeMaiTongMemberVO lhmtVO = new LianHeMaiTongMemberVO();
        if(memNo!=null && memNo.trim().length()>0){
            lhmtVO.setOwnerMemNo(memNo);
            OwnerMemberEntity ownerMemberEntity = ownerMemberService.getOwnerNoByMemberNo(memNo);
            if(ownerMemberEntity != null){
                lhmtVO.setOwnerName(ownerMemberEntity.getRealName());
            }
            RenterMemberEntity renterMemberEntity = renterMemberService.getRenterMemberByMemberNo(memNo);
            if(renterMemberEntity != null){
                lhmtVO.setOwnerName(renterMemberEntity.getRealName());
            }
        }else if(phone!=null && phone.trim().length()>0){


        }else if(platNum!=null && platNum.trim().length()>0){
            OwnerGoodsEntity ownerGoodsByPlatNum = ownerGoodsService.getOwnerGoodsByPlatNum(platNum);


        }else{
            LianHeMaiTongMemberException e = new LianHeMaiTongMemberException(ErrorCode.LHMT_QUERY_ERROR.getCode(),"至少输入一个参数条件");
            log.error("联合麦通查询异常",e);
            throw e;
        }
        return null;
    }
}
