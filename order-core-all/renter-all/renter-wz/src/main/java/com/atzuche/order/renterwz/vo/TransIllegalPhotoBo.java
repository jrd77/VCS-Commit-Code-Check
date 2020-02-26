package com.atzuche.order.renterwz.vo;

import java.util.List;

/**
 * TransIllegalPhotoBo
 *
 * @author shisong
 * @date 2019/12/31
 */
public class TransIllegalPhotoBo {

    /**
     * 违章唯一编码
     */
    private String wzcode;
    /**
     * 订单编号
     */
    private String orderno;
    /**
     * 车牌号
     */
    private String carNum;
    /**
     * 违章凭证图片
     */
    private List<PhotoPath> imagePath;

    public String getWzcode() {
        return wzcode;
    }

    public void setWzcode(String wzcode) {
        this.wzcode = wzcode;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public List<PhotoPath> getImagePath() {
        return imagePath;
    }

    public void setImagePath(List<PhotoPath> imagePath) {
        this.imagePath = imagePath;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    @Override
    public String toString() {
        return "TransIllegalPhotoBo{" +
                "wzcode='" + wzcode + '\'' +
                ", orderno='" + orderno + '\'' +
                ", carNum='" + carNum + '\'' +
                ", imagePath=" + imagePath +
                '}';
    }
}
