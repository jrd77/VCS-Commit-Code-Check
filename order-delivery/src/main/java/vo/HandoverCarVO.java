package vo;

import enums.UserTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author 胡春林
 * 仁云数据结构体
 */
@Data
@ToString
public class HandoverCarVO implements Serializable{

    private String orderNo;
    private String userType;
    private String description;
    private String serviceType;
    private String headName;
    private String headPhone;
    private String proId;
    private String messageId;

    public boolean isUserType()
    {
        if(StringUtils.isBlank(userType)) return false;
        return UserTypeEnum.isUserType(Integer.valueOf(userType));
    }

}
