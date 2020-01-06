package com.atzuche.order.admin.vo.resp.car;

import com.autoyol.doc.annotation.AutoDocProperty;
import lombok.Data;

import java.util.List;

@Data
public class CarOrderImageResVO implements java.io.Serializable {
    @AutoDocProperty("取车照片")
    private List <CarOrderImageDetailRespVO> rentImages;
    @AutoDocProperty("还车照片")
    private List<CarOrderImageDetailRespVO> revertImages;
    @AutoDocProperty("违章缴纳凭证")
    private List<CarOrderImageDetailRespVO>  depositImages;
    @AutoDocProperty("车主上传")
    private List<CarOrderImageDetailRespVO> memberUploadCarImages;
    @AutoDocProperty("平台上传")
    private List<CarOrderImageDetailRespVO> platformImages;

}
