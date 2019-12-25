package mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 胡春林
 *
 */
@Repository
public interface DelegationCarAdminMapper {

    /**
     * 根据车辆号查询代管车管理员手机号码
     * @param carNo
     * @return
     */
    Long getAdminMobileByCarNo(@Param("carNo")int carNo);
}
