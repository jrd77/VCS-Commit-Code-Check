package com.atzuche.order.delivery.vo.delivery;

import com.atzuche.order.delivery.entity.OwnerHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.OwnerHandoverCarRemarkEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarInfoEntity;
import com.atzuche.order.delivery.entity.RenterHandoverCarRemarkEntity;
import lombok.*;

import java.util.List;

/**
 * @author 胡春林
 * 流程相关数据
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HandoverProVO {

    private List<RenterHandoverCarRemarkEntity> renterHandoverCarInfoEntities;
    private List<OwnerHandoverCarRemarkEntity> ownerHandoverCarInfoEntities;
}
