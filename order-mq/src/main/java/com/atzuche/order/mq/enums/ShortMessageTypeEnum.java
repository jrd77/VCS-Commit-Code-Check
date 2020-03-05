package com.atzuche.order.mq.enums;

/**
 * @author 胡春林
 * 短信类型
 */
public enum ShortMessageTypeEnum {

    /*##################################################################短信SMS信息模版数据（新订单）###################################################################################################################*/
    CANCEL_ORDER_2_RENTER("CancelOrder2Renter", "由于您未在取车时间前支付$PayType$，您对“$CarBrand$”的订单(订单号：$orderNo$)已经自动取消。由于订单已经生效，平台将根据违约金规则进行处理。10100202"),
    CANCLE_ORDER_WARNINGF_OR_FREEDEPOSIT("CancleOrderWarningForFreeDeposit", "请您在30分钟内支付租车押金，否则订单(订单号:$orderNo$)将被系统自动取消。客服热线 10100202"),
    CAR_RENTALEND_2_RENTER("CarRentalEnd2Renter", "您租借的“$CarBrand$”(车牌$CarPlateNum$）行程已结束，感谢您选择凹凸共享租车。请登录手机客户端确认订单费用详情。租车费用将在24小时内在您的租车押金中扣除并返还剩余租车押金。如没有违章，您的违章押金将在15个工作日后进行结算退款。如有疑问请拨打10100202"),
    COST_DETAIL_2_RENTERCPIC("CostDetail2RenterCpic", "您已顺利完成订单(订单号：$orderNo$)，如果没有违章，你的违章押金将在15-20日内返还。欢迎下载使用凹凸租车App（下载链接：dwz.cn/139meh），我们将全力为您提供最好的租车服务。4006080202"),
    DISTRIBUTE_EXEMPT_PREORDER_AUTO_CANCELORDER_2_RENTER("DistributeExemptPreOrderAutoCancelOrder2Renter", "$realName$，由于您未在规定时间内支付租车押金，订单已被系统自动取消"),
    DISTRIBUTE_PAYILLEGAL_DEPOSIT_CANCEL_RENTER("DistributePayIllegalDepositCancelRenter", "由于您未在规定时间内支付违章押金，订单已被取消，系统将按平台规则扣除您的违约金"),
    DISTRIBUTE_PAYRENT_CARILLEGALDEPOSIT_CANCEL_RENTER("DistributePayRentCarIllegalDepositCancelRenter", "由于您未在规定时间内支付两笔押金，订单已被取消。若您想重新下单请联系我们 4006080202"),
    EXEMPT_PREORDER_AUTO_CANCEL_ORDER_2_OWNER("ExemptPreOrderAutoCancelOrder2Owner", "因租客未能完成租车押金支付，您$CarPlateNum$订单已取消"),
    EXEMPT_PREORDER_AUTO_CANCEL_ORDER_2_RENTER("ExemptPreOrderAutoCancelOrder2Renter", "由于您未在规定时间内支付租车押金，您的订单已被取消。点击重新找车  $indexUrl$"),
    EXEMPT_PREORDER_PAYRENT_CAR_DEPOSIT_2_RENTER("ExemptPreorderPayRentCarDeposit2Renter", "您对“$CarBrand$”(车牌$CarPlateNum$)的租车订单(订单号：$orderNo$)租车押金已支付完毕10100202"),
    NOTIFY_RENTER_TRANS_REQACCEPTED("NotifyRenterTransReqAccepted", "车主已接受您对“ $carBrand$$carType$”(车牌$carPlateNum$)的租车订单，请在1小时内支付租车押金，否则订单将被取消。如果订单时间需要提前或延后，请进入APP订单详情点击“修改订单“，车主同意后即可生效。"),
    NOTIFY_RENTER_TRANS_REQACCEPTEDPACKAGE("NotifyRenterTransReqAcceptedPackage", "恭喜您已成功提交订单，请在1小时内支付租车押金，否则订单将被取消。如果订单时间需要提前或延后，请进入订单详情点击“修改订单“，车主同意后即可生效。"),
    NOTIFY_RENTER_TRANS_REQACCEPTEDPACKAGE_SPECIAL_TIME("NotifyRenterTransReqAcceptedPackage_special_time", "恭喜您已成功提交订单，请在1小时内支付租车押金，否则订单将被取消。如果订单时间需要提前或延后，请进入订单详情点击“修改订单“，车主同意后即可生效。租期包含10.1-10.7期间的订单，违章查询周期延长至33天，因违章查询周期延长，违章押金使用预授权、信用减免、绑卡免押的订单，违章押金将在期间转为消费，待订单查询无违章后原路退还。"),
    NOTIFY_RENTER_TRANS_REQACCEPTED_SPECIAL_TIME("NotifyRenterTransReqAccepted_special_time", "车主已接受您对“ $carBrand$$carType$”(车牌$carPlateNum$)的租车订单，请在1小时内支付租车押金，否则订单将被取消。如果订单时间需要提前或延后，请进入APP订单详情点击“修改订单“，车主同意后即可生效。租期包含10.1-10.7期间的订单，违章查询周期延长至33天，因违章查询周期延长，违章押金使用预授权、信用减免、绑卡免押的订单，违章押金将在期间转为消费，待订单查询无违章后原路退还"),
    PAY_ILLEGAL_DEPOSIT_2_OWNERSERVICE("PayIllegalDeposit2OwnerService", "$realName$预订您“$CarBrand$”(车牌$CarPlateNum$)(订单号：$orderNo$)的违章押金支付成功 101002021"),
    PAY_ILLEGAL_DEPOSIT_2_RENTER("PayIllegalDeposit2Renter", "您预订“$CarBrand$”(车牌$CarPlateNum$）(订单号：$orderNo$)违章押金付费成功10100202"),
    PAY_ILLEGAL_DEPOSIT_CANCEL_OWNER("PayIllegalDepositCancelOwner", "因租客未能完成违章押金支付，您$CarPlateNum$的订单已取消，请不要将车辆交于租客"),
    PAY_ILLEGAL_DEPOSIT_CANCEL_OWNERNEWYEAR("PayIllegalDepositCancelOwnerNewYear", "$realName$，由于租客未在取车时间前支付违章押金，您的“$CarBrand$”(车牌$CarPlateNum$)的订单已经自动取消(订单号：$orderNo$，取车时间：$RentTime$)，请不要将车辆交于租客，根据违约金规则（春节订单条款），您将获得最多500元违约金补偿。10100202"),
    PAY_ILLEGAL_DEPOSIT_CANCEL_RENTER("PayIllegalDepositCancelRenter", "由于您未在规定时间内支付违章押金，订单已被取消，系统将按平台规则扣除您的违约金"),
    PAY_ILLEGAL_DEPOSIT_CANCEL_RENTERNEWYEAR("PayIllegalDepositCancelRenterNewYear", "$realName$，由于您未在取车时间前支付违章押金，您对“$CarBrand$”(车牌$CarPlateNum$)的订单已自动取消(订单号：$orderNo$，取车时间：$RentTime$)。根据违约金规则（春节订单条款），您需要支付最多500元违约金。如还需用车请您在App重新下单，如有疑问请致电客服：10100202"),
    PAY_RENT_CAR_DEPOSIT_2_OWNER("PayRentCarDeposit2Owner", "$realName$预订您“$CarBrand$”(车牌$CarPlateNum$)(订单号：$orderNo$)的租车押金支付成功。干净的车况有助于提示租客爱护车辆并获得租客好评。"),
    PAY_RENT_CAR_DEPOSIT_2_RENTER("PayRentCarDeposit2Renter", "你对“$CarBrand$”(车牌$CarPlateNum$)的租车订单(订单号:$orderNo$)租车押金已支付完毕10100202"),
    PAY_RENT_CAR_ILLEGAL_DEPOSIT_CANCEL_OWNER("PayRentCarIllegalDepositCancelOwner", "$realName$，由于租客未在规定时间内支付租车押金及违章押金，您的“$CarBrand$”(车牌$CarPlateNum$)的订单已经自动取消，并且不会产生违约责任(订单号：$orderNo$，取车时间：$RentTime$)。10100202"),
    PAY_RENT_CAR_ILLEGAL_DEPOSIT_CANCEL_RENTER("PayRentCarIllegalDepositCancelRenter", "$realName$，由于您未在规定时间内支付租车押金，您对“$CarBrand$”(车牌$CarPlateNum$)的订单已自动取消(订单号：$orderNo$，取车时间：$RentTime$)。如您是一时疏忽且还需用车，建议联系车主沟通后，在App重新下单，之前的车辆可在已取消订单中找到。客服热线：10100202"),
    REMIND_PAY_ILLEGAL_DEPOSIT_2_RENTER("RemindPayIllegalDeposit2Renter", "您预订的“$CarBrand$”(车牌$CarPlateNum$）尚未支付违章押金，如在取车前仍未完成，订单将默认取消，可能会产生违约金10100202"),
    REMIND_PAY_ILLEGAL_DEPOSITOWNER("RemindPayIllegalDepositOwner", "$realName$，您的“$CarBrand$”(车牌$CarPlateNum$)的一笔订单还未完成违章押金支付(订单号：$orderNo$，取车时间：$RentTime$)。为了您的爱车安全，请不要将车辆交于租客。如果在取车时间前租客仍未完成违章押金支付，订单将按租客违约取消处理。10100202"),
    REMIND_PAY_ILLEGAL_DEPOSITRENTER("CancelOrder2Renter", "您还未支付预定车辆的违章押金，请在$RentTime$前完成支付。否则该订单将被取消，并扣除您的违约费用"),
    REMIND_PAY_ILLEGAL_DEPOSITRENTERSRV("RemindPayIllegalDepositRenterSrv", "$realName$，您还未支付租用“$CarBrand$”(车牌$CarPlateNum$)订单的违章押金(订单号：$orderNo$，取车时间：$RentTime$)。如果在取车时间前$hours$小时未完成违章押金支付，系统将取消订单，并按您违约取消处理。10100202"),
    REMIND_PAY_RENT_CAR_ILLEGAL_DEPOSITOWNER("RemindPayRentCarIllegalDepositOwner", "$realName$，您的“$CarBrand$”(车牌$CarPlateNum$)的一笔订单还未完成租车押金及违章押金支付(订单号：$orderNo$，取车时间：$RentTime$)。为了您的爱车安全，请不要将车辆交于租客。如果在取车时间前租客仍未完成租车押金及违章押金支付，订单将按租客违约取消处理。10100202"),
    REMIND_PAY_RENT_CAR_ILLEGAL_DEPOSITRENTER("RemindPayRentCarIllegalDepositRenter", "$realName$，您还未支付租用“$CarBrand$”(车牌$CarPlateNum$)订单的租车押金及违章押金(订单号：$orderNo$，取车时间：$RentTime$)。为了交易能正常开始，请尽快完成押金支付。如果在取车时间前未完成租车押金及违章押金支付，订单将按您违约取消处理。10100202"),
    SELF_SUPPORT_RENT_DEPOSIT_PAID_NOTICE("SELF_SUPPORT_RENT_DEPOSIT_PAID_NOTICE", "您已成功支付凹凸自营车辆【$plateNum$】的租车押金，如有特殊用车需要请告诉我们 http://1t.click/hej，我们将竭力为您提供更舒适便捷的服务"),
    EXEMPT_PREORDER_REMIND_PAYRENT("ExemptPreOrderRemindPayRent", "您预订的($CarPlateNum$)的订单已被车主接受，取车时间：$RentTime$。请在$Minute$分钟内支付租车押金，否则订单将被系统取消。您可以使用信用卡或者借记卡进行支付，如遇困难可以借用亲友的银行卡。如信用卡额度不够，可致电银行客服提高信用卡临时额度，申请后一般即时生效。"),
    NO_EXEMPT_PREORDER_REMIND_PAYRENT("NoExemptPreOrderRemindPayRent", "您预订的($CarPlateNum$)的订单已被车主接受，取车时间：$RentTime$。请在$Minute$分钟内支付租车押金，否则订单将被系统取消。您可以使用信用卡或者借记卡进行支付，如遇困难可以借用亲友的银行卡。如信用卡额度不够，可致电银行客服提高信用卡临时额度，申请后一般即时生效。"),
    ILLAGE_MAX_SCORE_LIMIT_TEXT("illageMaxScoreLimitText", "尊敬的用户，您在订单时间$time$中产生score分违章，请在租期内尽快消除违章记录，完成后必须将处罚决定书及缴款凭证通过APP“违章查询” $linkUrl1$ 功能中上传，平台将暂扣您的所有押金待订单结束后15-20天再次查询，若无违章退还押金，请点击“违章内容” $linkUrl2$ 查看具体违章信息10100202"),
    ;

    private String value;

    private String name;

    ShortMessageTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取sms消息模版
     * @param value
     * @return
     */
    public static String getSmsTemplate(String value) {
        for (ShortMessageTypeEnum shortMessageTypeEnum : values()) {
            if (shortMessageTypeEnum.getValue().equals(value)) {
                return shortMessageTypeEnum.getName();
            }
        }
        return null;
    }

}
