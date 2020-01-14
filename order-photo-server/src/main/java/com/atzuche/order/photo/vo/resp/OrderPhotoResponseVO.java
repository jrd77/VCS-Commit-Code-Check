package com.atzuche.order.photo.vo.resp;

import com.atzuche.order.photo.dto.OrderPhotoDTO;
import com.atzuche.order.photo.entity.OrderPhotoEntity;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class OrderPhotoResponseVO {

    @AutoDocProperty(value = "取车照片列表")
    private List<OrderPhotoDTO> getCarPhotoList;

    @AutoDocProperty(value = "还车照片列表")
    private List<OrderPhotoDTO> returnCarPhotoList;

    @AutoDocProperty(value = "违章缴纳凭证照片列表")
    private OrderViolationPhotoResponseVO OrderViolationPhotoResponseVO;




}
