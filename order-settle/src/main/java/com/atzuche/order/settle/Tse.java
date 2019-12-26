package com.atzuche.order.settle;

import com.autoyol.autopay.gateway.constant.DataAppIdConstant;
import com.autoyol.autopay.gateway.constant.PublicKeySignConstants;
import com.autoyol.autopay.gateway.util.AESSecurityUtils;
import com.autoyol.autopay.gateway.vo.req.PayVo;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.autoyol.autopay.gateway.util.RSASecurityUtils.privateKeySignature;
import static com.autoyol.autopay.gateway.util.RSASecurityUtils.publicKeyVerifySignature;

public class Tse {
    public static void main(String[] args) throws Exception {
        //模拟支付
        PayVo vo = new PayVo();
        vo.setAtappId(DataAppIdConstant.APPID_SHORTRENT);
        vo.setPayAmt("1000");
        vo.setPayId("123456789100");
        vo.setInternalNo("100");
        vo.setAtpaySign("");  //首先設置為空

        //每次是一样的。
        //reqContent={"atappId":"20","payId":"123456789100","payKind":null,"payType":null,"paySource":null,"payEnv":null,"reqIp":null,"internalNo":null,"payAmt":"1000","payAmtSign":null,"payTitle":null,"openId":null,"os":null}
        //reqContent={"atappId":"20","payId":"123456789100","payKind":null,"payType":null,"paySource":null,"payEnv":null,"reqIp":null,"internalNo":"100","payAmt":"1000","payAmtSign":null,"payTitle":null,"openId":null,"os":null}

        ObjectMapper om = new ObjectMapper();
        String reqContent = om.writeValueAsString(vo);
        System.out.println("reqContent=" + reqContent);

//
//		//私钥加密
        String AUTO_TRANS_PRIVATE_KEY = "xxx";
        String atpaySign =  privateKeySignature(AUTO_TRANS_PRIVATE_KEY, reqContent);
        //单独传递
        System.out.println("签名串atpaysign="+atpaySign);

        vo.setAtpaySign(atpaySign);
        String reqContent2 = om.writeValueAsString(vo);
        System.out.println("reqContent2=" + reqContent2);

        String aeskey = "yyy";
        String mi = AESSecurityUtils.encrypt(aeskey, reqContent2);
        System.out.println("mi="+mi);


        // ***************************************************************************
        //支付平臺收到之後解密
        String jiemi = AESSecurityUtils.decrypt(aeskey, mi);
        System.out.println("jiemi="+jiemi);
        PayVo pv = om.readValue(jiemi, PayVo.class);
        String sign = pv.getAtpaySign();
        pv.setAtpaySign("");
        String reqContent3 = om.writeValueAsString(pv);
        System.out.println("reqContent3=" + reqContent3);
        boolean f = publicKeyVerifySignature(PublicKeySignConstants.AUTO_TRANS_PUBLIC_KEY, sign, reqContent3);
        System.out.println("验证是否通过="+f);

    }
}
