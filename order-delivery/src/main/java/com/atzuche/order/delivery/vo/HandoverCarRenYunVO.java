package com.atzuche.order.delivery.vo;

import com.atzuche.order.delivery.enums.UserTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author 胡春林
 * 仁云数据结构体(交接车)
 */
@Data
@ToString
public class HandoverCarRenYunVO implements Serializable{

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 用户类型1:租客,2:车主
     */
    private String userType;
    /**
     * 取还车描述
     */
    private String description;

    /**
     * 服务类型（take:取车，back:还车）
     */
    private String serviceType;

    /**
     * 车管家姓名
     */
    private String headName;

    /**
     * 车管家电话
     */
    private String headPhone;

    /**
     * 对应流程系统步骤，第一步传1，以此类推
     */
    private String proId;

    /**
     * 消息ID
     */
    private String messageId;

    public boolean isUserType()
    {
        if(StringUtils.isBlank(userType)) return false;
        return UserTypeEnum.isUserType(Integer.valueOf(userType));
    }

}
