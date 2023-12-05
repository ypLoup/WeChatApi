package com.WeChatApi.service.wechatApiService;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import javax.transaction.Transactional;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
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

import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.dto.getCashUrl;
import com.WeChatApi.bean.dto.prePayDto;
import com.WeChatApi.bean.dto.prePayDto2;
import com.WeChatApi.bean.dto.rechargeDto;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.dto.userChargeRecordDto;
import com.WeChatApi.bean.dto.userRechargeRecordDto;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.redPack;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.Arithmetic;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.CommonUtil;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.controller.base.WXUtils;
import com.WeChatApi.controller.base.WxDecodeUtil;
import com.WeChatApi.dao.businessMapper;
import com.WeChatApi.dao.operationOrderMapper;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.service.operationOrderService.operationOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;


@Service
@Transactional
public class wechatApiService {
	
	@Autowired
	private wechatUserMapper wechatUsermapper;
	
	@Autowired
	private operationOrderMapper operationOrdermapper;
	
	@Autowired
	private businessMapper businessmapper;
	
	@Autowired
	private static ResourceBundle res = ResourceBundle.getBundle("rccApi");
	
	private static String mchId= res.getString("wechat_mch_id");
	private static String AppSecret= res.getString("wechat_AppSecret");
	private static String Appid= res.getString("wechat_appid");
	private static String mchName= res.getString("wechat_mch_name");
	//private static String p12Url= res.getString("p12Url");
	
	//private String AppSecret="42da2ad4c7bfad7524559abb47267973";
	
	//private String Appid="wx606d7fcc6d1402c9";
	
	private static Logger log = Logger.getLogger(redPack.class.getName());
	
	
	/**
	 * С�����¼�ӿڵ�ַ
	 */
	private String loginUrl="https://api.weixin.qq.com/sns/jscode2session";

	public String wechatLogin(String jsCode,String ivData,String encryptedData) throws Exception {
		String phoneNumber="";
		if(StringUtils.isBlank(jsCode)){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"΢��С����jscode����Ϊ�գ�");
		}
		GetMethod getMethod = null;
		try{
		getMethod = new GetMethod(loginUrl+"?appid="+Appid+"&secret="+AppSecret+"&js_code="+jsCode) ;
		
        HttpClient client = new HttpClient(new HttpClientParams(),new SimpleHttpConnectionManager(true) );  
	    int response = client.executeMethod(getMethod); // POST
	    
	    if(response==200){
	    	String errorCode="0";
	    	JSONObject  dataJson = JSONObject.fromObject(getMethod.getResponseBodyAsString());
            
            if(dataJson.has("errcode")) {
            	errorCode = dataJson.getString("errcode");
            	if(errorCode.equals("40029")){
                	throw new BaseServiceException(
        					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
        					"΢��С����jscode��Ч��");
                }else if(errorCode.equals("-1")){
                	throw new BaseServiceException(
        					StatusCode.SYSTEM_ERROR.getCode(),
        					"΢��С����ϵͳ��æ����ʱ�뿪�����Ժ�����");
                }else if(errorCode.equals("40163")){
                	throw new BaseServiceException(
        					StatusCode.SYSTEM_ERROR.getCode(),
        					"΢��С����code����Ч������������");
                }
            }else{
            	wechatUser user=new wechatUser();
            	String openId=dataJson.getString("openid");
            	wechatUser haveUser= wechatUsermapper.findWechatUserInfoByOpenId(openId);
            	if(haveUser!=null&&StringUtils.isBlank(haveUser.getUserMobile())){
            		List<String> ids = new ArrayList<>();
            		ids.add(haveUser.getUserId().toString());
            		wechatUsermapper.deleteBatch(ids);
            	}
            	String sessionKey=dataJson.getString("session_key");
            	if(StringUtils.isNotBlank(encryptedData)&&StringUtils.isNotBlank(ivData)){
            		phoneNumber =WXUtils.decrypt(Appid,encryptedData,sessionKey,ivData);
            		user.setUserMobile(phoneNumber);
            	}
            	

            	log.info("phone,openId��"+phoneNumber+""+openId);
            	user.setUserOpenId(openId);
            	wechatUser haveUser2= wechatUsermapper.findWechatUserInfoByOpenId(openId);
            	wechatUser num =wechatUsermapper.findUserInfoByPhoneNum(phoneNumber);
            	//wechatUser num =wechatUsermapper.findWechatUserInfoCountByPhone(phoneNumber,openId);
            	if(num!=null){
            		user.setUserId(num.getUserId());
            		wechatUsermapper.updateWechatUserInfo(user);
            		return openId;
            	}
            	if(haveUser2!=null){
            		//wechatUsermapper.updateWechatUserInfoByOpenId(openId);
            		user.setUserId(haveUser2.getUserId());
            		wechatUsermapper.updateWechatUserInfo(user);
            		return openId;
            	}
            	
            	if(num==null&&haveUser2==null){
            		//log.info("insertWeChatUserInfo��"+JSON.toJSONString(user)+";num����"+JSON.toJSONString(num)+"haveUser����"+JSON.toJSONString(haveUser2)+"openId"+openId+"phoneNumber"+phoneNumber);
            		wechatUser insertUser= wechatUsermapper.findWechatUserInfoByOpenId(openId);
            		if(insertUser==null){
            			//log.info("insertWeChatUserInfo��ʼ��"+JSON.toJSONString(user));
            			long iszengsong =wechatUsermapper.findIsZengsong();
            			//long isUserId =wechatUsermapper.findWechatUserPointRecordByUserId(user.getUserId());
            			if(iszengsong==1){
            				user.setPoint(500);
            				wechatUsermapper.addWechatUserInfo(user);
            				//wechatUsermapper.addWechatUserPointRecord(user.getUserId());
            				userChargeRecordDto dto = new userChargeRecordDto();
            				dto.setR_user_id(user.getUserId());
            				dto.setR_type(6);
            				dto.setR_discount_id(0);
            				dto.setR_balances(0);
            				dto.setR_point(500);
            				dto.setR_status(1);
            				businessmapper.insertChargeRecord_addWechatUserPoint(dto);
            			}else{
            				wechatUsermapper.addWechatUserInfo(user);
            			}
            			
            		}
            		
            	}
            	
            	return openId;
            }    
            	
	    }else{
	    	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					"С�����¼�ӿڵ���ʧ��");
	    }
		}finally {
			getMethod.releaseConnection();
		}
		return jsCode;
		

	}

	public Map<String, Object> wechatSendredpack(redPack redpack, HttpServletRequest request) {
		
		try{

            //���ںŵ�appid
            //String appid = "wx606d7fcc6d1402c9";
            /**
             * ����APPID��ȡaccess_token
             * �ҵ�access_token������һ����ʱ����ÿ������Сʱˢ��һ��access_token��ֵ��
             * ���ұ�����redis���У���Ҫ����Ļ������ԣ���
             */
            //String access_token = RedisUtil.getJustOneMapValueFromRedis(jedisPool,appid,"access_token");
            //����˭�����û���openid
            String openid = redpack.getRe_openid();
            //�����ֵ�����100��
            Integer redValue = redpack.getTotal_amount();
            //��ʼ���ͺ��
            //logger.info("++++++++++++++��ʼ���ͺ��++++++++++++++++++");
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            /** ��ǰʱ�� yyyyMMddHHmmss */
            String currTime = CommonUtil.getCurrTime();
            /** 8λ���� */
            String strTime = currTime.substring(8, currTime.length());
            /** ��λ����� */
            String strRandom = CommonUtil.buildRandom(4) + "";
            //�̻�������
            parameters.put("mch_billno",strTime + strRandom);
            /** �̻��� */
            //String mch_id = "1604629952";
            parameters.put("mch_id", mchId);
            /** ����ַ��� */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            /** ���ں�APPID */
            parameters.put("wxappid", Appid);
            /** �̻����� */
            //String mch_name = "���ƳǷ���˾";
            parameters.put("send_name",mchName);
            /** �û�openid */
            parameters.put("re_openid",openid);
            /** ������ */
            parameters.put("total_amount",redValue);
            /** ������������� */
            parameters.put("total_num",1);
            /** ���ף���� */
            parameters.put("wishing",redpack.getWishing());
            /** ���ýӿڵĻ���Ip��ַ */
            parameters.put("client_ip",request.getRemoteAddr());
            /** ����� */
            String activityName = "����������������������";
            parameters.put("act_name",redpack.getAct_name());
            /** ��ע */
            parameters.put("remark","�ֽ�����");
            parameters.put("notify_way","MINI_PROGRAM_JSAPI");
            /** ����id  ���ź��ʹ�ó��������������200ʱ�ش�
             * PRODUCT_1:��Ʒ���� PRODUCT_2:�齱 PRODUCT_4:��ҵ�ڲ�����  PRODUCT_5:�������� */
            //parameters.put("scene_id","PRODUCT_2");
            /** �ʽ���Ȩ�̻��� */
            //parameters.put("consume_mch_id","");
            /** ���Ϣ  �ʽ���Ȩ�̻��ţ�����������Լ�̻�����ʱʹ��*/
            //parameters.put("risk_info","");
            /** MD5����ǩ��������ΪUTF-8���룬ע�����漸���������ƵĴ�Сд */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
            String requestJsonStr = new ObjectMapper().writeValueAsString(parameters);
            //logger.info("���͵���Ϣ��"+requestJsonStr);
            parameters.put("sign", sign);//
            /** ����xml�ṹ�����ݣ�����ͳһ�µ��ӿڵ����� */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * ��ȡ֤��
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,String> result = new HashMap<String,String>();
            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//���������ǵ�֤��ĵ�ַ�����������linux��������/usr����
                //String pathname="D:\\home\\xiuzhou\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //�˴�Ϊ֤�����ŵľ���·��
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
            	log.info("��ȡ֤����Ϣ��ʱ�����쳣�쳣��Ϣ�ǣ�"+e.getMessage());
                e.printStackTrace();
            }
            try {
                //String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
            	String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendminiprogramhb";
                HttpPost httpPost = new HttpPost(requestUrl);
                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                // ��������
                reqEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(reqEntity);
                log.info("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // ��request��ȡ��������
                        InputStream inputStream = entity.getContent();
                        // ��ȡ������
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(inputStream);
                        // �õ�xml��Ԫ��
                        Element root = document.getRootElement();
                        // �õ���Ԫ�ص������ӽڵ�
                        List<Element> elementList = root.elements();
                        // ���������ӽڵ�
                        for (Element e : elementList)
                        {
                            result.put(e.getName(), e.getText());
                        }
                        // �ͷ���Դ
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
            log.info("------------------���ͺ������---------------");
            log.info("���ͺ��΢�ŷ��ص���Ϣ�ǣ�"+new ObjectMapper().writeValueAsString(result));
            //���緢�ͳɹ��Ļ������淢�͵���Ϣ
            if(result.get("return_msg").equals("���ųɹ�")) {
            	log.info("������ųɹ�openid="+openid+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	Map<String, Object> map =CommonUtil.generateSignature_redpack(result);
                return map;
                }
            else {
            	log.info("�������ʧ�ܣ�openid="+openid+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	return null;
            }
        }
        catch (Exception e){
        	log.info("���ͺ���쳣���쳣��Ϣ�ǣ�"+e.getMessage());
        	return null;
        }

		
	}

	public Map<String, Object> getPrepayId(prePayDto prepaydto, HttpServletRequest request) throws IOException {
		
		try{

            //���ںŵ�appid
            //String appid = "wx606d7fcc6d1402c9";
            String openid = prepaydto.getUserOpenId();
            //��ʼ���ͺ��
            //logger.info("++++++++++++++��ʼ���ͺ��++++++++++++++++++");
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            /** ��ǰʱ�� yyyyMMddHHmmss */
            String currTime = CommonUtil.getCurrTime();
            /** 8λ���� */
            String strTime = currTime.substring(8, currTime.length());
            /** ��λ����� */
            String strRandom = CommonUtil.buildRandom(4) + "";
            //�̻�������
            //parameters.put("mch_billno",strTime + strRandom);
            
            
            /** ���ں�APPID */
            parameters.put("appid", Appid);
            parameters.put("body", "ͣ������");
            /** �̻��� */
            //String mch_id = "1604629952";
            parameters.put("mch_id", mchId);
            /** ����ַ��� */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/wechatApi/payCallback");
            parameters.put("out_trade_no", prepaydto.getOutTradeNo());
            parameters.put("openid", openid);
            parameters.put("spbill_create_ip", request.getRemoteAddr());
            parameters.put("total_fee", prepaydto.getOrderReceivable());
            parameters.put("trade_type", "JSAPI");
            //parameters.put("attach", prepaydto.getCouponRecordId());//�Ż�ȯrecordId
            
            /** MD5����ǩ��������ΪUTF-8���룬ע�����漸���������ƵĴ�Сд */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
            String requestJsonStr = new ObjectMapper().writeValueAsString(parameters);
            //logger.info("���͵���Ϣ��"+requestJsonStr);
            parameters.put("sign", sign);//
            /** ����xml�ṹ�����ݣ�����ͳһ�µ��ӿڵ����� */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * ��ȡ֤��
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,String> result = new HashMap<String,String>();
           try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//���������ǵ�֤��ĵ�ַ�����������linux��������/usr����
                //String pathname="D:\\home\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //�˴�Ϊ֤�����ŵľ���·��
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
            	log.info("��ȡ֤����Ϣ��ʱ�����쳣�쳣��Ϣ�ǣ�"+e.getMessage());
                e.printStackTrace();
            }
            try {
                String requestUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
                HttpPost httpPost = new HttpPost(requestUrl);
                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                // ��������
                reqEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(reqEntity);
                log.info("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // ��request��ȡ��������
                        InputStream inputStream = entity.getContent();
                        // ��ȡ������
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(inputStream);
                        // �õ�xml��Ԫ��
                        Element root = document.getRootElement();
                        // �õ���Ԫ�ص������ӽڵ�
                        List<Element> elementList = root.elements();
                        // ���������ӽڵ�
                        for (Element e : elementList)
                        {
                            result.put(e.getName(), e.getText());
                        }
                        // �ͷ���Դ
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
            //log.info("------------------���ͺ������---------------");
            log.info("����ȡPrepayId��"+new ObjectMapper().writeValueAsString(result));
            String refundjson=new ObjectMapper().writeValueAsString(prepaydto);//�ؼ�
            //log.info("����ȡPrepayId��"+CommonUtil.generateSignature(result));
            //���緢�ͳɹ��Ļ������淢�͵���Ϣ
            if(result.get("result_code").equals("SUCCESS")) {
            	Map<String, Object> map =CommonUtil.generateSignature(result);
            	wechatUsermapper.insertApiLogs("ͳһ�µ��ӿ�", refundjson, "success", new ObjectMapper().writeValueAsString(result));
            	//log.info("��ȡPrepayId="+openid+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	map.put("price", prepaydto.getOrderReceivable());
                return map;
                }
            else {
            	log.info("��ȡPrepayId="+openid+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	wechatUsermapper.insertApiLogs("ͳһ�µ��ӿ�", refundjson, "fail", new ObjectMapper().writeValueAsString(result));
            	throw new BaseServiceException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"ͳһ�µ��ӿڻ�ȡʧ�ܣ���������ϵ����Ա��"+"["+result.get("err_code_des")+"]");
            }
        }
        catch (Exception e){
        	log.info("ͳһ�µ��ӿ��쳣���쳣��Ϣ�ǣ�"+e.getMessage());
        	throw new BaseServiceException(
					StatusCode.SYSTEM_ERROR.getCode(),
					e.getMessage());
        }
	
	
	}
	
	
	
	
	
public Map<String, Object> getPrepayId_new(prePayDto2 prepaydto, HttpServletRequest request) throws IOException {
		
		try{

            //���ںŵ�appid
            //String appid = "wx606d7fcc6d1402c9";
            String openid = prepaydto.getUserOpenId();
            //��ʼ���ͺ��
            //logger.info("++++++++++++++��ʼ���ͺ��++++++++++++++++++");
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            /** ��ǰʱ�� yyyyMMddHHmmss */
            String currTime = CommonUtil.getCurrTime();
            /** 8λ���� */
            String strTime = currTime.substring(8, currTime.length());
            /** ��λ����� */
            String strRandom = CommonUtil.buildRandom(4) + "";
            //�̻�������
            //parameters.put("mch_billno",strTime + strRandom);
            
            
            /** ���ں�APPID */
            parameters.put("appid", Appid);
            parameters.put("body", "ͣ������");
            /** �̻��� */
            //String mch_id = "1604629952";
            parameters.put("mch_id", mchId);
            /** ����ַ��� */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/wechatApi/payCallback");
            if(prepaydto.getOutTradeNo().contains("J")){
            	log.info("��˳������"+prepaydto.getOutTradeNo());
            	String splita [] =prepaydto.getOutTradeNo().split("_");
        		String pklCode = splita[0].toString();
        		int max=10000;
            	int min=1000;
            	Random random = new Random();
            	String randomNum =  (random.nextInt(max - min) + min + 1)+"";
        		parameters.put("out_trade_no", pklCode+"_"+prepaydto.getOrderId()+"_"+randomNum);
        		
            }else{
            	 parameters.put("out_trade_no", prepaydto.getOutTradeNo());
            }
           
            parameters.put("openid", openid);
            parameters.put("spbill_create_ip", request.getRemoteAddr());
            parameters.put("total_fee", prepaydto.getOrderReceivable());
            parameters.put("trade_type", "JSAPI");
            if(StringUtils.isBlank(prepaydto.getCouponRecordId())){
            	parameters.put("attach", 0+"_"+prepaydto.getOrderId()+"_"+0);//�Ż�ȯrecordId
            }else{
            	parameters.put("attach", prepaydto.getCouponRecordId()+"_"+prepaydto.getOrderId()+"_"+prepaydto.getCouponType());//�Ż�ȯrecordId
            }
            
            //log.info("����֧�����Σ�"+JSON.toJSONString(parameters));
            
            
            /** MD5����ǩ��������ΪUTF-8���룬ע�����漸���������ƵĴ�Сд */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
            String requestJsonStr = new ObjectMapper().writeValueAsString(parameters);
            //logger.info("���͵���Ϣ��"+requestJsonStr);
            parameters.put("sign", sign);//
            log.info("����֧�����Σ�"+new ObjectMapper().writeValueAsString(parameters));
            /** ����xml�ṹ�����ݣ�����ͳһ�µ��ӿڵ����� */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * ��ȡ֤��
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,String> result = new HashMap<String,String>();
           try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//���������ǵ�֤��ĵ�ַ�����������linux��������/usr����
                //String pathname="D:\\home\\apiclient_cert.p12";
                //String pathname="D:\\home\\xiuzhou\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //�˴�Ϊ֤�����ŵľ���·��
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
            	log.info("��ȡ֤����Ϣ��ʱ�����쳣�쳣��Ϣ�ǣ�"+e.getMessage());
                e.printStackTrace();
            }
            try {
                String requestUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
                HttpPost httpPost = new HttpPost(requestUrl);
                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                // ��������
                reqEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(reqEntity);
                log.info("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // ��request��ȡ��������
                        InputStream inputStream = entity.getContent();
                        // ��ȡ������
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(inputStream);
                        // �õ�xml��Ԫ��
                        Element root = document.getRootElement();
                        // �õ���Ԫ�ص������ӽڵ�
                        List<Element> elementList = root.elements();
                        // ���������ӽڵ�
                        for (Element e : elementList)
                        {
                            result.put(e.getName(), e.getText());
                        }
                        // �ͷ���Դ
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
            //log.info("------------------���ͺ������---------------");
            log.info("����ȡPrepayId��"+new ObjectMapper().writeValueAsString(result));
            String refundjson=new ObjectMapper().writeValueAsString(prepaydto);//�ؼ�
            //log.info("����ȡPrepayId��"+CommonUtil.generateSignature(result));
            //���緢�ͳɹ��Ļ������淢�͵���Ϣ
            if(result.get("result_code").equals("SUCCESS")) {
            	Map<String, Object> map =CommonUtil.generateSignature(result);
            	wechatUsermapper.insertApiLogs("ͳһ�µ��ӿ�", refundjson, "success", new ObjectMapper().writeValueAsString(result));
            	//log.info("��ȡPrepayId="+openid+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	map.put("price", prepaydto.getOrderReceivable());
                return map;
                }
            else {
            	log.info("��ȡPrepayId="+openid+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	wechatUsermapper.insertApiLogs("ͳһ�µ��ӿ�", refundjson, "fail", new ObjectMapper().writeValueAsString(result));
            	throw new BaseServiceException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"ͳһ�µ��ӿڻ�ȡʧ�ܣ���������ϵ����Ա��"+"["+result.get("err_code_des")+"]");
            }
        }
        catch (Exception e){
        	log.info("ͳһ�µ��ӿ��쳣���쳣��Ϣ�ǣ�"+e.getMessage());
        	throw new BaseServiceException(
					StatusCode.SYSTEM_ERROR.getCode(),
					e.getMessage());
        }
	
	
	}
	
	
	
	
	
	
	
	/**
     * ����xml,���ص�һ��Ԫ�ؼ�ֵ�ԡ������һ��Ԫ�����ӽڵ㣬��˽ڵ��ֵ���ӽڵ��xml���ݡ�
     * @param strxml
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
	public Map<String,String> doXMLParse(String strxml) throws Exception {
        if(null == strxml || "".equals(strxml)) {
            return null;
        }

        Map<String,String> m = new HashMap<String,String>();
        InputStream in = String2Inputstream(strxml);
        SAXReader builder = new SAXReader();
        Document document = builder.read(in);
        Element root = document.getRootElement();
        List list = root.elements();
        Iterator it = list.iterator();
        while(it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.elements();
            if(children.isEmpty()) {
                v = e.getTextTrim();
            } else {
                v = getChildrenText(children);
            }

            m.put(k, v);
        }

        //�ر���
        in.close();

        return m;
    }

    private  InputStream String2Inputstream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    /**
     * ��ȡ�ӽ���xml
     * @param children
     * @return String
     */
    @SuppressWarnings("rawtypes")
    private static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if(!children.isEmpty()) {
            Iterator it = children.iterator();
            while(it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextTrim();
                List list = e.elements();
                sb.append("<" + name + ">");
                if(!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }
    
    
    public void insertApiLogs(String apiName,String apiKey,String apiResult,String apiRemarks){
    	wechatUsermapper.insertApiLogs(apiName,apiKey,apiResult,apiRemarks);
    }

	public String refundByWechat(refundDto dto) {
		
		try{

            
            //String appid = "wx606d7fcc6d1402c9";
            
            //String openid = prepaydto.getUserOpenId();
            
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            /** ��ǰʱ�� yyyyMMddHHmmss */
            String currTime = CommonUtil.getCurrTime();
            /** 8λ���� */
            String strTime = currTime.substring(8, currTime.length());
            /** ��λ����� */
            String strRandom = CommonUtil.buildRandom(4) + "";
           
            /** ���ں�APPID */
            parameters.put("appid", Appid);
            //parameters.put("body", "ͣ������");
            /** �̻��� */
            //String mch_id = "1604629952";
            parameters.put("mch_id", mchId);
            /** ����ַ��� */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/wechatApi/refundCallback");
            parameters.put("out_refund_no", dto.getOutRefundNo());
            parameters.put("transaction_id", dto.getTransactionId());

            parameters.put("total_fee", dto.getTotalFee());
            parameters.put("refund_fee", dto.getRefundFee());
            parameters.put("refund_desc", dto.getRefundReason());
            
            /** MD5����ǩ��������ΪUTF-8���룬ע�����漸���������ƵĴ�Сд */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
            String requestJsonStr = new ObjectMapper().writeValueAsString(parameters);

            parameters.put("sign", sign);//
            /** ����xml�ṹ�����ݣ�����ͳһ�µ��ӿڵ����� */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * ��ȡ֤��
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,String> result = new HashMap<String,String>();
           try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//���������ǵ�֤��ĵ�ַ�����������linux��������/usr����
                //String pathname="D:\\home\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //�˴�Ϊ֤�����ŵľ���·��
                try {
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
                        .build();
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
            	log.info("��ȡ֤����Ϣ��ʱ�����쳣�쳣��Ϣ�ǣ�"+e.getMessage());
                e.printStackTrace();
            }
            try {
                String requestUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
                HttpPost httpPost = new HttpPost(requestUrl);
                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                // ��������
                reqEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(reqEntity);
                log.info("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // ��request��ȡ��������
                        InputStream inputStream = entity.getContent();
                        // ��ȡ������
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(inputStream);
                        // �õ�xml��Ԫ��
                        Element root = document.getRootElement();
                        // �õ���Ԫ�ص������ӽڵ�
                        List<Element> elementList = root.elements();
                        // ���������ӽڵ�
                        for (Element e : elementList)
                        {
                            result.put(e.getName(), e.getText());
                        }
                        // �ͷ���Դ
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
            String refundjson=new ObjectMapper().writeValueAsString(dto);//�ؼ�
            log.info("�˿�JSON"+new ObjectMapper().writeValueAsString(result));

            if(result.get("result_code").equals("SUCCESS")) {

            	wechatUsermapper.insertApiLogs("΢���˿�ӿ�", refundjson, "success", new ObjectMapper().writeValueAsString(result));

                return result.get("result_code");
                }
            else {
            	wechatUsermapper.insertApiLogs("ͳһ�µ��ӿ�", refundjson, "fail", new ObjectMapper().writeValueAsString(result));
            	throw new BaseServiceException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"΢���˿�ӿڻ�ȡʧ�ܣ���������ϵ����Ա��"+"["+result.get("err_code_des")+"]");
            }
        }
        catch (Exception e){
        	log.info("΢���˿�ӿ��쳣���쳣��Ϣ�ǣ�"+e.getMessage());
        	throw new BaseServiceException(
					StatusCode.SYSTEM_ERROR.getCode(),
					e.getMessage());
        }
		
		
	}
	
	
	
/*public String refundByWechat_xz(refundDto dto) {
		
		try{

            
            //String appid = "wx606d7fcc6d1402c9";
            
            //String openid = prepaydto.getUserOpenId();
            
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            *//** ��ǰʱ�� yyyyMMddHHmmss *//*
            String currTime = CommonUtil.getCurrTime();
            *//** 8λ���� *//*
            String strTime = currTime.substring(8, currTime.length());
            *//** ��λ����� *//*
            String strRandom = CommonUtil.buildRandom(4) + "";
           
            *//** ���ں�APPID *//*
            parameters.put("appid", Appid);
            //parameters.put("body", "ͣ������");
            *//** �̻��� *//*
            //String mch_id = "1604629952";
            parameters.put("mch_id", mchId);
            *//** ����ַ��� *//*
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://api-app.xz.iparking.tech/mp/WeChatApi/wechatApi/refundCallback_xz");
            parameters.put("out_refund_no", dto.getOutRefundNo());
            parameters.put("transaction_id", dto.getTransactionId());

            parameters.put("total_fee", dto.getTotalFee());
            parameters.put("refund_fee", dto.getRefundFee());
            parameters.put("refund_desc", dto.getRefundReason());
            parameters.put("attach", dto.getOrderType());
            
            *//** MD5����ǩ��������ΪUTF-8���룬ע�����漸���������ƵĴ�Сд *//*
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
            String requestJsonStr = JSON.toJSONString(parameters);

            parameters.put("sign", sign);//
            *//** ����xml�ṹ�����ݣ�����ͳһ�µ��ӿڵ����� *//*
            String requestXML = CommonUtil.getRequestXml(parameters);
            *//**
             * ��ȡ֤��
             * 
             *//*
            CloseableHttpClient httpclient = null;
            Map<String,String> result = new HashMap<String,String>();
           try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//���������ǵ�֤��ĵ�ַ�����������linux��������/usr����
                //String pathname="D:\\home\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //�˴�Ϊ֤�����ŵľ���·��
                try {
                    keyStore.load(instream, mchId.toCharArray());
                } finally {
                    instream.close();
                }
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mchId.toCharArray())
                        .build();
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
            	log.info("��ȡ֤����Ϣ��ʱ�����쳣�쳣��Ϣ�ǣ�"+e.getMessage());
                e.printStackTrace();
            }
            try {
                String requestUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
                HttpPost httpPost = new HttpPost(requestUrl);
                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                // ��������
                reqEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(reqEntity);
                log.info("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // ��request��ȡ��������
                        InputStream inputStream = entity.getContent();
                        // ��ȡ������
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(inputStream);
                        // �õ�xml��Ԫ��
                        Element root = document.getRootElement();
                        // �õ���Ԫ�ص������ӽڵ�
                        List<Element> elementList = root.elements();
                        // ���������ӽڵ�
                        for (Element e : elementList)
                        {
                            result.put(e.getName(), e.getText());
                        }
                        // �ͷ���Դ
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
            String refundjson=JSON.toJSONString(dto);//�ؼ�
            log.info("�˿�JSON"+JSON.toJSONString(result));

            if(result.get("result_code").equals("SUCCESS")) {

            	wechatUsermapper.insertApiLogs("΢���˿�ӿ�", refundjson, "success", JSON.toJSONString(result));

                return result.get("result_code");
                }
            else {
            	wechatUsermapper.insertApiLogs("ͳһ�µ��ӿ�", refundjson, "fail", JSON.toJSONString(result));
            	throw new BaseServiceException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"΢���˿�ӿڻ�ȡʧ�ܣ���������ϵ����Ա��"+"["+result.get("err_code_des")+"]");
            }
        }
        catch (Exception e){
        	log.info("΢���˿�ӿ��쳣���쳣��Ϣ�ǣ�"+e.getMessage());
        	throw new BaseServiceException(
					StatusCode.SYSTEM_ERROR.getCode(),
					e.getMessage());
        }
		
		
	}*/

	public String getWechatCashUrl(getCashUrl dto) {
		
		if(StringUtils.isBlank(dto.getOrderno_atm())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"������Ų���Ϊ��");
		}
		
		if(StringUtils.isBlank(dto.getParkpotid())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"ͣ������Ų���Ϊ��");
		}
		
		
		
		if(StringUtils.isBlank(dto.getExtbusinessid())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�ֽ��������Ų���Ϊ��");
		}
		if(dto.getPaymenttotal()==null){
			throw new BaseServiceException(
					StatusCode.DATA_NOT_EXISTS.getCode(),
					"Ӧ�ɷ��ܽ���Ϊ��");
		}
		
		if(dto.getPayment()==null){
			throw new BaseServiceException(
					StatusCode.DATA_NOT_EXISTS.getCode(),
					"�ֽ��������Ϊ��");
		}
		
		int cashMent=dto.getPayment()-dto.getPaymenttotal();
		if(cashMent>0){//getCashUrl
			
			String outTradeNo=dto.getOrderno_atm();
			String[] outTradeNos=outTradeNo.split("_");
			String pakCode=outTradeNos[0];
			String orderNumber=outTradeNos[2];
			//String vehplate=dto.getVehPlate();
			operationOrderCondition condition = new operationOrderCondition();
			condition.setPkl_code(pakCode);
			condition.setOrder_number(orderNumber);
			//condition.setVeh_plate(vehplate);
			condition.setOrder_status(1);
			List<operationOrder>list = operationOrdermapper.findOrderInfo(condition);
			if(list.size()!=1){
				throw new BaseServiceException(
						StatusCode.DATA_NOT_MATCH.getCode(),
						"�ñʶ����������⣬���ʵ");
			}
			
			//findUserOpenId
			String vehplate=list.get(0).getVeh_plate();
			/*wechatUser userInfo = wechatUsermapper.findUserInfoByVehplate(vehplate);
			
			if(userInfo==null){
				throw new BaseServiceException(
						StatusCode.DATA_NOT_EXISTS.getCode(),
						"�ó���û����פ΢��С�����޷��������ʵ");
			}
			
			if(StringUtils.isBlank(userInfo.getUserOpenId())){
				throw new BaseServiceException(
						StatusCode.DATA_NOT_EXISTS.getCode(),
						"�ó���û����פ΢��С�����޷��������ʵ");
			}*/
			
			String path="https://jiashan.iparking.tech/change?sign=";
			//String openid_cashMent_vehplate_outTradeNo_orderNoAtm_orderNumber_totalMent_payMent=userInfo.getUserOpenId()+"@"+cashMent+"@"+vehplate+"@"+outTradeNo+"@"+dto.getExtbusinessid()+"@"+orderNumber+"@"+dto.getPaymenttotal()+"@"+dto.getPayment();
			String userId_cashMent_orderId_totalMent_payMent="99"+"@"+cashMent+"@"+list.get(0).getOrder_id()+"@"+dto.getPaymenttotal()+"@"+dto.getPayment();
			log.info("����ǰ++++++++++++++"+userId_cashMent_orderId_totalMent_payMent);
			Arithmetic des = new Arithmetic();// ʵ����һ������
			des.getKey("jsy,2016.");// �����ܳ�
			String sign = des.getEncString(userId_cashMent_orderId_totalMent_payMent);// �����ַ���,����String������
			String[] datas=userId_cashMent_orderId_totalMent_payMent.split("@");
			
			operationOrderCondition orderCondition = new operationOrderCondition();
        	orderCondition.setOrder_id(Integer.valueOf(datas[2].toString()));
        	List<operationOrder> order = operationOrdermapper.findOrderInfo(orderCondition);
        	String[] outTradeNos_Order=order.get(0).getOut_trade_no().split("_");
			String pakCode_Order=outTradeNos_Order[0];
		    String doubleRed=Double.valueOf(datas[3].toString())/100+"";
			Double redValueDouble=Double.valueOf(doubleRed);
			log.info("����������뵽pay"+order.get(0).getOut_trade_no());
			operationOrdermapper.insertOperationPay(order.get(0).getOut_trade_no(),outTradeNos_Order[2].toString(),pakCode_Order,order.get(0).getVeh_plate().toString(),order.get(0).getOut_trade_no(),order.get(0).getOut_trade_no(),datas[3].toString(),redValueDouble.toString(),"10","�ֽ�","�����ɷ�","�ֽ�֧��",datas[4].toString(),datas[1].toString());
			return path+sign;
		}else{
			return null;
		}

	}

	public Map<String, Object> redpack(Map<String, Object> map, HttpServletRequest request) {
		
		
		
		
		try{
            String signData=map.get("sign").toString();
            if(StringUtils.isBlank(signData)){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"�����ǩ������,���ʵ��");
            }
            Arithmetic des = new Arithmetic();// ʵ����һ������
            des.getKey("jsy,2016.");// �����ܳ�
           
            
            String userId_cashMent_orderId_totalMent_payMent= des.getDesString(signData);
            String[] datas=userId_cashMent_orderId_totalMent_payMent.split("@");
            Map<String, Object> redmap= new HashMap<>();
            redmap.put("orderNum", datas[2].toString());
           
            String wechatJson = "";
            wechatJson=operationOrdermapper.findRedPackByOrderNumber( datas[2].toString());
            if(StringUtils.isNotBlank(wechatJson)){
            	Map<String, Object> redmapResult=this.findRedpackStatus(redmap, request);
                if(redmapResult.containsKey("status")) {
                	if(redmapResult.get("status").equals("RECEIVED")){
                		long isHave=operationOrdermapper.findRedPackCountByOrderNumber(datas[2].toString());
                        
                        if(isHave!=0){
                        	throw new BaseServiceException(
            						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
            						"�ö����ֽ����ѷ��ųɹ�����ȡ�������ظ����ţ�");
                        }
                	}else if(redmapResult.get("status").equals("SENDING")){
                		throw new BaseServiceException(
        						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
        						"��������У�");
                		
                	}else if(redmapResult.get("status").equals("SENT")){
                		throw new BaseServiceException(
        						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
        						"����ѷ��ͣ�");
                		
                	}else if(redmapResult.get("status").equals("FAILED")){
                		throw new BaseServiceException(
        						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
        						"�������ʧ�ܣ�����ϵ����Ա��");
                		
                	}else if(redmapResult.get("status").equals("RFUND_ING")){
                		throw new BaseServiceException(
        						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
        						"����˿��У�");
                		
                	}else if(redmapResult.get("status").equals("REFUND")){
                		throw new BaseServiceException(
        						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
        						"������˿");
                		
                	}
                }else{
                	throw new BaseServiceException(
    						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
    						"��������У�");
                }
            }
            
            //���ݶ����Ų�ѯ������Ϣ
            List<wechatUser>  user=wechatUsermapper.findUserInfoByOrderNum(datas[2].toString());
            
            if(user.size()==0){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���ȹ�עС���򡾼���ͣ�����󶨳��ƺ���ȡ");
            }
            
            
            
            Integer redValue = Integer.valueOf(datas[1]);
            //��ʼ���ͺ��
            //logger.info("++++++++++++++��ʼ���ͺ��++++++++++++++++++");
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            /** ��ǰʱ�� yyyyMMddHHmmss */
            String currTime = CommonUtil.getCurrTime();
            /** 8λ���� */
            String strTime = currTime.substring(8, currTime.length());
            /** ��λ����� */
            String strRandom = CommonUtil.buildRandom(4) + "";
            //�̻�������
            String mch_billno=strTime + strRandom;
            String mchBillno=datas[2].toString().replaceAll("_", "")+mch_billno;
            parameters.put("mch_billno",mchBillno);
            /** �̻��� */
            parameters.put("mch_id", mchId);
            /** ����ַ��� */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            /** ���ں�APPID */
            parameters.put("wxappid", Appid);
            /** �̻����� */
           
            parameters.put("send_name",mchName);
            /** �û�openid */
            parameters.put("re_openid",user.get(0).getUserOpenId());
            /** ������ */
            parameters.put("total_amount",redValue);
            /** ������������� */
            parameters.put("total_num",1);
            /** ���ף���� */
            parameters.put("wishing","�ֽ����㡾"+user.get(0).getUserId()+"��");
            /** ���ýӿڵĻ���Ip��ַ */
            ResourceBundle res = ResourceBundle.getBundle("white_ip_list");
    	    String whiteIp = res.getString("whiteIp");
            parameters.put("client_ip",whiteIp);
            /** ����� */
            
            parameters.put("act_name","�ֽ����㡾"+user.get(0).getUserId()+"��");
            /** ��ע */
            parameters.put("remark","�ֽ�����");
            parameters.put("notify_way","MINI_PROGRAM_JSAPI");
            
            /** MD5����ǩ��������ΪUTF-8���룬ע�����漸���������ƵĴ�Сд */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);

            parameters.put("sign", sign);//
            log.info("parameters��"+parameters);
            /** ����xml�ṹ�����ݣ�����ͳһ�µ��ӿڵ����� */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * ��ȡ֤��
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,String> result = new HashMap<String,String>();
            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//���������ǵ�֤��ĵ�ַ�����������linux��������/usr����
                //String pathname="D:\\home\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //�˴�Ϊ֤�����ŵľ���·��
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
            	log.info("��ȡ֤����Ϣ��ʱ�����쳣�쳣��Ϣ�ǣ�"+e.getMessage());
                e.printStackTrace();
            }
            try {
                //String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
            	String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendminiprogramhb";
                HttpPost httpPost = new HttpPost(requestUrl);
                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                // ��������
                reqEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(reqEntity);
                log.info("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // ��request��ȡ��������
                        InputStream inputStream = entity.getContent();
                        // ��ȡ������
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(inputStream);
                        // �õ�xml��Ԫ��
                        Element root = document.getRootElement();
                        // �õ���Ԫ�ص������ӽڵ�
                        List<Element> elementList = root.elements();
                        // ���������ӽڵ�
                        for (Element e : elementList)
                        {
                            result.put(e.getName(), e.getText());
                        }
                        // �ͷ���Դ
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
	            log.info("------------------���ͺ������---------------");
	            log.info("���ͺ��΢�ŷ��ص���Ϣ�ǣ�"+new ObjectMapper().writeValueAsString(result));
	            Map<String, Object> dataMap= new HashMap<String, Object>();//openid_cashMent_vehplate_outTradeNo_orderNoAtm
	        	dataMap.put("userOpenId", user.get(0).getUserOpenId());
	        	dataMap.put("cashMent", datas[1].toString());
	        	dataMap.put("oderId", datas[2].toString());
	        	dataMap.put("totalMent", datas[3].toString());
	        	dataMap.put("payMent", datas[4].toString());
            if(result.get("return_msg").equals("���ųɹ�")) {
            	log.info("������ųɹ�openid="+user.get(0).getUserOpenId()+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	Map<String, Object> map1 =CommonUtil.generateSignature_redpack(result);
            	operationOrderCondition orderCondition = new operationOrderCondition();
            	orderCondition.setOrder_id(Integer.valueOf(datas[2].toString()));
            	List<operationOrder> order = operationOrdermapper.findOrderInfo(orderCondition);
            	String[] outTradeNos=order.get(0).getOut_trade_no().split("_");
    			String pakCode=outTradeNos[0];
    		    String doubleRed=Double.valueOf(datas[3].toString())/100+"";
    			Double redValueDouble=Double.valueOf(doubleRed);
    			/*operationOrdermapper.insertOperationPay(order.get(0).getOut_trade_no(),outTradeNos[2].toString(),pakCode,order.get(0).getVeh_plate().toString(),mch_billno,mch_billno,datas[3].toString(),redValueDouble.toString(),"10","�ֽ�","�����ɷ�","�ֽ�֧��",datas[4].toString(),datas[1].toString());*/
    			log.info("����������µ�pay"+order.get(0).getOut_trade_no());
    			operationOrdermapper.updateOperationPayInfoByOutTradeNo(mch_billno, datas[1].toString(), order.get(0).getOut_trade_no());
            	wechatUsermapper.insertRedpackRecord(user.get(0).getUserId().toString(),user.get(0).getUserOpenId(),redValue+"",datas[2].toString(),new ObjectMapper().writeValueAsString(dataMap),new ObjectMapper().writeValueAsString(result),new ObjectMapper().writeValueAsString(map1),"1","1","1");
                return map1;
                }
            else {
            	log.info("�������ʧ�ܣ�openid="+user.get(0).getUserOpenId()+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	wechatUsermapper.insertRedpackRecord(user.get(0).getUserId().toString(),user.get(0).getUserOpenId(),redValue+"",datas[2].toString(),new ObjectMapper().writeValueAsString(dataMap),new ObjectMapper().writeValueAsString(result),"fail","1","0","1");
            	return null;
            }
        }
        catch (Exception e){
        	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					e.getMessage());
        	
        }

	}

	public void cashOut_wechat(Map<String, Object> map, HttpServletRequest request) {
		
		try{
            String userOpenId=map.get("userOpenId").toString();
            if(StringUtils.isBlank(userOpenId)){
            	throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"С����openId����Ϊ�գ�");
            }
            
            int balance = Integer.valueOf(map.get("balance").toString());
            
            wechatUser  user = wechatUsermapper.findWechatUserInfoByOpenId(userOpenId);
            
            if(userOpenId.equals(user.getUserOpenId())==false){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���û���΢��С�����û������ʵ��");
            }
            
            if(user.getBalances()!=balance&&balance!=0&&balance>=1){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���ֽ����ʵ�ʽ�ƥ�䣬���ʵ��");
            }
            
            List<Map<String, Object>> refundList=new ArrayList<>();
            refundList=this.getRefundList(user,balance);
            for(Map<String, Object> refundmap :refundList){
            	
            	userChargeRecordDto userchargerecord = new userChargeRecordDto();
            	userchargerecord.setR_user_id(user.getUserId());
            	userchargerecord.setR_type(2);
            	userchargerecord.setR_balances(Integer.valueOf(refundmap.get("refundFee").toString()));
				userchargerecord.setR_point(0);
				userchargerecord.setR_discount_id(0);
				userchargerecord.setR_out_trade_no(refundmap.get("outTradeNo").toString());
				userchargerecord.setR_left_balances(0);
				userchargerecord.setR_left_point(0);
                int recordId=businessmapper.insertChargeRecord_cashOut(userchargerecord);

                SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
                /** ��ǰʱ�� yyyyMMddHHmmss */
                String currTime = CommonUtil.getCurrTime();
                /** 8λ���� */
                String strTime = currTime.substring(8, currTime.length());
                /** ��λ����� */
                String strRandom = CommonUtil.buildRandom(4) + "";
                //�̻�������
                String mch_billno=strTime + strRandom;
                /** ���ں�APPID */
                parameters.put("appid", Appid);
               
                /** �̻��� */
                parameters.put("mch_id", mchId);
                /** ����ַ��� */
                parameters.put("nonce_str", CommonUtil.getNonceStr());
                /**֧��������**/
                parameters.put("out_trade_no",refundmap.get("outTradeNo").toString());
                /** �̻��˿�� */
                parameters.put("out_refund_no","TK"+refundmap.get("outTradeNo").toString());
                /** ������� */
                parameters.put("total_fee",Integer.valueOf(refundmap.get("totalFee").toString()));
                /** �˿��� */
                parameters.put("refund_fee",Integer.valueOf(refundmap.get("refundFee").toString()));
                /** �˿���֪ͨurl */
                parameters.put("notify_url","https://jiashan.iparking.tech/mp/WeChatApi/wechatApi/cashOutCallback");
                /** �˿�ԭ�� */
                parameters.put("refund_desc","�û�:"+user.getUserId()+"����"+"_"+userchargerecord.getR_id());
                //parameters.put("attach",userchargerecord.getR_id());//��ֵ��¼��Id
                
                /** MD5����ǩ��������ΪUTF-8���룬ע�����漸���������ƵĴ�Сд */
                String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
                String sign = CommonUtil.createSign("UTF-8", parameters,api_key);

                parameters.put("sign", sign);//
                /** ����xml�ṹ�����ݣ�����ͳһ�µ��ӿڵ����� */
                String requestXML = CommonUtil.getRequestXml(parameters);
                /**
                 * ��ȡ֤��
                 * 
                 */
                CloseableHttpClient httpclient = null;
                Map<String,String> result = new HashMap<String,String>();
                try {
                    KeyStore keyStore = KeyStore.getInstance("PKCS12");
                    String pathname = "/tomcat/apiclient_cert.p12";//���������ǵ�֤��ĵ�ַ�����������linux��������/usr����
                    //String pathname="D:\\home\\apiclient_cert.p12";
                    FileInputStream instream = new FileInputStream(new File(pathname)); //�˴�Ϊ֤�����ŵľ���·��
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
                	log.info("��ȡ֤����Ϣ��ʱ�����쳣�쳣��Ϣ�ǣ�"+e.getMessage());
                    e.printStackTrace();
                }
                try {
                    //String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
                	//String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
                	String requestUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
                    HttpPost httpPost = new HttpPost(requestUrl);
                    StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                    // ��������
                    reqEntity.setContentType("application/x-www-form-urlencoded");
                    httpPost.setEntity(reqEntity);
                    log.info("executing request" + httpPost.getRequestLine());
                    CloseableHttpResponse response = httpclient.execute(httpPost);
                    try {
                        HttpEntity entity = response.getEntity();
                        System.out.println(response.getStatusLine());
                        if (entity != null) {
                            // ��request��ȡ��������
                            InputStream inputStream = entity.getContent();
                            // ��ȡ������
                            SAXReader reader = new SAXReader();
                            Document document = reader.read(inputStream);
                            // �õ�xml��Ԫ��
                            Element root = document.getRootElement();
                            // �õ���Ԫ�ص������ӽڵ�
                            List<Element> elementList = root.elements();
                            // ���������ӽڵ�
                            for (Element e : elementList)
                            {
                                result.put(e.getName(), e.getText());
                            }
                            // �ͷ���Դ
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
    	            log.info("------------------�������ֽ���---------------");
    	            log.info("����΢�����ַ��ص���Ϣ�ǣ�"+new ObjectMapper().writeValueAsString(result));
    	           
                if(result.get("result_code").equals("SUCCESS")) {
                	log.info("΢�����ֳɹ�openid="+userOpenId+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
                	wechatUsermapper.insertRedpackRecord(user.getUserId().toString(),userOpenId,refundmap.get("refundFee").toString()+"",refundmap.get("outTradeNo").toString(),new ObjectMapper().writeValueAsString(parameters),new ObjectMapper().writeValueAsString(result),null,"2","1","1");
                	
                	wechatUsermapper.updateWechatUserBalances(user.getUserId().toString());
                	
                    
                    }
                else {
                	//log.info("�������ʧ�ܣ�openid="+userOpenId+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
                	wechatUsermapper.insertRedpackRecord(user.getUserId().toString(),userOpenId,refundmap.get("refundFee").toString()+"",refundmap.get("outTradeNo").toString(),new ObjectMapper().writeValueAsString(parameters),new ObjectMapper().writeValueAsString(result),"fail","2","0","1");
                	throw new BaseServiceException(
        					StatusCode.API_FREQUENTLY_ERROR.getCode(),
        					result.get("return_msg")+","+result.get("err_code_des"));
                	
                }
            }
          
            
        }
        catch (Exception e){
        	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					e.getMessage());
        	
        }
		
	}

	private List<Map<String, Object>> getRefundList(wechatUser user, int balance) {
		List<userRechargeRecordDto> recordList = businessmapper.findUseRechargeRecordByUserId(user.getUserId().toString());
		if(recordList.size()==0){
			throw new BaseServiceException(
					StatusCode.DATA_NOT_EXISTS.getCode(),
					"���û�û�з����˿�Ķ�����¼��");
		}
		int sumBalance =0;
		int needRefundFee=balance;
		List<Map<String, Object>> refundList= new ArrayList<>();
		//Map<String, Object> refund= new HashMap<>();
		for(int i = 0;i<recordList.size();i++){
			Map<String, Object> refund= new HashMap<>();
			if(balance>sumBalance){
				
				refund.put("outTradeNo", recordList.get(i).getOutTradeNo());
				refund.put("totalFee", recordList.get(i).getBalances());
				sumBalance+=Integer.valueOf(recordList.get(i).getBalances());
				
				if(needRefundFee>Integer.valueOf(recordList.get(i).getBalances())){
					refund.put("refundFee", needRefundFee-(needRefundFee-Integer.valueOf(recordList.get(i).getBalances())));
				}else{
					refund.put("refundFee", needRefundFee);
				}
				
				
				needRefundFee=balance-sumBalance;
			}else{
				break;
			}
			if(refund!=null){
				refundList.add(refund);
			}
			
		}
		return refundList;
	}

	public Map<String, Object> findRedpackStatus(Map<String, Object> map, HttpServletRequest request) {
		try{
            String orderNum=map.get("orderNum").toString();
            if(StringUtils.isBlank(orderNum)){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"�����Ų���Ϊ�գ�");
            }
           
            String wechatJson = "";
            wechatJson=operationOrdermapper.findRedPackByOrderNumber(orderNum);
            
            //long isHave=operationOrdermapper.findRedPackCountByOrderNumber(datas[2].toString());
            
            if(StringUtils.isBlank(wechatJson)){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"�ö����ֽ����ѷ��ż�¼�����ڣ�");
            }
            JSONObject wechatJson_ = JSONObject.fromObject(wechatJson);
            //���ںŵ�appid
            //String appid = "wx606d7fcc6d1402c9";
           
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
          
            String mchBillno=wechatJson_.getString("mch_billno");
            parameters.put("mch_billno",mchBillno);
            /** �̻��� */
            parameters.put("mch_id", mchId);
            /** ����ַ��� */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            /** ���ں�APPID */
            parameters.put("appid", Appid);
           
            parameters.put("bill_type","MCHT");
     
            /** MD5����ǩ��������ΪUTF-8���룬ע�����漸���������ƵĴ�Сд */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);

            parameters.put("sign", sign);//
            log.info("parameters��"+parameters);
            /** ����xml�ṹ�����ݣ�����ͳһ�µ��ӿڵ����� */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * ��ȡ֤��
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,Object> result = new HashMap<String,Object>();
            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//���������ǵ�֤��ĵ�ַ�����������linux��������/usr����
                //String pathname="D:\\home\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //�˴�Ϊ֤�����ŵľ���·��
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
            	log.info("��ȡ֤����Ϣ��ʱ�����쳣�쳣��Ϣ�ǣ�"+e.getMessage());
                e.printStackTrace();
            }
            try {
                //String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
            	String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo";
                HttpPost httpPost = new HttpPost(requestUrl);
                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                // ��������
                reqEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(reqEntity);
                log.info("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // ��request��ȡ��������
                        InputStream inputStream = entity.getContent();
                        // ��ȡ������
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(inputStream);
                        // �õ�xml��Ԫ��
                        Element root = document.getRootElement();
                        // �õ���Ԫ�ص������ӽڵ�
                        List<Element> elementList = root.elements();
                        // ���������ӽڵ�
                        for (Element e : elementList)
                        {
                            result.put(e.getName(), e.getText());
                        }
                        // �ͷ���Դ
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
	            log.info("------------------��ѯ�������---------------");
	            log.info("��ѯ���΢�ŷ��ص���Ϣ�ǣ�"+new ObjectMapper().writeValueAsString(result));
	            Map<String, Object> dataMap= new HashMap<String, Object>();//openid_cashMent_vehplate_outTradeNo_orderNoAtm
	        	
	        	 if(result.get("result_code").equals("SUCCESS")) {
            	
            	//Map<String, Object> map1 =CommonUtil.generateSignature_redpack(result);
            	
                return result;
                }
            else {
            	
            	return null;
            }
        }
        catch (Exception e){
        	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					e.getMessage());
        	
        }
	}

	public Map<String, Object> roadPayByWechat(Map<String, Object> wechatCodeMap, HttpServletRequest request) {
		try{

            //���ںŵ�appid
            //String appid = "wx606d7fcc6d1402c9";
            String openid = wechatCodeMap.get("userOpenid").toString();
            //��ʼ���ͺ��
            //logger.info("++++++++++++++��ʼ���ͺ��++++++++++++++++++");
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            /** ��ǰʱ�� yyyyMMddHHmmss */
            String currTime = CommonUtil.getCurrTime();
            /** 8λ���� */
            String strTime = currTime.substring(8, currTime.length());
            /** ��λ����� */
            String strRandom = CommonUtil.buildRandom(4) + "";
            //�̻�������
            //parameters.put("mch_billno",strTime + strRandom);
            
            
            /** ���ں�APPID */
            parameters.put("appid", Appid);
            parameters.put("body", "·��ͣ������");
            /** �̻��� */
            parameters.put("mch_id", mchId);
            /** ����ַ��� */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/wechatApi/roadPayCallback");
            parameters.put("out_trade_no", wechatCodeMap.get("bizId").toString());
            parameters.put("openid", openid);
            parameters.put("spbill_create_ip", request.getRemoteAddr());
            parameters.put("total_fee", wechatCodeMap.get("sumBizAmount").toString());
            parameters.put("trade_type", "JSAPI");
            if(StringUtils.isNotBlank(wechatCodeMap.get("orderIds").toString())){
            	parameters.put("attach", wechatCodeMap.get("orderIds").toString());//Ƿ��id
            }else{
            	parameters.put("attach", 0);//��Ƿ��
            }
            
            System.out.println(parameters);
            
            
            /** MD5����ǩ��������ΪUTF-8���룬ע�����漸���������ƵĴ�Сд */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);
            String requestJsonStr = new ObjectMapper().writeValueAsString(parameters);
            //logger.info("���͵���Ϣ��"+requestJsonStr);
            parameters.put("sign", sign);//
            /** ����xml�ṹ�����ݣ�����ͳһ�µ��ӿڵ����� */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * ��ȡ֤��
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,String> result = new HashMap<String,String>();
           try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//���������ǵ�֤��ĵ�ַ�����������linux��������/usr����
                //String pathname="D:\\home\\apiclient_cert.p12";
                //String pathname="D:\\home\\xiuzhou\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //�˴�Ϊ֤�����ŵľ���·��
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
            	log.info("��ȡ֤����Ϣ��ʱ�����쳣�쳣��Ϣ�ǣ�"+e.getMessage());
                e.printStackTrace();
            }
            try {
                String requestUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
                HttpPost httpPost = new HttpPost(requestUrl);
                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                // ��������
                reqEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(reqEntity);
                log.info("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // ��request��ȡ��������
                        InputStream inputStream = entity.getContent();
                        // ��ȡ������
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(inputStream);
                        // �õ�xml��Ԫ��
                        Element root = document.getRootElement();
                        // �õ���Ԫ�ص������ӽڵ�
                        List<Element> elementList = root.elements();
                        // ���������ӽڵ�
                        for (Element e : elementList)
                        {
                            result.put(e.getName(), e.getText());
                        }
                        // �ͷ���Դ
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
            //log.info("------------------���ͺ������---------------");
            log.info("����ȡPrepayId��"+new ObjectMapper().writeValueAsString(result));
            String refundjson=new ObjectMapper().writeValueAsString(wechatCodeMap);//�ؼ�
            //log.info("����ȡPrepayId��"+CommonUtil.generateSignature(result));
            //���緢�ͳɹ��Ļ������淢�͵���Ϣ
            if(result.get("result_code").equals("SUCCESS")) {
            	Map<String, Object> map =CommonUtil.generateSignature(result);
            	wechatUsermapper.insertApiLogs("·�߽ɷ�ͳһ�µ��ӿ�", refundjson, "success", new ObjectMapper().writeValueAsString(result));
            	//log.info("��ȡPrepayId="+openid+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	//map.put("price", prepaydto.getOrderReceivable());
                return map;
                }
            else {
            	log.info("��ȡPrepayId="+openid+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	wechatUsermapper.insertApiLogs("·�߽ɷ�ͳһ�µ��ӿ�", refundjson, "fail", new ObjectMapper().writeValueAsString(result));
            	throw new BaseServiceException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"ͳһ�µ��ӿڻ�ȡʧ�ܣ���������ϵ����Ա��"+"["+result.get("err_code_des")+"]");
            }
        }
        catch (Exception e){
        	log.info("·�߽ɷ�ͳһ�µ��ӿڣ��쳣��Ϣ�ǣ�"+e.getMessage());
        	throw new BaseServiceException(
					StatusCode.SYSTEM_ERROR.getCode(),
					e.getMessage());
        }
	
	
	}

	public Map<String, Object> findRedpackStatus_web(Map<String, Object> map, HttpServletRequest request) {
		try{
            String orderNum=map.get("orderNum").toString();
            if(StringUtils.isBlank(orderNum)){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"�����Ų���Ϊ�գ�");
            }
           
            String wechatJson = "";
            wechatJson=operationOrdermapper.findRedPackByOrderNumber_web(orderNum);
            
            //long isHave=operationOrdermapper.findRedPackCountByOrderNumber(datas[2].toString());
            
            if(StringUtils.isBlank(wechatJson)){
            	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"�ö����ֽ����ѷ��ż�¼�����ڣ�");
            }
            JSONObject wechatJson_ = JSONObject.fromObject(wechatJson);
            //���ںŵ�appid
            //String appid = "wx606d7fcc6d1402c9";
           
            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
          
            String mchBillno=wechatJson_.getString("mch_billno");
            parameters.put("mch_billno",mchBillno);
            /** �̻��� */
            parameters.put("mch_id", mchId);
            /** ����ַ��� */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            /** ���ں�APPID */
            parameters.put("appid", Appid);
           
            parameters.put("bill_type","MCHT");
     
            /** MD5����ǩ��������ΪUTF-8���룬ע�����漸���������ƵĴ�Сд */
            String api_key = "jgkj2020jgkj2020jgkj2020jgkj2020";
            String sign = CommonUtil.createSign("UTF-8", parameters,api_key);

            parameters.put("sign", sign);//
            log.info("parameters��"+parameters);
            /** ����xml�ṹ�����ݣ�����ͳһ�µ��ӿڵ����� */
            String requestXML = CommonUtil.getRequestXml(parameters);
            /**
             * ��ȡ֤��
             * 
             */
            CloseableHttpClient httpclient = null;
            Map<String,Object> result = new HashMap<String,Object>();
            try {
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                String pathname = "/tomcat/apiclient_cert.p12";//���������ǵ�֤��ĵ�ַ�����������linux��������/usr����
                //String pathname="D:\\home\\apiclient_cert.p12";
                FileInputStream instream = new FileInputStream(new File(pathname)); //�˴�Ϊ֤�����ŵľ���·��
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
            	log.info("��ȡ֤����Ϣ��ʱ�����쳣�쳣��Ϣ�ǣ�"+e.getMessage());
                e.printStackTrace();
            }
            try {
                //String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
            	String requestUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo";
                HttpPost httpPost = new HttpPost(requestUrl);
                StringEntity reqEntity  = new StringEntity(requestXML, "utf-8");
                // ��������
                reqEntity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(reqEntity);
                log.info("executing request" + httpPost.getRequestLine());
                CloseableHttpResponse response = httpclient.execute(httpPost);
                try {
                    HttpEntity entity = response.getEntity();
                    System.out.println(response.getStatusLine());
                    if (entity != null) {
                        // ��request��ȡ��������
                        InputStream inputStream = entity.getContent();
                        // ��ȡ������
                        SAXReader reader = new SAXReader();
                        Document document = reader.read(inputStream);
                        // �õ�xml��Ԫ��
                        Element root = document.getRootElement();
                        // �õ���Ԫ�ص������ӽڵ�
                        List<Element> elementList = root.elements();
                        // ���������ӽڵ�
                        for (Element e : elementList)
                        {
                            result.put(e.getName(), e.getText());
                        }
                        // �ͷ���Դ
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
	            log.info("------------------��ѯ�������---------------");
	            log.info("��ѯ���΢�ŷ��ص���Ϣ�ǣ�"+new ObjectMapper().writeValueAsString(result));
	            Map<String, Object> dataMap= new HashMap<String, Object>();//openid_cashMent_vehplate_outTradeNo_orderNoAtm
	        	
	        	 if(result.get("result_code").equals("SUCCESS")) {
            	
            	//Map<String, Object> map1 =CommonUtil.generateSignature_redpack(result);
            	
                return result;
                }
            else {
            	
            	return null;
            }
        }
        catch (Exception e){
        	throw new BaseServiceException(
					StatusCode.API_FREQUENTLY_ERROR.getCode(),
					e.getMessage());
        	
        }
	}
    
	





}
