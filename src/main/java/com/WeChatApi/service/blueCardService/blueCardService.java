package com.WeChatApi.service.blueCardService;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.noVehiclePlate;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.dto.couponDto;
import com.WeChatApi.bean.dto.meetingCodeDto;
import com.WeChatApi.bean.dto.prePayDto;
import com.WeChatApi.bean.dto.prePayDto2;
import com.WeChatApi.bean.dto.storeCouponRecordDto;
import com.WeChatApi.bean.models.redPack;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.invoiceMapper;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.WeChatApi.service.zfbApiService.zfbApiService;

import com.alipay.api.response.AlipayTradeCreateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;





@Service
@Transactional
public class blueCardService {
	
	private static Logger log = Logger.getLogger(noVehiclePlate.class.getName());
	
	
	@Autowired
	private wechatUserMapper wechatUsermapper;
	
	@Autowired
	private wechatApiService wechatApiservice;
	
	@Autowired
	private invoiceMapper invoicemapper;
	
	
	@Autowired
	private zfbApiService zfbapiservice;

	public void UnlicensedCarIn(noVehiclePlate noVehiclePlate) throws HttpException, IOException {
		
		PostMethod postMethod = null;
		try{
		// �����ϴ��ļ�Ŀ¼
	    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
	    String unlicensedCarInUrl = res.getString("UnlicensedCarIn_url");
    	
	    postMethod = new PostMethod(unlicensedCarInUrl) ;

        NameValuePair[] Senddata = {
	            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
	            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
	            new NameValuePair("parkNumber",noVehiclePlate.getParkingCode()),
	            new NameValuePair("passagewayId",noVehiclePlate.getChannelCode()+""),
	            new NameValuePair("authId",noVehiclePlate.getUserOpenId())
	            
	    };
        postMethod.setRequestBody(Senddata);
        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
        log.info("����"+unlicensedCarInUrl+"��ʼ++++++++++++++++++++++++++++++++++++");
        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
	    int response = client.executeMethod(postMethod); // POST
	    
	    if(response==200){
	    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
            String errorCode = dataJson.getString("error_code");
            if(errorCode.equals("0")){
            	wechatUser userinfo = new wechatUser();
            	userinfo.setUserOpenId(noVehiclePlate.getUserOpenId());
            	userinfo.setUserVehiclePlateStatus(0);
            	long num =wechatUsermapper.findWechatUserInfoCountByOpenId(noVehiclePlate.getUserOpenId());
                if(num==0){
                	wechatUsermapper.addWechatUserInfo(userinfo);
                }
                wechatUsermapper.insertApiLogs("���Ƴ��볡�ӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "success", "");
            	log.info("���Ƴ��û�openId��"+noVehiclePlate.getUserOpenId()+"�볡�ɹ�");
            }else{
            	wechatUsermapper.insertApiLogs("���Ƴ��볡�ӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "fail", dataJson.getString("error_msg"));
            	log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"�볡ʧ��");
            	throw new BaseServiceException(
            			Integer.parseInt(errorCode),
            			dataJson.getString("error_msg"));
            }
           
            
	    }else{
	    	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					"��鿴���Ƴ��볡�ӿ�");
	    }
		}finally {
			postMethod.releaseConnection();
		}
		
	}
	
	
	
public Map<String, Object> UnlicensedCarOut(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {

		PostMethod postMethod = null;
		try{
		// �����ϴ��ļ�Ŀ¼
	    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
	    String unlicensedCarOutUrl = res.getString("UnlicensedCarOut_url");
    	
	    postMethod = new PostMethod(unlicensedCarOutUrl) ;
        NameValuePair[] Senddata = {
	            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
	            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
	            new NameValuePair("parkNumber",noVehiclePlate.getParkingCode()),
	            new NameValuePair("passagewayId",noVehiclePlate.getChannelCode()+""),
	            new NameValuePair("authId",noVehiclePlate.getUserOpenId())
	            
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
            
            wechatUsermapper.insertApiLogs("���Ƴ������ӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "success", "������:"+dataJsonR.getString("out_trade_no")+";���:"+dataJsonR.getInt("order_receivable"));

            	log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ�������óɹ�");
            	if(dataJsonR.getInt("order_receivable")==0){
            		Map<String, Object>priceMap=new HashMap<String, Object>();
            		priceMap.put("price",dataJsonR.getInt("order_receivable"));
            		return priceMap;
            	}
            	prePayDto prePaydto=new prePayDto(noVehiclePlate.getUserOpenId(), dataJsonR.getString("out_trade_no"), dataJsonR.getInt("order_receivable"));
            	return wechatApiservice.getPrepayId(prePaydto, request);
            }else{
            	wechatUsermapper.insertApiLogs("���Ƴ������ӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "fail", dataJson.getString("error_msg"));

            	log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ��������ʧ��");
            	throw new BaseServiceException(
            			Integer.parseInt(errorCode),
            			dataJson.getString("error_msg"));
            }
           
            
	    }else{
	    	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					"��鿴���Ƴ��������ýӿ�");
	    }
		}finally {
			postMethod.releaseConnection();
		}
		
	}


public Map<String, Object> UnlicensedCarOut_new(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {

	PostMethod postMethod = null;
	try{
	// �����ϴ��ļ�Ŀ¼
    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
    String unlicensedCarOutUrl = res.getString("UnlicensedCarOut_url");
	
    postMethod = new PostMethod(unlicensedCarOutUrl) ;
    NameValuePair[] Senddata = {
            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
            new NameValuePair("parkNumber",noVehiclePlate.getParkingCode()),
            new NameValuePair("passagewayId",noVehiclePlate.getChannelCode()+""),
            new NameValuePair("authId",noVehiclePlate.getUserOpenId())
            
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
        
        wechatUsermapper.insertApiLogs("���Ƴ������ӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "success", "������:"+dataJsonR.getString("out_trade_no")+";���:"+dataJsonR.getInt("order_receivable"));

        	log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ�������óɹ�");
        	/*if(dataJsonR.getInt("order_receivable")==0){
        		Map<String, Object>priceMap=new HashMap<String, Object>();
        		priceMap.put("price",dataJsonR.getInt("order_receivable"));
        		return priceMap;
        	}*/
        	Map<String, Object>priceMap=new HashMap<String, Object>();
        	//priceMap.put("out_trade_no", dataJsonR.getString("out_trade_no"));
    		priceMap.put("price",dataJsonR.getInt("order_receivable"));
    		log.info("�Ż�ȯ�б�"+dataJsonR.getJSONArray("coupon_list"));
    		List<storeCouponRecordDto> dtoList=new ArrayList<storeCouponRecordDto>();
    		for(int i=0;i<dataJsonR.getJSONArray("coupon_list").size();i++){
    			Object o=dataJsonR.getJSONArray("coupon_list").get(i);
    	        JSONObject jsonObject2=JSONObject.fromObject(o);
    	        storeCouponRecordDto dto=(storeCouponRecordDto)JSONObject.toBean(jsonObject2, storeCouponRecordDto.class);
    	        dtoList.add(dto);
    		}
    		log.info("�Ż�ȯ�б�ʵ��"+new ObjectMapper().writeValueAsString(dtoList));
    		priceMap.put("coupon_list", dtoList);
    		return priceMap;
        	/*prePayDto prePaydto=new prePayDto(noVehiclePlate.getUserOpenId(), dataJsonR.getString("out_trade_no"), dataJsonR.getInt("order_receivable"));
        	return wechatApiservice.getPrepayId(prePaydto, request);*/
        }else{
        	wechatUsermapper.insertApiLogs("���Ƴ������ӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "fail", dataJson.getString("error_msg"));

        	log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ��������ʧ��");
        	throw new BaseServiceException(
        			Integer.parseInt(errorCode),
        			dataJson.getString("error_msg"));
        }
       
        
    }else{
    	throw new BaseServiceException(
				StatusCode.API_FREQUENTLY_ERROR.getCode(),
				"��鿴���Ƴ��������ýӿ�");
    }
	}finally {
		postMethod.releaseConnection();
	}
	
}



		public Map<String, Object> licensedCarOut(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {
			
			PostMethod postMethod = null;
			try{
			// �����ϴ��ļ�Ŀ¼
		    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
		    String unlicensedCarOutUrl = res.getString("licensedCarOut_url");
	    	
		    postMethod = new PostMethod(unlicensedCarOutUrl) ;

	        NameValuePair[] Senddata = {
		            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
		            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
		            new NameValuePair("parkNumber",noVehiclePlate.getParkingCode()),
		            new NameValuePair("plate",noVehiclePlate.getVehiclePlate())
		            
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
	            
	            	log.info("���Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ�������óɹ�");
	            	wechatUsermapper.insertApiLogs("���Ƴ�������ѽӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "success", "������:"+dataJsonR.getString("out_trade_no")+";���:"+dataJsonR.getInt("order_receivable"));

	            	prePayDto prePaydto=new prePayDto(noVehiclePlate.getUserOpenId(), dataJsonR.getString("out_trade_no"), dataJsonR.getInt("order_receivable"));
	            	if(dataJsonR.getInt("order_receivable")==0){
	            		Map<String, Object>priceMap=new HashMap<String, Object>();
	            		priceMap.put("price",dataJsonR.getInt("order_receivable"));
	            		return priceMap;
	            	}
	            	return wechatApiservice.getPrepayId(prePaydto, request);
	            	
	            }else{
	            	log.info("���Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ��������ʧ��");
	            	wechatUsermapper.insertApiLogs("���Ƴ�������ѽӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "fail", dataJson.getString("error_msg"));

	            	throw new BaseServiceException(
	            			Integer.parseInt(errorCode),
	            			dataJson.getString("error_msg"));
	            }
	           
	            
		    }else{
		    	throw new BaseServiceException(
						StatusCode.API_FREQUENTLY_ERROR.getCode(),
						"��鿴���Ƴ�������ѽӿ�");
		    }
			}finally {
				postMethod.releaseConnection();
			}
			
		}
		
		
		
public Map<String, Object> licensedCarOut_new(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {
			
			PostMethod postMethod = null;
			try{
			// �����ϴ��ļ�Ŀ¼
		    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
		    String unlicensedCarOutUrl = res.getString("licensedCarOut_url");
	    	
		    postMethod = new PostMethod(unlicensedCarOutUrl) ;

	        NameValuePair[] Senddata = {
		            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
		            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
		            new NameValuePair("parkNumber",noVehiclePlate.getParkingCode()),
		            new NameValuePair("plate",noVehiclePlate.getVehiclePlate()),
		            new NameValuePair("authId",noVehiclePlate.getUserOpenId())
		            
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
	            
	            	log.info("���Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ�������óɹ�");
	            	wechatUsermapper.insertApiLogs("���Ƴ�������ѽӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "success", "������:"+dataJsonR.getString("out_trade_no")+";���:"+dataJsonR.getInt("order_receivable"));

	            	Map<String, Object>priceMap=new HashMap<String, Object>();
	            	priceMap.put("out_trade_no", dataJsonR.getString("out_trade_no"));
            		priceMap.put("price",dataJsonR.getInt("order_receivable"));
            		log.info("���Ƴ��Ż�ȯ"+dataJsonR.getJSONArray("coupon_list"));
            		List<storeCouponRecordDto> dtoList=new ArrayList<storeCouponRecordDto>();
            		for(int i=0;i<dataJsonR.getJSONArray("coupon_list").size();i++){
            			Object o=dataJsonR.getJSONArray("coupon_list").get(i);
            	        JSONObject jsonObject2=JSONObject.fromObject(o);
            	        storeCouponRecordDto dto=(storeCouponRecordDto)JSONObject.toBean(jsonObject2, storeCouponRecordDto.class);
            	        dtoList.add(dto);
            		}
            		log.info("���Ƴ��Ż�ȯʵ��"+new ObjectMapper().writeValueAsString(dtoList));
            		priceMap.put("coupon_list", dtoList);
            		//priceMap.put("coupon_list", dataJsonR.getJSONArray("coupon_list"));
            		return priceMap;
	            }else{
	            	log.info("���Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ��������ʧ��");
	            	wechatUsermapper.insertApiLogs("���Ƴ�������ѽӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "fail", dataJson.getString("error_msg"));

	            	throw new BaseServiceException(
	            			Integer.parseInt(errorCode),
	            			dataJson.getString("error_msg"));
	            }
	           
	            
		    }else{
		    	throw new BaseServiceException(
						StatusCode.API_FREQUENTLY_ERROR.getCode(),
						"��鿴���Ƴ�������ѽӿ�");
		    }
			}finally {
				postMethod.releaseConnection();
			}
			
		}



		public void callBackNotify(int order_id,String outTradeNo, String transactionId, String timeEnd, String couponFee,String payType,String couponRecordId,String couponType) throws HttpException, IOException {


			PostMethod postMethod = null;
			try{
            if(couponRecordId.equals("0")){
            	couponRecordId="";
            }
		    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
		    String unlicensedCarOutUrl = res.getString("notifyCallBack_url");
	    	
		    postMethod = new PostMethod(unlicensedCarOutUrl) ;
		    /*int newCouponFee=Integer.valueOf(couponFee)*100;
		    String totalFee=String.valueOf(newCouponFee);*/
		    NameValuePair[] Senddata = {
		            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
		            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
		            new NameValuePair("order_id",String.valueOf(order_id)),
		            new NameValuePair("out_trade_no",outTradeNo),
		            new NameValuePair("transaction_id",transactionId),
		            new NameValuePair("time_end",timeEnd),
		            new NameValuePair("total_fee",couponFee),
		            new NameValuePair("pay_type",payType),
		            new NameValuePair("coupon_record_id",couponRecordId),
		            new NameValuePair("coupon_type",couponType)
		            
		            
		    };
	        
	        
	        postMethod.setRequestBody(Senddata);
	        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

	       
	        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
		    int response = client.executeMethod(postMethod); // POST
		    
		    if(response==200){
		    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
		    	log.info("++++++++++++++++++����֧���ص�++++++++++++++++++++++"+dataJson);
	            String errorCode = dataJson.getString("error_code");
	            if(errorCode.equals("0")){
	            	log.info("������"+outTradeNo+"�ص��ɹ���");
	            	wechatUsermapper.insertApiLogs("����֧���ص��ӿ�", "������:"+outTradeNo+";��ˮ��:"+transactionId+";Ӧ�ɽ��:"+couponFee, "success", "");

	            	
	            }else{
	            	log.info("������"+outTradeNo+"�ص�ʧ�ܣ�");
	            	wechatUsermapper.insertApiLogs("����֧���ص��ӿ�", "������:"+outTradeNo+";��ˮ��:"+transactionId+";Ӧ�ɽ��:"+couponFee, "fail", dataJson.getString("error_msg"));
	            	throw new BaseServiceException(
	            			Integer.parseInt(errorCode),
	            			dataJson.getString("error_msg"));
	            }
	           
	            
		    }else{
		    	throw new BaseServiceException(
						StatusCode.API_FREQUENTLY_ERROR.getCode(),
						"��鿴����֧���ص��ӿ�");
		    }
			}finally {
				postMethod.releaseConnection();
			}
			
		}



		public void doInvoice(parkinglotsPay parkinglotsPay, invoice invoice) throws IOException {
			
			PostMethod postMethod = null;
			try{
			// �����ϴ��ļ�Ŀ¼
		    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
		    String doInvoiceUrl = res.getString("doInvoice_url");
	    	
		    postMethod = new PostMethod(doInvoiceUrl) ;

	        NameValuePair[] Senddata = {
		            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
		            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
		            new NameValuePair("invoiceAmount",parkinglotsPay.getPay_receipts_txt()+""),
		            new NameValuePair("companyName",invoice.getCompanyName()),
		            new NameValuePair("taxNumber",invoice.getTaxNumber()),
		            new NameValuePair("addressAndMobile",invoice.getAddressMobile()),
		            new NameValuePair("bankAndNumber",invoice.getBankNumber()),
		            new NameValuePair("email",invoice.getEmail()),
		            
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
	                
	            	log.info("�����ţ�"+parkinglotsPay.getOut_trade_no()+"��Ʊ�ɹ�");
	            	wechatUsermapper.insertApiLogs("��Ʊ�ӿ�", "������:"+parkinglotsPay.getOut_trade_no()+";˰��:"+invoice.getTaxNumber()+";��Ʊ���:"+parkinglotsPay.getPay_receipts_txt(), "success", "��Ʊ��ַ:"+dataJsonR.getString("pdfUrl"));
	            	parkinglotsPay pay=new parkinglotsPay();
	            	pay.setOut_trade_no(parkinglotsPay.getOut_trade_no());
	            	pay.setInvoice_pdf(dataJsonR.getString("pdfUrl"));
	            	pay.setInvoice_status(1);
	            	invoicemapper.updateParkingPayInfo(pay);

	            }else{
	            	log.info("��Ʊ�ţ�"+parkinglotsPay.getOut_trade_no()+"��Ʊʧ��");
	            	wechatUsermapper.insertApiLogs("��Ʊ�ӿ�", "������:"+parkinglotsPay.getOut_trade_no()+";˰��:"+invoice.getTaxNumber()+";��Ʊ���:"+parkinglotsPay.getPay_receipts_txt(), "fail", dataJson.getString("error_msg"));
	            	throw new BaseServiceException(
	            			Integer.parseInt(errorCode),
	            			dataJson.getString("error_msg"));
	            }
	           
	            
		    }else{
		    	throw new BaseServiceException(
						StatusCode.API_FREQUENTLY_ERROR.getCode(),
						"��鿴��Ʊ�ӿ�");
		    }
			}finally {
				postMethod.releaseConnection();
			}
			
			
		}



		public void doInvoiceSumPrice(List<String> outTradeNoList, invoice invoice2, Map<String, Object> sumPriceMap) throws IOException {
			
			PostMethod postMethod = null;
			try{
			// �����ϴ��ļ�Ŀ¼
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
	                
	            	log.info("�����ţ�"+outTradeNoList.toString()+"��Ʊ�ɹ�");
	            	wechatUsermapper.insertApiLogs("��Ʊ�ӿ�", "������:"+outTradeNoList.toString()+";˰��:"+invoice2.getTaxNumber()+";��Ʊ���:"+sumPriceMap, "success", "��Ʊ��ַ:"+dataJsonR.getString("pdfUrl"));
	            	parkinglotsPay pay=new parkinglotsPay();
	            	pay.setOut_trade_noList(outTradeNoList);
	            	pay.setInvoice_pdf(dataJsonR.getString("pdfUrl"));
	            	pay.setInvoice_status(1);
	            	pay.setInvoice_company_name(invoice2.getCompanyName());
	            	pay.setInvoice_tax_number(invoice2.getTaxNumber());
	            	pay.setInvoice_bank_number(invoice2.getBankNumber());
	            	pay.setInvoice_address_mobile(invoice2.getAddressMobile());
	            	pay.setInvoice_email(invoice2.getEmail());
	            	invoicemapper.updateParkingPayInfo(pay);

	            }else{
	            	log.info("��Ʊ�ţ�"+outTradeNoList.toString()+"��Ʊʧ��");
	            	parkinglotsPay pay=new parkinglotsPay();
	            	pay.setOut_trade_noList(outTradeNoList);
	            	//pay.setInvoice_pdf(dataJsonR.getString("pdfUrl"));
	            	//pay.setInvoice_status(1);
	            	pay.setInvoice_company_name(invoice2.getCompanyName());
	            	pay.setInvoice_tax_number(invoice2.getTaxNumber());
	            	pay.setInvoice_bank_number(invoice2.getBankNumber());
	            	pay.setInvoice_address_mobile(invoice2.getAddressMobile());
	            	pay.setInvoice_email(invoice2.getEmail());
	            	invoicemapper.updateParkingPayInfo(pay);
	            	wechatUsermapper.insertApiLogs("��Ʊ�ӿ�", "������:"+outTradeNoList.toString()+";˰��:"+invoice2.getTaxNumber()+";��Ʊ���:"+sumPriceMap, "fail", dataJson.getString("error_msg"));
	            	throw new BaseServiceException(
	            			Integer.parseInt(errorCode),
	            			dataJson.getString("error_msg"));
	            }
	           
	            
		    }else{
		    	throw new BaseServiceException(
						StatusCode.API_FREQUENTLY_ERROR.getCode(),
						"��鿴��Ʊ�ӿ�");
		    }
			}finally {
				postMethod.releaseConnection();
			}
			
		}
		
		
		
		public Map<String, Object> UnlicensedCarOut_zfb(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {

			PostMethod postMethod = null;
			try{
			// �����ϴ��ļ�Ŀ¼
		    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
		    String unlicensedCarOutUrl = res.getString("UnlicensedCarOut_url");
	    	
		    postMethod = new PostMethod(unlicensedCarOutUrl) ;
	        NameValuePair[] Senddata = {
		            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
		            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
		            new NameValuePair("parkNumber",noVehiclePlate.getParkingCode()),
		            new NameValuePair("passagewayId",noVehiclePlate.getChannelCode()+""),
		            new NameValuePair("authId",noVehiclePlate.getUserOpenId())
		            
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
	           
	            wechatUsermapper.insertApiLogs("���Ƴ������ӿ�_zfb", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "success", "������:"+dataJsonR.getString("out_trade_no")+";���:"+dataJsonR.getInt("order_receivable"));

	            	log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ�������óɹ�");
	            	prePayDto prePaydto=new prePayDto(noVehiclePlate.getUserOpenId(), dataJsonR.getString("out_trade_no"), dataJsonR.getInt("order_receivable"));
	            	if(dataJsonR.getInt("order_receivable")==0){
	            		Map<String, Object>priceMap=new HashMap<String, Object>();
	            		priceMap.put("price",dataJsonR.getInt("order_receivable"));
	            		priceMap.put("getPay", "");
	            		return priceMap;
	            	}
	            	Map<String, Object> returnMap=new HashMap<String, Object>();
	            	//returnMap.put("out_trade_no", dataJsonR.getInt("out_trade_no"));
	            	///returnMap.put("coupon_list", dataJsonR.get("coupon_list"));
	            	//returnMap.put("getPay", "");
	            	returnMap.put("price", dataJsonR.getInt("order_receivable"));
	            	returnMap.put("getPay", zfbapiservice.getPrepayId_zfb(prePaydto, request));
	            	return returnMap;
	            }else{
	            	wechatUsermapper.insertApiLogs("���Ƴ������ӿ�_zfb", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "fail", dataJson.getString("error_msg"));

	            	log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ��������ʧ��");
	            	throw new BaseServiceException(
	            			Integer.parseInt(errorCode),
	            			dataJson.getString("error_msg"));
	            }
	           
	            
		    }else{
		    	throw new BaseServiceException(
						StatusCode.API_FREQUENTLY_ERROR.getCode(),
						"��鿴���Ƴ��������ýӿ�");
		    }
			}finally {
				postMethod.releaseConnection();
			}
			
		}
		
		
		
		public Map<String, Object> UnlicensedCarOut_zfb_new(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {

			PostMethod postMethod = null;
			try{
			// �����ϴ��ļ�Ŀ¼
		    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
		    String unlicensedCarOutUrl = res.getString("UnlicensedCarOut_url");
	    	
		    postMethod = new PostMethod(unlicensedCarOutUrl) ;
	        NameValuePair[] Senddata = {
		            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
		            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
		            new NameValuePair("parkNumber",noVehiclePlate.getParkingCode()),
		            new NameValuePair("passagewayId",noVehiclePlate.getChannelCode()+""),
		            new NameValuePair("authId",noVehiclePlate.getUserOpenId())
		            
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
	           
	            wechatUsermapper.insertApiLogs("���Ƴ������ӿ�_zfb", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "success", "������:"+dataJsonR.getString("out_trade_no")+";���:"+dataJsonR.getInt("order_receivable"));

	            	log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ�������óɹ�");
	            	prePayDto prePaydto=new prePayDto(noVehiclePlate.getUserOpenId(), dataJsonR.getString("out_trade_no"), dataJsonR.getInt("order_receivable"));
	            	/*if(dataJsonR.getInt("order_receivable")==0){
	            		Map<String, Object>priceMap=new HashMap<String, Object>();
	            		priceMap.put("price",dataJsonR.getInt("order_receivable"));
	            		priceMap.put("getPay", "");
	            		return priceMap;
	            	}*/
	            	Map<String, Object> returnMap=new HashMap<String, Object>();
	            	//returnMap.put("out_trade_no", dataJsonR.getString("out_trade_no"));
	            	List<storeCouponRecordDto> dtoList=new ArrayList<storeCouponRecordDto>();
	            	log.info("�Ż�ȯ�б�"+dataJsonR.getJSONArray("coupon_list"));
	        		for(int i=0;i<dataJsonR.getJSONArray("coupon_list").size();i++){
	        			Object o=dataJsonR.getJSONArray("coupon_list").get(i);
	        	        JSONObject jsonObject2=JSONObject.fromObject(o);
	        	        storeCouponRecordDto dto=(storeCouponRecordDto)JSONObject.toBean(jsonObject2, storeCouponRecordDto.class);
	        	        dtoList.add(dto);
	        		}
	        		log.info("�Ż�ȯ�б�ʵ��"+new ObjectMapper().writeValueAsString(dtoList));
	        		returnMap.put("coupon_list", dtoList);
	            	//returnMap.put("coupon_list", dataJsonR.get("coupon_list"));
	            	returnMap.put("getPay", "");
	            	returnMap.put("price", dataJsonR.getInt("order_receivable"));
	            	//returnMap.put("getPay", zfbapiservice.getPrepayId_zfb(prePaydto, request));
	            	return returnMap;
	            }else{
	            	wechatUsermapper.insertApiLogs("���Ƴ������ӿ�_zfb", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "fail", dataJson.getString("error_msg"));

	            	log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ��������ʧ��");
	            	throw new BaseServiceException(
	            			Integer.parseInt(errorCode),
	            			dataJson.getString("error_msg"));
	            }
	           
	            
		    }else{
		    	throw new BaseServiceException(
						StatusCode.API_FREQUENTLY_ERROR.getCode(),
						"��鿴���Ƴ��������ýӿ�");
		    }
			}finally {
				postMethod.releaseConnection();
			}
			
		}



			public Map<String, Object> licensedCarOut_zfb(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {
				/*prePayDto prePaydto=new prePayDto("2088302409978811", "12212122313212331", 1);
            	Map<String, Object> returnMap=new HashMap<String, Object>();
            	returnMap.put("price", 1);
            	returnMap.put("getPay", zfbapiservice.getPrepayId_zfb(prePaydto, request));
            	return returnMap;*/
				PostMethod postMethod = null;
				try{
				// �����ϴ��ļ�Ŀ¼
			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String unlicensedCarOutUrl = res.getString("licensedCarOut_url");
		    	
			    postMethod = new PostMethod(unlicensedCarOutUrl) ;

		        NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("parkNumber",noVehiclePlate.getParkingCode()),
			            new NameValuePair("plate",noVehiclePlate.getVehiclePlate()),
			            new NameValuePair("authId",noVehiclePlate.getUserOpenId())
			            
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
		            
		            	log.info("���Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ�������óɹ�");
		            	wechatUsermapper.insertApiLogs("���Ƴ�������ѽӿ�_zfb", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "success", "������:"+dataJsonR.getString("out_trade_no")+";���:"+dataJsonR.getInt("order_receivable"));

		            	prePayDto prePaydto=new prePayDto(noVehiclePlate.getUserOpenId(), dataJsonR.getString("out_trade_no"), dataJsonR.getInt("order_receivable"));
		            	if(dataJsonR.getInt("order_receivable")==0){
		            		Map<String, Object>priceMap=new HashMap<String, Object>();
		            		priceMap.put("price",dataJsonR.getInt("order_receivable"));
		            		priceMap.put("getPay", "");
		            		return priceMap;
		            	}
		            	/*Map<String, Object> returnMap=new HashMap<String, Object>();
		            	returnMap.put("out_trade_no", dataJsonR.getInt("out_trade_no"));
		            	returnMap.put("price", dataJsonR.getInt("order_receivable"));
		            	returnMap.put("coupon_list", dataJsonR.get("coupon_list"));
		            	returnMap.put("getPay", "");*/
		            	Map<String, Object> returnMap=new HashMap<String, Object>();
		            	returnMap.put("price", dataJsonR.getInt("order_receivable"));
		            	returnMap.put("getPay", zfbapiservice.getPrepayId_zfb(prePaydto, request));
		            	return returnMap;
		            }else{
		            	log.info("���Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ��������ʧ��");
		            	wechatUsermapper.insertApiLogs("���Ƴ�������ѽӿ�_zfb", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "fail", dataJson.getString("error_msg"));

		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴���Ƴ�������ѽӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
			}
			
			
			public Map<String, Object> licensedCarOut_zfb_new(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {
				/*prePayDto prePaydto=new prePayDto("2088302409978811", "12212122313212331", 1);
            	Map<String, Object> returnMap=new HashMap<String, Object>();
            	returnMap.put("price", 1);
            	returnMap.put("getPay", zfbapiservice.getPrepayId_zfb(prePaydto, request));
            	return returnMap;*/
				PostMethod postMethod = null;
				try{
				// �����ϴ��ļ�Ŀ¼
			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String unlicensedCarOutUrl = res.getString("licensedCarOut_url");
		    	
			    postMethod = new PostMethod(unlicensedCarOutUrl) ;

		        NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("parkNumber",noVehiclePlate.getParkingCode()),
			            new NameValuePair("plate",noVehiclePlate.getVehiclePlate()),
			            new NameValuePair("authId",noVehiclePlate.getUserOpenId())
			            
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
		            
		            	log.info("���Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ�������óɹ�");
		            	wechatUsermapper.insertApiLogs("���Ƴ�������ѽӿ�_zfb", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "success", "������:"+dataJsonR.getString("out_trade_no")+";���:"+dataJsonR.getInt("order_receivable"));

		            	prePayDto prePaydto=new prePayDto(noVehiclePlate.getUserOpenId(), dataJsonR.getString("out_trade_no"), dataJsonR.getInt("order_receivable"));
		            	/*if(dataJsonR.getInt("order_receivable")==0){
		            		Map<String, Object>priceMap=new HashMap<String, Object>();
		            		priceMap.put("price",dataJsonR.getInt("order_receivable"));
		            		priceMap.put("getPay", "");
		            		return priceMap;
		            	}*/
		            	Map<String, Object> returnMap=new HashMap<String, Object>();
		            	//returnMap.put("out_trade_no", dataJsonR.getInt("out_trade_no"));
		            	returnMap.put("price", dataJsonR.getInt("order_receivable"));
		            	log.info("���Ƴ��Ż�ȯ_zfb"+dataJsonR.getJSONArray("coupon_list"));
		            	List<storeCouponRecordDto> dtoList=new ArrayList<storeCouponRecordDto>();
		        		for(int i=0;i<dataJsonR.getJSONArray("coupon_list").size();i++){
		        			Object o=dataJsonR.getJSONArray("coupon_list").get(i);
		        	        JSONObject jsonObject2=JSONObject.fromObject(o);
		        	        storeCouponRecordDto dto=(storeCouponRecordDto)JSONObject.toBean(jsonObject2, storeCouponRecordDto.class);
		        	        dtoList.add(dto);
		        		}
		        		log.info("���Ƴ��Ż�ȯʵ��_zfb"+new ObjectMapper().writeValueAsString(dtoList));
		            	returnMap.put("coupon_list", dtoList);
		            	returnMap.put("getPay", "");
		            	/*Map<String, Object> returnMap=new HashMap<String, Object>();
		            	returnMap.put("price", dataJsonR.getInt("order_receivable"));
		            	returnMap.put("getPay", zfbapiservice.getPrepayId_zfb(prePaydto, request));*/
		            	return returnMap;
		            }else{
		            	log.info("���Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ��������ʧ��");
		            	wechatUsermapper.insertApiLogs("���Ƴ�������ѽӿ�_zfb", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "fail", dataJson.getString("error_msg"));

		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴���Ƴ�������ѽӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
			}



			public void getCoupon(couponDto coupon) throws HttpException, IOException {
				
				if(StringUtils.isBlank(coupon.getUserOpenId())){
					throw new BaseServiceException(
							StatusCode.MISSING_OPENID_ERROR.getCode(),
							"С����openId����Ϊ�գ�");
				}
				
				if(StringUtils.isBlank(coupon.getCouponId())){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"�Ż�ȯ���벻��Ϊ�գ�");
				}
				
				if(StringUtils.isBlank(coupon.getVehPlate())){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"���ƺŲ���Ϊ�գ�");
				}
				
				if(StringUtils.isBlank(coupon.getUserId())){
					wechatUser user= wechatUsermapper.findWechatUserInfoByOpenId(coupon.getUserOpenId());
					if(user==null){
						throw new BaseServiceException(
								StatusCode.DATA_NOT_EXISTS.getCode(),
								"�û���Ϣ�����ڣ����ʵ��");
					}else{
						coupon.setUserId(user.getUserId().toString());
					}
				}
				PostMethod postMethod = null;
				try{

			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String getCouponUrl = res.getString("getCoupon_url");
		    	
			    postMethod = new PostMethod(getCouponUrl) ;

		        NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("coupon_id",coupon.getCouponId()),
			            new NameValuePair("user_id",coupon.getUserId()),
			            new NameValuePair("veh_plate",coupon.getVehPlate())
			            
			    };
		        postMethod.setRequestBody(Senddata);
		        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
		        log.info("����"+getCouponUrl+"��ʼ++++++++++++++++++++++++++++++++++++");
		        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
			    int response = client.executeMethod(postMethod); // POST
			    
			    if(response==200){
			    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
		            String errorCode = dataJson.getString("error_code");
		            if(errorCode.equals("0")){
		            	
		                wechatUsermapper.insertApiLogs("�Ż�ȯ���Žӿ�", "�û�id:"+coupon.getUserId()+";���ƺ�:"+coupon.getVehPlate()+";�Ż�ȯ����:"+coupon.getCouponId(), "success", "");
		            	//log.info("che'pai��"+noVehiclePlate.getUserOpenId()+"�볡�ɹ�");
		            }else{
		            	wechatUsermapper.insertApiLogs("�Ż�ȯ���Žӿ�", "�û�id:"+coupon.getUserId()+";���ƺ�:"+coupon.getVehPlate()+";�Ż�ȯ����:"+coupon.getCouponId(), "fail",dataJson.getString("error_msg"));
		            	//log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"�볡ʧ��");
		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴�Ż�ȯ���Žӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
			}



			public void getMeetingCode(meetingCodeDto meetingcode) throws HttpException, IOException {
				
				if(StringUtils.isBlank(meetingcode.getUserOpenId())){
					throw new BaseServiceException(
							StatusCode.MISSING_OPENID_ERROR.getCode(),
							"С����openId����Ϊ�գ�");
				}
				
				if(StringUtils.isBlank(meetingcode.getcId())){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"������벻��Ϊ�գ�");
				}
				
				if(StringUtils.isBlank(meetingcode.getVehPlate())){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"���ƺŲ���Ϊ�գ�");
				}
				
				if(StringUtils.isBlank(meetingcode.getVehPlateColor())){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"������ɫ����Ϊ�գ�");
				}
				
				if(StringUtils.isBlank(meetingcode.getUserName())){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"������������Ϊ�գ�");
				}
				
				if(StringUtils.isBlank(meetingcode.getUserMobile())){
					throw new BaseServiceException(
							StatusCode.MISSING_PARAMETER_ERROR.getCode(),
							"�����绰����Ϊ�գ�");
				}
				
				if(StringUtils.isBlank(meetingcode.getUserId())){
					wechatUser user= wechatUsermapper.findWechatUserInfoByOpenId(meetingcode.getUserOpenId());
					if(user==null){
						throw new BaseServiceException(
								StatusCode.DATA_NOT_EXISTS.getCode(),
								"�û���Ϣ�����ڣ����ʵ��");
					}else{
						meetingcode.setUserId(user.getUserId().toString());
					}
				}
				PostMethod postMethod = null;
				try{

			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String getMeetingCodeUrl = res.getString("getMeetingCode_url");
		    	
			    postMethod = new PostMethod(getMeetingCodeUrl) ;

		        NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("c_id",meetingcode.getcId()),
			            new NameValuePair("user_id",meetingcode.getUserId()),
			            new NameValuePair("user_name",meetingcode.getUserName()),
			            new NameValuePair("user_mobile",meetingcode.getUserMobile()),
			            new NameValuePair("veh_plate",meetingcode.getVehPlate()),
			            new NameValuePair("veh_plate_color",meetingcode.getVehPlateColor())
			            
			    };
		        postMethod.setRequestBody(Senddata);
		        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;
		        log.info("����"+getMeetingCodeUrl+"��ʼ++++++++++++++++++++++++++++++++++++");
		        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
			    int response = client.executeMethod(postMethod); // POST
			    
			    if(response==200){
			    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
		            String errorCode = dataJson.getString("error_code");
		            if(errorCode.equals("0")){
		            	
		                wechatUsermapper.insertApiLogs("������ɨ��ӿ�", "�û�id:"+meetingcode.getUserId()+";�û�����:"+meetingcode.getUserName()+";���ƺ�:"+meetingcode.getVehPlate()+";������:"+meetingcode.getcId(), "success", "");
		            	
		            }else{
		            	wechatUsermapper.insertApiLogs("������ɨ��ӿ�", "�û�id:"+meetingcode.getUserId()+";�û�����:"+meetingcode.getUserName()+";���ƺ�:"+meetingcode.getVehPlate()+";�������:"+meetingcode.getcId(), "fail",dataJson.getString("error_msg"));
		            	
		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴������ɨ��ӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
			}



			public int getUnlicensedCarOutMoney(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws HttpException, IOException {
				PostMethod postMethod = null;
				try{
				// �����ϴ��ļ�Ŀ¼
			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String unlicensedCarOutUrl = res.getString("UnlicensedCarOut_url");
		    	
			    postMethod = new PostMethod(unlicensedCarOutUrl) ;
		        NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("parkNumber",noVehiclePlate.getParkingCode()),
			            new NameValuePair("passagewayId",noVehiclePlate.getChannelCode()+""),
			            new NameValuePair("authId",noVehiclePlate.getUserOpenId())
			            
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
		            
		            wechatUsermapper.insertApiLogs("���Ƴ������ӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "success", "������:"+dataJsonR.getString("out_trade_no")+";���:"+dataJsonR.getInt("order_receivable"));

		            	log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ�������óɹ�");
		            	
		            		return dataJsonR.getInt("order_receivable");
		            
		            	
		            }else{
		            	wechatUsermapper.insertApiLogs("���Ƴ������ӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";ͨ����:"+noVehiclePlate.getChannelCode(), "fail", dataJson.getString("error_msg"));

		            	log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ��������ʧ��");
		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴���Ƴ��������ýӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
			}



			public int getlicensedCarOutMoney(noVehiclePlate noVehiclePlate, HttpServletRequest request) throws IOException {
				PostMethod postMethod = null;
				try{
				// �����ϴ��ļ�Ŀ¼
			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String unlicensedCarOutUrl = res.getString("licensedCarOut_url");
		    	
			    postMethod = new PostMethod(unlicensedCarOutUrl) ;

		        NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("parkNumber",noVehiclePlate.getParkingCode()),
			            new NameValuePair("plate",noVehiclePlate.getVehiclePlate())
			            
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
		            
		            	log.info("���Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ�������óɹ�");
		            	wechatUsermapper.insertApiLogs("���Ƴ�������ѽӿ�", "�û�openid:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "success", "������:"+dataJsonR.getString("out_trade_no")+";���:"+dataJsonR.getInt("order_receivable"));
		            	return dataJsonR.getInt("order_receivable");
		            }else{
		            	log.info("���Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ��������ʧ��");
		            	wechatUsermapper.insertApiLogs("���Ƴ�������ѽӿ�", "�û�openid:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "fail", dataJson.getString("error_msg"));

		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴���Ƴ�������ѽӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
			}



			public void callBackNotify_zfb(int orderId,String outTradeNo, String transactionId, String timeEnd, String couponFee,String payType,String couponRecordId,String couponType) throws HttpException, IOException {
				PostMethod postMethod = null;
				try{

			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String unlicensedCarOutUrl = res.getString("notifyCallBack_url");
		    	
			    postMethod = new PostMethod(unlicensedCarOutUrl) ;
			    Double f = Double.valueOf(couponFee);
			    int a = (int)Math.ceil(f);
			    int newCouponFee=(int) (f*100);
			    String totalFee=String.valueOf(newCouponFee);
			    NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("order_id",String.valueOf(orderId)),
			            new NameValuePair("out_trade_no",outTradeNo),
			            new NameValuePair("transaction_id",transactionId),
			            new NameValuePair("time_end",timeEnd.replaceAll("-","").replaceAll(":", "").replaceAll(" ", "")),
			            new NameValuePair("total_fee",totalFee),
			            new NameValuePair("pay_type",payType),
			            new NameValuePair("coupon_record_id",couponRecordId),
			            new NameValuePair("coupon_type",couponType)
			            
			    };
		        
		        
		        postMethod.setRequestBody(Senddata);
		        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

		       
		        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
			    int response = client.executeMethod(postMethod); // POST
			    
			    if(response==200){
			    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
			    	log.info("++++++++++++++++++֧��������֧���ص�++++++++++++++++++++++"+dataJson);
		            String errorCode = dataJson.getString("error_code");
		            if(errorCode.equals("0")){
		            	log.info("������"+outTradeNo+"�ص��ɹ���");
		            	wechatUsermapper.insertApiLogs("֧��������֧���ص��ӿ�", "������:"+outTradeNo+";��ˮ��:"+transactionId+";Ӧ�ɽ��:"+couponFee, "success", "");

		            	
		            }else{
		            	log.info("������"+outTradeNo+"�ص�ʧ�ܣ�");
		            	wechatUsermapper.insertApiLogs("֧��������֧���ص��ӿ�", "������:"+outTradeNo+";��ˮ��:"+transactionId+";Ӧ�ɽ��:"+couponFee, "fail", dataJson.getString("error_msg"));
		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴����֧���ص��ӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
			}



			public String getERCodeString(String couponId) throws HttpException, IOException {
				PostMethod postMethod = null;
				try{
				// �����ϴ��ļ�Ŀ¼
			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String getERCodeStringUrl = res.getString("getERCodeString_url");
		    	
			    postMethod = new PostMethod(getERCodeStringUrl) ;
		        NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("coupon_id",couponId)
  
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
		            //JSONObject  dataJsonR = JSONObject.fromObject(data);
		            
		            wechatUsermapper.insertApiLogs("�Ż�ȯ��ά���ַ����ӿ�", "couponId:"+couponId,"success",dataJson.getString("error_msg"));

		            
		            	
		            		return data;
		            
		            	
		            }else{
		            	wechatUsermapper.insertApiLogs("�Ż�ȯ��ά���ַ����ӿ�", "couponId:"+couponId,"fail",dataJson.getString("error_msg"));

		            	//log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ��������ʧ��");
		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴�Ż�ȯ��ά���ַ����ӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
				
				
				
				
			}



			public void scanERCodeGetCoupon(String encode, String userId) throws HttpException, IOException {
				PostMethod postMethod = null;
				try{
				// �����ϴ��ļ�Ŀ¼
			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String getERCodeStringUrl = res.getString("scanERCodeGetCoupon_url");
		    	
			    postMethod = new PostMethod(getERCodeStringUrl) ;
		        NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("encode_string",encode),
			            new NameValuePair("user_id",userId)
  
			    };

			    postMethod.setRequestBody(Senddata);
		        
		        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

		       
		        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
			    int response = client.executeMethod(postMethod); // POST
			    
			    if(response==200){
			    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
		            String errorCode = dataJson.getString("error_code");
		            if(errorCode.equals("0")){
		            
		            
		            wechatUsermapper.insertApiLogs("ɨ����ȡ�Ż�ȯ�ӿ�", "��ά������:"+encode+"userId:"+userId,"success",dataJson.getString("error_msg"));

		            	
		            }else{
		            	wechatUsermapper.insertApiLogs("ɨ����ȡ�Ż�ȯ�ӿ�", "��ά������:"+encode+"userId:"+userId,"fail",dataJson.getString("error_msg"));

		            	//log.info("���Ƴ��û�openId"+noVehiclePlate.getUserOpenId()+"��ȡ��������ʧ��");
		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴ɨ����ȡ�Ż�ȯ�ӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
				
				
				
			}



			public Map<String, Object> payByDiscountNotify(prePayDto2 dto) throws HttpException, IOException {
				
				PostMethod postMethod = null;
				try{

			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String unlicensedCarOutUrl = res.getString("payByDiscountNotify_url");
		    	
			    postMethod = new PostMethod(unlicensedCarOutUrl) ;
			    /*int newCouponFee=Integer.valueOf(couponFee)*100;
			    String totalFee=String.valueOf(newCouponFee);*/
			    NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("order_id",String.valueOf(dto.getOrderId())),
			            new NameValuePair("out_trade_no",dto.getOutTradeNo()),
			            new NameValuePair("total_fee",String.valueOf(dto.getBeforeAmount())),
			            new NameValuePair("coupon_record_id",dto.getCouponRecordId()),
			            new NameValuePair("coupon_type",dto.getCouponType())
			            
			            
			    };
		        
		        
		        postMethod.setRequestBody(Senddata);
		        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

		       
		        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
			    int response = client.executeMethod(postMethod); // POST
			    
			    if(response==200){
			    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
			    	log.info("++++++++++++++++++����֧���ص�++++++++++++++++++++++"+dataJson);
		            String errorCode = dataJson.getString("error_code");
		            if(errorCode.equals("0")){
		            	log.info("������"+dto.getOutTradeNo()+"�ص��ɹ���");
		            	wechatUsermapper.insertApiLogs("����֧���ص��ӿ�", "������:"+dto.getOutTradeNo()+";order_id:"+dto.getOrderId()+";Ӧ�ɽ��:"+dto.getBeforeAmount(), "success", "");

		            	return null;
		            }else{
		            	log.info("������"+dto.getOutTradeNo()+"�ص�ʧ�ܣ�");
		            	wechatUsermapper.insertApiLogs("����֧���ص��ӿ�", "������:"+dto.getOutTradeNo()+";order_id:"+dto.getOrderId()+";Ӧ�ɽ��:"+dto.getBeforeAmount(), "fail", dataJson.getString("error_msg"));
		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴����֧���ص��ӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
			}
			
			
			
public String payByDiscountNotify_zfb(prePayDto2 dto) throws HttpException, IOException {
				
				PostMethod postMethod = null;
				try{

			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String unlicensedCarOutUrl = res.getString("payByDiscountNotify_url");
		    	
			    postMethod = new PostMethod(unlicensedCarOutUrl) ;
			    /*int newCouponFee=Integer.valueOf(couponFee)*100;
			    String totalFee=String.valueOf(newCouponFee);*/
			    NameValuePair[] Senddata = {
			            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
			            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
			            new NameValuePair("order_id",String.valueOf(dto.getOrderId())),
			            new NameValuePair("out_trade_no",dto.getOutTradeNo()),
			            new NameValuePair("total_fee",String.valueOf(dto.getBeforeAmount())),
			            new NameValuePair("coupon_record_id",dto.getCouponRecordId()),
			            new NameValuePair("coupon_type",dto.getCouponType())
			            
			            
			    };
		        
		        
		        postMethod.setRequestBody(Senddata);
		        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

		       
		        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
			    int response = client.executeMethod(postMethod); // POST
			    
			    if(response==200){
			    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
			    	log.info("++++++++++++++++++����֧���ص�++++++++++++++++++++++"+dataJson);
		            String errorCode = dataJson.getString("error_code");
		            if(errorCode.equals("0")){
		            	log.info("������"+dto.getOutTradeNo()+"�ص��ɹ���");
		            	wechatUsermapper.insertApiLogs("����֧���ص��ӿ�", "������:"+dto.getOutTradeNo()+";order_id:"+dto.getOrderId()+";Ӧ�ɽ��:"+dto.getBeforeAmount(), "success", "");

		            	return null;
		            }else{
		            	log.info("������"+dto.getOutTradeNo()+"�ص�ʧ�ܣ�");
		            	wechatUsermapper.insertApiLogs("����֧���ص��ӿ�", "������:"+dto.getOutTradeNo()+";order_id:"+dto.getOrderId()+";Ӧ�ɽ��:"+dto.getBeforeAmount(), "fail", dataJson.getString("error_msg"));
		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴����֧���ص��ӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
			}



		     public String doInvoiceByStore(invoice invoice3, String records) throws HttpException, IOException {
		    	 PostMethod postMethod = null;
					try{
					// �����ϴ��ļ�Ŀ¼
				    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
				    String doInvoiceUrl = res.getString("doInvoice_url");
			    	
				    postMethod = new PostMethod(doInvoiceUrl) ;

			        NameValuePair[] Senddata = {
				            new NameValuePair("app_id","42ca3f6daff660c47147a25cfb29df229f630778"),
				            new NameValuePair("app_key","ee62d29effcfed3a1443524475fed8a684eec8f9"),
				            new NameValuePair("invoiceAmount",invoice3.getInvoiceAmount()),
				            new NameValuePair("companyName",invoice3.getCompanyName()),
				            new NameValuePair("taxNumber",invoice3.getTaxNumber()),
				            new NameValuePair("addressAndMobile",invoice3.getAddressMobile()),
				            new NameValuePair("bankAndNumber",invoice3.getBankNumber()),
				            new NameValuePair("email",invoice3.getEmail()),
				            new NameValuePair("remark","�̻���ֵ��Ʊ")
				            
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
			                
			            	log.info("�̻���ֵid��"+records+"��Ʊ�ɹ�");
			            	wechatUsermapper.insertApiLogs("�̻���Ʊ�ӿ�", "�̻���ֵid:"+records+";˰��:"+invoice3.getTaxNumber()+";��Ʊ���:"+invoice3.getInvoiceAmount(), "success", "��Ʊ��ַ:"+dataJsonR.getString("pdfUrl"));
			            	return dataJsonR.getString("pdfUrl");

			            }else{
			            	log.info("�̻���ֵid��"+records+"��Ʊʧ��");
			            	wechatUsermapper.insertApiLogs("�̻���Ʊ�ӿ�", "�̻���ֵid��"+records+";˰��:"+invoice3.getTaxNumber()+";��Ʊ���:"+invoice3.getInvoiceAmount(), "fail", dataJson.getString("error_msg"));
			            	throw new BaseServiceException(
			            			Integer.parseInt(errorCode),
			            			dataJson.getString("error_msg"));
			            }
			           
			            
				    }else{
				    	throw new BaseServiceException(
								StatusCode.API_FREQUENTLY_ERROR.getCode(),
								"��鿴��Ʊ�ӿ�");
				    }
					}finally {
						postMethod.releaseConnection();
					}
			

		     }



			public void delBackPlate(String plate) throws IOException {
				PostMethod postMethod = null;
				try{

			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String unlicensedCarOutUrl = res.getString("delBlackPlate_url");
		    	
			    postMethod = new PostMethod(unlicensedCarOutUrl) ;
			    /*int newCouponFee=Integer.valueOf(couponFee)*100;
			    String totalFee=String.valueOf(newCouponFee);*/
			    NameValuePair[] Senddata = {
			            new NameValuePair("app_id","a00a9b24866c5b32bb9ca4231fd0c47c54966fa7"),
			            new NameValuePair("app_key","164edf151624b0c7173f1747279ee13f812e4807"),
			            new NameValuePair("plate",String.valueOf(plate))
			            
			            
			    };
		        
		        
		        postMethod.setRequestBody(Senddata);
		        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

		       
		        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
			    int response = client.executeMethod(postMethod); // POST
			    
			    if(response==200){
			    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
			    	log.info("++++++++++++++++++ɾ���������ӿ�++++++++++++++++++++++"+dataJson);
		            String errorCode = dataJson.getString("error_code");
		            if(errorCode.equals("0")){
		            	log.info("ɾ�������������ƺ�:"+plate+"�ɹ���");
		            	wechatUsermapper.insertApiLogs("ɾ���������ӿ�", "���ƺ�:"+plate, "success", "");

		            	
		            }else{
		            	log.info("ɾ�������������ƺ�:"+plate+"ʧ�ܣ�"+dataJson.getString("error_msg"));
		            	wechatUsermapper.insertApiLogs("ɾ���������ӿ�", "���ƺ�:"+plate, "fail", dataJson.getString("error_msg"));
		            	
		            }
		           
		            
			    }else{
			    	
			    	log.info("��鿴ɾ���������ӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
			}



			/*public Map<String, Object> licensedCarOut_new_xiuzhou(noVehiclePlate noVehiclePlate,
					HttpServletRequest request) throws HttpException, IOException {
				PostMethod postMethod = null;
				try{
				// �����ϴ��ļ�Ŀ¼
			    ResourceBundle res = ResourceBundle.getBundle("blueCardApi");
			    String unlicensedCarOutUrlXiuZhou = res.getString("licensedCarOut_url_xiuzhou");
		    	
			    postMethod = new PostMethod(unlicensedCarOutUrlXiuZhou) ;

		        NameValuePair[] Senddata = {
			            new NameValuePair("appId","b6a81a14"),
			            new NameValuePair("appKey","9c50005e0c1ef261"),
			            new NameValuePair("pkl_code",noVehiclePlate.getParkingCode()),
			            new NameValuePair("plate",noVehiclePlate.getVehiclePlate())
			            
			    };
		        
		        
		        postMethod.setRequestBody(Senddata);
		        postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8") ;

		       
		        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
			    int response = client.executeMethod(postMethod); // POST
			    
			    if(response==200){
			    	JSONObject  dataJson = JSONObject.fromObject(postMethod.getResponseBodyAsString());
		            String errorCode = dataJson.getString("err_code");
		            if(errorCode.equals("0")){
		            String data = dataJson.getString("data");
		            JSONObject  dataJsonR = JSONObject.fromObject(data);
		            
		            	log.info("���Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ�������óɹ�");
		            	wechatUsermapper.insertApiLogs("�������Ƴ�������ѽӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "success", "������:"+dataJsonR.getString("out_trade_no")+";���:"+dataJsonR.getInt("order_receivable"));

		            	Map<String, Object>priceMap=new HashMap<String, Object>();
		            	priceMap.put("out_trade_no", dataJsonR.getString("order_number"));
	            		priceMap.put("price",dataJsonR.getInt("fee"));
	            		priceMap.put("order_id",dataJsonR.getString("order_id"));
	            		priceMap.put("veh_plate",dataJsonR.getString("veh_plate"));
	            		priceMap.put("order_entry_time",dataJsonR.getString("order_entry_time"));
	            		priceMap.put("veh_plate_color_txt",dataJsonR.getString("veh_plate_color_txt"));
	            		priceMap.put("car_type",dataJsonR.getString("car_type"));
	            		List<storeCouponRecordDto> dtoList=new ArrayList<storeCouponRecordDto>();
	            		priceMap.put("coupon_list", dtoList);
	            		return priceMap;
		            }else{
		            	log.info("�������Ƴ�"+noVehiclePlate.getVehiclePlate()+"��ȡ��������ʧ��");
		            	wechatUsermapper.insertApiLogs("�������Ƴ�������ѽӿ�", "�û�id:"+noVehiclePlate.getUserOpenId()+";ͣ�������:"+noVehiclePlate.getParkingCode()+";���ƺ�:"+noVehiclePlate.getVehiclePlate(), "fail", dataJson.getString("error_msg"));

		            	throw new BaseServiceException(
		            			Integer.parseInt(errorCode),
		            			dataJson.getString("error_msg"));
		            }
		           
		            
			    }else{
			    	throw new BaseServiceException(
							StatusCode.API_FREQUENTLY_ERROR.getCode(),
							"��鿴�������Ƴ�������ѽӿ�");
			    }
				}finally {
					postMethod.releaseConnection();
				}
				
				
			}*/
	
	

	

	
}
