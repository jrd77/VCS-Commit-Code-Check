package com.atzuche.order.coreapi.service;

import com.alibaba.fastjson.JSON;
import com.atzuche.order.commons.DateUtils;
import com.atzuche.order.coreapi.entity.vo.ExceptionEmailServerVo;
import com.atzuche.order.coreapi.service.remote.CarDetailService;
import com.atzuche.order.parentorder.entity.OrderEntity;
import com.atzuche.order.parentorder.service.OrderService;
import com.atzuche.order.rentercommodity.entity.RenterGoodsEntity;
import com.atzuche.order.rentercommodity.service.RenterGoodsService;
import com.atzuche.order.renterorder.entity.RenterOrderEntity;
import com.atzuche.order.renterorder.service.RenterOrderService;
import com.autoyol.car.api.model.dto.OrderCarInfoParamDTO;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@Slf4j
public class DeRunService {

    private Logger logger = LoggerFactory.getLogger(DeRunService.class);

    private static final String ENCODEING = "UTF-8";

    @Resource
    private ExceptionEmailService exceptionEmailService;

    @Resource
    private RenterGoodsService renterGoodsService;

    @Resource
    private RenterOrderService renterOrderService;

    @Resource
    private CarDetailService carDetailService;

    @Resource
    private OrderService orderService;

    @Value("${derun.emails}")
    private String emails;

    @Value("${derun.login}")
    private String login;

    @Value("${derun.password}")
    private String password;

    @Value("${derun.updateLeaseReqUrl}")
    private String updateLeaseReqUrl;

    private static final  String[] CITYS = {"北京市","深圳市","上海市","南京市","杭州市","广州市","海口市","三亚市"};

    private static final  Map<Integer, String> ERROR_CHAR = new HashMap<Integer, String>() {private static final long serialVersionUID = 1L;{
        put(0, "OK");put(10001, "authorization error");put(20001, "object not found");put(20002, "action not allowed");put(20003, "wrong parameters");
        put(20004, "unhandled error");put(20005, "uid/license_plate mismatch");
    }};

    private static final  Map<Integer, String> ERROR_ZHCHAR = new HashMap<Integer, String>() {private static final long serialVersionUID = 1L;{
        put(999999, "系统异常"); put(0, "正常");put(100005, "无此车辆");put(800006, "缺少参数");put(800007, "验签错误");
        put(900000, "操作失败");put(-10004, "自定义错误描述");
    }};

    private static final Integer USE_SPECIAL_PRICE = 1;
    /**
     * 租用状态变更（德润）
     * @param orderNo 订单号
     * @param status 0未租用，1租用中
     */
    public void changeRentStatus(String orderNo,int status){
        if(StringUtils.isBlank(orderNo)){
            log.info("orderNo is empty.");
            return;
        }
        //根据订单号 查询最后一个有效子订单的车牌
        RenterGoodsEntity renterGoodsEntity = renterGoodsService.queryInfoByOrderNo(orderNo);
        String carNum = "";
        Integer carNo = null;
        if(renterGoodsEntity != null){
            carNum = renterGoodsEntity.getCarPlateNum();
            carNo = renterGoodsEntity.getCarNo();
        }
        RenterOrderEntity renterOrder = renterOrderService.getRenterOrderByOrderNoAndIsEffective(orderNo);
        if(renterOrder == null){
            log.info("renterOrder is null.orderNo:[{}]", orderNo);
            return;
        }
        String expRentTime = DateUtils.formate(renterOrder.getExpRentTime(),DateUtils.DATE_DEFAUTE);
        String expRevertTime = DateUtils.formate(renterOrder.getExpRevertTime(),DateUtils.DATE_DEFAUTE);

        OrderEntity orderEntity = orderService.getOrderEntity(orderNo);
        String cityName = "";
        if(orderEntity != null){
            cityName = orderEntity.getCityName();
        }

        OrderCarInfoParamDTO dto =new OrderCarInfoParamDTO();
        dto.setCarAddressIndex(0);
        dto.setRentTime(LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
        dto.setRevertTime(LocalDateTime.now().plusDays(1L).toEpochSecond(ZoneOffset.of("+8")));
        dto.setCarNo(carNo);
        dto.setUseSpecialPrice(USE_SPECIAL_PRICE.equals(renterOrder.getIsUseSpecialPrice()));
        log.info("Query simNo.param is,dto:[{}]", JSON.toJSONString(dto));
        String simNo = carDetailService.querySimNoByCar(dto);
        log.info("Query simNo.result is,simNo:[{}]", simNo);
        this.changeRentStatus(simNo,String.valueOf(status),cityName,orderNo,carNum,renterOrder.getRenterMemNo(), expRentTime,expRevertTime);
    }


    /**
     * 租用状态变更（德润）
     * @param sim      SIM卡号
     * @param status   0未租用，1租用中
     * @param cityName 出租城市名称
     * @param orderNo 订单号
     * @param platNum  车牌号
     *@param renterNo 租客号
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    private void changeRentStatus(String sim, String status, String cityName, String orderNo,String platNum,
                            String renterNo, String startTime, String endTime){
        log.info("Change rent status.param is,sim:[{}],status:[{}],cityName:[{}],orderNo:[{}],platNum:[{}]," +
                "renterNo:[{}],startTime:[{}],endTime:[{}]", sim, status, cityName, orderNo, platNum, renterNo, startTime, endTime);
        ExceptionEmailServerVo email = exceptionEmailService.getEmailServer();
        String[] emails = this.exceptionEmail();
        try {
            Map<String, String> params =new HashMap<>();
            params.put("uid", sim);
            params.put("license_plate", platNum);
            params.put("status", status);
            params.put("login", login);
            params.put("password", password);
            params.put("group", group(cityName));
            params.put("customerId",renterNo);
            params.put("timeStart",startTime);
            params.put("timeEnd",endTime);
            params.put("orderId",orderNo);
            //请求参数
            String reqParam=this.MapToStrUrlEncode(params);
            log.info("reqParam is:[{}]",reqParam);
            String rpStr = this.sendGpsApiHttpsGet(updateLeaseReqUrl, reqParam);
            logger.info("租用状态变更 >> changeRentStatus >>reqUrl >> {}， reqParam >> {} >> res >> {}",updateLeaseReqUrl, reqParam,rpStr);
            if(rpStr!=null && !"".equals(rpStr)){
                Map<String, Object> result = this.parseDataForMap(rpStr);
                Integer resCode = (Integer)result.get("error");
                if (resCode != 0){
                    logger.error("租用状态变更 >> changeRentStatus >> {}", ERROR_CHAR.get(resCode));
                    String message=result.get("message")==null ?ERROR_ZHCHAR.get(resCode):result.get("message").toString();
                    String content=gpsExceptionContentMailContent(resCode,message,orderNo,startTime,endTime,platNum,sim,status);
                    new ExceptionGPSMail(email.getHostName(),email.getFromAddr(),email.getFromName(),email.getFromPwd(),emails).send("GPS->租用状态变更失败",content);
                }

            }
        } catch (Exception ex) {
            logger.error("租用状态变更 >> rentStatusChange >> error", ex);
            new ExceptionGPSMail(email.getHostName(),email.getFromAddr(),email.getFromName(),email.getFromPwd(),emails).send("GPS->租用状态变更异常，异常信息", ex+"【订单号码："+orderNo+",车牌号:"+platNum+",D类sim卡号:"+sim+"】");
        }
    }

    private String[] exceptionEmail() {
        return emails.split(",");
    }

    /**
     * 参数拼接
     * @param reqMap 入参map
     * @return 拼接后的值
     */
    public String MapToStrUrlEncode(Map<String,String> reqMap){
        StringBuilder reqStr= new StringBuilder();
        if(reqMap!=null && !reqMap.isEmpty()){
            for(Map.Entry<String, String> entry:reqMap.entrySet()){
                String key=entry.getKey();
                String value="";
                try {
                    value = URLEncoder.encode(entry.getValue(),ENCODEING);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                reqStr.append(key).append("=").append(value).append("&");
            }
        }
        return reqStr.toString();
    }

    /**
     * 请求德润接口
     * @param reqUrl 请求地址
     * @param reqParam 请求参数
     * @return 德润接口返回值
     */
    public String sendGpsApiHttpsGet(String reqUrl,String reqParam){
        String result ="";
        try {
            HttpClient client = this.createSSLInsecureClient();
            HttpGet get = new HttpGet(reqUrl+"?"+reqParam);
            HttpResponse response = client.execute(get);
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result= EntityUtils.toString(response.getEntity(),"utf-8");
            }
        } catch (Exception e) {
            logger.error("请求德润接口异常："+e.getMessage(),e);
        }
        return result;
    }
    /**
     * 创建https
     * @return CloseableHttpClient
     */
    private CloseableHttpClient createSSLInsecureClient()throws Exception {
        //信任所有
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (chain, authType) -> true).build();
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext, NoopHostnameVerifier.INSTANCE);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory).build();
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        return HttpClients.custom().setConnectionManager(connMgr).build();
    }

    /**
     * 给定城市围栏
     * @param cityStr 城市名称
     * @return 城市名称
     */
    private String group(String cityStr){
        for(String city:CITYS){
            if(city.contains(cityStr)) {
                return city;
            }
        }
        return "";
    }

    public Map<String, Object> parseDataForMap(Object data) {
        Map<String, Object> params = new HashMap<>();
        JSONObject jsonObject = JSONObject.fromObject(data);
        Iterator it = jsonObject.keys();
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            Object value = jsonObject.get(key);
            params.put(key, value);
        }
        return params;
    }


    /**
     * gps异常内容模板(统一模板)
     * @return 邮件内容
     */
    public String gpsExceptionContentMailContent(int resCode,String resDetail,String orderNo,String rentTime,String revertTime,String platNum,String sim,String status){
        return "错误码:" + resCode +
                ",错误定义:" + resDetail +
                "【订单号码：" + orderNo +
                ",订单开始时间：" + rentTime +
                "，订单结束时间：" + revertTime +
                ",车牌号:" + platNum +
                ",D类sim卡号:" + sim +
                "，状态status：" + (status.equals("1") ? "在租" : "返还") + "】";
    }

}
