package com.WeChatApi.bean.models;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.WeChatApi.controller.base.BaseServiceException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.eagle.sdk.base.INetTools;
import cn.com.eagle.sdk.bean.NetReqConfBean;
import cn.com.eagle.sdk.bean.OipReqBean;
import cn.com.eagle.sdk.bean.OipRspBean;
import cn.com.eagle.sdk.net.factory.NetToolsHttpFactory;

/**
 * 测试农信接口
 * @author 39496
 *
 */
public class rccWechatApiTest {

	private static Logger logger = LoggerFactory.getLogger("");
	public static void main(String[] args) throws ParseException, JsonParseException, JsonMappingException, IOException {
		
		//creatWechatPayOrder();//创建
		//findWechatPayQryOrder();//查询
		//closeWechatOrder();//关闭
		//refundWechatOrder();//退款
		//findRefundWechatOrder();//退款查询
		//String aa="872752322417164";
		WxBillGetDemo("872752322417150","20220317");
		/*String a="http://172.30.3.3:8322/evo-apigw/evo-accesscontrol/1.0.0/integrate/record/getValidateRecord/140109199903070515";
		int one = a.lastIndexOf("/");
		System.out.println(a.substring((one+1),a.length()));*/
		
	}
	
	
	/*******************嘉善停车测试************************/
    private static String appid="TB9J2021042000045461";
	
	private static String dlpId="FB000000043137";
	
	private static String prodId="TB9J2020071000258085";
	
	private static String version="1.0.0";
	
	private static String appPrivateKey="MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDICorSXk2juj2i2RRsvTiIQBuSgaQxCu6v+Wh9YdlH/VhgAzLklDLsyxaz0VCnCaWwFtigIX50Q0W2woPZnvJSd96VoyrW0kxm+nUGcTxR3iFfkK2cmr3E5Gm6lO2a+8xUFFXvzvILmGJuuo1NOZLTfOD0obqoED7+PgORluo41/w0qoskSMwXwllM6vWif6se5tUW8j197/W7ocMBsrj3OrYGqmXferVw3Z2p0N5GupNvMsodGA74wdS+PsVLG0iWTn0fwWYkXHsFl6H5lDQOs5kNnabWBkNPBMZWXYvbQYJB/PxvOTmosYczoygnF/0ypmrxZGJl4b6kRp6LUdkNAgMBAAECggEBAMNC3xzXJkvyCCYEZVZ7IUqF4SQR3ZZtiG1HM4Jn17LYjcIyfSP3sPnIh2GAqIxT82I9+rfb2dZcsBjkJ9VtvDVZgnBn8/H7Fs7SHRbrwyhafhMNYxnhRemB195jbscAkANmUjghtcwxs6tH95Nw5mv/konXsB5KtWJwYAVl3T0bgesJqUFid7Ab45xPXSp/cXbmsD8K+aT2D+Chhd0GFDkBxMrzooBZoSegUN8/uoX1KKfZLXSBEVy2AxUF2lbWqMhSEwK+J4T0qisnYoE65PsQ21vsuFQ5TnwNjaVm2I6dOuaaaBravbH0sif1a17QUHpOpNkv+q0Gs4KViSpKtTUCgYEA/lNNXMUapQFShcWI2nzL1pQhkMdAFcwj0ScgBN7B2jtvLi3MJWfDdTYXrGhHpZDHTHiv4x3C40ItoLKiLJGT2g+xhQ627tHF2ecEOi0OF6bvNSm4VLTM7ejB3J2KT/ZxYm1PuwvlDqXcaHJ8D69796mwMtlIrTLPnKpPdECQVucCgYEAyVu8tAg87VXNoDuY1DUqwqHwwgmQMWMdbamWNcO2x1vPw+98FeV7JUjLLssrAPVvtAAUkS8MWKaDnuCsKYUCXW4fZRCPXHxrKklMBLtIT2N7WvupiF/k0QOXr0Fy9maFRr/DFynwfBGqaIiHXKQklRPbMsCfDEFCfxn61PVB9esCgYEA3Oq/aLZOTsa3SDwuhJui9OH3xitOH6ET+7pgtdpJyaeXDCX8DwHzZv110u1CTy9T77lmD4Lqpz31JIIHHW/XXrQDH/GINitCEeX6Zg59D06SuztiQzJKUN6+h0TRwGPyWe1aNMBAWFj6/D92RKnKpXdg9sqHtutMLGpEWlc7CkECgYATP7LNXxA7+/zZOXlFqAN7cbGrSvC+1+PDTPfwEdKSQkOdTnKFQVfBW7EYuxFUWSIhifRtYJsEin6AnKe4nF96gQF9TtT1mWtBMpqSxTiHZlIvg2lUgR5FoNtKRK5xwaEx/zjuKPM1WRb6QTr3bzWUDr7gnitWJ5LsxZYF76ArmwKBgQCKrbMuGxMMewHl517kbBwV67uxEqzAo+8/Hs44HQuzeITRX+VY1OZzgsJLbq4ML3hzIrm3FY0p66h6zV3y+yw7v7UbWm8+l4In9tbWdgXwCcjXfbYbzqxa26mgz8087fZNZljo+1zVl67PgXC8mLqYdvlg2ivYZBsGz/yWNflmUA==";
	
	private static String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsfbdoj7mQ2C5bCq69iDan5ft6r3oO3GFa7Mtkxr8mevxrQjKLP3RBxNXzsV/lCm2Nr/P6/Pxohd11DI74fNThx+JtUFuiZTYXt7nGT/lSchhL9aqf+zrbNgLQB9yS3VhDg3NXixTcbSbnoBL4unjSBfG7AWCa1Bau98RoZcMHs10NVE1Xl74xXnuOUAxWhC722g6pZfv8EMcTxt0bQEi5cYZ17yahtmmOtCdx4hK26hDyCRib02rlsfRBK+Jxh0/5+L0eGjPbGj44QVb3WaO2D7jbogzba44uKZILqOYxTvR3jr8rMvuQ8Zbq2KKH3xGREXH0viRPJWQ4pC70bahNQIDAQAB";
    
	private static String netUrl="https://apiuat.zj96596.com.cn:8800/oip-gateway-server/zjrcuoip/gateway.do";
	
	private static String merchId="801531100210499";
	
	
	
	private static void WxBillGetDemo(String aa,String t) throws JsonParseException, JsonMappingException, IOException {
		
		logger.debug("-微信支付对账单获取-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.获取通讯连接对象
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.生成公共请求报文对象
		OipReqBean oipReqBean = new OipReqBean();
		// 设置应用id
		oipReqBean.setAppId(res.getString("appId_test"));
				// 设置开发者id
		oipReqBean.setDlpId(res.getString("dlpId_test"));
				// 设置产品id
		oipReqBean.setProdId(res.getString("wechat_prodId_test"));
		// 设置api方法名
		oipReqBean.setMethod("zjrcuoip.payment.bill.downloadurl.query");
		// 设置api版本
		oipReqBean.setVersion("1.0.0");
		// 设置时间戳

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		oipReqBean.setTimestamp(sf.format(new Date()));
		// 3.生成请求配置对象,配置请求地址/url等信息
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// 应用私钥
		netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
		// 请求地址
netReqConfBean.setNetUrl(res.getString("netUrl_test"));
// 开发者公钥
netReqConfBean.setPublicKey(res.getString("publicKey_test"));// 4.生成请求业务报文对象
		WXBillGetRequestBean WXbillGetRequestBean = new WXBillGetRequestBean();
		// 5.设置业务报文
		// 5.设置业务报文
		// 加载当前用户信息
		WXbillGetRequestBean.setBillDate(t);
		WXbillGetRequestBean.setBillType("ALL");
		WXbillGetRequestBean.setMerchId(aa);
		WXbillGetRequestBean.setTarType("GZIP");
		System.out.println("+++++++++++++++"+new ObjectMapper().writeValueAsString(WXbillGetRequestBean));
		// 6.生成业务报文json对象
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(WXbillGetRequestBean));
		// 通讯获取报文对象
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// 响应成功
		if (oipRspBean.isSuccess()) {
			logger.info("响应码：{}，验签结果：{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
			System.out.println(bizData.get("billDownloadUrl").toString());
		}
		
	}



	public static void creatWechatPayOrder()throws ParseException, JsonParseException, JsonMappingException, IOException{
		

		logger.info("-微信小程序订单创建-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.获取通讯连接对象
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.生成公共请求报文对象
		OipReqBean oipReqBean = new OipReqBean();
		// 设置应用id
		oipReqBean.setAppId(res.getString("appId_test"));
				// 设置开发者id
		oipReqBean.setDlpId(res.getString("dlpId_test"));
				// 设置产品id
		oipReqBean.setProdId(res.getString("wechat_prodId_test"));
		// 设置api方法名
		oipReqBean.setMethod("weixin.payment.create.order");
		// 设置api版本
		oipReqBean.setVersion(res.getString("version_test"));
		// 设置时间戳
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp=sf.format(new Date());
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time1 = df2.parse(timestamp);
		String bizTime=df1.format(time1);
		oipReqBean.setTimestamp(sf.format(new Date()));
		// 3.生成请求配置对象,配置请求地址/url等信息
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// 应用私钥
	
				netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
						// 请求地址
				netReqConfBean.setNetUrl(res.getString("netUrl_test"));
				// 开发者公钥
				netReqConfBean.setPublicKey(res.getString("publicKey_test"));
		// 4.生成请求业务报文对象
		WXOrderCreateGetRequestBean WXorderCreateGetRequestBean = new WXOrderCreateGetRequestBean();

		WXorderCreateGetRequestBean.setAppTp("03");
		WXorderCreateGetRequestBean.setTxTp("20");
		WXorderCreateGetRequestBean.setPurpPrtry("A6004004");
		WXorderCreateGetRequestBean.setMerchId(res.getString("bussines_test"));
		WXorderCreateGetRequestBean.setBizId(bizTime);
		WXorderCreateGetRequestBean.setPayCtAmount("0.01");
		WXorderCreateGetRequestBean.setProductDesc("小程序泊位缴费");
		WXorderCreateGetRequestBean.setChannelType("WEIXIN");
		WXorderCreateGetRequestBean.setDeviceInfo("64689789765468");
		WXorderCreateGetRequestBean.setUserIp("158.220.10.102");
		WXorderCreateGetRequestBean.setUserMac("158.220.1.1");
		WXorderCreateGetRequestBean.setTradeType("JSAPI");
		WXorderCreateGetRequestBean.setSubOpenid("o3J0E5VxSlIKw1sdwafQg_YlHbz4");
		WXorderCreateGetRequestBean.setSubAppid(res.getString("wechat_appid"));
        System.out.println("+++++++++++++++"+new ObjectMapper().writeValueAsString(WXorderCreateGetRequestBean));
		// 6.生成业务报文json对象
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(WXorderCreateGetRequestBean));
		// 通讯获取报文对象
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// 响应成功
		if (oipRspBean.isSuccess()) {
			
			String bizJson = oipRspBean.getBizContent();
			logger.info("参数：{}，响应码：{}，验签结果：{}，微信codeUrl:{}","",oipRspBean.getCode(),oipRspBean.isSignValid(),bizJson);
			if (StringUtils.isNotBlank(bizJson)) {
				Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				//bizData.get("wcPayData").toString();
				Map wxData = new ObjectMapper().readValue(bizData.get("wcPayData").toString(), Map.class);
				System.out.println(wxData);
				
			}
		}else{
			throw new BaseServiceException(
					Integer.valueOf(oipRspBean.getBizCode()),
					oipRspBean.getBizMsg());
		}
		
		
		
	}
	
	
	/**
	 * 查询订单
	 * @return 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static void findWechatPayQryOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		
		logger.debug("-微信支付订单信息查询-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.获取通讯连接对象
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.生成公共请求报文对象
		OipReqBean oipReqBean = new OipReqBean();
		// 设置应用id
				oipReqBean.setAppId(res.getString("appId_test"));
						// 设置开发者id
				oipReqBean.setDlpId(res.getString("dlpId_test"));
						// 设置产品id
				oipReqBean.setProdId(res.getString("wechat_prodId_test"));
				oipReqBean.setMethod("weixin.payment.order.qry");
		// 设置api版本
		oipReqBean.setVersion("1.0.0");
		// 设置时间戳
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp=sf.format(new Date());
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time1 = df2.parse(timestamp);
		String bizTime=df1.format(time1);
		oipReqBean.setTimestamp(sf.format(new Date()));
		// 3.生成请求配置对象,配置请求地址/url等信息
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// 应用私钥
		netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
		// 请求地址
		netReqConfBean.setNetUrl(res.getString("netUrl_test"));
		// 开发者公钥
		netReqConfBean.setPublicKey(res.getString("publicKey_test"));
        // 4.生成请求业务报文对象
		WXOrderQryGetRequestBean WXorderQryGetRequestBean = new WXOrderQryGetRequestBean();
		// 5.设置业务报文
		// 加载当前用户信息

		WXorderQryGetRequestBean.setOperFlag("0");
		WXorderQryGetRequestBean.setAppTp("03");
		//WXorderQryGetRequestBean.setBizId("2022010918424520302013919");
		WXorderQryGetRequestBean.setBizTime(bizTime);
		WXorderQryGetRequestBean.setMerchId("872752322417150");
		WXorderQryGetRequestBean.setOrderId("S1202203170045626516");
		WXorderQryGetRequestBean.setTxTp("05");
		  System.out.println("+++++++++++++++"+new ObjectMapper().writeValueAsString(WXorderQryGetRequestBean));
		// 6.生成业务报文json对象
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(WXorderQryGetRequestBean));
		// 通讯获取报文对象
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// 响应成功
		if (oipRspBean.isSuccess()) {
			logger.info("响应码：{}，验签结果：{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			if (StringUtils.isNotBlank(bizJson)) {
				Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				System.out.println(bizData);
			}
		}
	}
	
	
	
	public static void closeWechatOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		logger.debug("-微信支付交易关闭-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.获取通讯连接对象
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.生成公共请求报文对象
		OipReqBean oipReqBean = new OipReqBean();
		// 设置应用id
		oipReqBean.setAppId(res.getString("appId_test"));
		// 设置开发者id
        oipReqBean.setDlpId(res.getString("dlpId_test"));
		// 设置产品id
        oipReqBean.setProdId(res.getString("wechat_prodId_test"));
		// 设置api方法名
		oipReqBean.setMethod("weixin.payment.trade.close");
		// 设置api版本
		oipReqBean.setVersion("1.0.0");
		// 设置时间戳

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp=sf.format(new Date());
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time1 = df2.parse(timestamp);
		String bizTime=df1.format(time1);
		oipReqBean.setTimestamp(sf.format(new Date()));
		// 3.生成请求配置对象,配置请求地址/url等信息
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// 应用私钥
		netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
		// 请求地址
		netReqConfBean.setNetUrl(res.getString("netUrl_test"));
		// 开发者公钥
		netReqConfBean.setPublicKey(res.getString("publicKey_test"));
		// 4.生成请求业务报文对象
		WXCloseGetRequestBean WXcloseGetRequestBean = new WXCloseGetRequestBean();
		// 5.设置业务报文
		// 5.设置业务报文
		// 加载当前用户信息

		WXcloseGetRequestBean.setAppTp("03");
		WXcloseGetRequestBean.setBizId(bizTime);
		WXcloseGetRequestBean.setBizTime(bizTime);
		WXcloseGetRequestBean.setMerchId(res.getString("bussines_test"));
		//WXcloseGetRequestBean.setOrderId("2222213sdfa");
		WXcloseGetRequestBean.setPurpPrtry("C4000001");
		WXcloseGetRequestBean.setRefundBizid("20210519111811");
		WXcloseGetRequestBean.setTxTp("16");
		  System.out.println("+++++++++++++++"+new ObjectMapper().writeValueAsString(WXcloseGetRequestBean));
		// 6.生成业务报文json对象
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(WXcloseGetRequestBean));
		// 通讯获取报文对象
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// 响应成功
		if (oipRspBean.isSuccess()) {
			logger.info("响应码：{}，验签结果：{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
			System.out.println(bizData);
		}
	}
	
	
	public static void refundWechatOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		// TODO Auto-generated method stub
		logger.debug("-微信支付退款-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.获取通讯连接对象
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.生成公共请求报文对象
		OipReqBean oipReqBean = new OipReqBean();
		// 设置应用id
		oipReqBean.setAppId(res.getString("appId_test"));
		// 设置开发者id
        oipReqBean.setDlpId(res.getString("dlpId_test"));
		// 设置产品id
        oipReqBean.setProdId(res.getString("wechat_prodId_test"));
		// 设置api方法名
		oipReqBean.setMethod("weixin.payment.refund.execute");
		// 设置api版本
		oipReqBean.setVersion("1.0.0");
		// 设置时间戳
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp=sf.format(new Date());
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time1 = df2.parse(timestamp);
		String bizTime=df1.format(time1);
		oipReqBean.setTimestamp(sf.format(new Date()));
		// 3.生成请求配置对象,配置请求地址/url等信息
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// 应用私钥
				netReqConfBean.setAppPrivateKey(res.getString("appPrivateKey_test"));
				// 请求地址
				netReqConfBean.setNetUrl(res.getString("netUrl_test"));
				// 开发者公钥
				netReqConfBean.setPublicKey(res.getString("publicKey_test"));
				// 4.生成请求业务报文对象
		WXRefundGetRequestBean WXrefundGetRequestBean = new WXRefundGetRequestBean();
		// 5.设置业务报文
		WXrefundGetRequestBean.setAppTp("03");
		WXrefundGetRequestBean.setBizAmount("3.00");
		WXrefundGetRequestBean.setBizId(bizTime);
		WXrefundGetRequestBean.setBizTime(bizTime);
		WXrefundGetRequestBean.setMerchId("872752322417156");
		//WXrefundGetRequestBean.setOrderId("41234563876");
		WXrefundGetRequestBean.setPurpPrtry("A1207004");
		WXrefundGetRequestBean.setRefundBizid("2021072310373310503099777");
		//WXrefundGetRequestBean.setOrderId("S1202105170028111160");
		WXrefundGetRequestBean.setTxTp("15");
		WXrefundGetRequestBean.setFeeType("CNY");
		System.out.println("+++++++++++++++"+new ObjectMapper().writeValueAsString(WXrefundGetRequestBean));
		// 6.生成业务报文json对象
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(WXrefundGetRequestBean));
		// 通讯获取报文对象
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// 响应成功
		if (oipRspBean.isSuccess()) {
			logger.info("响应码：{}，验签结果：{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			if (StringUtils.isNotBlank(bizJson)) {
				Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				System.out.println(bizData);
			}
		}
	}
	
	
	
	public static void findRefundWechatOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		logger.debug("-微信支付退款信息查询-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.获取通讯连接对象
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.生成公共请求报文对象
		OipReqBean oipReqBean = new OipReqBean();
		// 设置应用id
		oipReqBean.setAppId(appid);
		// 设置开发者id
        oipReqBean.setDlpId(dlpId);
		// 设置产品id
        oipReqBean.setProdId(prodId);
		// 设置api方法名
		oipReqBean.setMethod("weixin.payment.refund.qry");
		// 设置api版本
		oipReqBean.setVersion("1.0.0");
		// 设置时间戳
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp=sf.format(new Date());
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time1 = df2.parse(timestamp);
		String bizTime=df1.format(time1);
		oipReqBean.setTimestamp(sf.format(new Date()));
		// 3.生成请求配置对象,配置请求地址/url等信息
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// 应用私钥
		netReqConfBean.setAppPrivateKey(appPrivateKey);
		// 请求地址
		netReqConfBean.setNetUrl(netUrl);
		// 开发者公钥
		netReqConfBean.setPublicKey(publicKey);// 4.生成请求业务报文对象
		WXRefundQryGetRequestBean WXrefundQryGetRequestBean = new WXRefundQryGetRequestBean();
		// 5.设置业务报文
		// 加载当前用户信息

		WXrefundQryGetRequestBean.setOperFlag("1");
		WXrefundQryGetRequestBean.setAppTp("03");
		WXrefundQryGetRequestBean.setBizId("20210604104313");
		WXrefundQryGetRequestBean.setBizTime(bizTime);
		WXrefundQryGetRequestBean.setMerchId(merchId);
		//WXrefundQryGetRequestBean.setOrderId("2122312321");
		WXrefundQryGetRequestBean.setTxTp("05");
		System.out.println("+++++++++++++++"+new ObjectMapper().writeValueAsString(WXrefundQryGetRequestBean));
		// 6.生成业务报文json对象
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(WXrefundQryGetRequestBean));

		// 通讯获取报文对象
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// 响应成功
		if (oipRspBean.isSuccess()) {
			logger.info("响应码：{}，验签结果：{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			if (StringUtils.isNotBlank(bizJson)) {
				Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				System.out.println(bizData);
			}
		}
	}
	

}
