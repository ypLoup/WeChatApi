package com.WeChatApi.service.rccApiService;



import net.sf.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.dto.roadPklPayCode;
import com.WeChatApi.bean.dto.userChargeRecordDto;
import com.WeChatApi.bean.models.UnifiedOrderRequestBean;
import com.WeChatApi.bean.models.UnitePayRefundRequestBean;
import com.WeChatApi.bean.models.WXOrderCreateGetRequestBean;
import com.WeChatApi.bean.models.WXRefundGetRequestBean;
import com.WeChatApi.bean.models.noVehiclePlate;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.roadParkinglotsPay;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.controller.base.test;
import com.WeChatApi.dao.businessMapper;
import com.WeChatApi.dao.operationOrderMapper;
import com.WeChatApi.dao.parkinglotsMapper;
import com.WeChatApi.dao.roadParkinglotsPayMapper;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.eagle.sdk.base.INetTools;
import cn.com.eagle.sdk.bean.NetReqConfBean;
import cn.com.eagle.sdk.bean.OipReqBean;
import cn.com.eagle.sdk.bean.OipRspBean;
import cn.com.eagle.sdk.net.factory.NetToolsHttpFactory;






@Service
@Transactional
public class rccApiService {
	
	private static Logger logger = LoggerFactory.getLogger(rccApiService.class);
	@Autowired
	private operationOrderMapper orderMapper;
	@Autowired
	private parkinglotsMapper parkingMapper;
	@Autowired
	private roadParkinglotsPayMapper roadpaymapper;
	
	@Autowired
	private wechatApiService wechatApiservice;
	
	@Autowired
	private businessMapper businessmapper;
	
	@Autowired
	private  blueCardService bluecardservice;
	
	@Autowired
	private wechatUserMapper wechatUsermapper;

	public String getJuHeCode(Map<String, Object> juHeCodeMap) throws ParseException, IOException {
		String orderIds=juHeCodeMap.get("orderIds").toString();
		String pklCode=juHeCodeMap.get("pklCode").toString();
		String wechat_pay_code="";//΢���̻���
		if(StringUtils.isNotBlank(orderIds)){//��Ƿ��
			/*String orderId[]=orderIds.split(",");
			String sumBizAmount=orderMapper.findRoadOperationOrderAmount(Arrays.asList(orderId));
			juHeCodeMap.put("sumBizAmount", sumBizAmount);
			logger.info("-΢��С����Ƿ�Ѷ�������-run--"+juHeCodeMap);
			
			int int_bizAmount=Integer.valueOf(juHeCodeMap.get("bizAmount").toString());
			String bizAmount=int_bizAmount+"";
			if(sumBizAmount.equals(bizAmount)==false){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�ɷѽ����ʵ��Ƿ�ѽ�һ�£����ʵ��");
			}*/
			logger.info("-΢��С����Ƿ�Ѷ�������-run--"+juHeCodeMap);
			if( orderIds.contains(",")==true){
				wechat_pay_code="872752322417163";//�ϲ�Ƿ��
			}else{//find road_plk_code
				roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pklCode);
				if(payCodeInfo==null){
					throw new BaseServiceException(
							StatusCode.CREDITAMOUNT_ERROR.getCode(),
							"�á�"+pklCode+"��ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
				}
				wechat_pay_code=payCodeInfo.getFour_pay_code();
			}
			
		}else{//�����ɷ�
			//System.out.println(pklCode);
			roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pklCode);
			if(payCodeInfo==null){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�á�"+pklCode+"��ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
			}
			wechat_pay_code=payCodeInfo.getFour_pay_code();
			
		}
		juHeCodeMap.put("wechat_pay_code", wechat_pay_code);
		
		logger.info("-��ɨ����ۺ�-��������-run--��"+juHeCodeMap);
		
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		
		if(StringUtils.isBlank(juHeCodeMap.get("bizId").toString())||StringUtils.isBlank(juHeCodeMap.get("bizAmount").toString())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"ȱ�ٱ����");
		}
		OipReqBean oipReqBean = new OipReqBean();
		
		// 1.��ȡͨѶ���Ӷ���
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.���ɹ��������Ķ���
		
		// ����Ӧ��id
		oipReqBean.setAppId(res.getString("appId_test"));
		
		// ���ÿ�����id
		oipReqBean.setDlpId(res.getString("dlpId_test"));
		// ���ò�Ʒid
		oipReqBean.setProdId(res.getString("prodId_test"));
		// ����api������
		oipReqBean.setMethod("zjrcuoip.pay.ord.pay");
		// ����api�汾
		oipReqBean.setVersion(res.getString("version_test"));
		// ����ʱ���
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp=sf.format(new Date());
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time1 = df2.parse(timestamp);
		String bizTime=df1.format(time1);
		oipReqBean.setTimestamp(timestamp);
		//oipReqBean.setTimestamp(juHeCodeMap.get("timestamp").toString());//2021-04-02 11:09:50
		// 3.�����������ö���,���������ַ/url����Ϣ
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// Ӧ��˽Կ
		netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
				// �����ַ
		netReqConfBean.setNetUrl(res.getString("netUrl_test"));
		// �����߹�Կ
		netReqConfBean.setPublicKey(res.getString("publicKey_test"));
		// 4.��������ҵ���Ķ���
		//���²�����Ϊ������Ǳ���������սӿ��ĵ�������ʵ�ʳ�����������
		UnifiedOrderRequestBean unifiedOrderRequestBean = new UnifiedOrderRequestBean();
		unifiedOrderRequestBean.setAppTp("03");//Ӧ�����ͣ�01-���Զ�WEB���룬02-POS���룬03-�ƶ�Ӧ�ý��룬04-�ƶ���H5����
		unifiedOrderRequestBean.setPurpPrtry("A6004011");//ҵ������ A6004011
		unifiedOrderRequestBean.setMerchId(wechat_pay_code);//�̻���
		unifiedOrderRequestBean.setTradeType("NATIVE");//NATIVE-ɨ��֧��
		unifiedOrderRequestBean.setBizId(juHeCodeMap.get("bizId").toString());//ҵ����ˮ�� �̻�ϵͳ�ڲ������ţ�ֻ�������֡���Сд��ĸ���Ҳ����ظ���
		unifiedOrderRequestBean.setBizTime(bizTime);//����ʱ��-20210402102911
		unifiedOrderRequestBean.setBizAmount(juHeCodeMap.get("bizAmount").toString());//���׽��-1.00
		unifiedOrderRequestBean.setOrdTitle("�ۺ��벴λ�ɷ�["+pklCode+"]");//��������
		unifiedOrderRequestBean.setDeviceNo("1121112");//�豸��
		unifiedOrderRequestBean.setAttach(juHeCodeMap.get("attach").toString());
        
		
		
		// 5.����ҵ����json����
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(unifiedOrderRequestBean));
		// ͨѶ��ȡ���Ķ���
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// ��Ӧ�ɹ�
		if (oipRspBean.isSuccess()) {
			
			String bizJson = oipRspBean.getBizContent();
			logger.info("������{}����Ӧ�룺{}����ǩ�����{}��΢��codeUrl:{}",juHeCodeMap,oipRspBean.getCode(),oipRspBean.isSignValid(),bizJson);
			if (StringUtils.isNotBlank(bizJson)) {
				Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				System.out.println(bizData.get("codeUrl"));
				return bizData.get("codeUrl").toString();
			}
		}else{
			throw new BaseServiceException(
					Integer.valueOf(oipRspBean.getBizCode()),
					oipRspBean.getBizMsg());
		}
		return null;
		
		

	}

	public Map getWechatCode(Map<String, Object> wechatCodeMap) throws JsonParseException, JsonMappingException, IOException {
		
		String orderIds=wechatCodeMap.get("orderIds").toString();
		
		String pklCode=wechatCodeMap.get("pklCode").toString();
		
		if(StringUtils.isNotBlank(orderIds)){
			String orderId[]=orderIds.split(",");
			String sumBizAmount=orderMapper.findRoadOperationOrderAmount(Arrays.asList(orderId));
			wechatCodeMap.put("sumBizAmount", sumBizAmount);
			logger.info("-΢��С����Ƿ�Ѷ�������-run--"+wechatCodeMap);
			if(sumBizAmount.equals(wechatCodeMap.get("bizAmount").toString())==false){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�ɷѽ����ʵ��Ƿ�ѽ�һ�£����ʵ��");
			}
			
		}
		/*String wechat_pay_code="";
		if( pklCode.contains(",")==true){
			wechat_pay_code="";
		}else{//find road_plk_code
			//wechat_pay_code=
		}*/
		
		logger.info("-΢��С���򶩵�����-run--"+wechatCodeMap);
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.��ȡͨѶ���Ӷ���
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.���ɹ��������Ķ���
		OipReqBean oipReqBean = new OipReqBean();
		// ����Ӧ��id
		oipReqBean.setAppId(res.getString("appId_test"));
				// ���ÿ�����id
		oipReqBean.setDlpId(res.getString("dlpId_test"));
				// ���ò�Ʒid
		oipReqBean.setProdId(res.getString("wechat_prodId_test"));
		// ����api������
		oipReqBean.setMethod("weixin.payment.create.order");
		// ����api�汾
		oipReqBean.setVersion(res.getString("version_test"));
		// ����ʱ���
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		oipReqBean.setTimestamp(sf.format(new Date()));
		// 3.�����������ö���,���������ַ/url����Ϣ
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// Ӧ��˽Կ
	
				netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
						// �����ַ
				netReqConfBean.setNetUrl(res.getString("netUrl_test"));
				// �����߹�Կ
				netReqConfBean.setPublicKey(res.getString("publicKey_test"));
		// 4.��������ҵ���Ķ���
		WXOrderCreateGetRequestBean WXorderCreateGetRequestBean = new WXOrderCreateGetRequestBean();

		WXorderCreateGetRequestBean.setAppTp("03");
		WXorderCreateGetRequestBean.setTxTp("20");
		WXorderCreateGetRequestBean.setPurpPrtry("A6004004");
		WXorderCreateGetRequestBean.setMerchId(res.getString("bussines_test"));
		WXorderCreateGetRequestBean.setBizId(wechatCodeMap.get("bizId").toString());
		WXorderCreateGetRequestBean.setPayCtAmount(wechatCodeMap.get("bizAmount").toString());
		WXorderCreateGetRequestBean.setProductDesc("С����λ�ɷ�");
		WXorderCreateGetRequestBean.setChannelType("WEIXIN");
		WXorderCreateGetRequestBean.setDeviceInfo("64689789765468");
		WXorderCreateGetRequestBean.setUserIp("158.220.10.102");
		WXorderCreateGetRequestBean.setUserMac("158.220.1.1");
		WXorderCreateGetRequestBean.setTradeType("JSAPI");
		WXorderCreateGetRequestBean.setSubOpenid(wechatCodeMap.get("userOpenid").toString());
		WXorderCreateGetRequestBean.setSubAppid(res.getString("wechat_appid"));
		WXorderCreateGetRequestBean.setAttach(wechatCodeMap.get("orderIds").toString());

		// 6.����ҵ����json����
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(WXorderCreateGetRequestBean));
		// ͨѶ��ȡ���Ķ���
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// ��Ӧ�ɹ�
		if (oipRspBean.isSuccess()) {
			
			String bizJson = oipRspBean.getBizContent();
			logger.info("������{}����Ӧ�룺{}����ǩ�����{}��΢��codeUrl:{}",wechatCodeMap,oipRspBean.getCode(),oipRspBean.isSignValid(),bizJson);
			if (StringUtils.isNotBlank(bizJson)) {
				Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				//bizData.get("wcPayData").toString();
				Map wxData = new ObjectMapper().readValue(bizData.get("wcPayData").toString(), Map.class);
				System.out.println(wxData);
				return wxData;
			}
		}else{
			throw new BaseServiceException(
					Integer.valueOf(oipRspBean.getBizCode()),
					oipRspBean.getBizMsg());
		}
		return null;
		
	
	}
	
	
public Map getWechatCode_new(Map<String, Object> wechatCodeMap) throws JsonParseException, JsonMappingException, IOException {
		
		String orderIds=wechatCodeMap.get("orderIds").toString();
		
		String pklCode=wechatCodeMap.get("pklCode").toString();
		String wechat_pay_code="";//΢���̻���
		if(StringUtils.isNotBlank(orderIds)){//��Ƿ��
			String orderId[]=orderIds.split(",");
			String sumBizAmount=orderMapper.findRoadOperationOrderAmount(Arrays.asList(orderId));
			wechatCodeMap.put("sumBizAmount", sumBizAmount);
			logger.info("-΢��С����Ƿ�Ѷ�������-run--"+wechatCodeMap);
			double a = Double.parseDouble(wechatCodeMap.get("bizAmount").toString());
			
			int int_bizAmount=(int) (a*100);
			String bizAmount=int_bizAmount+"";
			//int int_bizAmount=Integer.valueOf(wechatCodeMap.get("bizAmount").toString())*100;
			//String bizAmount=int_bizAmount+"";
			if(sumBizAmount.equals(bizAmount)==false){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�ɷѽ����ʵ��Ƿ�ѽ�һ�£����ʵ��");
			}
			if( orderIds.contains(",")==true){
				wechat_pay_code="872752322417164";//�ϲ�Ƿ��
			}else{//find road_plk_code
				roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pklCode);
				if(payCodeInfo==null){
					throw new BaseServiceException(
							StatusCode.CREDITAMOUNT_ERROR.getCode(),
							"�á�"+pklCode+"��ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
				}
				wechat_pay_code=payCodeInfo.getWechat_pay_code();
			}
			
		}else{//�����ɷ�
			//System.out.println(pklCode);
			roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pklCode);
			if(payCodeInfo==null){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�á�"+pklCode+"��ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
			}
			wechat_pay_code=payCodeInfo.getWechat_pay_code();
			
		}
		wechatCodeMap.put("wechat_pay_code", wechat_pay_code);
		
		
		logger.info("-΢��С���򶩵�����-run--"+wechatCodeMap);
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.��ȡͨѶ���Ӷ���
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.���ɹ��������Ķ���
		OipReqBean oipReqBean = new OipReqBean();
		// ����Ӧ��id
		oipReqBean.setAppId(res.getString("appId_test"));
		//oipReqBean.setAppId("1");
				// ���ÿ�����id
		oipReqBean.setDlpId(res.getString("dlpId_test"));
				// ���ò�Ʒid
		oipReqBean.setProdId(res.getString("wechat_prodId_test"));
		
		// ����api������
		oipReqBean.setMethod("weixin.payment.create.order");
		// ����api�汾
		oipReqBean.setVersion("1.0.0");
		// ����ʱ���
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		oipReqBean.setTimestamp(sf.format(new Date()));
		// 3.�����������ö���,���������ַ/url����Ϣ
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// Ӧ��˽Կ
	
				netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
						// �����ַ
				netReqConfBean.setNetUrl(res.getString("netUrl_test"));
				// �����߹�Կ
				netReqConfBean.setPublicKey(res.getString("publicKey_test"));
		// 4.��������ҵ���Ķ���
		WXOrderCreateGetRequestBean WXorderCreateGetRequestBean = new WXOrderCreateGetRequestBean();

		WXorderCreateGetRequestBean.setAppTp("03");
		WXorderCreateGetRequestBean.setTxTp("20");
		WXorderCreateGetRequestBean.setPurpPrtry("A6004004");
		WXorderCreateGetRequestBean.setMerchId(wechat_pay_code);
		WXorderCreateGetRequestBean.setBizId(wechatCodeMap.get("bizId").toString());
		if(wechatCodeMap.get("bizAmount").toString().contains(".")){
			WXorderCreateGetRequestBean.setPayCtAmount(wechatCodeMap.get("bizAmount").toString());
		}else{
			WXorderCreateGetRequestBean.setPayCtAmount(wechatCodeMap.get("bizAmount").toString()+".00");
		}
		
		WXorderCreateGetRequestBean.setProductDesc("С����λ�ɷ�["+pklCode+"]");
		WXorderCreateGetRequestBean.setChannelType("WEIXIN");
		//WXorderCreateGetRequestBean.setFeeType("");
		WXorderCreateGetRequestBean.setDeviceInfo("64689789765468");
		WXorderCreateGetRequestBean.setUserIp("158.220.10.102");
		WXorderCreateGetRequestBean.setUserMac("158.220.1.1");
		WXorderCreateGetRequestBean.setTradeType("JSAPI");
		WXorderCreateGetRequestBean.setSubOpenid(wechatCodeMap.get("userOpenid").toString());
		WXorderCreateGetRequestBean.setSubAppid(res.getString("wechat_appid"));
		WXorderCreateGetRequestBean.setAttach(wechatCodeMap.get("orderIds").toString());
		
		JSONObject json=new JSONObject();
		json.put("Ӧ��id", res.getString("appId_test"));
		json.put("������id", res.getString("dlpId_test"));
		json.put("��Ʒid", res.getString("wechat_prodId_test"));
		json.put("Ӧ��˽Կ", res.getString("appPrivateKey_test"));
		json.put("�����ַ", res.getString("netUrl_test"));
		json.put("�����߹�Կ", res.getString("publicKey_test"));
		json.put("�̻���", wechat_pay_code);
		json.put("С����appid", res.getString("wechat_appid"));
		
		
		//System.out.println("ũ����Ҫ����"+json);
		
		//System.out.println(new ObjectMapper().writeValueAsString(WXorderCreateGetRequestBean));

		// 6.����ҵ����json����
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(WXorderCreateGetRequestBean));
		// ͨѶ��ȡ���Ķ���
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// ��Ӧ�ɹ�
		if (oipRspBean.isSuccess()) {
			
			String bizJson = oipRspBean.getBizContent();
			logger.info("������{}����Ӧ�룺{}����ǩ�����{}��΢��codeUrl:{}",wechatCodeMap,oipRspBean.getCode(),oipRspBean.isSignValid(),bizJson);
			if (StringUtils.isNotBlank(bizJson)) {
				/*Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				//bizData.get("wcPayData").toString();
				Map wxData = new ObjectMapper().readValue(bizData.get("wcPayData").toString(), Map.class);
				//System.out.println(wxData);
				return wxData;*/
				 ObjectMapper objectMapper = new ObjectMapper();

		            // ��JSON�ַ�������ΪMap
		            Map<String, Object> jsonMap = objectMapper.readValue(bizJson, Map.class);

		            // ��Map�л�ȡ "wcPayData" ��ֵ
		            Map<String, Object> wcPayDataMap = (Map<String, Object>) jsonMap.get("wcPayData");
		            return wcPayDataMap;
			}
		}else{
			throw new BaseServiceException(
					Integer.valueOf(oipRspBean.getBizCode()),
					oipRspBean.getBizMsg());
		}
		return null;
		
	
	}

		public String JuHeRefundOrder(Map<String, Object> juHeCodeMap) throws ParseException, JsonParseException, JsonMappingException, IOException {
			roadParkinglotsPay pay=roadpaymapper.findRoadParkingLotsPayInfoByPayId(Integer.valueOf(juHeCodeMap.get("payId").toString()));
			roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pay.getPkl_code());
			if(payCodeInfo==null){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�á�"+pay.getPkl_code()+"��ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
			}
			logger.debug("-��ɨ����ۺ��˿�����-run--");
			ResourceBundle res = ResourceBundle.getBundle("rccApi");
			// 1.��ȡͨѶ���Ӷ���
			INetTools netTools = NetToolsHttpFactory.getHttpInstance();
			// 2.���ɹ��������Ķ���
			OipReqBean oipReqBean = new OipReqBean();
			oipReqBean.setAppId(res.getString("appId_test"));
			// ���ÿ�����id
			oipReqBean.setDlpId(res.getString("dlpId_test"));
			// ���ò�Ʒid
			oipReqBean.setProdId(res.getString("prodId_test"));
			// ����api������
			oipReqBean.setMethod("zjrcuoip.pay.refund.apply");
			// ����api�汾
			oipReqBean.setVersion(res.getString("version_test"));
			// ����ʱ���
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
			String timestamp=sf.format(new Date());
			DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time1 = df2.parse(timestamp);
			String bizTime=df1.format(time1);
			oipReqBean.setTimestamp(sf.format(new Date()));
			// 3.�����������ö���,���������ַ/url����Ϣ
			NetReqConfBean netReqConfBean = new NetReqConfBean();
			// Ӧ��˽Կ
			netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
					// �����ַ
			netReqConfBean.setNetUrl(res.getString("netUrl_test"));
			// �����߹�Կ
			netReqConfBean.setPublicKey(res.getString("publicKey_test"));// 4.��������ҵ���Ķ���
			UnitePayRefundRequestBean unitePayRefundRequestBean = new UnitePayRefundRequestBean();
			unitePayRefundRequestBean.setAppTp("03");
			unitePayRefundRequestBean.setAttach("");
			unitePayRefundRequestBean.setBizAmount(juHeCodeMap.get("bizAmount").toString());
			unitePayRefundRequestBean.setBizCurrency("CNY");
			String random=String.format("%04d",new Random().nextInt(9999));
			String outRefundNo="TK"+pay.getPay_number()+random;
			unitePayRefundRequestBean.setBizId(outRefundNo);
			//unitePayRefundRequestBean.setBizRemark("��ע��Ϣ");
			unitePayRefundRequestBean.setBizTime(bizTime);
			unitePayRefundRequestBean.setDeviceNo("111");
			if(pay.getPay_remark().contains("�ඩ��֧��")){
				unitePayRefundRequestBean.setMerchId("872752322417163");
			}else{
				unitePayRefundRequestBean.setMerchId(payCodeInfo.getFour_pay_code());
			}
			
			//unitePayRefundRequestBean.setOperatorId("");
			//unitePayRefundRequestBean.setOrderId("");
			unitePayRefundRequestBean.setPurpPrtry("A1207005");
			unitePayRefundRequestBean.setRefundBizid(pay.getPay_number());
			//unitePayRefundRequestBean.setOrderId("S1202105170028111160");
			unitePayRefundRequestBean.setRefundDesc(juHeCodeMap.get("refundDesc").toString());
			//unitePayRefundRequestBean.setStoreId("");
			logger.info("apiName:"+"�ۺ�����ɨ�˿�����"+"; param:"+new ObjectMapper().writeValueAsString(unitePayRefundRequestBean));

			oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(unitePayRefundRequestBean));

			OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
			logger.info("��Ӧ�룺{}����ǩ�����{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			/*if (StringUtils.isNotBlank(bizJson)) {
				Map bizData = JSON.parseObject(bizJson, Map.class);
				System.out.println(bizData);
			}*/
			if(oipRspBean.isSuccess()){
				refundDto dto = new refundDto();
				dto.setPayId(Integer.valueOf(juHeCodeMap.get("payId").toString()));
				dto.setOutRefundNo(outRefundNo);
				Float f = Float.parseFloat( juHeCodeMap.get("bizAmount").toString() ); 
				dto.setRefundFee((int)(f*100));
				dto.setRefundId(pay.getPay_number());
				dto.setRefundAdminId(Integer.valueOf(juHeCodeMap.get("refundAdminId").toString()));
				roadpaymapper.insertRoadPayRefund(dto);
				Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				return oipRspBean.getMsg();
			}else{
				throw new BaseServiceException(
						Integer.valueOf(oipRspBean.getBizCode()),
						oipRspBean.getBizMsg());
			}
		}

		public String WechatCodeRefundOrder(Map<String, Object> wechatCodeMap) throws ParseException, JsonProcessingException {
			// TODO Auto-generated method stub
			roadParkinglotsPay pay=roadpaymapper.findRoadParkingLotsPayInfoByPayId(Integer.valueOf(wechatCodeMap.get("payId").toString()));
			roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pay.getPkl_code());
			if(payCodeInfo==null){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�á�"+pay.getPkl_code()+"��ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
			}
			logger.debug("-΢��֧���˿�-run--");
			ResourceBundle res = ResourceBundle.getBundle("rccApi");
			// 1.��ȡͨѶ���Ӷ���
			INetTools netTools = NetToolsHttpFactory.getHttpInstance();
			// 2.���ɹ��������Ķ���
			OipReqBean oipReqBean = new OipReqBean();
			// ����Ӧ��id
			oipReqBean.setAppId(res.getString("appId_test"));
			// ���ÿ�����id
	        oipReqBean.setDlpId(res.getString("dlpId_test"));
			// ���ò�Ʒid
	        oipReqBean.setProdId(res.getString("wechat_prodId_test"));
			// ����api������
			oipReqBean.setMethod("weixin.payment.refund.execute");
			// ����api�汾
			oipReqBean.setVersion("1.0.0");
			// ����ʱ���
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
			String timestamp=sf.format(new Date());
			DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time1 = df2.parse(timestamp);
			String bizTime=df1.format(time1);
			oipReqBean.setTimestamp(sf.format(new Date()));
			// 3.�����������ö���,���������ַ/url����Ϣ
			NetReqConfBean netReqConfBean = new NetReqConfBean();
			// Ӧ��˽Կ
					netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
					// �����ַ
					netReqConfBean.setNetUrl(res.getString("netUrl_test"));
					// �����߹�Կ
					netReqConfBean.setPublicKey(res.getString("publicKey_test"));
					// 4.��������ҵ���Ķ���
			WXRefundGetRequestBean WXrefundGetRequestBean = new WXRefundGetRequestBean();
			// 5.����ҵ����
			WXrefundGetRequestBean.setAppTp("03");
			WXrefundGetRequestBean.setBizAmount(wechatCodeMap.get("bizAmount").toString());
			String random=String.format("%04d",new Random().nextInt(9999));
			String outRefundNo="TK"+pay.getPay_number()+random;
			WXrefundGetRequestBean.setBizId(outRefundNo);
			WXrefundGetRequestBean.setBizTime(bizTime);
			if(pay.getPay_remark().contains("�ϲ�֧��")){
				WXrefundGetRequestBean.setMerchId("872752322417164");//872752322417164
			}else{
				WXrefundGetRequestBean.setMerchId(payCodeInfo.getWechat_pay_code());
			}
			
			//WXrefundGetRequestBean.setOrderId("41234563876");
			WXrefundGetRequestBean.setPurpPrtry("A1207004");
			WXrefundGetRequestBean.setRefundBizid(pay.getPay_number());
			//WXrefundGetRequestBean.setOrderId("S1202105170028111160");
			WXrefundGetRequestBean.setTxTp("15");
			WXrefundGetRequestBean.setFeeType("CNY");
			WXrefundGetRequestBean.setRefundDesc(wechatCodeMap.get("refundDesc").toString());
			logger.info("+++++++++++++++"+new ObjectMapper().writeValueAsString(WXrefundGetRequestBean));
			// 6.����ҵ����json����
			oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(WXrefundGetRequestBean));
			// ͨѶ��ȡ���Ķ���
			OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
			// ��Ӧ�ɹ�
			logger.info("��Ӧ�룺{}����ǩ�����{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			if(oipRspBean.isSuccess()){
				refundDto dto = new refundDto();
				dto.setPayId(Integer.valueOf(wechatCodeMap.get("payId").toString()));
				dto.setOutRefundNo(outRefundNo);
				Float f = Float.parseFloat( wechatCodeMap.get("bizAmount").toString() ); 
				dto.setRefundFee((int)(f*100));
				dto.setRefundId(pay.getPay_number());
				dto.setRefundAdminId(Integer.valueOf(wechatCodeMap.get("refundAdminId").toString()));
				roadpaymapper.insertRoadPayRefund(dto);
				//Map bizData = JSON.parseObject(bizJson, Map.class);
				return oipRspBean.getMsg();
			}else{
				throw new BaseServiceException(
						Integer.valueOf(oipRspBean.getBizCode()),
						oipRspBean.getBizMsg());
			}
			
		}

		public String getJuHeCode_new(Map<String, Object> juHeCodeMap) throws ParseException, JsonProcessingException {
			String orderIds=juHeCodeMap.get("orderIds").toString();
			String pklCode=juHeCodeMap.get("pklCode").toString();
			String wechat_pay_code="";//΢���̻���
			
			if(orderIds.contains(",")==true){
				wechat_pay_code="872752320064750885";//�ϲ�Ƿ��
			}else{//find road_plk_code
				roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pklCode);
				if(payCodeInfo==null){
					throw new BaseServiceException(
							StatusCode.CREDITAMOUNT_ERROR.getCode(),
							"�á�"+pklCode+"��ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
				}
				wechat_pay_code=payCodeInfo.getFour_pay_code_new();
			}
			

			
			juHeCodeMap.put("wechat_pay_code", wechat_pay_code);
			
			logger.info("-�ۺ�·�߽ɷ�-��������-run--��"+juHeCodeMap);
			
			ResourceBundle res = ResourceBundle.getBundle("rccApi_new");
			
			if(StringUtils.isBlank(juHeCodeMap.get("bizId").toString())||StringUtils.isBlank(juHeCodeMap.get("bizAmount").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ȱ�ٱ����");
			}
			OipReqBean oipReqBean = new OipReqBean();
			
			// 1.��ȡͨѶ���Ӷ���
			INetTools netTools = NetToolsHttpFactory.getHttpInstance();
			// 2.���ɹ��������Ķ���
			
			// ����Ӧ��id
			oipReqBean.setAppId(res.getString("appId_test"));
			
			// ���ÿ�����id
			oipReqBean.setDlpId(res.getString("dlpId_test"));
			// ���ò�Ʒid
			oipReqBean.setProdId(res.getString("prodId_test"));
			// ����api������
			oipReqBean.setMethod("zjrcuoip.pay.uas.trxcrt");
			// ����api�汾
			oipReqBean.setVersion(res.getString("version_test"));
			// ����ʱ���
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
			String timestamp=sf.format(new Date());
			DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time1 = df2.parse(timestamp);
			String bizTime=df1.format(time1);
			oipReqBean.setTimestamp(timestamp);
			//oipReqBean.setTimestamp(juHeCodeMap.get("timestamp").toString());//2021-04-02 11:09:50
			// 3.�����������ö���,���������ַ/url����Ϣ
			NetReqConfBean netReqConfBean = new NetReqConfBean();
			// Ӧ��˽Կ
			netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
					// �����ַ
			netReqConfBean.setNetUrl(res.getString("netUrl_test"));
			// �����߹�Կ
			netReqConfBean.setPublicKey(res.getString("publicKey_test"));
			// 4.��������ҵ���Ķ���
			Map map =new HashMap();
			map.put("mchSeq", wechat_pay_code);
			map.put("txId", juHeCodeMap.get("bizId").toString());
			map.put("purpPrtry", "A001007");
			map.put("txDtTm", bizTime);
			map.put("amt", juHeCodeMap.get("bizAmount").toString());
			map.put("subject", "�ۺ��벴λ�ɷ�["+pklCode+"]");
			map.put("areaCd", "330000");
			map.put("termId", "00000000");
			map.put("notifyUrl", "https://jiashanmp.iparking.tech/pos/notify_nx_juhe_new.php");
			map.put("attach", juHeCodeMap.get("attach").toString());
			
			logger.info("apiName:"+"�ۺ�·�߽ɷ�"+"; param:"+new ObjectMapper().writeValueAsString(map));
			
			
			logger.info("apiName:"+"�ۺ�·�߽ɷ�����"+"; param:"+new ObjectMapper().writeValueAsString(oipReqBean));
			
			// 5.����ҵ����json����
			oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(map));
			// ͨѶ��ȡ���Ķ���
			try {
				OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
				// ��Ӧ�ɹ�
				if (oipRspBean.getCode().equals("00000000")) {
					
					String bizJson = oipRspBean.getBizContent();
					logger.info("������{}����Ӧ�룺{}����ǩ�����{}��΢��codeUrl:{}",juHeCodeMap,oipRspBean.getCode(),oipRspBean.isSignValid(),bizJson);
					if (StringUtils.isNotBlank(bizJson)) {
						Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
						System.out.println(bizData.get("codeUrl"));
						return bizData.get("codeUrl").toString();
					}
				}else{
					throw new BaseServiceException(
							Integer.valueOf(oipRspBean.getBizCode()),
							oipRspBean.getBizMsg());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			
			return null;
			
			


		}

		public String juHeRefundOrder_road(Map<String, Object> juHeCodeMap) throws ParseException, JsonParseException, JsonMappingException, IOException {
			//roadParkinglotsPay pay=roadpaymapper.findRoadParkingLotsPayInfoByPayId(Integer.valueOf(juHeCodeMap.get("payId").toString()));
			
			
			if(StringUtils.isBlank(juHeCodeMap.get("pklCode").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ͣ������Ų���Ϊ�գ�");
			}
			
			if(StringUtils.isBlank(juHeCodeMap.get("payId").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"֧����Ų���Ϊ�գ�");
			}
			
			if(StringUtils.isBlank(juHeCodeMap.get("bizId").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"֧�������Ų���Ϊ�գ�");
			}
			
			if(StringUtils.isBlank(juHeCodeMap.get("bizAmount").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"�˿����Ϊ�գ�");
			}
			
			if(StringUtils.isBlank(juHeCodeMap.get("outRefundNo").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"�˿��Ų���Ϊ�գ�");
			}
			
			roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(juHeCodeMap.get("pklCode").toString());
			if(payCodeInfo==null){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�á�"+juHeCodeMap.get("pklCode").toString()+"��ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
			}
			
			logger.debug("-��ɨ����ۺ��˿�����-run--");
			ResourceBundle res = ResourceBundle.getBundle("rccApi_new");
			// 1.��ȡͨѶ���Ӷ���
			INetTools netTools = NetToolsHttpFactory.getHttpInstance();
			// 2.���ɹ��������Ķ���
			OipReqBean oipReqBean = new OipReqBean();
			oipReqBean.setAppId(res.getString("appId_test"));
			// ���ÿ�����id
			oipReqBean.setDlpId(res.getString("dlpId_test"));
			// ���ò�Ʒid
			oipReqBean.setProdId(res.getString("prodId_test"));
			// ����api������
			oipReqBean.setMethod("zjrcuoip.pay.uas.refund");
			// ����api�汾
			oipReqBean.setVersion(res.getString("version_test"));
			// ����ʱ���
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
			String timestamp=sf.format(new Date());
			DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time1 = df2.parse(timestamp);
			String bizTime=df1.format(time1);
			oipReqBean.setTimestamp(sf.format(new Date()));
			// 3.�����������ö���,���������ַ/url����Ϣ
			NetReqConfBean netReqConfBean = new NetReqConfBean();
			// Ӧ��˽Կ
			netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
					// �����ַ
			netReqConfBean.setNetUrl(res.getString("netUrl_test"));
			// �����߹�Կ
			netReqConfBean.setPublicKey(res.getString("publicKey_test"));// 4.��������ҵ���Ķ���
			/*UnitePayRefundRequestBean unitePayRefundRequestBean = new UnitePayRefundRequestBean();
			unitePayRefundRequestBean.setAppTp("03");
			unitePayRefundRequestBean.setAttach("");
			unitePayRefundRequestBean.setBizAmount(juHeCodeMap.get("bizAmount").toString());
			unitePayRefundRequestBean.setBizCurrency("CNY");
			String random=String.format("%04d",new Random().nextInt(9999));
			String outRefundNo="TK"+pay.getPay_number()+random;
			unitePayRefundRequestBean.setBizId(outRefundNo);
			//unitePayRefundRequestBean.setBizRemark("��ע��Ϣ");
			unitePayRefundRequestBean.setBizTime(bizTime);
			unitePayRefundRequestBean.setDeviceNo("111");
			if(pay.getPay_remark().contains("�ඩ��֧��")){
				unitePayRefundRequestBean.setMerchId("872752322417163");
			}else{
				unitePayRefundRequestBean.setMerchId(payCodeInfo.getFour_pay_code_new());
			}
			
			unitePayRefundRequestBean.setPurpPrtry("A1207005");
			unitePayRefundRequestBean.setRefundBizid(pay.getPay_number());
			unitePayRefundRequestBean.setRefundDesc(juHeCodeMap.get("refundDesc").toString());*/
			Map map =new HashMap();
			/*if(pay.getPay_remark().contains("�ඩ��֧��")){
				map.put("mchSeq", "872752320064750885");
			}else{
				map.put("mchSeq",payCodeInfo.getFour_pay_code_new());
			}*/
			map.put("mchSeq",payCodeInfo.getFour_pay_code_new());
			map.put("attach", "·��ͣ�����˿"+"_"+juHeCodeMap.get("payId").toString());
			//String random=String.format("%04d",new Random().nextInt(9999));
			//String outRefundNo="TK"+pay.getPay_number()+random;
			map.put("txId", juHeCodeMap.get("outRefundNo").toString());
			map.put("txDtTm", bizTime);
			map.put("orglTxId", juHeCodeMap.get("bizId").toString());
			map.put("txAmt", juHeCodeMap.get("bizAmount").toString());
			map.put("notifyUrl", "https://jiashanmp.iparking.tech/pos/notify_nx_juhe_tk_new.php");
			logger.info("apiName:"+"·��ͣ�����˿�����"+"; param:"+new ObjectMapper().writeValueAsString(map));

			oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(map));

			OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
			logger.info("��Ӧ�룺{}����ǩ�����{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent().replace("/", "");
			logger.info(bizJson);
			Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
			if(oipRspBean.getCode().equals("00000000")){
				if(bizData.get("bizStsDesp").toString().equals("�˿�����ɹ�")){
					refundDto dto = new refundDto();
					dto.setPayId(Integer.valueOf(juHeCodeMap.get("payId").toString()));
					dto.setOutRefundNo(juHeCodeMap.get("outRefundNo").toString());
					Float f = Float.parseFloat( juHeCodeMap.get("bizAmount").toString() ); 
					dto.setRefundFee((int)(f*100));
					dto.setRefundId(juHeCodeMap.get("bizId").toString());
					dto.setRefundAdminId(Integer.valueOf(juHeCodeMap.get("refundAdminId").toString()));
					roadpaymapper.insertRoadPayRefund(dto);
					return bizData.get("bizStsDesp").toString();
				}else{
					throw new BaseServiceException(
							Integer.valueOf(bizData.get("bizSts").toString()),
							bizData.get("rjctRsn").toString());
				}
				
			}else{
				throw new BaseServiceException(
						Integer.valueOf(oipRspBean.getBizCode()),
						oipRspBean.getBizMsg());
			}
		}
		
		
		public String juHeRefundOrder_parking(Map<String, Object> juHeCodeMap) throws ParseException, JsonParseException, JsonMappingException, IOException {
			/*roadParkinglotsPay pay=roadpaymapper.findRoadParkingLotsPayInfoByPayId(Integer.valueOf(juHeCodeMap.get("payId").toString()));
			roadPklPayCode payCodeInfo=parkingMapper.findPayCodeInfoByPklCode(pay.getPkl_code());
			if(payCodeInfo==null){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�á�"+pay.getPkl_code()+"��ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
			}*/
			if(StringUtils.isBlank(juHeCodeMap.get("pklCode").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ͣ������Ų���Ϊ�գ�");
			}
			
			if(StringUtils.isBlank(juHeCodeMap.get("payId").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"֧����Ų���Ϊ�գ�");
			}
			
			if(StringUtils.isBlank(juHeCodeMap.get("bizId").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"֧�������Ų���Ϊ�գ�");
			}
			
			if(StringUtils.isBlank(juHeCodeMap.get("bizAmount").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"�˿����Ϊ�գ�");
			}
			
			if(StringUtils.isBlank(juHeCodeMap.get("outRefundNo").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"�˿��Ų���Ϊ�գ�");
			}
			
			logger.debug("-��ɨ����ۺ��˿�����-run--");
			ResourceBundle res = ResourceBundle.getBundle("rccApi_new");
			// 1.��ȡͨѶ���Ӷ���
			INetTools netTools = NetToolsHttpFactory.getHttpInstance();
			// 2.���ɹ��������Ķ���
			OipReqBean oipReqBean = new OipReqBean();
			oipReqBean.setAppId(res.getString("parking_appId_test"));
			// ���ÿ�����id
			oipReqBean.setDlpId(res.getString("parking_dlpId_test"));
			// ���ò�Ʒid
			oipReqBean.setProdId(res.getString("parking_prodId_test"));
			// ����api������
			oipReqBean.setMethod("zjrcuoip.pay.uas.refund");
			// ����api�汾
			oipReqBean.setVersion(res.getString("parking_version_test"));
			// ����ʱ���
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
			String timestamp=sf.format(new Date());
			DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time1 = df2.parse(timestamp);
			String bizTime=df1.format(time1);
			oipReqBean.setTimestamp(sf.format(new Date()));
			// 3.�����������ö���,���������ַ/url����Ϣ
			NetReqConfBean netReqConfBean = new NetReqConfBean();
			// Ӧ��˽Կ
			netReqConfBean.setAppPrivateKey(res.getString("parking_appPrivateKey_test"));
					// �����ַ
			netReqConfBean.setNetUrl(res.getString("parking_netUrl_test"));
			// �����߹�Կ
			netReqConfBean.setPublicKey(res.getString("parking_publicKey_test"));// 4.��������ҵ���Ķ���
			
			Map map =new HashMap();
			
			String mchSeq = parkingMapper.getParkinglotsMchSeqByPklCode(juHeCodeMap.get("pklCode").toString());	
			if(StringUtils.isBlank(mchSeq)){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�á�"+juHeCodeMap.get("pklCode").toString()+"�����ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
			}
			map.put("mchSeq",mchSeq);
			//map.put("mchSeq","872752320215307297");
			
			map.put("attach", "���ͣ�����˿"+"_"+juHeCodeMap.get("payId").toString());
			//String random=String.format("%04d",new Random().nextInt(9999));
			//String outRefundNo="TK"+"100011"+random;
			map.put("txId", juHeCodeMap.get("outRefundNo").toString());
			map.put("txDtTm", bizTime);
			map.put("orglTxId", juHeCodeMap.get("bizId").toString());
			map.put("txAmt", juHeCodeMap.get("bizAmount").toString());
			map.put("notifyUrl", "https://jiashanmp.iparking.tech/Bank/notifyNongXinPklTK");
			logger.info("apiName:"+"�ۺ�����ɨ�˿�����"+"; param:"+new ObjectMapper().writeValueAsString(map));

			oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(map));

			OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
			logger.info("��Ӧ�룺{}����ǩ�����{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent().replace("/", "");
			logger.info(bizJson);
			Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
			if(oipRspBean.getCode().equals("00000000")){
				if(bizData.get("bizStsDesp").toString().equals("�˿�����ɹ�")){
					refundDto dto = new refundDto();
					dto.setPayId(Integer.valueOf(juHeCodeMap.get("payId").toString()));
					dto.setOutRefundNo(juHeCodeMap.get("outRefundNo").toString());
					Float f = Float.parseFloat( juHeCodeMap.get("bizAmount").toString() ); 
					dto.setRefundFee((int)(f*100));
					dto.setRefundId(juHeCodeMap.get("bizId").toString());
					dto.setRefundAdminId(Integer.valueOf(juHeCodeMap.get("refundAdminId").toString()));
					businessmapper.insertPayRefund(dto);
					return bizData.get("bizStsDesp").toString();
				}else{
					throw new BaseServiceException(
							Integer.valueOf(bizData.get("bizSts").toString()),
							bizData.get("rjctRsn").toString());
				}
				/*refundDto dto = new refundDto();
				dto.setPayId(Integer.valueOf(juHeCodeMap.get("payId").toString()));
				dto.setOutRefundNo(outRefundNo);
				Float f = Float.parseFloat( juHeCodeMap.get("bizAmount").toString() ); 
				dto.setRefundFee((int)(f*100));
				dto.setRefundId(pay.getPay_number());
				dto.setRefundAdminId(Integer.valueOf(juHeCodeMap.get("refundAdminId").toString()));
				roadpaymapper.insertRoadPayRefund(dto);*/
			}else{
				throw new BaseServiceException(
						Integer.valueOf(oipRspBean.getBizCode()),
						oipRspBean.getBizMsg());
			}
		}
		
		
		

		public String getJuHeCode_parking(Map<String, Object> juHeCodeMap) throws ParseException, JsonProcessingException {
			
			if(StringUtils.isBlank(juHeCodeMap.get("bizId").toString())||StringUtils.isBlank(juHeCodeMap.get("bizAmount").toString())||StringUtils.isBlank(juHeCodeMap.get("pklCode").toString())){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"ȱ�ٱ����");
			}
			
			String pklCode=juHeCodeMap.get("pklCode").toString();
            String mchSeq = parkingMapper.getParkinglotsMchSeqByPklCode(pklCode);
				if(StringUtils.isBlank(mchSeq)){
					throw new BaseServiceException(
							StatusCode.CREDITAMOUNT_ERROR.getCode(),
							"�á�"+pklCode+"�����ͣ����û�а󶨶�Ӧ��֧�����̻������ʵ��");
				}
				
			
			juHeCodeMap.put("wechat_pay_code", mchSeq);
			
			logger.info("-ͣ������ɨ����ۺ�-��������-run--��"+juHeCodeMap);
			
			ResourceBundle res = ResourceBundle.getBundle("rccApi_new");
			
			
			OipReqBean oipReqBean = new OipReqBean();
			
			// 1.��ȡͨѶ���Ӷ���
			INetTools netTools = NetToolsHttpFactory.getHttpInstance();
			// 2.���ɹ��������Ķ���
			
			// ����Ӧ��id
			oipReqBean.setAppId(res.getString("parking_appId_test"));
			
			// ���ÿ�����id
			oipReqBean.setDlpId(res.getString("parking_dlpId_test"));
			// ���ò�Ʒid
			oipReqBean.setProdId(res.getString("parking_prodId_test"));
			// ����api������
			oipReqBean.setMethod("zjrcuoip.pay.uas.trxcrt");
			// ����api�汾
			oipReqBean.setVersion(res.getString("parking_version_test"));
			// ����ʱ���
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
			String timestamp=sf.format(new Date());
			DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time1 = df2.parse(timestamp);
			String bizTime=df1.format(time1);
			oipReqBean.setTimestamp(timestamp);
			
			// 3.�����������ö���,���������ַ/url����Ϣ
			NetReqConfBean netReqConfBean = new NetReqConfBean();
			// Ӧ��˽Կ
			netReqConfBean.setAppPrivateKey(res.getString("parking_appPrivateKey_test"));
					// �����ַ
			netReqConfBean.setNetUrl(res.getString("parking_netUrl_test"));
			// �����߹�Կ
			netReqConfBean.setPublicKey(res.getString("parking_publicKey_test"));
		
			Map map =new HashMap();
			map.put("mchSeq", mchSeq);
			map.put("txId", juHeCodeMap.get("bizId").toString());
			map.put("purpPrtry", "A001007");
			map.put("txDtTm", bizTime);
			map.put("amt", juHeCodeMap.get("bizAmount").toString());
			map.put("subject", "�ۺ�����ͣ�����ɷ�["+pklCode+"]");
			map.put("areaCd", "330000");
			map.put("termId", "00000000");
			map.put("notifyUrl", "https://jiashanmp.iparking.tech/Bank/notifyNongXinPkl");
			map.put("attach", juHeCodeMap.get("attach").toString());
			logger.info("apiName:"+"�ۺ�����ɨ��������"+"; param:"+new ObjectMapper().writeValueAsString(map));
			logger.info("apiName:"+"�ۺ�����ɨ������������"+"; param:"+new ObjectMapper().writeValueAsString(oipReqBean));
			
			// 5.����ҵ����json����
			oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(map));
			// ͨѶ��ȡ���Ķ���
			try {
				OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
				// ��Ӧ�ɹ�
				if (oipRspBean.getCode().equals("00000000")) {
					String bizJson = oipRspBean.getBizContent();
					logger.info("������{}����Ӧ�룺{}����ǩ�����{}��΢��codeUrl:{}",juHeCodeMap,oipRspBean.getCode(),oipRspBean.isSignValid(),bizJson);
					if (StringUtils.isNotBlank(bizJson)) {
						Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
						return bizData.get("codeUrl").toString();
					}
				}else{
					throw new BaseServiceException(
							Integer.valueOf(oipRspBean.getBizCode()),
							oipRspBean.getBizMsg());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}
		
		
		

		public void paymentOther(Map<String, Object> wechatCodeMap) throws IOException {
			
			String orderIds=wechatCodeMap.get("orderIds").toString();
			
			String userOpenId=wechatCodeMap.get("userOpenId").toString();
			
			String payType=wechatCodeMap.get("payType").toString();
			
			if(StringUtils.isBlank(orderIds)){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"������Ϣ����Ϊ��");
			}
			
			if(StringUtils.isBlank(userOpenId)){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"С�����û�openid����Ϊ��");
			}
			
			
			String orderId[]=orderIds.split(",");
			String sumBizAmount=orderMapper.findRoadOperationOrderAmount(Arrays.asList(orderId));
			wechatCodeMap.put("sumBizAmount", sumBizAmount);
			double a = Double.parseDouble(wechatCodeMap.get("bizAmount").toString());
			
			int int_bizAmount=(int) (a*100);
			String bizAmount=int_bizAmount+"";
			if(sumBizAmount.equals(bizAmount)==false){
				throw new BaseServiceException(
						StatusCode.CREDITAMOUNT_ERROR.getCode(),
						"�ɷѽ����ʵ��Ƿ�ѽ�һ�£����ʵ��");
			}
			
			wechatUser wechatUser =wechatUsermapper.findWechatUserInfoByOpenId(userOpenId);
			
			if(payType.equals("2")){
				int newBalances = wechatUser.getBalances() -int_bizAmount;
				
				if(wechatUser.getBalances()<int_bizAmount){
					throw new BaseServiceException(
							StatusCode.CREDITAMOUNT_ERROR.getCode(),
							"Ԥ��ֵ���㣬�޷��ɷѣ�");
				}
				List<String> plateList = new ArrayList<>();
				String outTradeNo= "";
				for(String oId :Arrays.asList(orderId)){
					
					List<Map<String, Object>> roadOrderInfoList = orderMapper.findRoadOrderInfoByOrderId(oId);
					plateList.add(roadOrderInfoList.get(0).get("veh_plate").toString());
					outTradeNo=roadOrderInfoList.get(0).get("order_number").toString();
					orderMapper.insertSelectRoadPayBy(roadOrderInfoList.get(0).get("order_number").toString(),roadOrderInfoList.get(0).get("pkl_code").toString(),roadOrderInfoList.get(0).get("pkll_code").toString(),roadOrderInfoList.get(0).get("veh_plate").toString(),roadOrderInfoList.get(0).get("order_receivable").toString());
					orderMapper.updateRoadOrderInfoByOutTradeNo(roadOrderInfoList.get(0).get("order_arrears").toString(),"35",roadOrderInfoList.get(0).get("order_number").toString());
					
				}
				
				userChargeRecordDto userchargerecord = new userChargeRecordDto();
            	userchargerecord.setR_user_id(wechatUser.getUserId());
            	userchargerecord.setR_type(3);
            	userchargerecord.setR_balances(int_bizAmount);
				userchargerecord.setR_point(0);
				userchargerecord.setR_discount_id(0);
				userchargerecord.setR_out_trade_no(outTradeNo);
				userchargerecord.setR_left_balances(newBalances);
				userchargerecord.setR_left_point(wechatUser.getPoint());
                businessmapper.insertChargeRecord_cashOut(userchargerecord);
				
				
				
				wechatUsermapper.updateWechatUserInfoMoney(newBalances, wechatUser.getPoint(), wechatUser.getUserId().toString());
				Set<String> set = new HashSet<>(plateList);
				plateList.clear();
				plateList.addAll(set);
				if(plateList.size()>0){
					for(String p:plateList){
						bluecardservice.delBackPlate(p);
					}
				}
				
				
			}
			
			
			if(payType.equals("3")){
				int newPoint = wechatUser.getPoint() -int_bizAmount;
				if(wechatUser.getPoint()<int_bizAmount){
					throw new BaseServiceException(
							StatusCode.CREDITAMOUNT_ERROR.getCode(),
							"ƽ̨���㣬�޷��ɷѣ�");
				}
				String outTradeNo= "";
				List<String> plateList = new ArrayList<>();
				for(String oId :Arrays.asList(orderId)){
					
					List<Map<String, Object>> roadOrderInfoList = orderMapper.findRoadOrderInfoByOrderId(oId);
					plateList.add(roadOrderInfoList.get(0).get("veh_plate").toString());
					outTradeNo=roadOrderInfoList.get(0).get("order_number").toString();
					orderMapper.insertSelectRoadPayBy(roadOrderInfoList.get(0).get("order_number").toString(),roadOrderInfoList.get(0).get("pkl_code").toString(),roadOrderInfoList.get(0).get("pkll_code").toString(),roadOrderInfoList.get(0).get("veh_plate").toString(),roadOrderInfoList.get(0).get("order_receivable").toString());
					orderMapper.updateRoadOrderInfoByOutTradeNo(roadOrderInfoList.get(0).get("order_arrears").toString(),"35",roadOrderInfoList.get(0).get("order_number").toString());
					
				}
				userChargeRecordDto userchargerecord = new userChargeRecordDto();
            	userchargerecord.setR_user_id(wechatUser.getUserId());
            	userchargerecord.setR_type(3);
            	userchargerecord.setR_balances(0);
				userchargerecord.setR_point(int_bizAmount);
				userchargerecord.setR_discount_id(0);
				userchargerecord.setR_out_trade_no(outTradeNo);
				userchargerecord.setR_left_balances(wechatUser.getBalances());
				userchargerecord.setR_left_point(newPoint);
                businessmapper.insertChargeRecord_cashOut(userchargerecord);
				
                
                wechatUsermapper.updateWechatUserInfoMoney(wechatUser.getBalances(), newPoint, wechatUser.getUserId().toString());
				Set<String> set = new HashSet<>(plateList);
				plateList.clear();
				plateList.addAll(set);
				if(plateList.size()>0){
					for(String p:plateList){
						bluecardservice.delBackPlate(p);
					}
				}
			}
			
			
		}

		
	    
}
