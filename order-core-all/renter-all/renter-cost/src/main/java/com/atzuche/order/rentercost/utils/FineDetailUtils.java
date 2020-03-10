package com.atzuche.order.rentercost.utils;

import com.atzuche.order.commons.enums.FineTypeEnum;
import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.rentercost.entity.ConsoleRenterOrderFineDeatailEntity;
import com.atzuche.order.rentercost.entity.RenterOrderFineDeatailEntity;

import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/9 2:37 下午
 **/
public class FineDetailUtils {
    private FineDetailUtils(){}

    /**
     * 获得后台操作的罚金列表中指定罚金总额
     * @param entityList
     * @param fineTypeEnum
     * @return
     */
    public static int getRenterConsoleFineAmt(List<ConsoleRenterOrderFineDeatailEntity> entityList, FineTypeEnum fineTypeEnum){
        int total =0;
        for(ConsoleRenterOrderFineDeatailEntity entity:entityList){
            if(fineTypeEnum.getFineType().equals(entity.getFineType())&&entity.getFineAmount()!=null){
                if(entity.getFineSubsidySourceCode().equals(SubsidySourceCodeEnum.RENTER.getCode())) {
                    total = total + entity.getFineAmount();
                }
            }
        }
        return total;
    }

    /**
     * 获得修改订单取车违约金的总额
     * @param entityList
     * @return
     */
    public static int getRenterConsoleModifyGetFineAmt(List<ConsoleRenterOrderFineDeatailEntity> entityList){
        return getRenterConsoleFineAmt(entityList,FineTypeEnum.MODIFY_GET_FINE);
    }

    /**
     * 获得修改订单还车违约金的总额
     * @param entityList
     * @return
     */
    public static int getRenterConsoleModifyReturnFineAmt(List<ConsoleRenterOrderFineDeatailEntity> entityList){
        return getRenterConsoleFineAmt(entityList,FineTypeEnum.MODIFY_RETURN_FINE);
    }

    /**
     * 获得修改订单提前还车违约金的总额
     * @param entityList
     * @return
     */
    public static int getRenterConsoleModifyAdvanceFineAmt(List<ConsoleRenterOrderFineDeatailEntity> entityList){
        return getRenterConsoleFineAmt(entityList,FineTypeEnum.MODIFY_ADVANCE);
    }

    /**
     * 获得取消订单违约金的总额
     * @param entityList
     * @return
     */
    public static int getRenterConsoleCancelFineAmt(List<ConsoleRenterOrderFineDeatailEntity> entityList){
        return getRenterConsoleFineAmt(entityList,FineTypeEnum.CANCEL_FINE);
    }

    /**
     * 获得延迟还车违约金的总额
     * @param entityList
     * @return
     */
    public static int getRenterConsoleDelayFineAmt(List<ConsoleRenterOrderFineDeatailEntity> entityList){
        return getRenterConsoleFineAmt(entityList,FineTypeEnum.DELAY_FINE);
    }

    /**
     * 获得租客提前还车罚金的总额
     * @param entityList
     * @return
     */
    public static int getRenterConsoleAdvanceReturnFineAmt(List<ConsoleRenterOrderFineDeatailEntity> entityList){
        return getRenterConsoleFineAmt(entityList,FineTypeEnum.RENTER_ADVANCE_RETURN);
    }


    /**
     * 获得租客延后还车罚金的总额
     * @param entityList
     * @return
     */
    public static int getRenterConsoleDelayReturnFineAmt(List<ConsoleRenterOrderFineDeatailEntity> entityList){
        return getRenterConsoleFineAmt(entityList,FineTypeEnum.RENTER_DELAY_RETURN);
    }

    /**
     * 获得取还车违约金罚金的总额
     * @param entityList
     * @return
     */
    public static int getRenterConsoleGetReturnCarFineAmt(List<ConsoleRenterOrderFineDeatailEntity> entityList){
        return getRenterConsoleFineAmt(entityList,FineTypeEnum.GET_RETURN_CAR);
    }




    /**
     * 获取订单相关的罚金列表中指定的罚金类型总额
     * @param entityList
     * @param fineTypeEnum
     * @return
     */
    public static int getRenterOrderFineAmt(List<RenterOrderFineDeatailEntity> entityList,FineTypeEnum fineTypeEnum){
        int total =0;
        for(RenterOrderFineDeatailEntity entity:entityList){
            if(fineTypeEnum.getFineType().equals(entity.getFineType())&&entity.getFineAmount()!=null){
                if(entity.getFineSubsidySourceCode().equals(SubsidySourceCodeEnum.RENTER.getCode())) {
                    total = total + entity.getFineAmount();
                }
            }
        }
        return total;
    }


    /**
     * 获得修改订单取车违约金的总额
     * @param entityList
     * @return
     */
    public static int getRenterOrderModifyGetFineAmt(List<RenterOrderFineDeatailEntity> entityList){
        return getRenterOrderFineAmt(entityList,FineTypeEnum.MODIFY_GET_FINE);
    }

    /**
     * 获得修改订单还车违约金的总额
     * @param entityList
     * @return
     */
    public static int getRenterOrderModifyReturnFineAmt(List<RenterOrderFineDeatailEntity> entityList){
        return getRenterOrderFineAmt(entityList,FineTypeEnum.MODIFY_RETURN_FINE);
    }

    /**
     * 获得修改订单提前还车违约金的总额
     * @param entityList
     * @return
     */
    public static int getRenterOrderModifyAdvanceFineAmt(List<RenterOrderFineDeatailEntity> entityList){
        return getRenterOrderFineAmt(entityList,FineTypeEnum.MODIFY_ADVANCE);
    }

    /**
     * 获得取消订单违约金的总额
     * @param entityList
     * @return
     */
    public static int getRenterOrderCancelFineAmt(List<RenterOrderFineDeatailEntity> entityList){
        return getRenterOrderFineAmt(entityList,FineTypeEnum.CANCEL_FINE);
    }

    /**
     * 获得延迟还车违约金的总额
     * @param entityList
     * @return
     */
    public static int getRenterOrderDelayFineAmt(List<RenterOrderFineDeatailEntity> entityList){
        return getRenterOrderFineAmt(entityList,FineTypeEnum.DELAY_FINE);
    }

    /**
     * 获得租客提前还车罚金的总额
     * @param entityList
     * @return
     */
    public static int getRenterOrderAdvanceReturnFineAmt(List<RenterOrderFineDeatailEntity> entityList){
        return getRenterOrderFineAmt(entityList,FineTypeEnum.RENTER_ADVANCE_RETURN);
    }


    /**
     * 获得租客延后还车罚金的总额
     * @param entityList
     * @return
     */
    public static int getRenterOrderDelayReturnFineAmt(List<RenterOrderFineDeatailEntity> entityList){
        return getRenterOrderFineAmt(entityList,FineTypeEnum.RENTER_DELAY_RETURN);
    }

    /**
     * 获得取还车违约金罚金的总额
     * @param entityList
     * @return
     */
    public static int getRenterOrderGetReturnCarFineAmt(List<RenterOrderFineDeatailEntity> entityList){
        return getRenterOrderFineAmt(entityList,FineTypeEnum.GET_RETURN_CAR);
    }

}
