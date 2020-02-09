package com.atzuche.order.rentercost.utils;

import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.vo.res.rentcosts.OrderConsoleSubsidyDetailEntity;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderSubsidyDetailEntity;

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
            if(detailEntity.getSubsidySourceCode()!=null&& sourceCodeEnum.getCode().equals(detailEntity.getSubsidySourceCode())){
                platformList.add(detailEntity);
            }
            if(detailEntity.getSubsidyTargetCode()!=null&&sourceCodeEnum.getCode().equals(detailEntity.getSubsidyTargetCode())){
                platformList.add(detailEntity);
            }
        }
        return platformList;
    }

    /**
     * 从Console租客订单补贴中获取所有和指定方（平台、车主）相关租客补贴
     * @param all
     * @return
     */
    public static List<OrderConsoleSubsidyDetailEntity> getConsoleRenterSubsidyList(List<OrderConsoleSubsidyDetailEntity> all, SubsidySourceCodeEnum sourceCodeEnum){
        List<OrderConsoleSubsidyDetailEntity> platformList = new ArrayList<>();
        for(OrderConsoleSubsidyDetailEntity detailEntity:all){
            if(detailEntity.getSubsidySourceCode()!=null&& sourceCodeEnum.getCode().equals(detailEntity.getSubsidySourceCode())&&detailEntity.getSubsidyTargetCode()!=null&&
                    SubsidySourceCodeEnum.RENTER.getCode().equals(detailEntity.getSubsidyTargetCode())){
                platformList.add(detailEntity);
            }
            if(detailEntity.getSubsidyTargetCode()!=null&&sourceCodeEnum.getCode().equals(detailEntity.getSubsidyTargetCode())){
                platformList.add(detailEntity);
            }
        }
        return platformList;
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
