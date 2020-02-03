package com.atzuche.order.commons;

import com.atzuche.order.commons.entity.dto.OwnerGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.OwnerMemberDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.commons.vo.req.NormalOrderReqVO;
import com.atzuche.order.commons.vo.req.OrderReqVO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 下单订单请求上下午
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2019/12/19 7:32 下午
 **/
@Data
public class OrderReqContext {

    public static final String RISK_AUDIT_ID="risk_audit_id";

    /**
     * 车主会员信息
     */
    private OwnerMemberDTO ownerMemberDto;
    /**
     * 租客会员信息
     */
    private RenterMemberDTO renterMemberDto;
    /**
     * 车主商品信息
     **/
    private OwnerGoodsDetailDTO ownerGoodsDetailDto;
    /**
     * 租客商品信息
     */
    private RenterGoodsDetailDTO renterGoodsDetailDto;
    /**
     * 提交订单请求信息
     */
    private OrderReqVO orderReqVO;
    /**
     * 提供扩展参数，但是该参数不能直接操作
     */
    private Map<String,String> internalParams = new HashMap<>();


    public void setRiskAuditId(String riskAuditId){
        this.internalParams.put(RISK_AUDIT_ID,riskAuditId);
    }

    public String getRiskAuditId(){
        return this.internalParams.get(RISK_AUDIT_ID);
    }

    public void setInternalParams(Map<String,String> maps){
        throw new RuntimeException("not supported");
    }


}
