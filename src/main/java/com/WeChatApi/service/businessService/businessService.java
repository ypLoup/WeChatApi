package com.WeChatApi.service.businessService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.Pack200.Packer;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpException;
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
import org.springframework.transaction.annotation.Transactional;

import com.WeChatApi.bean.condition.businessCouponCondition;
import com.WeChatApi.bean.condition.storeChargeRecordCondition;
import com.WeChatApi.bean.dto.businessCouponDto;
import com.WeChatApi.bean.dto.rechargeDto;
import com.WeChatApi.bean.dto.refundDto;
import com.WeChatApi.bean.dto.storeChargeDiscountDto;
import com.WeChatApi.bean.dto.storeChargeRecordDto;
import com.WeChatApi.bean.dto.storeCouponRecordDto;
import com.WeChatApi.bean.dto.userChargeRecordDto;
import com.WeChatApi.bean.dto.userRechargeRecordDto;
import com.WeChatApi.bean.dto.userStoreDto;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.parkinglotsPayRefund;
import com.WeChatApi.bean.models.redPack;
import com.WeChatApi.bean.models.roadParkinglotsPay;
import com.WeChatApi.bean.models.userStore;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseException;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.CommonUtil;
import com.WeChatApi.controller.base.MD5Util;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.businessMapper;
import com.WeChatApi.dao.roadParkinglotsPayMapper;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.wechatApiService.wechatApiService;
import com.WeChatApi.service.zfbApiService.zfbApiService;
import com.fasterxml.jackson.databind.ObjectMapper;




@Service
public class businessService {
	
	@Autowired
	private businessMapper businessmapper;
	
	@Autowired
	private wechatUserMapper usermapper;
	
	@Autowired
	private wechatApiService wechatservice;
	
	@Autowired
	private zfbApiService alipayservice;
	
	@Autowired
	private roadParkinglotsPayMapper roadpaymapper;
	
	@Autowired
	private blueCardService blue;
	
	private static Logger log = Logger.getLogger(redPack.class.getName());

	public void loginByWechat(Map<String, Object> loginMap) {
		
		String userOpenId=loginMap.get("userOpenId").toString();
		
		String userName=loginMap.get("userName").toString();
		
		String password=loginMap.get("password").toString();
		
		if(StringUtils.isBlank(userOpenId)){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"С����openId����Ϊ�գ�");
		}
		
		if(StringUtils.isBlank(userName)){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�̻�ƽ̨�˺Ų���Ϊ�գ�");
				}
		
		if(StringUtils.isBlank(password)){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�̻�ƽ̨���벻��Ϊ�գ�");
		}
		
		String mdPassword=DigestUtils.md5Hex((password+"_za2e3s0").getBytes());
		
		userStore userStore = businessmapper.findUserStoreByUserName(userName,mdPassword);
		if(userStore==null){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�޶�Ӧ�̻���Ϣ���޷��󶨣�");	
		}
		
		
		
	    wechatUser user=usermapper.findWechatUserInfoByOpenId(userOpenId);
	    
	    if(user==null){
	    	throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�޶�Ӧ�û���Ϣ���޷��󶨣�");
	    }
	    
	    
	    
	    List<userStore> userStoreList = businessmapper.findUserStoreByPhone(user.getUserMobile());
		if(userStoreList.size()>1){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"���û��󶨶���̻��������Ա��ʵ��Ϣ��");
		}
		if(userStoreList.size()==0){
			
			if(userStore.getIsBind()==1){
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���̻��Ѿ��󶨣������ظ��󶨣�");	
			      }
			
			if(user.getRoleType()==2||user.getStoreId()!=null){
		    	throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���û��Ѿ��󶨣��޷��ظ���!");
		    }
			
			usermapper.changeUserRoleTypeByUserOpenId(userOpenId,userStore.getSuId());
			
			businessmapper.changeUserStoreIsBindBy(userStore.getSuId());
		}
	    
	    
	    
	    
	     
		
		
		

	}
	@Transactional
	public void addCoupon(businessCouponDto dto) {
        //����store_charge_record���Ż�ȯ�۳���¼
		storeChargeRecordDto storeChargedto=new storeChargeRecordDto();
		
		if(StringUtils.isBlank(dto.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"С����openId����Ϊ�գ�");
		}
		
		if(StringUtils.isBlank(dto.getC_title())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�Ż�ȯ���ⲻ��Ϊ��");
		}
		
		if(dto.getC_amount()==null){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�Ż�ȯ����Ϊ��");
		}
		
		if(dto.getC_count()==null){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�Ż�ȯ��������Ϊ��");
		}
		
		if(StringUtils.isBlank(dto.getC_start_time())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�Ż�ȯ��ʼʱ�䲻��Ϊ��");
		}
		
		if(StringUtils.isBlank(dto.getC_end_time())){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�Ż�ȯ����ʱ�䲻��Ϊ��");
		}
		
		if(dto.getStore_id()==null){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�̻����벻��Ϊ��");
		}
		BigDecimal fen = new BigDecimal("100");
		BigDecimal count = new BigDecimal(dto.getC_count());
		BigDecimal userAmount=dto.getC_amount().multiply(fen).multiply(count);//�����Ż�ȯ��Ҫ�Ľ��
		
		userStore userStore=businessmapper.findUserStoreBySuId(dto.getStore_id());
		BigDecimal storeBalances=new BigDecimal(userStore.getSuBalances());//���̳�ֵ���
		BigDecimal storePoint = new BigDecimal(userStore.getSuPoint());//�����������
		if(userAmount.compareTo(storeBalances.add(storePoint))>0){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�̻�����,���ȳ�ֵ�����ѣ�");
		}
		if(userAmount.compareTo(storeBalances)<=0){//userAmount<=storeBalances
			storeChargedto.setR_balances(userAmount.intValue());
			storeChargedto.setR_point(0);
			dto.setC_balances(userAmount.intValue());
			userStoreDto dto2 = new userStoreDto(dto.getStore_id(), storeBalances.subtract(userAmount).intValue(), null);
			businessmapper.updatePriceByUserStoreDto(dto2);//�����̻�id�������̻����
		}else{
			dto.setC_balances(storeBalances.intValue());
			dto.setC_point(userAmount.subtract(storeBalances).intValue());
			storeChargedto.setR_balances(storeBalances.intValue());
			storeChargedto.setR_point(userAmount.subtract(storeBalances).intValue());
			userStoreDto dto2 = new userStoreDto(dto.getStore_id(), 0, storePoint.subtract(userAmount.subtract(storeBalances)).intValue());
			businessmapper.updatePriceByUserStoreDto(dto2);//�����̻�id�������̻����
		}
		
		businessmapper.insertStoreCouponInfoByDto(dto);//�����Ż�ȯ������¼
		
		for(int i =0;i<dto.getC_count();i++){
			List<storeCouponRecordDto> list =new ArrayList<>();
			int radomnum=(int)(Math.random()*900+100);
			long longtime=System.currentTimeMillis();
			storeCouponRecordDto storecouponrecorddto=new storeCouponRecordDto();
			storecouponrecorddto.setR_coupon_id(dto.getC_id());
			storecouponrecorddto.setR_coupon_code(userStore.getSuParkCode()+"_"+userStore.getSuId()+"_"+dto.getC_id()+"_"+longtime+"_"+Integer.toString(radomnum));
			storecouponrecorddto.setR_amount(dto.getC_amount().multiply(fen).intValue());
			storecouponrecorddto.setR_park_code(userStore.getSuParkCode());
			storecouponrecorddto.setR_park_name(userStore.getSuParkName());
			storecouponrecorddto.setR_start_time(dto.getC_start_time());
			storecouponrecorddto.setR_end_time(dto.getC_end_time());
			list.add(storecouponrecorddto);
			businessmapper.insertStoreCouponRecordDtoByList(list);
		}
		
		
		storeChargedto.setR_store_id(dto.getStore_id());
		storeChargedto.setR_type(2);
		storeChargedto.setR_discount_id(0);
		//storeChargedto.setR_balances(userchargerecord.getR_balances());
		//storeChargedto.setR_point(userchargerecord.getR_point());
		storeChargedto.setCharge_channel(1);
		businessmapper.insertStoreChargeRecord(storeChargedto);
		
		
	}

	public List<businessCouponDto> findStoreCouponByCondition(businessCouponCondition condition) {
		if(StringUtils.isBlank(condition.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"С����openId����Ϊ�գ�");
		}
		if(condition.getStoreId()==null){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�̻����벻��Ϊ��");
		}
		return businessmapper.findStoreCouponByCondition(condition);
	}

	public long findStoreCouponCountByCondition(businessCouponCondition condition) {
		
		return businessmapper.findStoreCouponCountByCondition(condition);
	}

	
	public void settleUpCoupon(businessCouponDto dto) {
		//����store_charge_record���Ż�ȯ�����¼
		storeChargeRecordDto storeChargedto=new storeChargeRecordDto();
		
		if(StringUtils.isBlank(dto.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"С����openId����Ϊ�գ�");
		}
		
		if(dto.getStore_id()==null){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�̻����벻��Ϊ��");
		}
		
		if(dto.getC_id()==null){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�Ż�ȯ���벻��Ϊ��");
		}
		businessCouponDto dto3=businessmapper.findBusinessCouponByCid(dto.getC_id());
		if(dto3.getC_is_balance()==2){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�Ż�ȯ�ѽ���");
		}
		if(dto3.getC_count()==dto3.getC_count_received()){//�Ѿ�ȫ����ȡ��ϣ�ֱ�Ӹ����Ż�ȯ״̬Ϊ�ѽ���
			storeChargedto.setR_balances(0);
			storeChargedto.setR_point(0);
			businessmapper.updateStoreCouponStatusBycId(dto.getC_id());
		}else{
			//int unUsedCoupon=dto.getcCount()-dto.getcCountReceived();
			BigDecimal fen = new BigDecimal("100");
			BigDecimal count = new BigDecimal(dto3.getC_count_received());
			BigDecimal UserAmount=dto3.getC_amount().multiply(fen).multiply(count);//�Ż�ȯ���õĽ��
			
			//*******************�̻�Ŀǰ���*********************//
			userStore userStore=businessmapper.findUserStoreBySuId(dto.getStore_id());
			BigDecimal storeBalances=new BigDecimal(userStore.getSuBalances());//���̳�ֵ���
			BigDecimal storePoint = new BigDecimal(userStore.getSuPoint());//�����������
			
			BigDecimal couponBalances= new BigDecimal(dto3.getC_balances());
			BigDecimal couponPoint=new BigDecimal(dto3.getC_point());
			if(UserAmount.compareTo(couponBalances)<=0){//userAmount<=couponBalances
				userStoreDto dto2 = new userStoreDto(dto.getStore_id(), storeBalances.add(couponBalances.subtract(UserAmount)).intValue(), storePoint.add(couponPoint).intValue());
				storeChargedto.setR_balances(couponBalances.subtract(UserAmount).intValue());
				storeChargedto.setR_point(0);
				businessmapper.updatePriceByUserStoreDto(dto2);//�����̻�id�������̻����
				businessmapper.updateStoreCouponStatusBycId(dto.getC_id());
			}else{//userAmount>couponBalances
				userStoreDto dto2 = new userStoreDto(dto.getStore_id(), null, storePoint.add((couponBalances.add(couponPoint)).subtract(UserAmount)).intValue());
				storeChargedto.setR_balances(0);
				storeChargedto.setR_point((couponBalances.add(couponPoint)).subtract(UserAmount).intValue());
				businessmapper.updatePriceByUserStoreDto(dto2);//�����̻�id�������̻����
				businessmapper.updateStoreCouponStatusBycId(dto.getC_id());
			}
		}
		
		storeChargedto.setR_store_id(dto.getStore_id());
		storeChargedto.setR_type(3);
		storeChargedto.setR_discount_id(0);
		//storeChargedto.setR_balances(userchargerecord.getR_balances());
		//storeChargedto.setR_point(userchargerecord.getR_point());
		storeChargedto.setCharge_channel(1);
		businessmapper.insertStoreChargeRecord(storeChargedto);
	}

	public List<Map<String, Object>> findStoreDiscountByCondition(Map<String, Object> paramMap) {
		String userOpenId=paramMap.get("userOpenId").toString();
				if(StringUtils.isBlank(userOpenId)){
					throw new BaseServiceException(
							StatusCode.MISSING_OPENID_ERROR.getCode(),
							"С����openId����Ϊ�գ�");
				}
				
		wechatUser user = usermapper.findWechatUserInfoByOpenId(userOpenId);
		
		List<userStore> userStoreList = businessmapper.findUserStoreByPhone(user.getUserMobile());
		if(userStoreList.size()>1){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"���û��󶨶���̻��������Ա��ʵ��Ϣ��");
		}
		String storeId="";
		if(userStoreList.size()==1){
			storeId=userStoreList.get(0).getSuId()+"";
		}

		//return businessmapper.findStoreDiscountByCondition();
		return businessmapper.findStoreDiscountByStoreId(storeId);
	}

	@Transactional
	public Map<String, Object> rechargeByWechat(rechargeDto recharge, HttpServletRequest request)throws BaseException  {
		userChargeRecordDto userchargerecord = new userChargeRecordDto();
		wechatUser user = usermapper.findWechatUserInfoByOpenId(recharge.getUserOpenId());
		if(user==null){
			throw new BaseException(
					StatusCode.DATA_NOT_MATCH.getCode(),
					"�޸��û���Ϣ�����ʵ��");
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(recharge.getUserOpenId())){
			throw new BaseException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"С����openId����Ϊ�գ�");
		}
		
		
		if(recharge.getStoreId()==null){
			throw new BaseException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�̻���Ų���Ϊ�գ�");
		}
		if(recharge.getScdId()!=null){//�鿴�Ƿ�����ۿ�
			storeChargeDiscountDto discount=businessmapper.findStoreDiscountByScdId(recharge.getScdId());
			if(discount!=null){
				if(discount.getScd_discount()!=null&&discount.getScd_discount()!=0){
					int amout= recharge.getAmount()*discount.getScd_discount()/100;
					recharge.setRealAmout(amout*100);
					recharge.setDiscountAmount((recharge.getAmount()-amout)*100);
					userchargerecord.setR_user_id(user.getUserId());
					userchargerecord.setR_type(1);
					userchargerecord.setR_balances(amout*100);
					userchargerecord.setR_point((recharge.getAmount()-amout)*100);
					userchargerecord.setR_discount_id(recharge.getScdId());
				}else if(discount.getScd_condition_amount()!=null&&discount.getScd_condition_amount()!=0&&discount.getScd_condition_amount()<=recharge.getAmount()){//�����ۿۣ���������
					int a =recharge.getAmount()*100/discount.getScd_condition_amount();
					int rechargePoint=discount.getScd_present_amount()*a;
					userchargerecord.setR_user_id(user.getUserId());
					userchargerecord.setR_type(1);
					userchargerecord.setR_balances(recharge.getAmount()*100);
					userchargerecord.setR_point(rechargePoint);
					userchargerecord.setR_discount_id(recharge.getScdId());
					int amout= recharge.getAmount();
					recharge.setRealAmout(amout*100);
					recharge.setDiscountAmount(0);
				}else{//����������
					int amout= recharge.getAmount();
					recharge.setRealAmout(amout*100);
					recharge.setDiscountAmount(0);
					userchargerecord.setR_user_id(user.getUserId());
					userchargerecord.setR_type(1);
					userchargerecord.setR_balances(recharge.getAmount()*100);
					userchargerecord.setR_point(0);
					userchargerecord.setR_discount_id(recharge.getScdId());
				}
			}else{
				int amout= recharge.getAmount();
				recharge.setRealAmout(amout*100);
				recharge.setDiscountAmount(0);
				userchargerecord.setR_user_id(user.getUserId());
				userchargerecord.setR_type(1);
				userchargerecord.setR_balances(recharge.getAmount()*100);
				userchargerecord.setR_point(0);
				userchargerecord.setR_discount_id(recharge.getScdId());
			}
			
			
			
		}
		
		if(recharge.getRealAmout()==null||recharge.getRealAmout()==0){
			throw new BaseException(
					StatusCode.DATA_NOT_MATCH.getCode(),
					"��ֵʵ������Ϊ0���߲���Ϊ�գ�");
		}
		
		
		recharge.setUserId(user.getUserId());
		Map<String, Object>businessMap=new HashMap<String, Object>();
		userStore store=businessmapper.findUserStoreBySuId(recharge.getStoreId());
		businessMap.put("suFullName", store.getSuFullName());
		//int recordId=businessmapper.insertChargeRecord(userchargerecord);
		storeChargeRecordDto dto=new storeChargeRecordDto();
		dto.setR_store_id(recharge.getStoreId());
		dto.setR_type(4);
		dto.setR_discount_id(recharge.getScdId());
		dto.setR_balances(userchargerecord.getR_balances());
		dto.setR_point(userchargerecord.getR_point());
		dto.setCharge_channel(1);
		businessmapper.insertStoreChargeRecord(dto);
		map =this.getChargePrepayIdNew(recharge,dto,request);
		businessMap.put("rechargeAmout", map.get("price"));
		businessMap.put("disCountAmount", recharge.getDiscountAmount()/100);
		map.put("rechargeInfo", businessMap);
		return map;
	}

	public Map<String, Object> rechargeByAlipay(rechargeDto recharge, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<String, Object>();
		userChargeRecordDto userchargerecord = new userChargeRecordDto();
		
		
		if(StringUtils.isBlank(recharge.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"С����openId����Ϊ�գ�");
		}
		
		wechatUser user = usermapper.findWechatUserInfoByOpenId(recharge.getUserOpenId());
		if(user==null){
			throw new BaseException(
					StatusCode.DATA_NOT_MATCH.getCode(),
					"�޸��û���Ϣ�����ʵ��");
		}
		
		
		
		if(recharge.getStoreId()==null){
			throw new BaseServiceException(
					StatusCode.DATA_NOT_MATCH.getCode(),
					"�̻���Ų���Ϊ�գ�");
		}
		/*if(recharge.getScdId()!=null){//�鿴�Ƿ�����ۿ�
			storeChargeDiscountDto discount=businessmapper.findStoreDiscountByScdId(recharge.getScdId());
			if(discount.getScd_discount()!=null&&discount.getScd_discount()!=0){
				int amout= recharge.getAmount()*discount.getScd_discount()/100;
				recharge.setRealAmout(amout*100);
				recharge.setDiscountAmount((recharge.getAmount()-amout)*100);
			}else{
				int amout= recharge.getAmount();
				recharge.setRealAmout(amout*100);
				recharge.setDiscountAmount(0);
			}
		}*/
		if(recharge.getScdId()!=null){//�鿴�Ƿ�����ۿ�
			storeChargeDiscountDto discount=businessmapper.findStoreDiscountByScdId(recharge.getScdId());
			if(discount!=null){
				if(discount.getScd_discount()!=null&&discount.getScd_discount()!=0){
					int amout= recharge.getAmount()*discount.getScd_discount()/100;
					recharge.setRealAmout(amout*100);
					recharge.setDiscountAmount((recharge.getAmount()-amout)*100);
					userchargerecord.setR_user_id(user.getUserId());
					userchargerecord.setR_type(1);
					userchargerecord.setR_balances(amout*100);
					userchargerecord.setR_point((recharge.getAmount()-amout)*100);
					userchargerecord.setR_discount_id(recharge.getScdId());
				}else if(discount.getScd_condition_amount()!=null&&discount.getScd_condition_amount()!=0&&discount.getScd_condition_amount()<=recharge.getAmount()){//�����ۿۣ���������
					int a =recharge.getAmount()*100/discount.getScd_condition_amount();
					int rechargePoint=discount.getScd_present_amount()*a;
					userchargerecord.setR_user_id(user.getUserId());
					userchargerecord.setR_type(1);
					userchargerecord.setR_balances(recharge.getAmount()*100);
					userchargerecord.setR_point(rechargePoint);
					userchargerecord.setR_discount_id(recharge.getScdId());
					int amout= recharge.getAmount();
					recharge.setRealAmout(amout*100);
					recharge.setDiscountAmount(0);
				}else{//����������
					int amout= recharge.getAmount();
					recharge.setRealAmout(amout*100);
					recharge.setDiscountAmount(0);
					userchargerecord.setR_user_id(user.getUserId());
					userchargerecord.setR_type(1);
					userchargerecord.setR_balances(recharge.getAmount()*100);
					userchargerecord.setR_point(0);
					userchargerecord.setR_discount_id(recharge.getScdId());
				}
			}else{
				int amout= recharge.getAmount();
				recharge.setRealAmout(amout*100);
				recharge.setDiscountAmount(0);
				userchargerecord.setR_user_id(user.getUserId());
				userchargerecord.setR_type(1);
				userchargerecord.setR_balances(recharge.getAmount()*100);
				userchargerecord.setR_point(0);
				userchargerecord.setR_discount_id(recharge.getScdId());
			}
			
			
			
		}
		
		if(recharge.getRealAmout()==null||recharge.getRealAmout()==0){
			throw new BaseException(
					StatusCode.DATA_NOT_MATCH.getCode(),
					"��ֵʵ������Ϊ0���߲���Ϊ�գ�");
		}
		
		
		Map<String, Object>businessMap=new HashMap<String, Object>();
		userStore store=businessmapper.findUserStoreBySuId(recharge.getStoreId());
		businessMap.put("suFullName", store.getSuFullName());
		//int recordId=businessmapper.insertChargeRecord(userchargerecord);
		storeChargeRecordDto dto=new storeChargeRecordDto();
		dto.setR_store_id(recharge.getStoreId());
		dto.setR_type(4);
		dto.setR_discount_id(recharge.getScdId());
		dto.setR_balances(userchargerecord.getR_balances());
		dto.setR_point(userchargerecord.getR_point());
		dto.setCharge_channel(2);
		int recordId=businessmapper.insertStoreChargeRecord(dto);
		//int recordId=businessmapper.insertChargeRecord(userchargerecord);
		//businessmapper.inserStoreChargeRecord(recharge.getStoreId(),4,recharge.getScdId(),userchargerecord.getR_balances(),userchargerecord.getR_point());
		String aliPaykey =alipayservice.getChargePrepayIdByAliPay(recharge,dto.getR_id(),request);
		businessMap.put("rechargeAmout", recharge.getRealAmout());
		businessMap.put("disCountAmount", recharge.getDiscountAmount());
		businessMap.put("alipayReturn", aliPaykey);
		//map.put("rechargeInfo", businessMap);
		return businessMap;
		
	}

	public void addUserStoreBalances(String scdId_storeId_recordId) {

        String[] aa=scdId_storeId_recordId.split("_");
        
        String  scdId=aa[0];
        
        String storeId=aa[1];
        
        String recordId=aa[2];
        
        int storeIdOfInt=Integer.valueOf(storeId);
        int scdIdOfInt=Integer.valueOf(scdId);
        storeChargeDiscountDto discount= businessmapper.findStoreDiscountByScdId(scdIdOfInt);
        userStore userStore=businessmapper.findUserStoreBySuId(storeIdOfInt);
        storeChargeRecordDto chargerecord=businessmapper.findStoreChargeRecordByRid(recordId);
        if(chargerecord==null){
        	throw new BaseException(
					StatusCode.DATA_NOT_MATCH.getCode(),
					"û�ж�Ӧ�ĳ�ֵ��¼��");
        }
        //int balances=Integer.valueOf(couponFee);
        //int scd_condition_amount_fen=discount.getScd_condition_amount()*100;//������
        //int scd_present_amount_fen=discount.getScd_present_amount()*100;//���ͽ��
        BigDecimal storeBalances=new BigDecimal(userStore.getSuBalances());//���̳�ֵ���
		BigDecimal storePoint = new BigDecimal(userStore.getSuPoint());//�����������
		BigDecimal recordBalances=new BigDecimal(chargerecord.getR_balances());//���̳�ֵ���
		BigDecimal recordPoint = new BigDecimal(chargerecord.getR_point());//�����������
		//BigDecimal rechargeBalances=new BigDecimal(balances);
		/*if(discount.getScd_type()==1&&discount.getScd_discount()!=null&&discount.getScd_discount()!=0){//�ۿ�

	        	int point=balances*100/discount.getScd_discount()*(100-discount.getScd_discount());
	        	BigDecimal rechargePoint=new BigDecimal(point);
	        	userStoreDto dto2 = new userStoreDto(storeIdOfInt, storeBalances.add(rechargeBalances).intValue(), storePoint.add(rechargePoint).intValue());
				businessmapper.updatePriceByUserStoreDto(dto2);//�����̻�id�������̻����

		}else if(discount.getScd_type()==2&&balances>=scd_condition_amount_fen){//���ͽ��
			int a =balances/scd_condition_amount_fen;
			BigDecimal rechargePoint=new BigDecimal(scd_present_amount_fen*a);
			userStoreDto dto2 = new userStoreDto(storeIdOfInt, storeBalances.add(rechargeBalances).intValue(), storePoint.add(rechargePoint).intValue());
			businessmapper.updatePriceByUserStoreDto(dto2);//�����̻�id�������̻����
			
		}else{//����
			userStoreDto dto2 = new userStoreDto(storeIdOfInt, storeBalances.add(rechargeBalances).intValue(), null);
			businessmapper.updatePriceByUserStoreDto(dto2);//�����̻�id�������̻����
		}*/
		userStoreDto dto2 = new userStoreDto(storeIdOfInt, storeBalances.add(recordBalances).intValue(), storePoint.add(recordPoint).intValue());
		businessmapper.updatePriceByUserStoreDto(dto2);//�����̻�id�������̻����
        
		
		
		
	}

	public void insertChargePayLog(String logTxt, String chargeRecordId, String logType) {
		// TODO Auto-generated method stub
		businessmapper.insertChargePayLog(logTxt,chargeRecordId,logType);
	}
	
	
	
	@Transactional
	public Map<String, Object> getChargePrepayId(rechargeDto recharge, Integer recordId, HttpServletRequest request)throws BaseException {


		try{

            //���ںŵ�appid
            String appid = "wx606d7fcc6d1402c9";
           
            String openid = recharge.getUserOpenId();

            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            /** ��ǰʱ�� yyyyMMddHHmmss */
            String currTime = CommonUtil.getCurrTime();
            /** 8λ���� */
            String strTime = currTime.substring(8, currTime.length());
            /** ��λ����� */
            String strRandom = CommonUtil.buildRandom(4) + "";
            
            String outTradeNo=currTime+strRandom;

            
            
            /** ���ں�APPID */
            parameters.put("appid", appid);
            parameters.put("body", "�̻�id"+recharge.getStoreId()+"����ֵ");
            /** �̻��� */
            String mch_id = "1604629952";
            parameters.put("mch_id", mch_id);
            /** ����ַ��� */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/business/wechatPayCallback");
            parameters.put("out_trade_no", outTradeNo);
            parameters.put("openid", openid);
            parameters.put("spbill_create_ip", request.getRemoteAddr());
            parameters.put("total_fee", recharge.getRealAmout());
            parameters.put("trade_type", "JSAPI");
            parameters.put("attach", recharge.getScdId()+"_"+recharge.getStoreId()+"_"+recordId);//�ۿ�����_�̻�id_��ֵ��¼��Id
            
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
                    keyStore.load(instream, mch_id.toCharArray());
                } finally {
                    instream.close();
                }
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mch_id.toCharArray())
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
           
            if(result.get("return_code").equals("SUCCESS")) {
            	Map<String, Object> map =CommonUtil.generateSignature(result);
            	usermapper.insertApiLogs("ͳһ�µ��ӿڡ����̻�����ֵ","�̻���"+recharge.getStoreId()+"�ۿ����ͣ�"+recharge.getScdId()+"openid:"+openid+";�����ţ�"+outTradeNo+";��"+recharge.getAmount()+";ʵ����"+recharge.getRealAmout(), "success", map.toString());
            	map.put("price", recharge.getAmount());
                return map;
                }
            else {
            	//log.info("��ȡPrepayId="+openid+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	usermapper.insertApiLogs("ͳһ�µ��ӿڡ����̻�����ֵ", "�̻���"+recharge.getStoreId()+"�ۿ����ͣ�"+recharge.getScdId()+"openid:"+openid+";�����ţ�"+outTradeNo+";��"+recharge.getAmount()+";ʵ����"+recharge.getRealAmout(), "fail", result.toString());
            	throw new BaseException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"�̻�ͳһ�µ��ӿڻ�ȡʧ�ܣ���������ϵ����Ա��");
            }
        }
        catch (Exception e){
        	throw new BaseException(
					StatusCode.SYSTEM_ERROR.getCode(),
					"�̻�ͳһ�µ��ӿڻ�ȡʧ�ܣ���������ϵ����Ա��");
        }
		
	}
	
	@Transactional
	public Map<String, Object> getChargePrepayIdNew(rechargeDto recharge, storeChargeRecordDto dto, HttpServletRequest request)throws BaseException {


		try{

            //���ںŵ�appid
            String appid = "wx606d7fcc6d1402c9";
           
            String openid = recharge.getUserOpenId();

            SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
            /** ��ǰʱ�� yyyyMMddHHmmss */
            String currTime = CommonUtil.getCurrTime();
            /** 8λ���� */
            String strTime = currTime.substring(8, currTime.length());
            /** ��λ����� */
            String strRandom = CommonUtil.buildRandom(4) + "";
            
            String outTradeNo=currTime+strRandom;

            
            
            /** ���ں�APPID */
            parameters.put("appid", appid);
            parameters.put("body", "�̻�id"+recharge.getStoreId()+"����ֵ");
            /** �̻��� */
            String mch_id = "1604629952";
            parameters.put("mch_id", mch_id);
            /** ����ַ��� */
            parameters.put("nonce_str", CommonUtil.getNonceStr());
            parameters.put("notify_url", "https://jiashan.iparking.tech/mp/WeChatApi/business/wechatPayCallback_new");
            parameters.put("out_trade_no", outTradeNo);
            parameters.put("openid", openid);
            parameters.put("spbill_create_ip", request.getRemoteAddr());
            parameters.put("total_fee", recharge.getRealAmout());
            parameters.put("trade_type", "JSAPI");
            parameters.put("attach", recharge.getScdId()+"_"+recharge.getStoreId()+"_"+dto.getR_id());//�ۿ�����_�̻�id_recordId
            
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
                    keyStore.load(instream, mch_id.toCharArray());
                } finally {
                    instream.close();
                }
                // Trust own CA and all self-signed certs
                SSLContext sslcontext = SSLContexts.custom()
                        .loadKeyMaterial(keyStore, mch_id.toCharArray())
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
           
            if(result.get("return_code").equals("SUCCESS")) {
            	Map<String, Object> map =CommonUtil.generateSignature(result);
            	usermapper.insertApiLogs("ͳһ�µ��ӿڡ����̻�����ֵ","�̻���"+recharge.getStoreId()+"�ۿ����ͣ�"+recharge.getScdId()+"openid:"+openid+";�����ţ�"+outTradeNo+";��"+recharge.getAmount()+";ʵ����"+recharge.getRealAmout(), "success", map.toString());
            	map.put("price", recharge.getAmount());
                return map;
                }
            else {
            	//log.info("��ȡPrepayId="+openid+",����ʱ���ǣ�"+CommonUtil.getPreDay(new Date(),0));
            	usermapper.insertApiLogs("ͳһ�µ��ӿڡ����̻�����ֵ", "�̻���"+recharge.getStoreId()+"�ۿ����ͣ�"+recharge.getScdId()+"openid:"+openid+";�����ţ�"+outTradeNo+";��"+recharge.getAmount()+";ʵ����"+recharge.getRealAmout(), "fail", result.toString());
            	throw new BaseException(
    					StatusCode.SYSTEM_ERROR.getCode(),
    					"�̻�ͳһ�µ��ӿڻ�ȡʧ�ܣ���������ϵ����Ա��");
            }
        }
        catch (Exception e){
        	throw new BaseException(
					StatusCode.SYSTEM_ERROR.getCode(),
					"�̻�ͳһ�µ��ӿڻ�ȡʧ�ܣ���������ϵ����Ա��");
        }
		
	}

	public void updateChargeRecordStatus(String recordId,Integer status) {
		// TODO Auto-generated method stub
		businessmapper.updateChargeRecordStatus(recordId,status);
	}

	public userStore findUserStoreBySuId(Map<String, Object> storeMap) {
		int storeId=Integer.valueOf(storeMap.get("storeId").toString());
		String userOpenId=storeMap.get("userOpenId").toString();
		
		wechatUser user = usermapper.findWechatUserInfoByOpenId(userOpenId);//�û�����󶨵�����id
		
		List<userStore> userStoreList = businessmapper.findUserStoreByPhone(user.getUserMobile());
		
		
		/*if(user.getStoreId()==storeId){
			return businessmapper.findUserStoreBySuId(storeId);
		}*/
		
		if(userStoreList.size()>1){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"���û��ֻ��Ű󶨶���̻��������Ա��ʵ��Ϣ��");
		}
		
		if(userStoreList.size()==1){
			
			if(userStoreList.get(0).getSuId()==storeId){
				return businessmapper.findUserStoreBySuId(storeId);
			}else{
				throw new BaseServiceException(
						StatusCode.MISSING_PARAMETER_ERROR.getCode(),
						"���û��ֻ�����Ȩ�����̻��������Ա��ʵ��Ϣ��");
			}
			
			
		}else{
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"���û���Ȩ�����̻��������Ա��ʵ��Ϣ��");
		}
		
		
		
		
	}

	public userChargeRecordDto findChargeRecordByRid(String recordId) {
		// TODO Auto-generated method stub
		return businessmapper.findChargeRecordByRid(recordId);
	}

	public void inserStoreChargeRecord(Integer valueOf, int i, Integer valueOf2, Integer r_balances, Integer r_point) {
		// TODO Auto-generated method stub
		businessmapper.inserStoreChargeRecord(valueOf, i, valueOf2, r_balances, r_point);
	}

	public void payRefund(refundDto dto, HttpServletRequest request) throws IOException {
		parkinglotsPay pay=businessmapper.findParkingLotsPayInfoByPayId(dto.getPayId());
		int sumRefundFee=dto.getRefundFee()+pay.getRefund_amount();
		if(sumRefundFee>dto.getTotalFee()){
			throw new BaseException(
					StatusCode.SYSTEM_ERROR.getCode(),
					"�˿���ܴ���֧����");
		}
		log.info("�������"+new ObjectMapper().writeValueAsString(dto));
		if(dto.getPayType()==1){//΢���˿�
			log.info("΢���˿ʼ========================"+new ObjectMapper().writeValueAsString(dto)+"ip��ַ��"+request.getRemoteAddr());
			String result =wechatservice.refundByWechat(dto);
			log.info("΢���˿����========================"+new ObjectMapper().writeValueAsString(dto)+"�����"+result);
			if(result.equals("SUCCESS")){
				businessmapper.insertPayRefund(dto);
			}
			
			
		} 
		if(dto.getPayType()==2){//֧�����˿�
			log.info("֧�����˿ʼ========================"+new ObjectMapper().writeValueAsString(dto)+"ip��ַ��"+request.getRemoteAddr());
			Map<String, Object> resultMap= alipayservice.refundByAlipay(dto);
			log.info("֧�����˿����========================"+new ObjectMapper().writeValueAsString(dto)+"�����"+resultMap.get("resultCode"));
			if(resultMap.get("resultCode").equals("SUCCESS")){
				dto.setRefundId(resultMap.get("refundId").toString());
				businessmapper.insertPayRefund_success(dto);
				businessmapper.intsertPayRefundLog(resultMap.get("logTxt").toString(),dto.getrId());
				businessmapper.updateParkingLotsPayRefundAmountByPayId(dto.getPayId(),sumRefundFee);
			}
			
		}
	
		
		
		
	}

	public void updatePayrefundStatus(String outRefundNo, String tradeNo) {
		// TODO Auto-generated method stub
		businessmapper.updatePayrefundStatus(outRefundNo,tradeNo);
	}

	public void intsertPayRefundLog(String jsonString, Integer getrId) {
		// TODO Auto-generated method stub
		businessmapper.intsertPayRefundLog(jsonString,getrId);
	}

	public parkinglotsPayRefund findRefundDtoByOutNo(String outTradeNo) {
		// TODO Auto-generated method stub
		return businessmapper.findRefundDtoByOutNo(outTradeNo);
	}

	public void updatePayRefundByOutNo(String outTradeNo, String refundId) {
		// TODO Auto-generated method stub
		businessmapper.updatePayRefundByOutNo(outTradeNo,refundId);
	}

	public parkinglotsPay findParkingLotsPayInfoByPayId(Integer payId) {
		// TODO Auto-generated method stub
		return businessmapper.findParkingLotsPayInfoByPayId(payId);
	}

	public void updateParkingLotsPayRefundAmountByPayId(Integer payId, int sumRefundAmount) {
		// TODO Auto-generated method stub
		businessmapper.updateParkingLotsPayRefundAmountByPayId(payId,sumRefundAmount);
	}

	public void setStoreCouponUnused() {
		
		businessmapper.setStoreCouponUnused();
	}

	public List<storeChargeRecordDto> findUserChargeInfoByCondition(storeChargeRecordCondition condition) {
		if(StringUtils.isBlank(condition.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"С����openId����Ϊ�գ�");
		}
		if(condition.getR_store_id()==null){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�̻�id����Ϊ�գ�");
		}
		/*if(condition.getR_type()==null){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"��¼���Ͳ���Ϊ��");
		}*/
		return businessmapper.findUserChargeInfoByCondition(condition);
	}

	public long findUserChargeCountByCondition(storeChargeRecordCondition condition) {
		// TODO Auto-generated method stub
		return businessmapper.findUserChargeCountByCondition(condition);
	}

	public void insertUserChargePayLog(String logTxt, String chargeRecordId, String logType) {
		// TODO Auto-generated method stub
		businessmapper.insertUserChargePayLog(logTxt,chargeRecordId,logType);
	}

	public String getERCodeString(Map<String, Object> map) throws HttpException, IOException {
		
		String couponId=map.get("couponId").toString();
		String userOpenId=map.get("userOpenId").toString();
		if(StringUtils.isBlank(userOpenId)){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"С����openId����Ϊ�գ�");
		}
		if(StringUtils.isBlank(couponId)){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�Ż�ȯid����Ϊ�գ�");
		}
		
		return blue.getERCodeString(couponId);
	}

	public void scanERCodeGetCoupon(Map<String, Object> map) throws HttpException, IOException {
		
		String encode=map.get("encode").toString();
		String userOpenId=map.get("userOpenId").toString();
		if(StringUtils.isBlank(userOpenId)){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"С����openId����Ϊ�գ�");
		}
		if(StringUtils.isBlank(encode)){
			throw new BaseServiceException(
					StatusCode.MISSING_PARAMETER_ERROR.getCode(),
					"�Ż�ȯ��ά�����ݲ���Ϊ�գ�");
		}
		wechatUser aa =usermapper.findWechatUserInfoByOpenId(userOpenId);
		
		
	 blue.scanERCodeGetCoupon(encode,aa.getUserId().toString());
		
	}
	public storeChargeRecordDto findStoreChargeRecordByRid(String recordId) {
		// TODO Auto-generated method stub
		return businessmapper.findStoreChargeRecordByRid(recordId);
	}
	public void updateStoreChargeRecordStatus(String recordId, Integer status) {
		// TODO Auto-generated method stub
		businessmapper.updateStoreChargeRecordStatus(recordId,status);
	}
	public void insertUserRechargeRecordByDto(userRechargeRecordDto record) {
		// TODO Auto-generated method stub
		businessmapper.insertUserRechargeRecordByDto(record);
	}
	public void updateUserReChargeRecord(String outTradeNo, String refundFee) {
		// TODO Auto-generated method stub
		businessmapper.updateUserReChargeRecord(outTradeNo,refundFee);
	}
	/*public void payRefund_xz(refundDto dto, HttpServletRequest request) {
		if(dto.getOrderType().equals("1")){
			parkinglotsPay pay=businessmapper.findParkingLotsPayInfoByPayId(dto.getPayId());
			int sumRefundFee=dto.getRefundFee()+pay.getRefund_amount();
			if(sumRefundFee>dto.getTotalFee()){
				throw new BaseException(
						StatusCode.SYSTEM_ERROR.getCode(),
						"�˿���ܴ���֧����");
			}
			log.info("�������"+JSON.toJSONString(dto));
			if(dto.getPayType()==1){//΢���˿�
				log.info("΢���˿ʼ========================"+JSON.toJSONString(dto)+"ip��ַ��"+request.getRemoteAddr());
				String result =wechatservice.refundByWechat_xz(dto);
				log.info("΢���˿����========================"+JSON.toJSONString(dto)+"�����"+result);
				if(result.equals("SUCCESS")){
					businessmapper.insertPayRefund(dto);
				}
				
				
			} 
		}else{
			roadParkinglotsPay pay=roadpaymapper.findRoadParkingLotsPayInfoByPayId(Integer.valueOf(dto.getPayId()));
			int sumRefundFee=dto.getRefundFee()+pay.getRefund_amount();
			if(sumRefundFee>dto.getTotalFee()){
				throw new BaseException(
						StatusCode.SYSTEM_ERROR.getCode(),
						"�˿���ܴ���֧����");
			}
			log.info("�������"+JSON.toJSONString(dto));
			if(dto.getPayType()==1){//΢���˿�
				log.info("΢���˿ʼ========================"+JSON.toJSONString(dto)+"ip��ַ��"+request.getRemoteAddr());
				String result =wechatservice.refundByWechat_xz(dto);
				log.info("΢���˿����========================"+JSON.toJSONString(dto)+"�����"+result);
				if(result.equals("SUCCESS")){
					
						roadpaymapper.insertRoadPayRefund(dto);
				}
				
				
			} 
		}
		
		if(dto.getPayType()==2){//֧�����˿�
			log.info("֧�����˿ʼ========================"+JSON.toJSONString(dto)+"ip��ַ��"+request.getRemoteAddr());
			Map<String, Object> resultMap= alipayservice.refundByAlipay(dto);
			log.info("֧�����˿����========================"+JSON.toJSONString(dto)+"�����"+resultMap.get("resultCode"));
			if(resultMap.get("resultCode").equals("SUCCESS")){
				dto.setRefundId(resultMap.get("refundId").toString());
				businessmapper.insertPayRefund_success(dto);
				businessmapper.intsertPayRefundLog(resultMap.get("logTxt").toString(),dto.getrId());
				businessmapper.updateParkingLotsPayRefundAmountByPayId(dto.getPayId(),sumRefundFee);
			}
			
		}
	
		
		
		
	}*/
	public parkinglotsPayRefund findRoadRefundDtoByOutNo(String outRefundNo) {
		// TODO Auto-generated method stub
		return businessmapper.findRoadRefundDtoByOutNo(outRefundNo);
	}
	public void updateRoadPayRefundByOutNo(String outRefundNo, String refundId) {
		// TODO Auto-generated method stub
		businessmapper.updateRoadPayRefundByOutNo(outRefundNo,refundId);
	}
	public long findUserRechargeRecordCountByOutTradeNo(String outTradeNo) {
		// TODO Auto-generated method stub
		return businessmapper.findUserRechargeRecordCountByOutTradeNo(outTradeNo);
	}
	public long findStoreChargePayLogCount(String recordId) {
		// TODO Auto-generated method stub
		return businessmapper.findStoreChargePayLogCount(recordId);
	}
	public void updateChargeRecordRLeft(int newBalances, int newPoint, String recordId) {
		// TODO Auto-generated method stub
		businessmapper.updateChargeRecordRLeft(newBalances,newPoint,recordId);
	}
	

	
}
