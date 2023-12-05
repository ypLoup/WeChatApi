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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.eagle.sdk.base.INetTools;
import cn.com.eagle.sdk.bean.NetReqConfBean;
import cn.com.eagle.sdk.bean.OipReqBean;
import cn.com.eagle.sdk.bean.OipRspBean;
import cn.com.eagle.sdk.net.factory.NetToolsHttpFactory;

/**
 * ����ũ�Žӿ�
 * @author 39496
 *
 */
public class rccApiTest {
	
	private static String appid="TB9J2021052400002823";
	
	private static String dlpId="FB000000000830";
	
	private static String prodId="TB9J2020110417591975";
	
	private static String version="1.0.0";
	
	private static String appPrivateKey="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALgZXFASnrmlgCXr2lp167lbBp1hCs7p3/wNWtSWWL6b9LiyeSqH3n4V45f/F0VkHjvTJH+/xQkAlzd6rO2TkRP4GkkvysHITFLBxOH2iTEC/++zPznVGXpTINGGxq2LoG8tvlelw+dD2kaIQUpvNGSHE6ylzNkOTPulBSB6Meg3AgMBAAECgYAlt7RtB1OTr2+w1UB4Nh6kbBzP05j2SX5FUqsgUOk/rA4YZSBj8VBJPszRUEcBRYDYOusIbU6+CGvyxuR1DD21ozQoriuEL0ZNGKWE3mNYoGmyi64JwH11jwCZelWZcUMc9VxaCxonB0e+L+qXbdHPZYK0ckcRxjyVyU3N1O9zwQJBAPFKi7MX7vqv6rktN2ztsKlRuaTAovlB6ogs8Cq6SnKWS7/k5a2Krl3uyCH/CFJ49fek2a9JnXldynGLoJ1dZ+ECQQDDUk6JEZMz6QG54EKIsCsv0wdMqGSXLl5Yfv8HEcl8Ne3Oab+d9gftoH9+Ij1gQnQfF/+AaFk+I4cw7XTnk/MXAkALF9dGW5JUGiRbcRW8P01A643yYhzcAsjKi+5auIfVrs06vEZG7TpI/UJcNcJnMEJ2qTCM24CO5N36zGWM9o9hAkAr8F0Yeqqpt9YakXeGNlNQy+FNfmg6lkTFFbSbS4YO+jbIA4QGre00qLFll6BFAk4LHKPuqArDbsyf1htBaUf7AkAQ8GPVNMiagKbo85oKZb0RyqaVLOsQljjlyjfGNk0S5VaQOWuduDUGLwQyLzBsp782LDte8BERwmkho0tUNBcL";
	
	private static String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhDtlp0Oq+o88zAv0pkK494wN6PCkvlheRfGxAA8S0M377jfa7ON8EpGjhQ1TPZuLAYKXBHEEu6WKMuyYLpgfKswOkAteQQ9oIN4I9ckw7HuShssLFwZfHhjBYjkyOWErdA8lJr2RgHltK72udPIZXIU0prJuQlolNWdMJ1VS9jdzMg4iPkdN8hDUFGZFpm5hQFMAVYuPDimPKdOcMAoGCjn363GHPkfrH9mNKjVNBGgWsxrMDk9/jhZ8DiwsxZ/9Bb0KNc5Iv2xMK1UNZgxewSXffyipKNnMOYykV1PMHV2wrDTQONQg8xKNt/yo2NjK1xgUgjwEoUUao1ctFy9o2QIDAQAB";
    
	private static String netUrl="https://api.zj96596.com/oip-gateway-server/zjrcuoip/gateway.do";
	
	private static String merchId="871596222399729";
	
	/*******************����ͣ������***********************
    private static String appid="TB9J2021042000045461";
	
	private static String dlpId="FB000000043137";
	
	private static String prodId="TB9J2022060206215975";
	
	private static String version="1.0.0";
	
	private static String appPrivateKey="MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDICorSXk2juj2i2RRsvTiIQBuSgaQxCu6v+Wh9YdlH/VhgAzLklDLsyxaz0VCnCaWwFtigIX50Q0W2woPZnvJSd96VoyrW0kxm+nUGcTxR3iFfkK2cmr3E5Gm6lO2a+8xUFFXvzvILmGJuuo1NOZLTfOD0obqoED7+PgORluo41/w0qoskSMwXwllM6vWif6se5tUW8j197/W7ocMBsrj3OrYGqmXferVw3Z2p0N5GupNvMsodGA74wdS+PsVLG0iWTn0fwWYkXHsFl6H5lDQOs5kNnabWBkNPBMZWXYvbQYJB/PxvOTmosYczoygnF/0ypmrxZGJl4b6kRp6LUdkNAgMBAAECggEBAMNC3xzXJkvyCCYEZVZ7IUqF4SQR3ZZtiG1HM4Jn17LYjcIyfSP3sPnIh2GAqIxT82I9+rfb2dZcsBjkJ9VtvDVZgnBn8/H7Fs7SHRbrwyhafhMNYxnhRemB195jbscAkANmUjghtcwxs6tH95Nw5mv/konXsB5KtWJwYAVl3T0bgesJqUFid7Ab45xPXSp/cXbmsD8K+aT2D+Chhd0GFDkBxMrzooBZoSegUN8/uoX1KKfZLXSBEVy2AxUF2lbWqMhSEwK+J4T0qisnYoE65PsQ21vsuFQ5TnwNjaVm2I6dOuaaaBravbH0sif1a17QUHpOpNkv+q0Gs4KViSpKtTUCgYEA/lNNXMUapQFShcWI2nzL1pQhkMdAFcwj0ScgBN7B2jtvLi3MJWfDdTYXrGhHpZDHTHiv4x3C40ItoLKiLJGT2g+xhQ627tHF2ecEOi0OF6bvNSm4VLTM7ejB3J2KT/ZxYm1PuwvlDqXcaHJ8D69796mwMtlIrTLPnKpPdECQVucCgYEAyVu8tAg87VXNoDuY1DUqwqHwwgmQMWMdbamWNcO2x1vPw+98FeV7JUjLLssrAPVvtAAUkS8MWKaDnuCsKYUCXW4fZRCPXHxrKklMBLtIT2N7WvupiF/k0QOXr0Fy9maFRr/DFynwfBGqaIiHXKQklRPbMsCfDEFCfxn61PVB9esCgYEA3Oq/aLZOTsa3SDwuhJui9OH3xitOH6ET+7pgtdpJyaeXDCX8DwHzZv110u1CTy9T77lmD4Lqpz31JIIHHW/XXrQDH/GINitCEeX6Zg59D06SuztiQzJKUN6+h0TRwGPyWe1aNMBAWFj6/D92RKnKpXdg9sqHtutMLGpEWlc7CkECgYATP7LNXxA7+/zZOXlFqAN7cbGrSvC+1+PDTPfwEdKSQkOdTnKFQVfBW7EYuxFUWSIhifRtYJsEin6AnKe4nF96gQF9TtT1mWtBMpqSxTiHZlIvg2lUgR5FoNtKRK5xwaEx/zjuKPM1WRb6QTr3bzWUDr7gnitWJ5LsxZYF76ArmwKBgQCKrbMuGxMMewHl517kbBwV67uxEqzAo+8/Hs44HQuzeITRX+VY1OZzgsJLbq4ML3hzIrm3FY0p66h6zV3y+yw7v7UbWm8+l4In9tbWdgXwCcjXfbYbzqxa26mgz8087fZNZljo+1zVl67PgXC8mLqYdvlg2ivYZBsGz/yWNflmUA==";
	
	private static String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsfbdoj7mQ2C5bCq69iDan5ft6r3oO3GFa7Mtkxr8mevxrQjKLP3RBxNXzsV/lCm2Nr/P6/Pxohd11DI74fNThx+JtUFuiZTYXt7nGT/lSchhL9aqf+zrbNgLQB9yS3VhDg3NXixTcbSbnoBL4unjSBfG7AWCa1Bau98RoZcMHs10NVE1Xl74xXnuOUAxWhC722g6pZfv8EMcTxt0bQEi5cYZ17yahtmmOtCdx4hK26hDyCRib02rlsfRBK+Jxh0/5+L0eGjPbGj44QVb3WaO2D7jbogzba44uKZILqOYxTvR3jr8rMvuQ8Zbq2KKH3xGREXH0viRPJWQ4pC70bahNQIDAQAB";
    
	private static String netUrl="https://apiuat.zj96596.com.cn:8800/oip-gateway-server/zjrcuoip/gateway.do";
	
	private static String merchId="872752322417163";*/
	
	
	
	/*******************���Ƴ���ͣ������************************/
    /*private static String appid="TB9J2022080900032636";
	
	private static String dlpId="pb000020037277";
	
	private static String prodId="TB9J2021110931437302";
	
	private static String version="1.0.0";
	
	private static String appPrivateKey="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCEOBTk4sr5YAUoOG/OvLsDu5/t2UDjGGqkpIyO4C8zas8zfHA7jqQP1GEI7DvT3+KeV8pF/D0BRq3hGhrS9cxSWJeTK7/qixdg6vOPLNGOJmPZjD3mD/hfsWcVA6kK0EXD+OEedy7OPn/uLjiD5eNx+FET5J3aL4WnobsiRHVa6+3xbalvVJt86AcFYoVrXe2ptheGbMLNE8CbFUWNUhDT+VapeqA7bNqULfb20gSn/vngMSC2SCD4owkasqp0q1gE81ZM+dF/ac0S3dGiHvaOU1yALRgtQkR1VWl5lPUa092NtDZlYeCFrCzg3f6K9kasKzYCCtRsFFZ3ANbQDX+DAgMBAAECggEAJc+7+fD5xvM0xlnNb7v9Z4XiqKx2p/s1lDwSjlGCbhvfgYPWvNGADZ7Y2SpluaPPNSU6DWWjNJg35aYHgckVU39OaaIbUkHQv8CxQoK4swQ7jd1TXlW22/bp2dKgJx2EthSYnJPM6OJqx3ykYOP/dXpkBWJd2iW4/UV9NMGLJiLkmZ42tFMXONaqbJOm+4aMd8zwULE1ncidckv79WYLoSKypsAfWhjkNQzC6lW52xja4ESXpdRFvohZDIDLdEO4gFeOCriP+Cu+deui5vEIaAz+kfgT7/oEBHnJlIcX+Zsl8rt8WUF8PPuKK/f5ZY9H4Tv+22eWXAbLEMXUV5VugQKBgQDNo9rWp+xaRyoQBse+zSWHkqsDowcvf0eFjzAhYcvu6PruNEjPnw0u/zI7pwb7d1oZPvmx65YiXckNW1FgFZ3IMJD+BZErS8cv7nRdncRciXFPseKhfC+Nndc1nlvYIeNI9bm+S+ICNEPURbhAPr9l1Qtcuc7xW/N5yEXKKyGq4wKBgQCkmULwr2WxaEFSRoFgNn/BHCHxCP5H1k3k2D5w/2CjQQrCQrohxUQTF4Y8bjCMVYOapvXssjgbJDvs3/IUBUvWT17XxRCXxIIYriZLJBJFiTidAY76sW3LKO566DCqOdUvrnEZDI2I1SO76jRlBx34vKCZSJU/mG5ZZYhb0hva4QKBgANnjA7ztsED3plnU1VAVje5YOVthIfvxoJajYRv9w1chBL/gJkXXAOELBO6vy3YmwBB/6ZHbTPic5qqAV3i3brbuvrJos6hsYmEnB6UpxSVHfAg1MyxnqPskgyMM58PaJDjqJCS9wnsTZctFFDT+R05eretR9TNHenNPAKN1j/bAoGAL+Ug42RGVQ8fIs+9Sb/SAOEsxzR0uXaUh+icksjc1+RPOyOrgbQjEOMhfmgZ3FvtxYybttpNFSi//zdS+5xm6t+Bm4uOPntB1+ik3+yJHI7HWHhhoHUMm7c3XbA+qOnXLN63rjBG5vAOS9nkkRk3EW/h03iZZT33ri+rM/y5ekECgYBYcf4y8DeqfrwKVr1ymlbvqCBMtD4twlmWjH6SxeayFLT/hfVzu9e40CeP32hxyOW/3Cpn0WhUXX+4c/C1LJ6GKXNnG6uOZioUzsAK91ElrY7M/jqG2prCDBPU9yanfhS0rrlWJOIin7vuYOQ3RiZBCIS3XTtUgyVOkNDD8tbF6A==";
	
	private static String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlV5N9mM/pZLKKP1ERZO2LrUJ/gEFwvPmzIo2nirYWOxEO+GwyvyKA89K8dgz0OWqxkDrjnEqt3GgbS5TDmV2T8g+kuaAsoa2D+ulgvPpmZMSGqGhF3QZJJWHWGFOa1OI4x5i/+ZdVdA2Pdsp5gqF024QUI2pviaek5DTYd+UdAd2ghqGk8MViQDjqDh+t6tS+30HNfZTQKtKG0Jit4rJ0gFaKkkDqbwq8hV0/BDai615q2IIutIoTk5yxaZtEDUz+XZnOrAKvIk8/4ORDUqTBONAKSMsisDVc24RiMsy7aDsGR9dcq0ZuyIcRHubIpwa2TW0t4T3JQfyVOuor3qCTQIDAQAB";
	
	private static String netUrl="https://apiuat.zj96596.com.cn:8801/oip-gateway-server/zjrcuoip/gateway.do";
	
	private static String merchId="801531120013130771";*/
	
	private static Logger logger = LoggerFactory.getLogger("");
	public static void main(String[] args) throws ParseException, JsonParseException, JsonMappingException, IOException {
		
		//creatFourPayOrder();//����
		findFourYardsPayQryOrder();//��ѯ
		//closeFourOrder();//�ر�
		//refundOrder();//�˿�
		//findRefundOrder();//�˿��ѯ
		//WxBillGetDemo("872752322417163","20220830");
		
		/*String a="3.51";
		Float f = Float.parseFloat( a ); 
		
		System.out.println((int)(f*100));*/
		//System.out.println((int)((Math.random()*9+1)*1));
	}
	
       private static void WxBillGetDemo(String a,String t) throws JsonParseException, JsonMappingException, IOException {
		
		logger.debug("-΢��֧�����˵���ȡ-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.��ȡͨѶ���Ӷ���
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.���ɹ��������Ķ���
		OipReqBean oipReqBean = new OipReqBean();
		// ����Ӧ��id
		oipReqBean.setAppId(appid);
				// ���ÿ�����id
		oipReqBean.setDlpId(dlpId);
				// ���ò�Ʒid
		oipReqBean.setProdId(prodId);
		// ����api������
		oipReqBean.setMethod("zjrcuoip.payment.bill.downloadurl.query");
		// ����api�汾
		oipReqBean.setVersion("1.0.0");
		// ����ʱ���

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		oipReqBean.setTimestamp(sf.format(new Date()));
		// 3.�����������ö���,���������ַ/url����Ϣ
		NetReqConfBean netReqConfBean = new NetReqConfBean();
		// Ӧ��˽Կ
		netReqConfBean.setAppPrivateKey(appPrivateKey);
		// �����ַ
        netReqConfBean.setNetUrl(netUrl);
// �����߹�Կ
        netReqConfBean.setPublicKey(publicKey);// 4.��������ҵ���Ķ���
		WXBillGetRequestBean WXbillGetRequestBean = new WXBillGetRequestBean();
		// 5.����ҵ����
		// 5.����ҵ����
		// ���ص�ǰ�û���Ϣ
		WXbillGetRequestBean.setBillDate(t);
		WXbillGetRequestBean.setBillType("ALL");
		WXbillGetRequestBean.setMerchId(a);
		WXbillGetRequestBean.setTarType("GZIP");
		System.out.println("+++++++++++++++"+new ObjectMapper().writeValueAsString(WXbillGetRequestBean));
		// 6.����ҵ����json����
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(WXbillGetRequestBean));
		// ͨѶ��ȡ���Ķ���
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// ��Ӧ�ɹ�
		if (oipRspBean.isSuccess()) {
			logger.info("��Ӧ�룺{}����ǩ�����{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
			System.out.println(bizData.get("billDownloadUrl").toString());
		}
		
	}
	
	
	
	public static void creatFourPayOrder()throws ParseException, JsonParseException, JsonMappingException, IOException{
		
		logger.info("-��ɨ����ۺ�-��������-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.��ȡͨѶ���Ӷ���
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.���ɹ��������Ķ���
		OipReqBean oipReqBean = new OipReqBean();
		// ����Ӧ��id
		oipReqBean.setAppId(appid);
		// ���ÿ�����id
        oipReqBean.setDlpId(dlpId);
		// ���ò�Ʒid
        oipReqBean.setProdId(prodId);
				// ����api������
				oipReqBean.setMethod("zjrcuoip.pay.ord.pay");
				// ����api�汾
				oipReqBean.setVersion(version);
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
	    netReqConfBean.setAppPrivateKey(appPrivateKey);
		// �����ַ
		netReqConfBean.setNetUrl(netUrl);
		// �����߹�Կ
		netReqConfBean.setPublicKey(publicKey);
				// 4.��������ҵ���Ķ���
		//���²�����Ϊ������Ǳ���������սӿ��ĵ�������ʵ�ʳ�����������
		UnifiedOrderRequestBean unifiedOrderRequestBean = new UnifiedOrderRequestBean();
		unifiedOrderRequestBean.setAppTp("03");//Ӧ�����ͣ�01-���Զ�WEB���룬02-POS���룬03-�ƶ�Ӧ�ý��룬04-�ƶ���H5����
		unifiedOrderRequestBean.setPurpPrtry("A6004011");//ҵ������ A6004011
		unifiedOrderRequestBean.setMerchId("872752322417163");//�̻���
		unifiedOrderRequestBean.setTradeType("NATIVE");//NATIVE-ɨ��֧��
		unifiedOrderRequestBean.setBizId(bizTime);//ҵ����ˮ�� �̻�ϵͳ�ڲ������ţ�ֻ�������֡���Сд��ĸ���Ҳ����ظ���
		unifiedOrderRequestBean.setBizTime(bizTime);//����ʱ��
		unifiedOrderRequestBean.setBizAmount("0.01");//���׽��
		unifiedOrderRequestBean.setOrdTitle("���Ʒ��ͣ���ɷ�");//��������
		unifiedOrderRequestBean.setDeviceNo("1121112");//�豸��
		logger.info("apiName:"+"�ۺ�����ɨ��������"+"; param:"+new ObjectMapper().writeValueAsString(unifiedOrderRequestBean));
		// 5.����ҵ����json����
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(unifiedOrderRequestBean));
		// ͨѶ��ȡ���Ķ���
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// ��Ӧ�ɹ�
		if (oipRspBean.isSuccess()) {
			logger.info("��Ӧ�룺{}����ǩ�����{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			if (StringUtils.isNotBlank(bizJson)) {
				Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				//System.out.println(bizData);
				logger.info("apiName:"+"�ۺ�����ɨ��������"+"; return:"+new ObjectMapper().writeValueAsString(bizData));
			}
		}
		
	}
	
	
	/**
	 * ��ѯ����
	 * @return 
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public static void findFourYardsPayQryOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		
		logger.debug("-��ɨ����ۺ�-������ѯ-run--");
        // 1.��ȡͨѶ���Ӷ���
        INetTools netTools = NetToolsHttpFactory.getHttpInstance();
        // 2.���ɹ��������Ķ���
        OipReqBean oipReqBean = new OipReqBean();
        // ����Ӧ��id
        oipReqBean.setAppId(appid);
        // ���ÿ�����id
        oipReqBean.setDlpId(dlpId);
        // ���ò�Ʒid
        oipReqBean.setProdId(prodId);
        // ����api������
        oipReqBean.setMethod("zjrcuoip.pay.ord.query");
        // ����api�汾
        oipReqBean.setVersion("1.0.0");
        // ����ʱ���
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        oipReqBean.setTimestamp(sf.format(new Date()));
        // 3.�����������ö���,���������ַ/url����Ϣ
        NetReqConfBean netReqConfBean = new NetReqConfBean();
        // Ӧ��˽Կ
        netReqConfBean.setAppPrivateKey(appPrivateKey);
                // �����ַ
        netReqConfBean.setNetUrl(netUrl);
        // �����߹�Կ
        netReqConfBean.setPublicKey(publicKey);
        // 4.��������ҵ���Ķ���
        PayOrderQryGetRequestBean PayOrderGetRequestBean = new PayOrderQryGetRequestBean();
        // 5.����ҵ����
        // ���ص�ǰ�û���Ϣ
        PayOrderGetRequestBean.setAppTp("03");
        //PayOrderGetRequestBean.setBizId("202208061220011080222174");
        PayOrderGetRequestBean.setBizTime("20220907101414");
        PayOrderGetRequestBean.setMerchId("872752322417141");
        PayOrderGetRequestBean.setOrderId("S1202208250063718020");
        PayOrderGetRequestBean.setPurpPrtry("A9001001");
        // 6.����ҵ����json����
        oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(PayOrderGetRequestBean));
        // ͨѶ��ȡ���Ķ���
        OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
        // ��Ӧ�ɹ�
        if (oipRspBean.isSuccess()) {
            logger.info("��Ӧ�룺{}����ǩ�����{}", oipRspBean.getCode(), oipRspBean.isSignValid());
            String bizJson = oipRspBean.getBizContent();
            if (StringUtils.isNotBlank(bizJson)) {
                Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
                System.out.println(bizData);
            }
        }
	}
	
	
	
	
	
	
	
	
	public static void closeFourOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		logger.info("-��ɨ����ۺ�-�����ر�-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.��ȡͨѶ���Ӷ���
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.���ɹ��������Ķ���
		OipReqBean oipReqBean = new OipReqBean();
		// ����Ӧ��id
				oipReqBean.setAppId(appid);
				// ���ÿ�����id
				oipReqBean.setDlpId(dlpId);
				// ���ò�Ʒid
				oipReqBean.setProdId(prodId);
				// ����api������
				oipReqBean.setMethod("zjrcuoip.pay.ord.close");
				// ����api�汾
				oipReqBean.setVersion(version);
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
				netReqConfBean.setAppPrivateKey(appPrivateKey);
						// �����ַ
				netReqConfBean.setNetUrl(netUrl);
				// �����߹�Կ
				netReqConfBean.setPublicKey(publicKey);
				// 4.��������ҵ���Ķ���
		OrderCloseGetRequestBean orderCloseGetRequestBean = new OrderCloseGetRequestBean();
		// 5.����ҵ����
		// ���ص�ǰ�û���Ϣ
		orderCloseGetRequestBean.setAppTp("03");
		orderCloseGetRequestBean.setBizId(bizTime);
		orderCloseGetRequestBean.setBizTime(bizTime);
		orderCloseGetRequestBean.setMerchId(merchId);
		//orderCloseGetRequestBean.setOrderId("2122312321");
		orderCloseGetRequestBean.setPurpPrtry("C5000002");
		orderCloseGetRequestBean.setReverseBizid("20210527110230");
		logger.info("apiName:"+"�ۺ�����ɨ�رն���"+"; param:"+new ObjectMapper().writeValueAsString(orderCloseGetRequestBean));
		// 6.����ҵ����json����
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(orderCloseGetRequestBean));
		// ͨѶ��ȡ���Ķ���
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// ��Ӧ�ɹ�
		if (oipRspBean.isSuccess()) {
			logger.info("��Ӧ�룺{}����ǩ�����{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			if (StringUtils.isNotBlank(bizJson)) {
				Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
				System.out.println(bizData);
			}
		}
	}
	
	
	public static void refundOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		// TODO Auto-generated method stub
				logger.debug("-��ɨ����ۺ��˿�����-run--");
				ResourceBundle res = ResourceBundle.getBundle("rccApi");
				// 1.��ȡͨѶ���Ӷ���
				INetTools netTools = NetToolsHttpFactory.getHttpInstance();
				// 2.���ɹ��������Ķ���
				OipReqBean oipReqBean = new OipReqBean();
				oipReqBean.setAppId(appid);
				// ���ÿ�����id
				oipReqBean.setDlpId(dlpId);
				// ���ò�Ʒid
				oipReqBean.setProdId(prodId);
				// ����api������
				oipReqBean.setMethod("zjrcuoip.pay.refund.apply");
				// ����api�汾
				oipReqBean.setVersion(version);
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
				netReqConfBean.setAppPrivateKey(appPrivateKey);
						// �����ַ
				netReqConfBean.setNetUrl(netUrl);
				// �����߹�Կ
				netReqConfBean.setPublicKey(publicKey);// 4.��������ҵ���Ķ���
				UnitePayRefundRequestBean unitePayRefundRequestBean = new UnitePayRefundRequestBean();
				unitePayRefundRequestBean.setAppTp("03");
				unitePayRefundRequestBean.setAttach("");
				unitePayRefundRequestBean.setBizAmount("7.00");
				unitePayRefundRequestBean.setBizCurrency("CNY");
				unitePayRefundRequestBean.setBizId(bizTime);
				//unitePayRefundRequestBean.setBizRemark("��ע��Ϣ");
				unitePayRefundRequestBean.setBizTime(bizTime);
				unitePayRefundRequestBean.setDeviceNo("111");
				unitePayRefundRequestBean.setMerchId(merchId);
				//unitePayRefundRequestBean.setOperatorId("");
				//unitePayRefundRequestBean.setOrderId("");
				unitePayRefundRequestBean.setPurpPrtry("A1207005");
				unitePayRefundRequestBean.setRefundBizid("2021051712502110200878694");
				//unitePayRefundRequestBean.setOrderId("S1202105170028111160");
				//unitePayRefundRequestBean.setRefundDesc("�˿�ԭ��");
				//unitePayRefundRequestBean.setStoreId("");
				logger.info("apiName:"+"�ۺ�����ɨ�˿�����"+"; param:"+new ObjectMapper().writeValueAsString(unitePayRefundRequestBean));

				oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(unitePayRefundRequestBean));

				OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
				logger.info("��Ӧ�룺{}����ǩ�����{}", oipRspBean.getCode(), oipRspBean.isSignValid());
				String bizJson = oipRspBean.getBizContent();
				if (StringUtils.isNotBlank(bizJson)) {
					Map bizData = new ObjectMapper().readValue(bizJson, Map.class);
					System.out.println(bizData);
				}
	}
	
	
	
	public static void findRefundOrder() throws ParseException, JsonParseException, JsonMappingException, IOException{
		logger.debug("-��ɨ����ۺ�-�����˿��ѯ-run--");
		ResourceBundle res = ResourceBundle.getBundle("rccApi");
		// 1.��ȡͨѶ���Ӷ���
		INetTools netTools = NetToolsHttpFactory.getHttpInstance();
		// 2.���ɹ��������Ķ���
		OipReqBean oipReqBean = new OipReqBean();
		oipReqBean.setAppId(appid);
		// ���ÿ�����id
		oipReqBean.setDlpId(dlpId);
		// ���ò�Ʒid
		oipReqBean.setProdId(prodId);
		// ����api������
		oipReqBean.setMethod("zjrcuoip.pay.refund.query");
		// ����api�汾
		oipReqBean.setVersion(version);
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
		netReqConfBean.setAppPrivateKey(appPrivateKey);
				// �����ַ
		netReqConfBean.setNetUrl(netUrl);
		// �����߹�Կ
		netReqConfBean.setPublicKey(publicKey);// 4.��������ҵ���Ķ���// 4.��������ҵ���Ķ���
		QrCodeRefundQryGetRequestBean qrCodeRefundQryGetRequestBean = new QrCodeRefundQryGetRequestBean();
		// 5.����ҵ����
		//���ص�ǰ�û���Ϣ
		qrCodeRefundQryGetRequestBean.setAppTp("03");//Ӧ������  01-���Զ�WEB���룬02-POS���룬03-�ƶ�Ӧ�ý��룬04-�ƶ���H5����
		qrCodeRefundQryGetRequestBean.setPurpPrtry("A9001002");//A9001002-�˿��ѯ
		qrCodeRefundQryGetRequestBean.setMerchId(merchId);//�̻���
		qrCodeRefundQryGetRequestBean.setBizId("20210527153309");
		//qrCodeRefundQryGetRequestBean.setOrderId("232222222");
		qrCodeRefundQryGetRequestBean.setBizTime(bizTime);
		//qrCodeRefundQryGetRequestBean.setBizRemark("1111111111");
		logger.info("apiName:"+"�ۺ�����ɨ�˿��ѯ"+"; param:"+new ObjectMapper().writeValueAsString(qrCodeRefundQryGetRequestBean));
		// 6.����ҵ����json����
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(qrCodeRefundQryGetRequestBean));
		// ͨѶ��ȡ���Ķ���
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// ��Ӧ�ɹ�
		if (oipRspBean.isSuccess()) {
			logger.info("��Ӧ�룺{}����ǩ�����{}", oipRspBean.getCode(), oipRspBean.isSignValid());
			String bizJson = oipRspBean.getBizContent();
			if (StringUtils.isNotBlank(bizJson)) {
				Map bizData =new ObjectMapper().readValue(bizJson, Map.class);
				System.out.println(bizData);
			}
		}
	}
	

}
