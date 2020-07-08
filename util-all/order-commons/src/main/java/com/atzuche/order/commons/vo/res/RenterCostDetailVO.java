package com.atzuche.order.commons.vo.res;

import com.atzuche.order.commons.vo.DepostiDetailVO;
import com.atzuche.order.commons.vo.OrderSupplementDetailVO;
import com.atzuche.order.commons.vo.WzDepositDetailVO;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 租客基础费用，即没有减免过的费用，系统自动计算的费用
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 6:07 下午
 **/
@Data
@ToString
public class RenterCostDetailVO {
    @AutoDocProperty(value = "订单号")
    private String orderNo;
    @AutoDocProperty(value = "子单号")
    private String renterOrderNo;
    @AutoDocProperty(value = "租客会员号")
    private String memNo;
    @AutoDocProperty(value = "租金总额")
    private Integer rentAmt;
    @AutoDocProperty(value = "平台保障费总额")
    private Integer insuranceAmt;
    @AutoDocProperty(value = "补充保障服务费总额")
    private Integer abatementInsuranceAmt;
    @AutoDocProperty(value = "手续费")
    private Integer fee;
    @AutoDocProperty(value = "附加驾驶人保险总额")
    private Integer extraDriverInsuranceAmt;

    @AutoDocProperty(value = "轮胎保障服务费")
    public Integer tyreInsurAmt;

    @AutoDocProperty(value = "驾乘无忧保障服务费")
    public Integer driverInsurAmt;
    
    @AutoDocProperty(value = "精准取车服务费")
    private Integer accurateGetSrvAmt;
    
    @AutoDocProperty(value = "精准还车服务费")
    private Integer accurateReturnSrvAmt;

    @AutoDocProperty(value = "配送费详情及其总额")
    private RenterDeliveryFeeDetailVO deliveryFeeDetail;

    @AutoDocProperty(value = "超里程费用总额")
    private Integer mileageCostAmt;
    @AutoDocProperty(value = "油费总额")
    private Integer oilCostAmt;
    @AutoDocProperty(value = "罚金及其详情")
    private RenterFineVO fineDetail;

    @AutoDocProperty(value = "补贴及其详情")
    private RenterSubsidyDetailVO subsidyDetail;

    @AutoDocProperty(value = "违章费用详情及其总额")
    private RenterWzCostVO wzCostDetail;

    @AutoDocProperty(value = "违章押金")
    private WzDepositDetailVO wzDepositDetailVO;

    @AutoDocProperty(value = "车辆押金")
    private DepostiDetailVO depostiDetailVO;

    @AutoDocProperty(value = "补付列表")
    private List<OrderSupplementDetailVO> supplementDetailVOList;

    @AutoDocProperty(value = "消费总额")
    private int totalAmt;


    public int getTotalAmt(){
        int total= (rentAmt==null?0:rentAmt)
                +(insuranceAmt==null?0:insuranceAmt)
                +(abatementInsuranceAmt==null?0:abatementInsuranceAmt)
                +(fee==null?0:fee)
                +(extraDriverInsuranceAmt==null?0:extraDriverInsuranceAmt)
                +(tyreInsurAmt==null?0:tyreInsurAmt)
                +(driverInsurAmt==null?0:driverInsurAmt)
                +(accurateGetSrvAmt==null?0:accurateGetSrvAmt)
                +(accurateReturnSrvAmt==null?0:accurateReturnSrvAmt);
        if(deliveryFeeDetail!=null){
            total= total+deliveryFeeDetail.getDeliveryTotal();
        }
        if(mileageCostAmt!=null){
            total= total+mileageCostAmt;
        }
        if(oilCostAmt!=null){
            total= total+oilCostAmt;
        }
        if(fineDetail!=null){
            total=total+fineDetail.getTotalFine();
        }
        if(subsidyDetail!=null){
            total=total+subsidyDetail.getTotalSubsidy();
        }
        if(wzCostDetail!=null){
            total=total+wzCostDetail.getTotalWzCostAmt();
        }
        return total;
    }







}


