package com.atzuche.order.delivery.vo.delivery;

import com.atzuche.order.commons.entity.dto.RenterMemberDTO;
import com.atzuche.order.delivery.entity.RenterDeliveryAddrEntity;
import com.atzuche.order.delivery.enums.UsedDeliveryTypeEnum;
import com.atzuche.order.delivery.vo.delivery.convert.Converter;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author 胡春林
 * 配送地址信息
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenterDeliveryAddrDTO {

    /**
     * 主订单号
     */
    private String orderNo;
    /**
     * 子订单号
     */
    private String renterOrderNo;
    /**
     * 预计取车地址
     */
    private String expGetCarAddr;
    /**
     * 预计还车地址
     */
    private String expReturnCarAddr;
    /**
     * 预计取车经度
     */
    private String expGetCarLon;
    /**
     * 预计取车维度
     */
    private String expGetCarLat;
    /**
     * 预计还车经度
     */
    private String expReturnCarLon;
    /**
     * 预计还车纬度
     */
    private String expReturnCarLat;
    /**
     * 实际取车地址
     */
    private String actGetCarAddr;
    /**
     * 实际还车地址
     */
    private String actReturnCarAddr;
    /**
     * 实际取车经度
     */
    private String actGetCarLon;
    /**
     * 实际取车维度
     */
    private String actGetCarLat;
    /**
     * 实际还车经度
     */
    private String actReturnCarLon;
    /**
     * 实际还车纬度
     */
    private String actReturnCarLat;
    /**
     * 取车人姓名
     */
    private String getCarUserName;
    /**
     * 取车人电话
     */
    private String getCarUserPhone;
    /**
     * 还车人姓名
     */
    private String returnCarUserName;
    /**
     * 还车人电话
     */
    private String returnCarUserPhone;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 创建人
     */
    private String createOp;

    /**
     * 0-正常，1-已逻辑删除
     */
    private Integer isDelete;


    /**
     * 相互转换
     */
    private static class RenterDeliveryAddrDTOConvert extends Converter<RenterDeliveryAddrDTO, RenterDeliveryAddrEntity> {

        @Override
        protected RenterDeliveryAddrEntity doForWard(RenterDeliveryAddrDTO renterDeliveryAddrDTO) {
            RenterDeliveryAddrEntity renterDeliveryAddrEntity = new RenterDeliveryAddrEntity();
            BeanUtils.copyProperties(renterDeliveryAddrDTO, renterDeliveryAddrEntity);
            return null;
        }

        @Override
        protected RenterDeliveryAddrDTO doBackWard(RenterDeliveryAddrEntity renterDeliveryAddrEntity) {
            RenterDeliveryAddrDTO renterDeliveryAddrDTO = RenterDeliveryAddrDTO.builder().build();
            BeanUtils.copyProperties(renterDeliveryAddrEntity, renterDeliveryAddrDTO);
            return renterDeliveryAddrDTO;
        }

        @Override
        public RenterDeliveryAddrEntity apply(RenterDeliveryAddrDTO renterDeliveryAddrDTO) {
            return null;
        }
    }

    /**
     * 设置参数
     * @param orderType
     * @param renterMemberDTO
     */
    public void setParamsTypeValue(Integer orderType, RenterMemberDTO renterMemberDTO) {
        if (orderType == UsedDeliveryTypeEnum.USED.getValue().intValue()) {
            setGetCarUserName(renterMemberDTO.getRealName());
            setGetCarUserPhone(renterMemberDTO.getPhone());
        } else {
            setReturnCarUserName(renterMemberDTO.getRealName());
            setReturnCarUserPhone(renterMemberDTO.getPhone());
        }
    }

    /**
     * 转换成DTO
     * @param renterDeliveryAddrEntity
     * @return
     */
    public RenterDeliveryAddrDTO convertToDTO(RenterDeliveryAddrEntity renterDeliveryAddrEntity) {

        RenterDeliveryAddrDTOConvert renterDeliveryAddrDTOConvert = new RenterDeliveryAddrDTOConvert();
        return renterDeliveryAddrDTOConvert.doBackWard(renterDeliveryAddrEntity);
    }

    /**
     * 转换成entity
     * @return
     */
    public RenterDeliveryAddrEntity convertToEntity() {

        RenterDeliveryAddrDTOConvert renterDeliveryAddrDTOConvert = new RenterDeliveryAddrDTOConvert();
        return renterDeliveryAddrDTOConvert.doForWard(this);
    }


}
