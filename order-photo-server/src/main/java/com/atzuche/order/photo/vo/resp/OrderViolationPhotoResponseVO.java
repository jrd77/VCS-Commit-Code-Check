package com.atzuche.order.photo.vo.resp;

import com.atzuche.order.photo.dto.OrderPhotoDTO;
import com.atzuche.order.photo.entity.OrderPhotoEntity;
import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class OrderViolationPhotoResponseVO {

    @AutoDocProperty(value = "租客上传照片列表")
    private List<OrderPhotoDTO> renterPhotoList;

    @AutoDocProperty(value = "车主上传照片列表")
    private List<OrderPhotoDTO> ownerPhotoList;

    @AutoDocProperty(value = "平台上传照片列表")
    private List<OrderPhotoDTO> platformPhotoList;




}
