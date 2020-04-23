/**
 * 
 */
package com.atzuche.order.settle.service.notservice;

import com.atzuche.order.cashieraccount.entity.CashierEntity;
import com.atzuche.order.cashieraccount.service.notservice.CashierNoTService;
import com.atzuche.order.cashieraccount.vo.req.CashierRefundApplyReqVO;
import com.atzuche.order.commons.entity.dto.CostBaseDTO;
import com.atzuche.order.commons.entity.dto.MileageAmtDTO;
import com.atzuche.order.commons.entity.dto.RenterGoodsDetailDTO;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.account.CostTypeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.enums.RenterHandoverCarTypeEnum;
import com.atzuche.order.delivery.vo.handover.HandoverCarRepVO;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.settle.vo.req.RefundApplyVO;
import com.atzuche.order.settle.vo.req.SettleOrders;
import com.autoyol.autopay.gateway.constant.DataPayKindConstant;
import com.autoyol.autopay.gateway.constant.DataPayTypeConstant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author jhuang
 * 结算的代理 公共类
 */
@Service
@Slf4j
public class OrderSettleProxyService {
	@Autowired
	private CashierNoTService cashierNoTService;
	
	public CostTypeEnum getCostTypeEnumByConsoleCost(String fineSubsidyCode){
        CostTypeEnum costTypeEnum = CostTypeEnum.OWNER_CONSOLE_COST;

        if(SubsidySourceCodeEnum.OWNER.getCode().equals(fineSubsidyCode)){
            costTypeEnum = CostTypeEnum.OWNER_CONSOLE_COST;
        }
        if(SubsidySourceCodeEnum.RENTER.getCode().equals(fineSubsidyCode)){
            costTypeEnum = CostTypeEnum.RENTER_CONSOLE_COST;
        }
        return costTypeEnum;
    }
	
    public CostTypeEnum getCostTypeEnumBySubsidy(String fineSubsidyCode){
        CostTypeEnum costTypeEnum = CostTypeEnum.OWNER_SUBSIDY;
        if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(fineSubsidyCode)){
            costTypeEnum = CostTypeEnum.CONSOLE_SUBSIDY;
        }
        if(SubsidySourceCodeEnum.OWNER.getCode().equals(fineSubsidyCode)){
            costTypeEnum = CostTypeEnum.OWNER_SUBSIDY;
        }
        if(SubsidySourceCodeEnum.RENTER.getCode().equals(fineSubsidyCode)){
            costTypeEnum = CostTypeEnum.RENTER_SUBSIDY;
        }
        return costTypeEnum;
    }
    
	public CostTypeEnum getCostTypeEnumByFine(String fineSubsidyCode){
        CostTypeEnum costTypeEnum = CostTypeEnum.OWNER_FINE;
        if(SubsidySourceCodeEnum.PLATFORM.getCode().equals(fineSubsidyCode)){
            costTypeEnum = CostTypeEnum.CONSOLE_FINE;
        }
        if(SubsidySourceCodeEnum.OWNER.getCode().equals(fineSubsidyCode)){
            costTypeEnum = CostTypeEnum.OWNER_FINE;
        }
        if(SubsidySourceCodeEnum.RENTER.getCode().equals(fineSubsidyCode)){
            costTypeEnum = CostTypeEnum.RENTER_FINE;
        }
        return costTypeEnum;
    }
	
	/**
     *  租客返回基本信息
     * @return
     */
    public CostBaseDTO getCostBaseRent(SettleOrders settleOrders,RenterOrderEntity renterOrder){
        CostBaseDTO costBaseDTO = new CostBaseDTO();
        costBaseDTO.setOrderNo(renterOrder.getOrderNo());
        costBaseDTO.setMemNo(renterOrder.getRenterMemNo());
        costBaseDTO.setRenterOrderNo(renterOrder.getRenterOrderNo());
        costBaseDTO.setStartTime(renterOrder.getExpRentTime());
        costBaseDTO.setEndTime(renterOrder.getExpRevertTime());
        return costBaseDTO;
    }
    
    /**
     * 交接车-获取超里程费用
     */
    public MileageAmtDTO getMileageAmtDTO(SettleOrders settleOrders, RenterOrderEntity renterOrder,HandoverCarRepVO handoverCarRep,RenterGoodsDetailDTO renterGoodsDetail) {
        MileageAmtDTO mileageAmtDTO = new MileageAmtDTO();
        CostBaseDTO costBaseDTO = getCostBaseRent(settleOrders,renterOrder);
        mileageAmtDTO.setCostBaseDTO(costBaseDTO);

        mileageAmtDTO.setCarOwnerType(renterGoodsDetail.getCarOwnerType());
        mileageAmtDTO.setGuideDayPrice(renterGoodsDetail.getCarGuideDayPrice());
        mileageAmtDTO.setDayMileage(renterGoodsDetail.getCarDayMileage());

        //默认值0  取/还 车里程数
        mileageAmtDTO.setGetmileage(0);
        mileageAmtDTO.setReturnMileage(0);
        List<RenterHandoverCarInfoEntity> renterHandoverCarInfos = handoverCarRep.getRenterHandoverCarInfoEntities();
        if(!CollectionUtils.isEmpty(renterHandoverCarInfos)){
            for(int i=0;i<renterHandoverCarInfos.size();i++){
                RenterHandoverCarInfoEntity renterHandoverCarInfo = renterHandoverCarInfos.get(i);
                if(RenterHandoverCarTypeEnum.OWNER_TO_RENTER.getValue().equals(renterHandoverCarInfo.getType())
                        ||  RenterHandoverCarTypeEnum.RENYUN_TO_RENTER.getValue().equals(renterHandoverCarInfo.getType())
                ){
                    mileageAmtDTO.setGetmileage(Objects.isNull(renterHandoverCarInfo.getMileageNum())?0:renterHandoverCarInfo.getMileageNum());
                }

                if(RenterHandoverCarTypeEnum.RENTER_TO_OWNER.getValue().equals(renterHandoverCarInfo.getType())
                        ||  RenterHandoverCarTypeEnum.RENTER_TO_RENYUN.getValue().equals(renterHandoverCarInfo.getType())
                ){
                    mileageAmtDTO.setReturnMileage(Objects.isNull(renterHandoverCarInfo.getMileageNum())?0:renterHandoverCarInfo.getMileageNum());

                }
            }
        }
        return mileageAmtDTO;
    }
    
    
    /**
     *   返回可退还租车费用,租车费用是消费的方式。
     * @param refundApplyVO
     * @return
     */
    public List<CashierRefundApplyReqVO> getCashierRefundApply(RefundApplyVO refundApplyVO) {
        int refundAmt = refundApplyVO.getRefundAmt();
        List<CashierRefundApplyReqVO> cashierRefundApplys = new ArrayList<>();
        //1 租车费用
        //11
        CashierEntity cashierEntity = cashierNoTService.getCashierEntityNoWallet(refundApplyVO.getSettleOrders().getOrderNo(),refundApplyVO.getSettleOrders().getRenterMemNo(), DataPayKindConstant.RENT_AMOUNT);
        if(Objects.nonNull(cashierEntity) && Objects.nonNull(cashierEntity.getId()) && refundAmt<0){
            CashierRefundApplyReqVO vo = new CashierRefundApplyReqVO();
            BeanUtils.copyProperties(cashierEntity,vo);
            vo.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST.getCashNo());
            vo.setRenterCashCodeEnum(refundApplyVO.getRenterCashCodeEnum());
            vo.setPaySource(cashierEntity.getPaySource());
//            vo.setPayType(cashierEntity.getPayType());  
            //固定04 退货 200407
            vo.setPayType(DataPayTypeConstant.PUR_RETURN);
            vo.setRemake(refundApplyVO.getRemarke());
            int amt = refundAmt + cashierEntity.getPayAmt();
            vo.setAmt(amt>=0?refundAmt:-cashierEntity.getPayAmt());
            cashierRefundApplys.add(vo);
            refundAmt = refundAmt + cashierEntity.getPayAmt();
        }
        
        //03
        if(refundAmt<0){
            List<CashierEntity> cashierEntitys = cashierNoTService.getCashierEntitys(refundApplyVO.getSettleOrders().getOrderNo(),refundApplyVO.getSettleOrders().getRenterMemNo(), DataPayKindConstant.RENT_INCREMENT);
            if(!CollectionUtils.isEmpty(cashierEntitys)){
                for(int i=0;i<cashierEntitys.size();i++){
                    if(refundAmt<0){
                        CashierEntity cashierElement = cashierEntitys.get(i);
                        CashierRefundApplyReqVO vo = new CashierRefundApplyReqVO();
                        BeanUtils.copyProperties(cashierElement,vo);
                        vo.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AGAIN.getCashNo());
                        vo.setRenterCashCodeEnum(refundApplyVO.getRenterCashCodeEnum());
                        vo.setPaySource(cashierElement.getPaySource());
                        //固定04 退货 200410
                        vo.setPayType(DataPayTypeConstant.PUR_RETURN);
                        vo.setRemake(refundApplyVO.getRemarke());
                        int amt = refundAmt + cashierElement.getPayAmt();
                        vo.setAmt(amt>=0?refundAmt:-cashierElement.getPayAmt());
                        cashierRefundApplys.add(vo);
                        refundAmt = refundAmt + cashierElement.getPayAmt();
                    }

                }
            }
        }
        
        //12  ADD 200407 
        if(refundAmt<0){
            List<CashierEntity> cashierEntitys = cashierNoTService.getCashierEntitys(refundApplyVO.getSettleOrders().getOrderNo(),refundApplyVO.getSettleOrders().getRenterMemNo(), DataPayKindConstant.RENT_AMOUNT_AFTER);
            if(!CollectionUtils.isEmpty(cashierEntitys)){
                for(int i=0;i<cashierEntitys.size();i++){
                    if(refundAmt<0){
                        CashierEntity cashierElement = cashierEntitys.get(i);
                        CashierRefundApplyReqVO vo = new CashierRefundApplyReqVO();
                        BeanUtils.copyProperties(cashierElement,vo);
                        vo.setFlag(RenterCashCodeEnum.ACCOUNT_RENTER_RENT_COST_AFTER.getCashNo());
                        vo.setRenterCashCodeEnum(refundApplyVO.getRenterCashCodeEnum());
                        vo.setPaySource(cashierElement.getPaySource());
                        //固定04 退货 200410
                        vo.setPayType(DataPayTypeConstant.PUR_RETURN);
                        vo.setRemake(refundApplyVO.getRemarke());
                        int amt = refundAmt + cashierElement.getPayAmt();
                        vo.setAmt(amt>=0?refundAmt:-cashierElement.getPayAmt());
                        cashierRefundApplys.add(vo);
                        refundAmt = refundAmt + cashierElement.getPayAmt();
                    }

                }
            }
        }
        

        return cashierRefundApplys;
    }
    
    
	
}
