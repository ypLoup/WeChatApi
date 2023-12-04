package com.WeChatApi.controller.RccApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.WeChatApi.bean.condition.userInfoCondition;
import com.WeChatApi.bean.condition.wechatUserCondition;
import com.WeChatApi.bean.dto.prePayDto;
import com.WeChatApi.bean.dto.prePayDto2;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.models.WXOrderQryGetRequestBean;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.parkinglotsPayRefund;
import com.WeChatApi.bean.models.redPack;
import com.WeChatApi.bean.models.userInfo;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.controller.base.WxDecodeUtil;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.businessService.businessService;
import com.WeChatApi.service.operationOrderService.operationOrderService;
import com.WeChatApi.service.rccApiService.rccApiService;
import com.WeChatApi.service.userInfoService.userInfoService;

import java.io.PrintWriter;
import java.security.Security;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.WeChatApi.service.wechatUserService.wechatUserService;
import com.WeChatApi.service.zfbApiService.zfbApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.eagle.sdk.base.INetTools;
import cn.com.eagle.sdk.bean.NetReqConfBean;
import cn.com.eagle.sdk.bean.OipReqBean;
import cn.com.eagle.sdk.bean.OipRspBean;
import cn.com.eagle.sdk.net.factory.NetToolsHttpFactory;
import sun.misc.BASE64Encoder;

@RequestMapping("/rccApi")
@Controller
public class RccApiController extends BaseController {
	
	@Autowired
	private rccApiService rccApi;
	

	
	
	
	/**
	 *农信接口获取聚合码
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * 
	 * 
	 */
	@RequestMapping(value = "/getJuHeCode", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getJuHeCode(@RequestBody Map<String,Object> juHeCodeMap) throws  IOException {
		logger.info("apiName:"+"/rccApi/getJuHeCode"+"; param:"+new ObjectMapper().writeValueAsString(juHeCodeMap));
		try {
			String codeUrl=rccApi.getJuHeCode(juHeCodeMap);
			return backJsonResult(codeUrl);
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	/**
	 *农信接口获取聚合码(新服务商)_路边
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * 
	 * 
	 */
	@RequestMapping(value = "/getJuHeCode_road", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getJuHeCode_road(@RequestBody Map<String,Object> juHeCodeMap) throws  IOException {
		logger.info("apiName:"+"/rccApi/getJuHeCode_road"+"; param:"+new ObjectMapper().writeValueAsString(juHeCodeMap));
		try {
			String codeUrl=rccApi.getJuHeCode_new(juHeCodeMap);
			return backJsonResult(codeUrl);
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	/**
	 *农信接口获取聚合码(新服务商)_封闭停车场
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * 
	 * 
	 */
	@RequestMapping(value = "/getJuHeCode_parking", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getJuHeCode_parking(@RequestBody Map<String,Object> juHeCodeMap) throws IOException {
		logger.info("apiName:"+"/rccApi/getJuHeCode_parking"+"; param:"+new ObjectMapper().writeValueAsString(juHeCodeMap));
		try {
			String codeUrl=rccApi.getJuHeCode_parking(juHeCodeMap);
			return backJsonResult(codeUrl);
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	/**
	 * 农信聚合退款
	 * @param juHeCodeMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/juHeRefundOrder", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap JuHeRefundOrder(@RequestBody Map<String,Object> juHeCodeMap) throws  IOException {
		logger.info("apiName:"+"/rccApi/JuHeRefundOrder"+"; param:"+new ObjectMapper().writeValueAsString(juHeCodeMap));
		try {
			String codeUrl=rccApi.JuHeRefundOrder(juHeCodeMap);
			return backJsonResult(codeUrl);
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	
	/**
	 * 农信聚合退款_新商户号——road
	 * @param juHeCodeMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/juHeRefundOrder_road", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap juHeRefundOrder_road(@RequestBody Map<String,Object> juHeCodeMap) throws  IOException {
		logger.info("apiName:"+"/rccApi/juHeRefundOrder_road"+"; param:"+new ObjectMapper().writeValueAsString(juHeCodeMap));
		try {
			String codeUrl=rccApi.juHeRefundOrder_road(juHeCodeMap);
			return backJsonResult(codeUrl);
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	
	/**
	 * 农信聚合退款_新商户号——parking
	 * @param juHeCodeMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/juHeRefundOrder_parking", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap juHeRefundOrder_parking(@RequestBody Map<String,Object> juHeCodeMap) throws IOException {
		logger.info("apiName:"+"/rccApi/juHeRefundOrder_parking"+"; param:"+new ObjectMapper().writeValueAsString(juHeCodeMap));
		try {
			String codeUrl=rccApi.juHeRefundOrder_parking(juHeCodeMap);
			return backJsonResult(codeUrl);
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	/**
	 * 农信聚合退款
	 * @param juHeCodeMap
	 * @return
	 */
	/*@RequestMapping(value = "/juHeRefundOrder", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap juHeRefundOrder_xz(@RequestBody Map<String,Object> juHeCodeMap) {
		logger.info("apiName:"+"/rccApi/juHeRefundOrder"+"; param:"+JSON.toJSONString(juHeCodeMap));
		try {
			String codeUrl=rccApi.JuHeRefundOrder(juHeCodeMap);
			return backJsonResult(codeUrl);
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}*/
	
	
	/**
	 *农信接口获取小程序聚合码（嘉善）
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * 
	 * 
	 */
	@RequestMapping(value = "/getWechatCode", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getWechatCode(@RequestBody Map<String,Object> wechatCodeMap) throws  IOException {
		logger.info("apiName:"+"/rccApi/getWechatCode"+"; param:"+new ObjectMapper().writeValueAsString(wechatCodeMap));
		try {
			Map codeUrl=rccApi.getWechatCode_new(wechatCodeMap);
			return backJsonResult(codeUrl);
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	/**
	 *其他支付方式
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 * 
	 * 
	 */
	@RequestMapping(value = "/paymentOther", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap paymentOther(@RequestBody Map<String,Object> wechatCodeMap) throws  IOException {
		logger.info("apiName:"+"/rccApi/paymentOther"+"; param:"+new ObjectMapper().writeValueAsString(wechatCodeMap));
		try {
			rccApi.paymentOther(wechatCodeMap);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	/**
	 *秀洲路边缴费
	 * 
	 * 
	 *//*
	@RequestMapping(value = "/getWechatCode", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap getWechatCode(@RequestBody Map<String,Object> wechatCodeMap, HttpServletRequest request) {
		logger.info("apiName:"+"/rccApi/getWechatCode_xiuzhou"+"; param:"+JSON.toJSONString(wechatCodeMap));
		try {
			Map codeUrl=rccApi.getWechatCode_new_xiuzhou(wechatCodeMap,request);
			return backJsonResult(codeUrl);
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}*/
	
	
	/**
	 * 农信微信小程序退款
	 * @param wechatCodeMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/wechatCodeRefundOrder", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap WechatCodeRefundOrder(@RequestBody Map<String,Object> wechatCodeMap) throws  IOException {
		logger.info("apiName:"+"/rccApi/WechatCodeRefundOrder"+"; param:"+new ObjectMapper().writeValueAsString(wechatCodeMap));
		try {
			String codeUrl=rccApi.WechatCodeRefundOrder(wechatCodeMap);
			return backJsonResult(codeUrl);
			//return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), "暂停服务！");
		}catch(BaseException e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(e.getErrorCode(), e.getErrorMsg());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return backJsonFailureMsg(StatusCode.SYSTEM_ERROR.getCode(), StatusCode.SYSTEM_ERROR.getErrorMsg());
		}
	}
	
	
	/**
	 * 查询农信小程序订单信息
	 * @param wechatCodeMap
	 * @return
	 * @throws ParseException 
	 * @throws IOException 
	 */
	@RequestMapping(value = "/findWechatPayQryOrder", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public JsonNode findWechatPayQryOrder(@RequestBody Map<String,Object> wechatCodeMap) throws ParseException, IOException {
		logger.info("apiName:"+"/rccApi/findWechatPayQryOrder"+"; param:"+new ObjectMapper().writeValueAsString(wechatCodeMap));
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
		WXorderQryGetRequestBean.setMerchId(wechatCodeMap.get("merchId").toString());
		WXorderQryGetRequestBean.setOrderId(wechatCodeMap.get("orderId").toString());
		WXorderQryGetRequestBean.setTxTp("05");
		// 6.生成业务报文json对象
		oipReqBean.setBizContent(new ObjectMapper().writeValueAsString(WXorderQryGetRequestBean));
		// 通讯获取报文对象
		OipRspBean oipRspBean = netTools.execute(oipReqBean, netReqConfBean);
		// 响应成功
		if (oipRspBean.isSuccess()) {
			
			String bizJson = oipRspBean.getBizContent();
			if (StringUtils.isNotBlank(bizJson)) {
				//Map bizData = JSON.parseObject(bizJson, Map.class);
				ObjectMapper objectMapper = new ObjectMapper();
				//JSONObject jsonObject =  JSON.parseObject(bizJson);
				JsonNode jsonNode = objectMapper.readTree(bizJson);
				return jsonNode;
			}
		}
		return null;
	
	}

}
