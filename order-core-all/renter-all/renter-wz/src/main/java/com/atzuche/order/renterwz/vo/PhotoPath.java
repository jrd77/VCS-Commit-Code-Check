package com.atzuche.order.renterwz.vo;

/**
 * PhotoPath
 *
 * @author shisong
 * @date 2019/12/31
 */
public class PhotoPath {

    /**
     * 用户类型: 1-租客，2-车主，3-平台
     */
    private String userType;
    /**
     * 图片地址
     */
    private String img;
    /**
     * 车牌号
     */
    private String carNum;
    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    @Override
    public String toString() {
        return "PhotoPath{" +
                "userType='" + userType + '\'' +
                ", img='" + img + '\'' +
                ", carNum='" + carNum + '\'' +
                '}';
    }
}
