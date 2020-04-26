package com.atzuche.order.rentercost.utils;

import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.enums.cashcode.RenterCashCodeEnum;
import com.atzuche.order.commons.vo.res.cost.RenterOrderSubsidyDetailResVO;
import com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity;
import com.atzuche.order.rentercost.entity.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderSubsidyDetailEntity;


import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 4:26 下午
 **/
public class OrderSubsidyDetailUtils {

    /**
     * 从租客订单补贴中获取所有和指定方（平台、车主）相关补贴
     * @param all
     * @return
     */
    public static List<RenterOrderSubsidyDetailEntity> getRenterSubsidyList(List<RenterOrderSubsidyDetailEntity> all, SubsidySourceCodeEnum sourceCodeEnum){
        List<RenterOrderSubsidyDetailEntity> platformList = new ArrayList<>();
        for(RenterOrderSubsidyDetailEntity detailEntity:all){
            if(detailEntity.getSubsidyTargetCode()!=null&&sourceCodeEnum.getCode().equals(detailEntity.getSubsidyTargetCode())){
                platformList.add(detailEntity);
            }
        }
        return platformList;
    }

    public static int getRenterSubsidyAmt(List<RenterOrderSubsidyDetailEntity> all, RenterCashCodeEnum cashCodeEnum){
        int total=0;
        for(RenterOrderSubsidyDetailEntity detailEntity:all){
            if(cashCodeEnum.getCashNo().equals(detailEntity.getSubsidyCostCode())&&detailEntity.getSubsidyAmount()!=null){
                total = total+detailEntity.getSubsidyAmount();
            }
        }
        return total;
    }
    public static int getRenterResvoSubsidyAmt(List<RenterOrderSubsidyDetailResVO> all, RenterCashCodeEnum cashCodeEnum){
        int total=0;
        for(RenterOrderSubsidyDetailResVO detailEntity:all){
            if(cashCodeEnum.getCashNo().equals(detailEntity.getSubsidyCostCode())&&detailEntity.getSubsidyAmount()!=null){
                total = total+detailEntity.getSubsidyAmount();
            }
        }
        return total;
    }
    public static int getConsoleRenterSubsidyAmt(List<OrderConsoleSubsidyDetailEntity> all, RenterCashCodeEnum cashCodeEnum){
        int total=0;
        for(OrderConsoleSubsidyDetailEntity detailEntity:all){
            if(cashCodeEnum.getCashNo().equals(detailEntity.getSubsidyCostCode())&&detailEntity.getSubsidyAmount()!=null){
                total = total+detailEntity.getSubsidyAmount();
            }
        }
        return total;
    }

    /**
     * 租客给平台的费用
     * @param all
     * @param subsidySourceCodeEnum
     * @return
     */
    public static int getRenterSubsidyByCode(List<RenterOrderSubsidyDetailEntity> all,SubsidySourceCodeEnum subsidySourceCodeEnum,SubsidySourceCodeEnum subsidyTargetCodeEnum){
        int total =0;
        for(RenterOrderSubsidyDetailEntity detailEntity:all){
            if(subsidySourceCodeEnum.getCode().equals(detailEntity.getSubsidySourceCode())
                    && subsidyTargetCodeEnum.getCode().equals(detailEntity.getSubsidyTargetCode())
                    &&detailEntity.getSubsidyAmount()!=null){
                total = total+detailEntity.getSubsidyAmount();
            }
        }
        return total;
    }

    /**
     * 无条件的租客给平台的费用
     * @param all
     * @param subsidySourceCodeEnum
     * @return
     */
    public static int getOrderConsoleCostDetail(List<com.atzuche.order.rentercost.entity.OrderConsoleCostDetailEntity> all, SubsidySourceCodeEnum subsidySourceCodeEnum, SubsidySourceCodeEnum subsidyTargetCodeEnum){
        int total =0;
        for(OrderConsoleCostDetailEntity detailEntity:all){
            if(subsidySourceCodeEnum.getCode().equals(detailEntity.getSubsidySourceCode())
                    && subsidyTargetCodeEnum.getCode().equals(detailEntity.getSubsidyTargetCode())
                    &&detailEntity.getSubsidyAmount()!=null){
                total = total+detailEntity.getSubsidyAmount();
            }
        }
        return total;
    }

    /**
     * 获得平台优惠券补贴金额
     * @param all
     * @return
     */
    public static int getRenterPlatCouponSubsidyAmt(List<RenterOrderSubsidyDetailEntity> all){
        return getRenterSubsidyAmt(all,RenterCashCodeEnum.REAL_COUPON_OFFSET);
    }
    /**
     * 获得取还车优惠券补贴金额
     * @param all
     * @return
     */
    public static int getRenterGetCarFeeSubsidyAmt(List<RenterOrderSubsidyDetailEntity> all){
        return getRenterSubsidyAmt(all,RenterCashCodeEnum.GETCARFEE_COUPON_OFFSET);
    }
    /**
     * 获得车主券补贴金额
     * @param all
     * @return
     */
    public static int getRenterOwnerCouponSubsidyAmt(List<RenterOrderSubsidyDetailEntity> all){
        return getRenterSubsidyAmt(all,RenterCashCodeEnum.OWNER_COUPON_OFFSET_COST);
    }
    /**
     * 获得凹凸币补贴金额
     * @param all
     * @return
     */
    public static int getRenterAutoCoinSubsidyAmt(List<RenterOrderSubsidyDetailEntity> all){
        return getRenterSubsidyAmt(all,RenterCashCodeEnum.AUTO_COIN_DEDUCT);
    }

    /**
     * 获得限时立减补贴金额
     * @param all
     * @return
     */
    public static int getRenterRealLimitDeductSubsidyAmt(List<RenterOrderSubsidyDetailEntity> all){
        return getRenterSubsidyAmt(all,RenterCashCodeEnum.REAL_LIMIT_REDUCTI);
    }

    /**
     * 获得升级补贴金额
     * @param all
     * @return
     */
    public static int getRenterUpateSubsidyAmt(List<RenterOrderSubsidyDetailEntity> all){
        return getRenterSubsidyAmt(all,RenterCashCodeEnum.DISPATCHING_AMT);
    }

    public static int getConsoleRenterUpateSubsidyAmt(List<OrderConsoleSubsidyDetailEntity> all){
        return getConsoleRenterSubsidyAmt(all,RenterCashCodeEnum.SUBSIDY_DISPATCHING_AMT);
    }

    /**
     * 从Console租客订单补贴中获取所有和指定方（平台、车主）相关租客补贴
     * @param all
     * @return
     */
    public static List<OrderConsoleSubsidyDetailEntity> getConsoleRenterSubsidyList(List<OrderConsoleSubsidyDetailEntity> all, SubsidySourceCodeEnum sourceCodeEnum){
        List<OrderConsoleSubsidyDetailEntity> platformList = new ArrayList<>();
        for(OrderConsoleSubsidyDetailEntity detailEntity:all){
            if(detailEntity.getSubsidyTargetCode()!=null&&sourceCodeEnum.getCode().equals(detailEntity.getSubsidyTargetCode())){
                platformList.add(detailEntity);
            }
        }
        return platformList;
    }

    /**
     * 返回后台补贴中指定方（租客或者车主）的相关补贴总额
     * @param all
     * @param sourceCodeEnum
     * @param cashCodeEnum
     * @return
     */
    public static int getConsoleSubsidyAmt(List<OrderConsoleSubsidyDetailEntity> all,SubsidySourceCodeEnum sourceCodeEnum,RenterCashCodeEnum cashCodeEnum){
        int total=0;
        for(OrderConsoleSubsidyDetailEntity entity:all){
            if(entity.getSubsidyAmount()!=null&&entity.getSubsidyTargetCode()!=null&&sourceCodeEnum.getCode().equals(entity.getSubsidyTargetCode())&&cashCodeEnum.getCashNo().equals(entity.getSubsidyCostCode())){
                total = total+entity.getSubsidyAmount();
            }
        }
        return total;

    }



    /**
     * 从Console租客订单补贴中获取所有和平台相关补贴
     * @param all
     * @return
     */
    public static List<OrderConsoleSubsidyDetailEntity> getConsolePlatformRenterSubsidyList(List<OrderConsoleSubsidyDetailEntity> all){
        return getConsoleRenterSubsidyList(all,SubsidySourceCodeEnum.PLATFORM);
    }



    /**
     * 从租客订单补贴中获取所有和平台相关的租客补贴
     * @param all
     * @return
     */
    public static List<RenterOrderSubsidyDetailEntity> getPlatformRenterSubsidyList(List<RenterOrderSubsidyDetailEntity> all){
        return getRenterSubsidyList(all,SubsidySourceCodeEnum.PLATFORM);
    }

    /**
     * 从租客订单补贴中获取所有和车主相关的租客补贴
     * @param all
     * @return
     */
    public static List<RenterOrderSubsidyDetailEntity> getCarOwnerRenterSubsidyList(List<RenterOrderSubsidyDetailEntity> all){
        return getRenterSubsidyList(all,SubsidySourceCodeEnum.OWNER);
    }

}
