package com.WeChatApi.bean.models;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class rccApiTest_new {

	/*******************嘉善场内停车测试************************/
    private static String appid="TB9J2022080900032636";
	
	private static String dlpId="pb000020037277";
	
	private static String prodId="TB9J2021110931437302";
	
	private static String version="1.0.0";
	
	private static String appPrivateKey="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCEOBTk4sr5YAUoOG/OvLsDu5/t2UDjGGqkpIyO4C8zas8zfHA7jqQP1GEI7DvT3+KeV8pF/D0BRq3hGhrS9cxSWJeTK7/qixdg6vOPLNGOJmPZjD3mD/hfsWcVA6kK0EXD+OEedy7OPn/uLjiD5eNx+FET5J3aL4WnobsiRHVa6+3xbalvVJt86AcFYoVrXe2ptheGbMLNE8CbFUWNUhDT+VapeqA7bNqULfb20gSn/vngMSC2SCD4owkasqp0q1gE81ZM+dF/ac0S3dGiHvaOU1yALRgtQkR1VWl5lPUa092NtDZlYeCFrCzg3f6K9kasKzYCCtRsFFZ3ANbQDX+DAgMBAAECggEAJc+7+fD5xvM0xlnNb7v9Z4XiqKx2p/s1lDwSjlGCbhvfgYPWvNGADZ7Y2SpluaPPNSU6DWWjNJg35aYHgckVU39OaaIbUkHQv8CxQoK4swQ7jd1TXlW22/bp2dKgJx2EthSYnJPM6OJqx3ykYOP/dXpkBWJd2iW4/UV9NMGLJiLkmZ42tFMXONaqbJOm+4aMd8zwULE1ncidckv79WYLoSKypsAfWhjkNQzC6lW52xja4ESXpdRFvohZDIDLdEO4gFeOCriP+Cu+deui5vEIaAz+kfgT7/oEBHnJlIcX+Zsl8rt8WUF8PPuKK/f5ZY9H4Tv+22eWXAbLEMXUV5VugQKBgQDNo9rWp+xaRyoQBse+zSWHkqsDowcvf0eFjzAhYcvu6PruNEjPnw0u/zI7pwb7d1oZPvmx65YiXckNW1FgFZ3IMJD+BZErS8cv7nRdncRciXFPseKhfC+Nndc1nlvYIeNI9bm+S+ICNEPURbhAPr9l1Qtcuc7xW/N5yEXKKyGq4wKBgQCkmULwr2WxaEFSRoFgNn/BHCHxCP5H1k3k2D5w/2CjQQrCQrohxUQTF4Y8bjCMVYOapvXssjgbJDvs3/IUBUvWT17XxRCXxIIYriZLJBJFiTidAY76sW3LKO566DCqOdUvrnEZDI2I1SO76jRlBx34vKCZSJU/mG5ZZYhb0hva4QKBgANnjA7ztsED3plnU1VAVje5YOVthIfvxoJajYRv9w1chBL/gJkXXAOELBO6vy3YmwBB/6ZHbTPic5qqAV3i3brbuvrJos6hsYmEnB6UpxSVHfAg1MyxnqPskgyMM58PaJDjqJCS9wnsTZctFFDT+R05eretR9TNHenNPAKN1j/bAoGAL+Ug42RGVQ8fIs+9Sb/SAOEsxzR0uXaUh+icksjc1+RPOyOrgbQjEOMhfmgZ3FvtxYybttpNFSi//zdS+5xm6t+Bm4uOPntB1+ik3+yJHI7HWHhhoHUMm7c3XbA+qOnXLN63rjBG5vAOS9nkkRk3EW/h03iZZT33ri+rM/y5ekECgYBYcf4y8DeqfrwKVr1ymlbvqCBMtD4twlmWjH6SxeayFLT/hfVzu9e40CeP32hxyOW/3Cpn0WhUXX+4c/C1LJ6GKXNnG6uOZioUzsAK91ElrY7M/jqG2prCDBPU9yanfhS0rrlWJOIin7vuYOQ3RiZBCIS3XTtUgyVOkNDD8tbF6A==";
	
	private static String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlV5N9mM/pZLKKP1ERZO2LrUJ/gEFwvPmzIo2nirYWOxEO+GwyvyKA89K8dgz0OWqxkDrjnEqt3GgbS5TDmV2T8g+kuaAsoa2D+ulgvPpmZMSGqGhF3QZJJWHWGFOa1OI4x5i/+ZdVdA2Pdsp5gqF024QUI2pviaek5DTYd+UdAd2ghqGk8MViQDjqDh+t6tS+30HNfZTQKtKG0Jit4rJ0gFaKkkDqbwq8hV0/BDai615q2IIutIoTk5yxaZtEDUz+XZnOrAKvIk8/4ORDUqTBONAKSMsisDVc24RiMsy7aDsGR9dcq0ZuyIcRHubIpwa2TW0t4T3JQfyVOuor3qCTQIDAQAB";
	
	private static String netUrl="https://apiuat.zj96596.com.cn:8801/oip-gateway-server/zjrcuoip/gateway.do";
	
	private static String merchId="801531120013130771";
	
	private static Logger logger = LoggerFactory.getLogger("");
	public static void main(String[] args) throws ParseException, JsonParseException, JsonMappingException, IOException {
		
		//creatFourPayOrder();//创建
		//findFourYardsPayQryOrder();//查询
		//closeFourOrder();//关闭
		//refundOrder();//退款
		//findRefundOrder();//退款查询
		//FW000000132， road FW000001055
		WxBillGetDemo("FW000000132");
	}
	
       private static void WxBillGetDemo(String mchSeq) throws JsonParseException, JsonMappingException, IOException {
		
		logger.debug("-微信支付对账单获取-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi_new");
		// 1.获取通讯连接对象
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.生成公共请求报文对象
		OipReqBean oipReqBean = new OipReqBean();
		// 设置应用id
		oipReqBean.setAppId(res.getString("parking_appId_test"));
					
		// 设置开发者id
		oipReqBean.setDlpId(res.getString("parking_dlpId_test"));
		// 设置产品id
		oipReqBean.setProdId(res.getString("parking_prodId_test"));
		// 设置api方法名
		oipReqBean.setMethod("zjrcuoip.payment.uas.bill.downloadurl.query");
		// 设置api版本
		oipReqBean.setVersion("1.0.0");
		// 设置时间戳

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		oipReqBean.setTimestamp(sf.format(new Date()));
		// 3.生成请求配置对象,配置请求地址/url等信息
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		netReqConfBean.setAppPrivateKey(res.getString("parking_appPrivateKey_test"));
		// 请求地址
        netReqConfBean.setNetUrl(res.getString("parking_netUrl_test"));
        // 开发者公钥
        netReqConfBean.setPublicKey(res.getString("parking_publicKey_test"));
		/*WXBillGetRequestBean WXbillGetRequestBean = new WXBillGetRequestBean();
		// 5.设置业务报文
		// 5.设置业务报文
		// 加载当前用户信息
		WXbillGetRequestBean.setBillDate("20230514");
		WXbillGetRequestBean.setBillType("ALL");
		WXbillGetRequestBean.setMerchId("872752320215307297");
		WXbillGetRequestBean.setTarType("GZIP");
		System.out.println("+++++++++++++++"+JSON.toJSONString(WXbillGetRequestBean));*/
      //以map接收所有参数生成业务报文，主要是必输项，具体参考接口文档
      		Map map =new HashMap();
      		map.put("billDate", "20231124");
      		map.put("isvSeq", mchSeq);
      		map.put("billType", "ALL");
      		map.put("tarType", "GZIP");
      		String bizContent = new ObjectMapper().writeValueAsString(map);
      		System.out.println(bizContent);
		// 6.生成业务报文json对象
		oipReqBean.setBizContent(bizContent);
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
	
	
	
	public static void creatFourPayOrder()throws ParseException, IOException{
		
		logger.info("-主扫四码聚合-订单创建-run--");
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
				oipReqBean.setMethod("zjrcuoip.pay.uas.trxcrt");
				// 设置api版本
				oipReqBean.setVersion(version);
		// 设置时间戳
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp=sf.format(new Date());
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time1 = df2.parse(timestamp);
		String bizTime=df1.format(time1);
		oipReqBean.setTimestamp(timestamp);
		// 3.生成请求配置对象,配置请求地址/url等信息
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// 应用私钥
	    netReqConfBean.setAppPrivateKey(appPrivateKey);
		// 请求地址
		netReqConfBean.setNetUrl(netUrl);
		// 开发者公钥
		netReqConfBean.setPublicKey(publicKey);
				// 4.生成请求业务报文对象
		//以下参数仅为必输项，非必输项请参照接口文档，根据实际场景自行设置
		/*UnifiedOrderRequestBean unifiedOrderRequestBean = new UnifiedOrderRequestBean();
		unifiedOrderRequestBean.setAppTp("03");//应用类型，01-电脑端WEB接入，02-POS接入，03-移动应用接入，04-移动端H5接入
		unifiedOrderRequestBean.setPurpPrtry("A6004011");//业务种类 A6004011
		unifiedOrderRequestBean.setMerchId(merchId);//商户号
		unifiedOrderRequestBean.setTradeType("NATIVE");//NATIVE-扫码支付
		unifiedOrderRequestBean.setBizId(bizTime);//业务流水号 商户系统内部订单号，只能是数字、大小写字母，且不能重复。
		unifiedOrderRequestBean.setBizTime(bizTime);//交易时间
		unifiedOrderRequestBean.setBizAmount("0.01");//交易金额
		unifiedOrderRequestBean.setOrdTitle("嘉善封闭停车缴费");//订单标题
		unifiedOrderRequestBean.setDeviceNo("1121112");//设备号		
*/
		Map map =new HashMap();
		//map.put("isvChnlId", "123456");
		//map.put("isvSeq", "123456");
		//map.put("isvAppId", "123456");
		map.put("mchSeq", merchId);
		map.put("txId", bizTime);
		//map.put("txTp", "01");
		//map.put("bizCgy", "A001");
		map.put("purpPrtry", "A001007");
		map.put("txDtTm", bizTime);
		map.put("amt", "1.00");
		map.put("subject", "嘉善封闭停车缴费");
		map.put("areaCd", "330000");
		map.put("termId", "00000000");
		map.put("notifyUrl", "http://101.68.222.195:8081/platform/openApi/rccNotifyInfo");
		
		
		logger.info("apiName:"+"聚合码主扫创建订单"+"; param:"+new ObjectMapper().writeValueAsString(map));
		// 5.生成业务报文json对象
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(map));
		// 通讯获取报文对象
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// 响应成功
		if (oipRspBean.isSuccess()) {
			logger.info("响应码：{}，验签结果：{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			if (StringUtils.isNotBlank(bizJson)) {
				Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				//System.out.println(bizData);
				logger.info("apiName:"+"聚合码主扫创建订单"+"; return:"+new ObjectMapper().writeValueAsString(bizData));
			}
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
	public static void findFourYardsPayQryOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		
		logger.info("-被扫四码聚合-订单查询-run--");
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
		oipReqBean.setMethod("zjrcuoip.pay.ord.query");
		// 设置api版本
		oipReqBean.setVersion(version);
		// 设置时间戳
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		oipReqBean.setTimestamp(sf.format(new Date()));
		// 3.生成请求配置对象,配置请求地址/url等信息
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// 应用私钥
		netReqConfBean.setAppPrivateKey(appPrivateKey);
				// 请求地址
		netReqConfBean.setNetUrl(netUrl);
		// 开发者公钥
		netReqConfBean.setPublicKey(publicKey);
		// 4.生成请求业务报文对象
		PayOrderQryGetRequestBean PayOrderGetRequestBean = new PayOrderQryGetRequestBean();
		// 5.设置业务报文
		PayOrderGetRequestBean.setAppTp("03");
		PayOrderGetRequestBean.setBizId("20210526153645");
		//SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateFormat df1 = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp=sf.format(new Date());
		DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time1 = df2.parse(timestamp);
		String bizTime=df1.format(time1);
		PayOrderGetRequestBean.setBizTime(bizTime);
		PayOrderGetRequestBean.setMerchId(merchId);
		//PayOrderGetRequestBean.setOrderId("2122312321");
		PayOrderGetRequestBean.setPurpPrtry("A9001001");
		logger.info("apiName:"+"聚合码查询订单"+"; param:"+new ObjectMapper().writeValueAsString(PayOrderGetRequestBean));
		// 6.生成业务报文json对象
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(PayOrderGetRequestBean));
		// 通讯获取报文对象
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// 响应成功
		if (oipRspBean.isSuccess()) {
			logger.info("响应码：{}，验签结果：{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			if (StringUtils.isNotBlank(bizJson)) {
				Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				System.out.println(bizData);
				//打印：{cashFee=null, industrySepcDetail=null, advanceAmount=null, orderId=S1202104020026692229, settleAmount=null, discountAmount=null, invoiceAmount=null, buyerLogonId=null, buyerUserName=null, returnCode=M, asyncPaymentMode=null, subCode=0000, settleTransRate=null, cashFeeCurrency=CNY, cardBalance=null, bizId=20210402110211001, cardAttr=null, storeName=null, attach={"appId":"TB9J2021040100043159","thirdAttach":""}, tradeType=NATIVE, settlementTotalFee=null, transPayRate=null, transTime=20210402, pointAmount=null, buyerUserType=null, bankType=null, accNo=null, settleCurrency=CNY, accName=null, bizCurrency=CNY, bizAmount=1.00, transactionId=null, chargeFlags=null, returnMsg=处理中, tunnelType=null, subOpenid=null, subMsg=null, authTradePayMode=null, mdiscountAmount=null}

			}
		}
	}
	
	
	
	public static void closeFourOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		logger.info("-主扫四码聚合-订单关闭-run--");
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
				oipReqBean.setMethod("zjrcuoip.pay.uas.ordclose");
				// 设置api版本
				oipReqBean.setVersion(version);
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
				netReqConfBean.setPublicKey(publicKey);
				// 4.生成请求业务报文对象
		/*OrderCloseGetRequestBean orderCloseGetRequestBean = new OrderCloseGetRequestBean();
		// 5.设置业务报文
		// 加载当前用户信息
		orderCloseGetRequestBean.setAppTp("03");
		orderCloseGetRequestBean.setBizId(bizTime);
		orderCloseGetRequestBean.setBizTime(bizTime);
		orderCloseGetRequestBean.setMerchId(merchId);
		//orderCloseGetRequestBean.setOrderId("2122312321");
		orderCloseGetRequestBean.setPurpPrtry("C5000002");
		orderCloseGetRequestBean.setReverseBizid("20210527110230");*/
				Map map =new HashMap();
				//map.put("isvChnlId", "123456");
				//map.put("isvSeq", "123456");
				//map.put("isvAppId", "123456");
				map.put("mchSeq", merchId);
				//map.put("txTp", "04");
				map.put("bizId", bizTime);
				map.put("orglTxId", "20220819173004");
				//map.put("orglMsgId", "");
				map.put("txDtTm", bizTime);
				map.put("remark", "订单关闭");
		logger.info("apiName:"+"聚合码主扫关闭订单"+"; param:"+new ObjectMapper().writeValueAsString(map));
		// 6.生成业务报文json对象
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(map));
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
	
	
	public static void refundOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		// TODO Auto-generated method stub
				logger.debug("-被扫四码聚合退款申请-run--");
				ResourceBundle res = ResourceBundle.getBundle("rccApi_new");
				// 1.获取通讯连接对象
				INetTools netTools = NetToolsHttpFactory.getHttpInstance();
				// 2.生成公共请求报文对象
				OipReqBean oipReqBean = new OipReqBean();
				oipReqBean.setAppId(res.getString("appId_test"));
				// 设置开发者id
				oipReqBean.setDlpId(res.getString("dlpId_test"));
				// 设置产品id
				oipReqBean.setProdId(res.getString("prodId_test"));
				// 设置api方法名
				oipReqBean.setMethod("zjrcuoip.pay.uas.refund");
				// 设置api版本
				oipReqBean.setVersion(version);
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
				netReqConfBean.setPublicKey(res.getString("publicKey_test"));// 4.生成请求业务报文对象
				/*UnitePayRefundRequestBean unitePayRefundRequestBean = new UnitePayRefundRequestBean();
				unitePayRefundRequestBean.setAppTp("03");
				unitePayRefundRequestBean.setAttach("");
				unitePayRefundRequestBean.setBizAmount("7.00");
				unitePayRefundRequestBean.setBizCurrency("CNY");
				unitePayRefundRequestBean.setBizId(bizTime);
				unitePayRefundRequestBean.setBizTime(bizTime);
				unitePayRefundRequestBean.setDeviceNo("111");
				unitePayRefundRequestBean.setMerchId(merchId);
		
				unitePayRefundRequestBean.setPurpPrtry("A1207005");
				unitePayRefundRequestBean.setRefundBizid("2021051712502110200878694");*/
				
				Map map =new HashMap();
				//map.put("isvChnlId", "123456");
				//map.put("isvSeq", "123456");
				//map.put("isvAppId", "123456");
				map.put("mchSeq", "872752320064750885");
				map.put("attach", "测试退款");
				map.put("txId", "202211170113");
				map.put("txDtTm", bizTime);
				map.put("orglTradeNo", "SPS202211080943327192025185650");
				map.put("txAmt", "0.01");
				map.put("notifyUrl", "https://jiashanmp.iparking.tech/pos/notify_nx_juhe_new.php");
			
				logger.info("apiName:"+"聚合码主扫退款请求"+"; param:"+new ObjectMapper().writeValueAsString(map));

				oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(map));

				OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
				logger.info("响应码：{}，验签结果：{}", oipRspBean.getCode(), oipRspBean.isSignValid());
				String bizJson = oipRspBean.getBizContent();
				if (StringUtils.isNotBlank(bizJson)) {
					Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
					System.out.println(bizData);
				}
	}
	
	
	
	public static void findRefundOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		logger.debug("-被扫四码聚合-订单退款查询-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.获取通讯连接对象
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.生成公共请求报文对象
		OipReqBean oipReqBean = new OipReqBean();
		oipReqBean.setAppId(appid);
		// 设置开发者id
		oipReqBean.setDlpId(dlpId);
		// 设置产品id
		oipReqBean.setProdId(prodId);
		// 设置api方法名
		oipReqBean.setMethod("zjrcuoip.pay.refund.query");
		// 设置api版本
		oipReqBean.setVersion(version);
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
		netReqConfBean.setPublicKey(publicKey);// 4.生成请求业务报文对象// 4.生成请求业务报文对象
		QrCodeRefundQryGetRequestBean qrCodeRefundQryGetRequestBean = new QrCodeRefundQryGetRequestBean();
		// 5.设置业务报文
		//加载当前用户信息
		qrCodeRefundQryGetRequestBean.setAppTp("03");//应用类型  01-电脑端WEB接入，02-POS接入，03-移动应用接入，04-移动端H5接入
		qrCodeRefundQryGetRequestBean.setPurpPrtry("A9001002");//A9001002-退款查询
		qrCodeRefundQryGetRequestBean.setMerchId(merchId);//商户号
		qrCodeRefundQryGetRequestBean.setBizId("20210527153309");
		//qrCodeRefundQryGetRequestBean.setOrderId("232222222");
		qrCodeRefundQryGetRequestBean.setBizTime(bizTime);
		//qrCodeRefundQryGetRequestBean.setBizRemark("1111111111");
		logger.info("apiName:"+"聚合码主扫退款查询"+"; param:"+new ObjectMapper().writeValueAsString(qrCodeRefundQryGetRequestBean));
		// 6.生成业务报文json对象
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(qrCodeRefundQryGetRequestBean));
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
