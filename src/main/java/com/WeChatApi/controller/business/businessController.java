package com.WeChatApi.controller.business;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.ibatis.annotations.Param;

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

import com.WeChatApi.bean.condition.businessCouponCondition;
import com.WeChatApi.bean.condition.couponCondition;
import com.WeChatApi.bean.condition.invoiceCondition;
import com.WeChatApi.bean.condition.parkinglotsCondition;
import com.WeChatApi.bean.condition.parkinglotsPayCondition;
import com.WeChatApi.bean.condition.storeChargeRecordCondition;
import com.WeChatApi.bean.dto.businessCouponDto;
import com.WeChatApi.bean.dto.couponDto;
import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.dto.meetingCodeDto;
import com.WeChatApi.bean.dto.rechargeDto;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.dto.storeChargeRecordDto;
import com.WeChatApi.bean.dto.userChargeRecordDto;
import com.WeChatApi.bean.dto.userRechargeRecordDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.userInfo;
import com.WeChatApi.bean.models.userStore;
import com.WeChatApi.controller.base.BaseController;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.businessService.businessService;
import com.WeChatApi.service.invoiceService.invoiceService;
import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.alipay.api.internal.util.AlipaySignature;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;




@RequestMapping("/business")
@Controller
public class businessController extends BaseController {
	
	@Autowired
	private businessService businessservice;

	@Autowired
	private wechatApiService wechatApiservice;
	
	
	/**
	 * �û��󶨳��̻�
	 * @param loginMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/userBind", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap userBind(@RequestBody Map<String, Object> loginMap) throws  IOException {
		logger.info("apiName:"+"/business/userBind"+"; param:"+new ObjectMapper().writeValueAsString(loginMap));
		try {
			businessservice.loginByWechat(loginMap);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	 * ����storeId��ȡ�̻���Ϣ
	 * @param storeMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findStoreInfo", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findStoreInfo(@RequestBody Map<String, Object> storeMap) throws  IOException {
		logger.info("apiName:"+"/business/findStoreInfo"+"; param:"+new ObjectMapper().writeValueAsString(storeMap));
		try {
			userStore store=businessservice.findUserStoreBySuId(storeMap);
			return backJsonResult(store,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	/***
	 * ����Ż�ȯ
	 * @param businessCouponCondition
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/addCoupon", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap addCoupon(@RequestBody businessCouponDto dto) throws  IOException {
		logger.info("apiName:"+"/business/addCoupon"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		try {
			businessservice.addCoupon(dto);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	 * ��ѯ�̻��Ż�ȯ
	 * @param dto
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findStoreCouponByCondition", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findStoreCouponByCondition(@RequestBody businessCouponCondition condition) throws  IOException {
		logger.info("apiName:"+"/business/findStoreCouponByCondition"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<businessCouponDto> list = null;
		try {
			businessservice.setStoreCouponUnused();//�������Ż�ȯc_status =2
			list = businessservice.findStoreCouponByCondition(condition);
			long total=businessservice.findStoreCouponCountByCondition(condition);
			return backJsonPageResult(list,total, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
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
	 * ��ѯ�û���ֵ�����˿���Ϣ
	 * @param dto
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findUserChargeInfoByCondition", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findUserChargeInfoByCondition(@RequestBody storeChargeRecordCondition condition) throws  IOException {
		logger.info("apiName:"+"/business/findUserChargeInfoByCondition"+"; param:"+new ObjectMapper().writeValueAsString(condition));
		List<storeChargeRecordDto> list = null;
		try {
			list = businessservice.findUserChargeInfoByCondition(condition);
			long total=businessservice.findUserChargeCountByCondition(condition);
			return backJsonPageResult(list,total, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
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
	
	
	
	
	
	/***
	 * �����Ż�ȯ
	 * @param businessCouponCondition
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/settleUpCoupon", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap settleUpCoupon(@RequestBody businessCouponDto dto) throws  IOException {
		logger.info("apiName:"+"/business/settleUpCoupon"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		try {
			businessservice.settleUpCoupon(dto);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	 * ��ѯ�ۿ��б�
	 * @param dto
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/findStoreDiscountByCondition", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap findStoreDiscountByCondition(@RequestBody Map<String, Object> paramMap) throws  IOException {
		logger.info("apiName:"+"/business/findStoreDiscountByCondition"+"; param:"+new ObjectMapper().writeValueAsString(paramMap));
		List<Map<String, Object>> list = null;
		try {
			list = businessservice.findStoreDiscountByCondition(paramMap);
			return backJsonResult(list, StatusCode.SUCESSLAYUI.getCode(), StatusCode.SUCESS.getErrorMsg());
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
	 * ΢�ų�ֵ
	 * @param paramMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/rechargeByWechat", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap rechargeByWechat(@RequestBody rechargeDto recharge,HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/business/rechargeByWechat"+"; param:"+new ObjectMapper().writeValueAsString(recharge));
		Map<String, Object>businessMap=new HashMap<String, Object>();
		try {
			businessMap=businessservice.rechargeByWechat(recharge,request);
			return backJsonResult(businessMap,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	 * �̻�΢��֧���ص��ӿ�
	 */
	/*@RequestMapping(value = "/wechatPayCallback", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
    public void payCallback(HttpServletRequest request,HttpServletResponse response) {
        String inputLine = "";
        String notityXml = "";
        try {
            while((inputLine = request.getReader().readLine()) != null){
                notityXml += inputLine;
            }
            //�ر���
            request.getReader().close();
            logger.info("+++++++++++++++++++++++++�̻�΢�Żص���ʼ+++++++++++++++++++++++++++++");
            logger.info("΢�Żص�������Ϣ��"+notityXml);
            //������Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            JSONObject jsonObject = JSONObject.fromObject(map);
            String scdId_storeId_recordId=map.get("attach");
            String[] aa=scdId_storeId_recordId.split("_");
            String scdId=aa[0];
            String storeId=aa[1];
            String balances=aa[2];
            String point=aa[3];
            //�ж� ֧���Ƿ�ɹ�
            //String scdId_storeId="";
            if("SUCCESS".equals(map.get("result_code"))){
                String outTradeNo = map.get("out_trade_no");
                String transactionId = map.get("transaction_id");
                String timeEnd = map.get("time_end");
                String couponFee = map.get("total_fee");
               
                //userChargeRecordDto userChargeRecord=businessservice.findChargeRecordByRid(recordId);
                
                businessservice.addUserStoreBalances(scdId_storeId_recordId);
                //businessservice.updateChargeRecordStatus(recordId,1);//�û���ֵ��¼��
                
                businessservice.inserStoreChargeRecord(Integer.valueOf(storeId),4,Integer.valueOf(scdId),Integer.valueOf(balances),Integer.valueOf(point));//�̻���ֵ��¼��
                businessservice.insertChargePayLog(jsonObject.toString(),recordId,"1");
                wechatApiservice.insertApiLogs("�̻�΢��֧���ص��ӿ�", "�����ţ�"+outTradeNo+";��ˮ�ţ�"+transactionId+";��"+couponFee+"�ۿ�_�̻���"+scdId_storeId_recordId, "success", "");
                
                StringBuffer buffer = new StringBuffer();
                buffer.append("<xml>");
                buffer.append("<return_code>"+map.get("result_code")+"</return_code>");
                buffer.append("<return_msg>OK</return_msg>");
                buffer.append("</xml>");

                //��΢�ŷ��������� �ɹ���ʾ �����һֱѯ�� ���Ƿ����� �Ƿ�ص��ɹ�
                PrintWriter writer = response.getWriter();
                //����
                writer.print(buffer.toString());
            }else{
            	//logger.info("΢�Żص������̻������ţ�"+map.get("out_trade_no")+"/"+map.get("err_code")+"/"+map.get("err_code_des"));
            	 
            	 //businessservice.updateChargeRecordStatus(recordId,3);
            	 //businessservice.insertChargePayLog(jsonObject.toString(),recordId,"2");
            	 wechatApiservice.insertApiLogs("�̻�΢��֧���ص��ӿ�", "�����ţ�"+map.get("out_trade_no")+";��ˮ�ţ�"+map.get("transaction_id")+";��"+map.get("coupon_fee")+"�ۿ�_�̻���"+map.get("attach"), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
            	 
            	StringBuffer buffer = new StringBuffer();
                buffer.append("<xml>");
                buffer.append("<return_code>"+map.get("result_code")+"</return_code>");
                buffer.append("<return_msg>OK</return_msg>");
                buffer.append("</xml>");

                //��΢�ŷ��������� �ɹ���ʾ �����һֱѯ�� ���Ƿ����� �Ƿ�ص��ɹ�
                PrintWriter writer = response.getWriter();
                //����
                writer.print(buffer.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
	
	
	@RequestMapping(value = "/wechatPayCallback_new", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
    public void wechatPayCallback_new(HttpServletRequest request,HttpServletResponse response) {
        String inputLine = "";
        String notityXml = "";
        try {
            while((inputLine = request.getReader().readLine()) != null){
                notityXml += inputLine;
            }
            //�ر���
            request.getReader().close();
            logger.info("+++++++++++++++++++++++++�̻�΢�Żص���ʼ+++++++++++++++++++++++++++++");
            logger.info("΢�Żص�������Ϣ��"+notityXml);
            //������Map
            Map<String,String> map =wechatApiservice.doXMLParse(notityXml);
            JSONObject jsonObject = JSONObject.fromObject(map);
            String scdId_storeId_recordId=map.get("attach");
            String[] aa=scdId_storeId_recordId.split("_");
            String scdId=aa[0];
            String storeId=aa[1];
            String recordId=aa[2];
            
            //�ж� ֧���Ƿ�ɹ�
            //String scdId_storeId="";
            if("SUCCESS".equals(map.get("result_code"))){
                String outTradeNo = map.get("out_trade_no");
                String transactionId = map.get("transaction_id");
                String timeEnd = map.get("time_end");
                String couponFee = map.get("total_fee");
               
                //storeChargeRecordDto storeChargeRecord=businessservice.findStoreChargeRecordByRid(recordId);
                long num =businessservice.findStoreChargePayLogCount(recordId);
                logger.info("store_charge_pay_log����¼��"+num);
                if(num!=0){
                	StringBuffer buffer = new StringBuffer();
                    buffer.append("<xml>");
                    buffer.append("<return_code>"+map.get("result_code")+"</return_code>");
                    buffer.append("<return_msg>OK</return_msg>");
                    buffer.append("</xml>");

                    //��΢�ŷ��������� �ɹ���ʾ �����һֱѯ�� ���Ƿ����� �Ƿ�ص��ɹ�
                    PrintWriter writer = response.getWriter();
                    //����
                    writer.print(buffer.toString());
                }else{
                	businessservice.addUserStoreBalances(scdId_storeId_recordId);
                    businessservice.updateStoreChargeRecordStatus(recordId,1);//�����̻���ֵ��
                    
                    //businessservice.inserStoreChargeRecord(Integer.valueOf(storeId),4,Integer.valueOf(scdId),storeChargeRecord.getR_balances(),storeChargeRecord.getR_point());//�̻���ֵ��¼��
                    businessservice.insertChargePayLog(jsonObject.toString(),recordId,"1");
                    wechatApiservice.insertApiLogs("�̻�΢��֧���ص��ӿ�", "�����ţ�"+outTradeNo+";��ˮ�ţ�"+transactionId+";��"+couponFee+"�ۿ�_�̻���"+scdId_storeId_recordId, "success", "");
                    
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("<xml>");
                    buffer.append("<return_code>"+map.get("result_code")+"</return_code>");
                    buffer.append("<return_msg>OK</return_msg>");
                    buffer.append("</xml>");

                    //��΢�ŷ��������� �ɹ���ʾ �����һֱѯ�� ���Ƿ����� �Ƿ�ص��ɹ�
                    PrintWriter writer = response.getWriter();
                    //����
                    writer.print(buffer.toString());
                }
                
            }else{
            	//logger.info("΢�Żص������̻������ţ�"+map.get("out_trade_no")+"/"+map.get("err_code")+"/"+map.get("err_code_des"));
            	 
            	 //businessservice.updateChargeRecordStatus(recordId,3);
            	 //businessservice.insertChargePayLog(jsonObject.toString(),recordId,"2");
            	 wechatApiservice.insertApiLogs("�̻�΢��֧���ص��ӿ�", "�����ţ�"+map.get("out_trade_no")+";��ˮ�ţ�"+map.get("transaction_id")+";��"+map.get("coupon_fee")+"�ۿ�_�̻���"+map.get("attach"), "fail", map.get("err_code")+"/"+map.get("err_code_des"));
            	 
            	StringBuffer buffer = new StringBuffer();
                buffer.append("<xml>");
                buffer.append("<return_code>"+map.get("result_code")+"</return_code>");
                buffer.append("<return_msg>OK</return_msg>");
                buffer.append("</xml>");

                //��΢�ŷ��������� �ɹ���ʾ �����һֱѯ�� ���Ƿ����� �Ƿ�ص��ɹ�
                PrintWriter writer = response.getWriter();
                //����
                writer.print(buffer.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * ֧������ֵ
	 * @param paramMap
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/rechargeByAlipay", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelMap rechargeByAlipay(@RequestBody rechargeDto recharge,HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/business/rechargeByAlipay"+"; param:"+new ObjectMapper().writeValueAsString(recharge));
		Map<String, Object>businessMap=new HashMap<String, Object>();
		try {
			businessMap=businessservice.rechargeByAlipay(recharge,request);
			return backJsonResult(businessMap,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	
	@RequestMapping(value = "/aliPayCallback", method = RequestMethod.POST)
	@ResponseBody
	@Transactional
	public String notifyUrl(HttpServletRequest request,HttpServletResponse response) throws Exception{
		 //��ȡ֧�������͹�������Ϣ
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        //ѭ����ȡ�����е�ֵ
        for(String str:requestParams.keySet()) {
            String name =str;
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                      : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("֧�����̻�������Ϣ��"+params);
        JSONObject jsonObject = JSONObject.fromObject(params);
        //����SDK��֤ǩ��
        boolean signVerified = AlipaySignature.rsaCheckV1(params, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqliWU4ndPe3KkRlQe85h2SDNb2l9UbPeaNmZZaced7Oc6T896WyZzB6+42gMQOOrRPWZui7ZCGmT8wzjTwFGXb9Zb/qmzXmV6nnQpz6NXCOJBL9VyKLm/EYjA0A5mwDwBD75trMXydcGWOdcQCuPi9HrH2YSJ9QUK2KzdCKQ3XMahhdtyjQcFxMXooVhPvnGi+4LgmsOJZU45cfZLnItHCPWDqRVrsoOjiNHHGVdUOxdKTor0suIE1W/IMfP0ITmMe0H1L+lItd+FBjDGfP2sd8R8phgrNi6b11Jg7ZXXCzsC9tkXn84lyKbWknybISYU9Sy1Lr9e/XgF9uX1jU76wIDAQAB", "UTF-8", "RSA2");

        if(signVerified) {
            //��ȡ��֧����״̬ TRADE_SUCCESS��֧���ɹ�
            //String trade_status =request.getParameter("trade_status");
        	String trade_status=jsonObject.getString("trade_status");
            int couponFee=0;
            //String scdId_storeId=request.getParameter("passback_params");
            String scdId_storeId=jsonObject.getString("passback_params");
          String[] aa=scdId_storeId.split("_");
          String scdId=aa[0];
          String storeId=aa[1];
          String recordId=aa[2];
          logger.info("trade_status��"+trade_status);
             if (trade_status.equals("TRADE_SUCCESS")){
            	 //bluecardservice.callBackNotify(request.getParameter("out_trade_no"),request.getParameter("trade_no"),request.getParameter("gmt_payment"),request.getParameter("total_amount"),"1");
            	 //couponFee=Integer.valueOf(request.getParameter("total_amount"))*100;
            	 //userChargeRecordDto userChargeRecord=businessservice.findChargeRecordByRid(recordId);
            	 logger.info("֧�����̻���Ϣ���¿�ʼ��"+scdId_storeId);
            	 businessservice.addUserStoreBalances(scdId_storeId);
            	
            	 
            	 //businessservice.insertChargePayLog("�����ţ�"+request.getParameter("out_trade_no")+";��ˮ�ţ�"+request.getParameter("trade_no")+";��"+couponFee+"�ۿ�_�̻���"+scdId_storeId,"�����ţ�"+request.getParameter("trade_no")+";��ˮ�ţ�"+request.getParameter("trade_no"),"1");
            	 businessservice.updateStoreChargeRecordStatus(recordId,1);
            	 //businessservice.inserStoreChargeRecord(Integer.valueOf(storeId),4,Integer.valueOf(scdId),userChargeRecord.getR_balances(),userChargeRecord.getR_point());//�̻���ֵ��¼��
            	 businessservice.insertChargePayLog(jsonObject.toString(),recordId,"1");
            	 wechatApiservice.insertApiLogs("֧�����̻�֧���ص��ӿ�", "�û�id"+request.getParameter("buyer_id")+"�����ţ�"+request.getParameter("out_trade_no")+";��ˮ�ţ�"+request.getParameter("trade_no")+";��"+request.getParameter("total_amount"), "success", "");
            	 return "success";
             }else {
            	 
            	 //businessservice.insertChargePayLog("�����ţ�"+request.getParameter("out_trade_no")+";��ˮ�ţ�"+request.getParameter("trade_no")+";��"+couponFee+"�ۿ�_�̻���"+scdId_storeId,"�����ţ�"+request.getParameter("trade_no")+";��ˮ�ţ�"+request.getParameter("trade_no"),"2");
            	 businessservice.updateChargeRecordStatus(recordId,3);
            	 businessservice.insertChargePayLog(jsonObject.toString(),recordId,"2");
            	 wechatApiservice.insertApiLogs("֧�����̻�֧���ص��ӿ�", "�û�id"+request.getParameter("buyer_id")+"�����ţ�"+request.getParameter("out_trade_no")+";��ˮ�ţ�"+request.getParameter("trade_no")+";��"+request.getParameter("total_amount"), "fail", params.toString());
            	 return "failure";
             }
        }
        //ǩ����֤ʧ��
        else {
        	logger.info("֧����ǩ����֤ʧ�ܣ�"+AlipaySignature.getSignCheckContentV1(params));
             
        	return "failure";
        }

	}
	
	
	/***
	 * �˿�����
	 * @param refundDto
	 * @return
	 */
	/*@RequestMapping(value = "/payRefund_xz", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap payRefund_xz(@RequestBody refundDto dto,HttpServletRequest request) {
		logger.info("apiName:"+"/business/payRefund_xz"+"; param:"+JSON.toJSONString(dto));
		try {
			businessservice.payRefund_xz(dto,request);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	
	/***
	 * �˿�����
	 * @param refundDto
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/payRefund", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap payRefund(@RequestBody refundDto dto,HttpServletRequest request) throws  IOException {
		logger.info("apiName:"+"/business/payRefund"+"; param:"+new ObjectMapper().writeValueAsString(dto));
		try {
			businessservice.payRefund(dto,request);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	/***
	 * ��ȡ�Ż�ȯ��ά���ַ���
	 * @param map
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/getERCodeString", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap getERCodeString(@RequestBody Map<String, Object> map) throws  IOException {
		logger.info("apiName:"+"/business/getERCodeString"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			String eRCode= businessservice.getERCodeString(map);
			return backJsonResult(eRCode,StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	
	/***
	 * ɨ����ȡ�Ż�ȯ
	 * @param map
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonGenerationException 
	 */
	@RequestMapping(value = "/scanERCodeGetCoupon", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap scanERCodeGetCoupon(@RequestBody Map<String, Object> map) throws  IOException {
		logger.info("apiName:"+"/business/scanERCodeGetCoupon"+"; param:"+new ObjectMapper().writeValueAsString(map));
		try {
			businessservice.scanERCodeGetCoupon(map);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	
	/***
	 * �˿�����
	 * @param refundDto
	 * @return
	 *//*
	@RequestMapping(value = "/insertUserRechargeRecordByDto", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	@Transactional
	public ModelMap insertUserRechargeRecordByDto(@RequestBody userRechargeRecordDto dto,HttpServletRequest request) {
		logger.info("apiName:"+"/business/payRefund"+"; param:"+JSON.toJSONString(dto));
		try {
			businessservice.insertUserRechargeRecordByDto(dto);
			return backJsonSuccessMsg(StatusCode.SUCESSWECHAT.getCode(), StatusCode.SUCESSWECHAT.getErrorMsg());
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
	
	
	
	
	

}
