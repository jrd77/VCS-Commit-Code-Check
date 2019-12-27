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
        String AUTO_TRANS_PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI7uBjKrIcBlBOc6LAimLHhbRZJv+K9LdBg9alFC92eTEieCJ70p2qI8BfbOKs11T2hqyz0hJd13kCPWajZQJR2L8ytitnXFP5cAdZLaZznZ6avUzE1J0abgl5n/cel1hANHebPXhB0s1a81d1zBgcfTDkY2jqop2F+L3WVWqmwTAgMBAAECgYA/AZpl156nS2Df9XrXzTkpTULcDNQZoi9pSBmH/PdHB0Qnem6+KqcggUk3xSaFL8NzijBRoD0q5bv6sFhd3PKe601WSQjmPL9NUNclP0UDGPps7tIkyOphoU7P26lZja5Bc/doC/fd93zSCCQvMJ9q9yP0R5t0sU/8zmSpHuDPgQJBAN2EkW+mzpt8sOx/BddEj/bwpiZw6FFFIXHzL7S4eS9s7BIfipfQD/tgVFnZpPLFAGHV0ReM4L7aEgxwXuyGYvMCQQClLb21v7a1dA0egPrMfrXif5tad6W8owqi7+b7gyTkLPe/yrvRjzUOfDM0UJQN6ZE08EL4exgLn4W1ttxhlNphAkEAi+1PyU1/OSy8vcdHM0H2BrDg42ty9NNMWySYv/m/YzvUq2YNL/SYA9xHrDIVRd03bpWX7N5qoWQy/nSFIbeKDQJABRLTmeqKlSY8ZOx3R6K4uX1L60pzh8jGQdz2jlEn5+5NqdFzVWt9qLU96WR0N70t3Z9nnzRAt9Kti7hTKpgk4QJAcAkr0uDuHo86i8Nk1Nwwi4Q1ATbSeGp8x1rJISFb4xrO3KGGyjkYTfm0I49JxFXwpZYRIgdfhtHqwhDTLzkCPg==";
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
