package com.atzuche.order.coreapi.service;

import com.atzuche.order.accountrenterwzdepost.entity.AccountRenterWzDepositEntity;
import com.atzuche.order.cashieraccount.service.CashierQueryService;
import com.atzuche.order.commons.AuthorizeEnum;
import com.atzuche.order.commons.LocalDateTimeUtils;
import com.atzuche.order.commons.enums.wz.WzCostEnums;
import com.atzuche.order.commons.exceptions.OrderNotFoundException;
import com.atzuche.order.commons.vo.WzDepositDetailVO;
import com.atzuche.order.commons.vo.res.RenterWzCostVO;
import com.atzuche.order.renterwz.entity.RenterOrderWzCostDetailEntity;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailService;
import com.atzuche.order.renterwz.service.RenterOrderWzCostDetailUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/13 11:59 上午
 **/
@Service
public class WzCostFacadeService {
    @Autowired
    private RenterOrderWzCostDetailService renterOrderWzCostDetailService;

    @Autowired
    private CashierQueryService cashierQueryService;


    /**
     * 获得某个订单的违章费用明细
     * @param orderNo
     * @return
     */
    public RenterWzCostVO getRenterWzCostDetail(String orderNo){
        List<RenterOrderWzCostDetailEntity> list =  renterOrderWzCostDetailService.queryInfosUnitByOrderNo(orderNo);

        RenterWzCostVO renterWzCostVO = new RenterWzCostVO();
        renterWzCostVO.setWzFineAmt(RenterOrderWzCostDetailUtils.getWzCostAmt(list, WzCostEnums.WZ_FINE));
        renterWzCostVO.setWzServiceCostAmt(RenterOrderWzCostDetailUtils.getWzCostAmt(list, WzCostEnums.WZ_SERVICE_COST));
        renterWzCostVO.setWzStopCostAmt(RenterOrderWzCostDetailUtils.getWzCostAmt(list, WzCostEnums.WZ_STOP_COST));
        renterWzCostVO.setWzDysFineAmt(RenterOrderWzCostDetailUtils.getWzCostAmt(list, WzCostEnums.WZ_DYS_FINE));
        renterWzCostVO.setWzOtherAmt(RenterOrderWzCostDetailUtils.getWzCostAmt(list, WzCostEnums.WZ_OTHER_FINE));
        renterWzCostVO.setWzInsuranceAmt(RenterOrderWzCostDetailUtils.getWzCostAmt(list, WzCostEnums.INSURANCE_CLAIM));

        return renterWzCostVO;
    }

    /**
     * 获得订单违章押金的相关信息
     * @param orderNo
     * @return
     */
    public WzDepositDetailVO getWzDepostDetail(String orderNo){
        AccountRenterWzDepositEntity accountRenterWzDepositEntity = cashierQueryService.queryWzDeposit(orderNo);
        if(accountRenterWzDepositEntity==null){
            throw new OrderNotFoundException(orderNo);
        }

        WzDepositDetailVO wzDepositDetailVO = new WzDepositDetailVO();

        BeanUtils.copyProperties(accountRenterWzDepositEntity,wzDepositDetailVO);
        if(accountRenterWzDepositEntity.getSettleTime()!=null){
            wzDepositDetailVO.setSettleTime(LocalDateTimeUtils.localDateTimeToDate(accountRenterWzDepositEntity.getSettleTime()));
        }
        Integer wzIsAuthorize = accountRenterWzDepositEntity.getIsAuthorize();
        int expReturnWzDeposit = 0;
        if(wzIsAuthorize == null){
            expReturnWzDeposit = accountRenterWzDepositEntity.getShishouDeposit()==null?0:accountRenterWzDepositEntity.getShishouDeposit();
        }else if(AuthorizeEnum.IS.getCode() == wzIsAuthorize){
            expReturnWzDeposit =  accountRenterWzDepositEntity.getAuthorizeDepositAmt() == null?0: accountRenterWzDepositEntity.getAuthorizeDepositAmt();
        }else if(AuthorizeEnum.NOT.getCode() == wzIsAuthorize){
            expReturnWzDeposit =  accountRenterWzDepositEntity.getShishouDeposit()==null?0: accountRenterWzDepositEntity.getShishouDeposit();
        }else if(AuthorizeEnum.CREDIT.getCode() == wzIsAuthorize){
            int authorizeDepositAmt = accountRenterWzDepositEntity.getAuthorizeDepositAmt() == null ? 0 : accountRenterWzDepositEntity.getAuthorizeDepositAmt();
            expReturnWzDeposit =  accountRenterWzDepositEntity.getCreditPayAmt()==null?0+authorizeDepositAmt: accountRenterWzDepositEntity.getCreditPayAmt()+authorizeDepositAmt;
        }else{
            expReturnWzDeposit =  accountRenterWzDepositEntity.getShishouDeposit()==null?0: accountRenterWzDepositEntity.getShishouDeposit();
        }
        wzDepositDetailVO.setShishouDeposit(expReturnWzDeposit);
        /*if(accountRenterWzDepositEntity.getIsAuthorize()!=null&&accountRenterWzDepositEntity.getIsAuthorize()!=0){
            wzDepositDetailVO.setShishouDeposit(accountRenterWzDepositEntity.getCreditPayAmt()+accountRenterWzDepositEntity.getShishouDeposit());
        }*/

        wzDepositDetailVO.setShouldReturnDeposit(wzDepositDetailVO.getShishouDeposit());



        return wzDepositDetailVO;

    }
}
