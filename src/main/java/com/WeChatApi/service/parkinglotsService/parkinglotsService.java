package com.WeChatApi.service.parkinglotsService;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.dto.parkinglotsChargeRecordDto;
import com.WeChatApi.bean.dto.userChargeDiscountDto;
import com.WeChatApi.bean.dto.userChargeRecordDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.redPack;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.CommonUtil;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.invoiceMapper;
import com.WeChatApi.dao.parkinglotsMapper;
import com.WeChatApi.dao.wechatUserMapper;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;



@Service
@Transactional
public class parkinglotsService {
	
	@Autowired
	private parkinglotsMapper parkinglotsmapper;
	
	@Autowired
	private wechatUserMapper wechatUsermapper;
	
	@Autowired
	private static ResourceBundle res = ResourceBundle.getBundle("rccApi");
	
	private static String mchId= res.getString("wechat_mch_id");
	private static String AppSecret= res.getString("wechat_AppSecret");
	private static String Appid= res.getString("wechat_appid");
	private static String mchName= res.getString("wechat_mch_name");

	
	private static Logger log = Logger.getLogger(redPack.class.getName());
	
	@Autowired
	private invoiceMapper invoicemapper;
	
	
	public List<parkinglots> findParkinglotInfo() {
		// TODO Auto-generated method stub
		return parkinglotsmapper.findParkinglotInfo();
	}

	public long findParkinglotInfoCount() {
		// TODO Auto-generated method stub
		return parkinglotsmapper.findParkinglotInfoCount();
	}

	public Map<String, Object> findParkinglotsCharge() {
		Map<String, Object> resultMap =  new HashMap<>();
		List<Map<String, String>> resultList = new ArrayList<>();
		resultList=parkinglotsmapper.findParkinglotsCharge();
		if(resultList.size()==0){
			return resultMap;
		}else{
			List<Map<String, String>> chargeMapList = new ArrayList<>();
			    resultMap.put("c_tilte", String.valueOf(resultList.get(0).get("c_tilte")));
			    resultMap.put("c_parking_code", String.valueOf(resultList.get(0).get("c_parking_code")));
			    resultMap.put("c_remark", String.valueOf(resultList.get(0).get("c_remark")));
				Map<String, String> chargeMap =  new HashMap<>();
				chargeMap.put("c_id", String.valueOf(resultList.get(0).get("c_id")));
				chargeMap.put("c_balances", String.valueOf(resultList.get(0).get("c_balances")));
				chargeMap.put("c_point", String.valueOf(resultList.get(0).get("c_point")));
				chargeMap.put("c_package_status", String.valueOf(resultList.get(0).get("c_package1_status")));
				chargeMap.put("c_package_id", "1");
				chargeMapList.add(chargeMap);
				Map<String, String> chargeMap1 =  new HashMap<>();
				chargeMap1.put("c_id", String.valueOf(resultList.get(0).get("c_id")));
				chargeMap1.put("c_balances", String.valueOf(resultList.get(0).get("c_balances2")));
				chargeMap1.put("c_point", String.valueOf(resultList.get(0).get("c_point2")));
				chargeMap1.put("c_package_status", String.valueOf(resultList.get(0).get("c_package2_status")));
				chargeMap1.put("c_package_id", "2");
				chargeMapList.add(chargeMap1);
				
				Map<String, String> chargeMap2 =  new HashMap<>();
				chargeMap2.put("c_id", String.valueOf(resultList.get(0).get("c_id")));
				chargeMap2.put("c_balances", String.valueOf(resultList.get(0).get("c_balances3")));
				chargeMap2.put("c_point", String.valueOf(resultList.get(0).get("c_point3")));
				chargeMap2.put("c_package_status", String.valueOf(resultList.get(0).get("c_package3_status")));
				chargeMap2.put("c_package_id", "3");
				chargeMapList.add(chargeMap2);
				resultMap.put("chargeMapList", chargeMapList);
				return resultMap;
		}
		
	
	}

	public Map<String, Object> topUpParkingByUser(Map<String, String> map, HttpServletRequest request) throws JsonProcessingException {
		
		
		if(StringUtils.isBlank(map.get("userOpenId").toString())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"小程序openId不能为空！");
		}
		
		if(StringUtils.isBlank(map.get("cr_charge_id").toString())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"充值活动id不能为空！");
		}
		
		if(StringUtils.isBlank(map.get("c_package_id").toString())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"套餐id不能为空！");
		}
		
		
		if(StringUtils.isBlank(map.get("charge_type").toString())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"充值类型不能为空！");
		}
		
		wechatUser user = wechatUsermapper.findWechatUserInfoByOpenId(map.get("userOpenId").toString());
		if(user==null){
			throw new BaseException(
					StatusCode.DATA_NOT_MATCH.getCode(),
					"无该用户信息，请核实！");
		}
		List<Map<String, String>> parkinglotsChargeMap = new ArrayList<Map<String,String>>();
		parkinglotsChargeMap = parkinglotsmapper.findParkinglotsChargeByChargeId(map.get("cr_charge_id").toString());
		if(parkinglotsChargeMap.size()==0){
			throw new BaseException(
					StatusCode.DATA_NOT_MATCH.getCode(),
					"无该停车场优惠活动，请核实！");
		}
		
		parkinglotsChargeRecordDto parkinglotsChargeRecord = new parkinglotsChargeRecordDto();
		parkinglotsChargeRecord.setCr_charge_id(Integer.valueOf(map.get("cr_charge_id").toString()));
		if(map.get("c_package_id").toString().equals("1")){
			parkinglotsChargeRecord.setCr_c_balances(Integer.valueOf(parkinglotsChargeMap.get(0).get("c_balances").toString()));
			parkinglotsChargeRecord.setCr_c_point(Integer.valueOf(parkinglotsChargeMap.get(0).get("c_point").toString()));
		}else if(map.get("c_package_id").toString().equals("2")){
			parkinglotsChargeRecord.setCr_c_balances(Integer.valueOf(parkinglotsChargeMap.get(0).get("c_balances2").toString()));
			parkinglotsChargeRecord.setCr_c_point(Integer.valueOf(parkinglotsChargeMap.get(0).get("c_point2").toString()));
		}else if(map.get("c_package_id").toString().equals("3")){
			parkinglotsChargeRecord.setCr_c_balances(Integer.valueOf(parkinglotsChargeMap.get(0).get("c_balances3").toString()));
			parkinglotsChargeRecord.setCr_c_point(Integer.valueOf(parkinglotsChargeMap.get(0).get("c_point3").toString()));
		}
		
		parkinglotsChargeRecord.setCr_user_id(user.getUserId());
		parkinglotsChargeRecord.setCr_status(0);
		parkinglotsChargeRecord.setUserOpenId(user.getUserOpenId());
		
		if(map.get("charge_type").toString().equals("1")){//for wechat
			parkinglotsChargeRecord.setCharge_type(1);
			Map<String, Object> recordmap = new HashMap<String, Object>();
            Map<String, Object>businessMap=new HashMap<String, Object>();
			parkinglotsmapper.insertParkinglotsChargeRecord(parkinglotsChargeRecord);
			recordmap =this.getChargePrepayId_parkingCharge(parkinglotsChargeRecord,request);
			return recordmap;
		} else if(map.get("charge_type").toString().equals("2")) { // for alipay
			parkinglotsChargeRecord.setCharge_type(2);
			Map<String, Object> recordmap = new HashMap<String, Object>();
			Map<String, Object>businessMap=new HashMap<String, Object>();
			parkinglotsmapper.insertParkinglotsChargeRecord(parkinglotsChargeRecord);
			String aliPaykey =this.getChargePrepayIdByAliPay_parkingCharge(parkinglotsChargeRecord,request);
			/*DecimalFormat decimalFormat=new DecimalFormat(".00");
	        String rechargeAmout=decimalFormat.format(userRecharge.getRealAmout()/100);
	        String disCountAmount=decimalFormat.format(userRecharge.getDiscountAmount()/100);
			businessMap.put("rechargeAmout", Float.parseFloat(rechargeAmout));
			businessMap.put("disCountAmount", Float.parseFloat(disCountAmount));*/
			businessMap.put("alipayReturn", aliPaykey);
			return businessMap;
			
			
		}
		return null;
		
		
		
	}

	private String getChargePrepayIdByAliPay_parkingCharge(parkinglotsChargeRecordDto parkinglotsChargeRecord,
			HttpServletRequest request) throws JsonProcessingException {
		//获得初始化的AlipayClient
				AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","2021002116635517","MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDgrTZKCortL5ye3LVe3XhT/2FNVkeQZuMSKUbjJa8sFVyrASg0qTKpHNfNQ+WNSArFxLXUSJ6jnJnwKy8OBVuz7zgpTYfUbInNuxAussbOGvMwSzSF/ATWXJwPTjNj4atOcIvMwUG+33d9qBKa1V2mZz2y6XnkMKzLAFYPfFVAGM9mc37dN3gWd/wzV3n8D2SUQtOhPDqtcpiK4rHtUNp09b+ZQmCrTIXClhOaqAuAxzLJQ1+O0SOg48/Ta00RSoBRrDjn5ufrwDKpSArVF68WSmgQhlHzh/UO1ODY0a2XIDZD4BwNVlDF5sHpbJmvo53eJkFO9OZzsCdE7ixzQ1FhAgMBAAECggEBAIWEKbkCXuDo5Gg7vMStVC6Gmyo8VbbojZRjVy7xXBIUd8dfnqb8zZBWBaCD8sxsvYodhfirVyrfi1OANixc3swlIvjYjBmejp3lxo4Zy677sP1IE+RzieRhkJQ/4qY+m3C3zTxWtF1bq0TPNo8wsMCMpcvGl5sVmfnhL0NwnbDCcCPfLsRJmmeAVWTZZr6wIcXSMJ2UD5Rxjt3hZDs0H/e1MtPOY+AfUsZpCQg6IMlDovU7ceB8FxjWUTNM5lTyUzwiXY/stSZcQhPdJpDyxz1Tw4ifDku4rC5HGOL8NuisI5u/nDfoRWzEKPEAQh4hmBsGEAApQ4n2bMS5d7qrV6kCgYEA/mOxJA6RAkL9rmuqrngaYMpcG74+WFxCz10IVzV4JVcHqGSv3NhvMgE3m+usXHHRa0h/4usAlVrVJD/s2SampRrfVwf4mjxqL+9OSMW+CaSNWwaT5yBLMR/SPm+wATl1cfqE99Bg17BrHdCcw7sOUdS3E3OKtpMRYl55jG3amCMCgYEA4hlcwUzTuALQKDnfYpEtNnEtJ2Vi8i55ALM4t/LVBM/A+1dtuGExXcLypPOSzuqKr+z+zy/I4YlzG8iDRE0g7hbbb/jroK8unwKZ+HItEMi1YYPgQY2vvpcGsGJ/nkRhLzhc4+ccD+SHSmeNC26zurm9WqU66h1RQMwsoeMTpqsCgYA6ZxzWEFoOUN742lMXNw+nRQ4ceHLLynK0NMGjc+0fdXmWmXcelroAmvLvYw3xM1hG7yAdMqKxp2bjqaFwwUTlBuRbkMLsdg5S7vfkETHt+M9TJJF0MJ/SqWGVYALiZRSnlEJOupJhMmmFMM2syhchhlp5pr8j52PIV7REHzxeTQKBgHWQVH+aUBhFNjFEblgVPtopHR54BGwDKB3qEtrrJEJ9OE44lq+hsgab4IlOsY5vvC9f3tqicSR80OKob0Drjeze67kcSIDjti0uMBtUWwR4KIAND2Vmo/RJGUxL2ynTSmlAOGS0fg3xeTuB/n/sTv4PqFriwXCMT/yObcK7jTNDAoGBAOlrRAljuL67pdrsNDrELhDaULXP2kmuMup5iSmU5QN2i+6M77Hq4gmUuNPrHMNAwFhhCq/FTViWZYh3LgM2/l323ppbXIqsiqLXtbq5Ff+nee50RiwN1sT7KMDv00BBqLOjuDeQeOeD57RXVflgbMdnKaEZKupuFVyOQmKIREZF","json","UTF-8","MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB","RSA2");
		        //设置请求参数
		        AlipayTradeCreateRequest request2 = new AlipayTradeCreateRequest();
		        
		        /** 当前时间 yyyyMMddHHmmss */
		        String currTime = CommonUtil.getCurrTime();
		        /** 8位日期 */
		        //String strTime = currTime.substring(8, currTime.length());
		        /** 四位随机数 */
		        String strRandom = CommonUtil.buildRandom(4) + "";
		        
		        String outTradeNo=currTime+strRandom;
		        
		        JSONObject json=new JSONObject();
		        //订单号
		        json.put("out_trade_no",outTradeNo);
		        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
		        json.put("total_amount",parkinglotsChargeRecord.getCr_c_balances()/100.0f);
		        //描述
		        json.put("subject","用户:"+parkinglotsChargeRecord.getUserOpenId()+"停车场预充值");
		        //用户唯一标识id 这里必须使用buyer_id 参考文档
		        json.put("buyer_id",parkinglotsChargeRecord.getUserOpenId());
		        json.put("passback_params", parkinglotsChargeRecord.getCr_id());
		        //对象转化为json字符串
		        String jsonStr=json.toString();
		        //商户通过该接口进行交易的创建下单
		        request2.setBizContent(jsonStr);
		        //回调地址 是能够访问到的域名加上方法名
		        request2.setNotifyUrl("https://jiashan.iparking.tech/mp/WeChatApi/parklots/aliPayCallback");
		        try {
		        	Map<String, Object>returnMap=new HashMap<String, Object>();
		            AlipayTradeCreateResponse response = alipayClient.execute(request2);
		            if(response.isSuccess()){
		            	log.info(response.getBody());
		            	wechatUsermapper.insertApiLogs("支付宝统一下单接口_停车场预充值",new ObjectMapper().writeValueAsString(json), "success", response.getTradeNo());
		            	return response.getTradeNo();
		            }else{
		            	wechatUsermapper.insertApiLogs("支付宝统一下单接口_停车场预充值",new ObjectMapper().writeValueAsString(json), "fail", response.getSubCode()+"_"+response.getSubMsg());
		            	throw new BaseServiceException(
								StatusCode.API_FREQUENTLY_ERROR.getCode(),response.getSubCode()+"_"
										+ response.getSubMsg()
								);
		            }
		        } catch (AlipayApiException e) {
		            e.printStackTrace();
		        }
		        return null;
	}
	
	
	
	
	

	private Map<String, Object> getChargePrepayId_parkingCharge(parkinglotsChargeRecordDto parkinglotsChargeRecord,
			HttpServletRequest request) {
		
		try{ 
            String openid = parkinglotsChargeRecord.getUserOpenId();

            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            /** 当前时间 yyyyMMddHHmmss */
            String currTime = CommonUtil.getCurrTime();
            /** 8位日期 */
            String strTime = currTime.substring(8, currTime.length());
            /** 四位随机数 */
            String strRandom = CommonUtil.buildRandom(4) + "";
            
            String outTradeNo=currTime+strRandom;

            /** 公众号APPID */
            parameters.put("appid", Appid);
            parameters.put("body", "用户:"+parkinglotsChargeRecord.getUserOpenId()+"停车场优惠充值");
            /** 商户号 */
            parameters.put("mch_id", mchId);
            /** 随机字符串 */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/parklots/wechatPayCallback");
            parameters.put("out_trade_no", outTradeNo);
            parameters.put("openid", openid);
            parameters.put("spbill_create_ip", request.getRemoteAddr());
            parameters.put("total_fee", parkinglotsChargeRecord.getCr_c_balances());
            parameters.put("trade_type", "JSAPI");
            parameters.put("attach", parkinglotsChargeRecord.getCr_id());//停车场优惠记录id
            
            /** MD5进行签名，必须为UTF-8编码，注意上面几个参数名称的大小写 */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
            String requestJsonStr = new ObjectMapper().writeValueAsString(parameters);
            //logger.info("发送的信息是"+requestJsonStr);
            parameters.put("sign", sign);//
            /** 生成xml结构的数据，用于统一下单接口的请求 */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * 读取证书
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,String> result = new HashMap<String,String>();
           try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//这里填你们的证书的地址，我这里放在linux服务器的/usr下面
                //String pathname="D:\\home\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //此处为证书所放的绝对路径
                try {
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
                        .build();
                // Allow TLSv1 protocol only
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        sslcontext,
                        new String[]{"TLSv1"},
                        null,
                        SSLConnectionSocketFactory.getDefaultHostnameVerifier());//SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER
                httpclient = HttpClients.custom()
                        .setSSLSocketFactory(sslsf)
                        .build();
            }
            catch (Exception e){
            	//log.info("读取证书信息的时候发生异常异常信息是："+e.getMessage());
                e.printStackTrace();
            }
            try {
                String requestUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
                HttpPost httpPost = new HttpPost(requestUrl);
                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                // 设置类型
                reqEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(reqEntity);
                //log.info("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // 从request中取得输入流
                        InputStream inputStream = entity.getContent();
                        // 读取输入流
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(inputStream);
                        // 得到xml根元素
                        Element root = document.getRootElement();
                        // 得到根元素的所有子节点
                        List<Element> elementList = root.elements();
                        // 遍历所有子节点
                        for (Element e : elementList)
                        {
                            result.put(e.getName(), e.getText());
                        }
                        // 释放资源
                        inputStream.close();
                    }
                    EntityUtils.consume(entity);
                }
                finally {
                    if(response!=null) {
                        response.close();
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    httpclient.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(result.get("return_code").equals("SUCCESS")) {
            	Map<String, Object> map =CommonUtil.generateSignature(result);
            	wechatUsermapper.insertApiLogs("微信统一下单接口——停车场优惠充值",new ObjectMapper().writeValueAsString(parameters), "success", new ObjectMapper().writeValueAsString(map));
            	map.put("price", parkinglotsChargeRecord.getCr_c_balances());
                return map;
                }
            else {
            	//log.info("获取PrepayId="+openid+",发送时间是："+CommonUtil.getPreDay(new Date(),0));
            	wechatUsermapper.insertApiLogs("微信统一下单接口——停车场优惠充值", new ObjectMapper().writeValueAsString(parameters), "fail", new ObjectMapper().writeValueAsString(result));
            	throw new BaseException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"停车场优惠充值接口获取失败，请重新联系管理员！");
            }
        }
        catch (Exception e){
        	throw new BaseException(
					StatusCode.SYSTEM_ERROR.getCode(),
					"停车场优惠充值接口获取失败，请重新联系管理员！");
        }
	}

	public List<Map<String, String>> findParkinglotsChargeRecord(Map<String, String> map) {
		if(StringUtils.isBlank(map.get("userOpenId").toString())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"小程序openId不能为空！");
		}
		
		if(StringUtils.isBlank(map.get("fpUrlStatus").toString())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"开票类型不能为空！");
		}
		wechatUser user = wechatUsermapper.findWechatUserInfoByOpenId(map.get("userOpenId").toString());
		
		return parkinglotsmapper.findParkinglotsChargeRecord(user.getUserId().toString(),map.get("fpUrlStatus").toString());
	}

	/**
     * 发起开票
     * @param invoiceDto
     * @throws IOException 
     */
	public void doInvoice(doInvoiceDto invoiceDto) throws IOException {
		String userOpenId = invoiceDto.getUserOpenId();
		
		if(StringUtils.isBlank(invoiceDto.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"用户userOpenId不能为空！");
		}
		invoice invoice2 = new  invoice();
		invoice2.setUserOpenId(userOpenId);
		invoice2.setCompanyName(invoiceDto.getCompanyName());
		invoice2.setTaxNumber(invoiceDto.getTaxNumber());
		invoice2.setBankNumber(invoiceDto.getBankNumber());
		invoice2.setEmail(invoiceDto.getEmail());
		invoice2.setAddressMobile(invoiceDto.getAddressMobile());
		invoice invoice = invoicemapper.findInvoiceInfoByTaxNumber(invoiceDto.getTaxNumber(),invoiceDto.getUserOpenId());
		if(invoice==null){
			/*if(StringUtils.isBlank(invoiceDto.getBankNumber())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"开户行账号不能为空！");
			}*/
			
			if(StringUtils.isBlank(invoiceDto.getCompanyName())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"发票抬头不能为空！");
			}
			/*if(StringUtils.isBlank(invoiceDto.getTaxNumber())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"发票税号不能为空！");
			}*/
			if(StringUtils.isBlank(invoiceDto.getEmail())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"邮箱地址不能为空！");
			}
			/*if(StringUtils.isBlank(invoiceDto.getAddressMobile())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"联系地址电话不能为空！");
			}*/
			
			
			List<String> outTradeNoList = new ArrayList<>();
			for(String outTradeNo : invoiceDto.getOutTradeNos().toString().split(",")){
				/*parkinglotsPay parkinglots=invoicemapper.findParkingPayNoInvoiceInfoByOutTradeNo(outTradeNo);
				if(parkinglots!=null){
					bluecardservice.doInvoice(parkinglots,invoice2);
				}*/
				outTradeNoList.add(outTradeNo);
			}
			if(outTradeNoList.size()>0){
				Map<String, Object> sumPriceMap = parkinglotsmapper.getSumPriceByOutTradeNoList(outTradeNoList);
				//String vehiclePates=invoicemapper.getVehiclesByOutTradeNoList(outTradeNoList);
				this.doInvoiceSumPrice(outTradeNoList,invoice2,sumPriceMap);
			}
			
			
			invoicemapper.addInvoiceInfo(invoice2);
		}else{
			List<String> outTradeNoList = new ArrayList<>();
			for(String outTradeNo : invoiceDto.getOutTradeNos().toString().split(",")){
				/*parkinglotsPay parkinglots=invoicemapper.findParkingPayNoInvoiceInfoByOutTradeNo(outTradeNo);
				if(parkinglots!=null){
					bluecardservice.doInvoice(parkinglots,invoice);
				}*/
				outTradeNoList.add(outTradeNo);
			}
			if(outTradeNoList.size()>0){
				Map<String, Object> sumPriceMap = parkinglotsmapper.getSumPriceByOutTradeNoList(outTradeNoList);
				this.doInvoiceSumPrice(outTradeNoList,invoice2,sumPriceMap);
			}
			invoice2.setId(invoice.getId());
			invoicemapper.updateInvoiceInfo(invoice2);
		}
		
		
		
	}

	private void doInvoiceSumPrice(List<String> outTradeNoList, invoice invoice2, Map<String, Object> sumPriceMap) throws HttpException, IOException {
		PostMethod postMethod = null;
		try{
		// 设置上传文件目录
	    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
	    String doInvoiceUrl = res.getString("doInvoice_url");
    	
	    postMethod = new PostMethod(doInvoiceUrl) ;

        NameValuePair[] Senddata = {
	            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
	            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
	            new NameValuePair("invoiceAmount",sumPriceMap.get("price")+""),
	            new NameValuePair("companyName",invoice2.getCompanyName()),
	            new NameValuePair("taxNumber",invoice2.getTaxNumber()),
	            new NameValuePair("addressAndMobile",invoice2.getAddressMobile()),
	            new NameValuePair("bankAndNumber",invoice2.getBankNumber()),
	            new NameValuePair("email",invoice2.getEmail()),
	            new NameValuePair("remark",sumPriceMap.get("vehPlates")+"")
	            
	    };
        
        
        postMethod.setRequestBody(Senddata);
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

       
        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
	    int response = client.executeMethod(postMethod); // POST
	    
	    if(response==200){
	    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
            String errorCode = dataJson.getString("error_code");
            if(errorCode.equals("0")){
            String data = dataJson.getString("data");
            JSONObject  dataJsonR = JSONObject.fromObject(data);
                
            	log.info("订单号："+outTradeNoList.toString()+"开票成功");
            	wechatUsermapper.insertApiLogs("停车场充值优惠开票接口", "订单号:"+outTradeNoList.toString()+";税号:"+invoice2.getTaxNumber()+";开票金额:"+sumPriceMap, "success", "发票地址:"+dataJsonR.getString("pdfUrl"));
            	parkinglotsPay pay=new parkinglotsPay();
            	pay.setOut_trade_noList(outTradeNoList);
            	pay.setInvoice_pdf(dataJsonR.getString("pdfUrl"));
            	pay.setInvoice_status(1);
            	pay.setInvoice_company_name(invoice2.getCompanyName());
            	pay.setInvoice_tax_number(invoice2.getTaxNumber());
            	pay.setInvoice_bank_number(invoice2.getBankNumber());
            	pay.setInvoice_address_mobile(invoice2.getAddressMobile());
            	pay.setInvoice_email(invoice2.getEmail());
            	invoicemapper.updateParkingPayInfo_parking_charge_record(pay);

            }else{
            	log.info("开票号："+outTradeNoList.toString()+"开票失败");
            	/*parkinglotsPay pay=new parkinglotsPay();
            	pay.setOut_trade_noList(outTradeNoList);
            	//pay.setInvoice_pdf(dataJsonR.getString("pdfUrl"));
            	//pay.setInvoice_status(1);
            	pay.setInvoice_company_name(invoice2.getCompanyName());
            	pay.setInvoice_tax_number(invoice2.getTaxNumber());
            	pay.setInvoice_bank_number(invoice2.getBankNumber());
            	pay.setInvoice_address_mobile(invoice2.getAddressMobile());
            	pay.setInvoice_email(invoice2.getEmail());
            	invoicemapper.updateParkingPayInfo(pay);*/
            	wechatUsermapper.insertApiLogs("停车场充值优惠开票接口", "订单号:"+outTradeNoList.toString()+";税号:"+invoice2.getTaxNumber()+";开票金额:"+sumPriceMap, "fail", dataJson.getString("error_msg"));
            	throw new BaseServiceException(
            			Integer.parseInt(errorCode),
            			dataJson.getString("error_msg"));
            }
           
            
	    }else{
	    	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					"请查看停车场充值优惠开票接口");
	    }
		}finally {
			postMethod.releaseConnection();
		}
		
	}


	
}
