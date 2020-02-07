package com.atzuche.order.commons;

import com.atzuche.order.commons.enums.SubsidySourceCodeEnum;
import com.atzuche.order.commons.vo.res.rentcosts.RenterOrderSubsidyDetailEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lianglin.sjtu@gmail.com">AndySjtu</a>
 * @date 2020/2/7 4:26 下午
 **/
public class RenterOrderSubsidyDetailUtils {

    /**
     * 从订单补贴中获取所有和指定方（平台、车主）相关补贴
     * @param all
     * @return
     */
    public static List<RenterOrderSubsidyDetailEntity> getSubsidyList(List<RenterOrderSubsidyDetailEntity> all,SubsidySourceCodeEnum sourceCodeEnum){
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
     * 从订单补贴中获取所有和平台相关补贴
     * @param all
     * @return
     */
    public static List<RenterOrderSubsidyDetailEntity> getPlatformSubsidyList(List<RenterOrderSubsidyDetailEntity> all){
        return getSubsidyList(all,SubsidySourceCodeEnum.PLATFORM);
    }

    /**
     * 从订单补贴中获取所有和车主相关补贴
     * @param all
     * @return
     */
    public static List<RenterOrderSubsidyDetailEntity> getCarOwnerSubsidyList(List<RenterOrderSubsidyDetailEntity> all){
        return getSubsidyList(all,SubsidySourceCodeEnum.OWNER);
    }


}
