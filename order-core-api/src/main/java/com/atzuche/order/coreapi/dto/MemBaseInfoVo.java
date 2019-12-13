/**
  * Copyright 2019 bejson.com 
  */
package com.atzuche.order.coreapi.dto;

import lombok.Data;

import java.util.Date;


@Data
public class MemBaseInfoVo {
    private Integer memNo;
    private String token;
    private String nickName;
    private String firstName;
    private String realName;
    private Integer gender;
    private String mobile;
    private String userBasePath;
    private String portrait;
    private String email;
    private String emailAuth;
    private Integer ownerResTimes;
    private Long ownerResTotalTime;
    private Long ownerResAvgTime;
    private Integer rentTimes;
    private Long renterResTotalTime;
    private Long renterResAvgTime;
    private Integer id_card_auth;
    private Integer id_card_back_auth;
    private Integer dri_lic_auth;
    private Integer driViceLicAuth;
    private Integer day_req_count;
    private Integer day_cancel_req_count;
    private Integer rent_flag;
    private Integer renterRating;    //作为租客时的信用评级
    private Date driLicFirstTime;
    private String regtime;
    private Integer repeatTimeOrder;  //是否可下重复订单:0否  1是
    private Integer internalStaff;  //内部员工:0否、1是
    //准驾车型
    private String driLicAllowCar;
    //是否为后台更新
    private Integer ratingUpdateFlag;//后台更新会员等级：0：否，1：是
    private Integer renterWrongdoer;
    private Integer wrongdoerUpdateFlag;
    private Integer memberFlag;//会员等级
    private String inviterCode;//邀请人邀请码
    private String inviteCode;//邀请码
    private Date regTime;//注册时间
    private String idNo;  //身份证号
    private Integer practiceFlag;
    private String crmCustId;//crm客户id
    private String phone;
    private String addr;
    private String creditCardNo;
    private String birthday;
    private Integer buyTimes;//成功租用车辆次数
    private Integer sellTimes;//成功租用车辆次数(作为车主)
    private Integer isShelvesGiveAutocoin;
    private String idCard;//身份证正面图片路径
    private String plateNum;//车牌号
    private Integer ownerTypeEx;//车辆类型
    
}