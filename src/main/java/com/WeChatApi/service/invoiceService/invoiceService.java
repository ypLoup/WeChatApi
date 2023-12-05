package com.WeChatApi.service.invoiceService;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Pack200.Packer;

import javax.transaction.Transactional;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.WeChatApi.bean.condition.couponCondition;
import com.WeChatApi.bean.condition.invoiceCondition;
import com.WeChatApi.bean.condition.operationOrderCondition;
import com.WeChatApi.bean.condition.parkinglotsCondition;
import com.WeChatApi.bean.condition.parkinglotsPayCondition;
import com.WeChatApi.bean.dto.doInvoiceDto;
import com.WeChatApi.bean.models.invoice;
import com.WeChatApi.bean.models.operationOrder;
import com.WeChatApi.bean.models.parkinglots;
import com.WeChatApi.bean.models.parkinglotsPay;
import com.WeChatApi.bean.models.storeInvoice;
import com.WeChatApi.bean.models.wechatUser;
import com.WeChatApi.controller.base.BaseServiceException;
import com.WeChatApi.controller.base.SqlUtil;
import com.WeChatApi.controller.base.StatusCode;
import com.WeChatApi.dao.invoiceMapper;
import com.WeChatApi.dao.operationOrderMapper;
import com.WeChatApi.dao.parkinglotsMapper;
import com.WeChatApi.dao.wechatUserMapper;
import com.WeChatApi.service.blueCardService.blueCardService;
import com.WeChatApi.service.wechatUserService.wechatUserService;



@Service
@Transactional
public class invoiceService {
	
	@Autowired
	private invoiceMapper invoicemapper;
	
	@Autowired
	private blueCardService bluecardservice;
	
	@Autowired 
	private wechatUserMapper wechatusermapper;
	
	@Autowired
	private operationOrderMapper operationOrdermapper;

	public List<invoice> findInvoiceInfo(invoiceCondition condition) {
		if(StringUtils.isBlank(condition.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"openId����Ϊ�գ�");
		}
		
		return invoicemapper.findInvoiceInfo(condition);
	}

	public long findInvoiceInfoCount(invoiceCondition condition) {
		// TODO Auto-generated method stub
		return invoicemapper.findInvoiceInfoCount(condition);
	}

	public void addInvoiceInfo(invoice invoice) {
		
		invoice aa = invoicemapper.findInvoiceInfoByTaxNumber(invoice.getTaxNumber(), invoice.getUserOpenId());
		
		if(aa!=null){
			throw new BaseServiceException(
					StatusCode.DATA_IS_EXISTS.getCode(),
					"��Ʊ��Ϣ�Ѵ��ڣ������ظ���ӣ�");
		}
		
		if(StringUtils.isBlank(invoice.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"openId����Ϊ�գ�");
		}
		
		/*if(StringUtils.isBlank(invoice.getBankNumber())){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"�������˺Ų���Ϊ�գ�");
		}*/
		
		if(StringUtils.isBlank(invoice.getCompanyName())){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"��Ʊ̧ͷ����Ϊ�գ�");
		}
		/*if(StringUtils.isBlank(invoice.getTaxNumber())){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"��Ʊ˰�Ų���Ϊ�գ�");
		}*/
		if(StringUtils.isBlank(invoice.getEmail())){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"�����ַ����Ϊ�գ�");
		}
		/*if(StringUtils.isBlank(invoice.getAddressMobile())){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"��ϵ��ַ�绰����Ϊ�գ�");
		}*/
		
		invoicemapper.addInvoiceInfo(invoice);
		
	}

	public void updateInvoiceInfo(invoice invoice) {
		// TODO Auto-generated method stub
		
		if(StringUtils.isBlank(invoice.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"openId����Ϊ�գ�");
		}
		
		if(invoice.getId()==null){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"��Ʊ��Ϣ��������Ϊ�գ�");
		}
		
		if(StringUtils.isNotBlank(invoice.getTaxNumber())){
			invoice aa = invoicemapper.findInvoiceInfoByTaxNumber(invoice.getTaxNumber(), invoice.getUserOpenId());
			
			if(aa!=null){
				throw new BaseServiceException(
						StatusCode.DATA_IS_EXISTS.getCode(),
						"�޸ĺ�Ŀ�Ʊ��Ϣ�Ѵ��ڣ����ʵ�������");
			}
		}
		
		invoicemapper.updateInvoiceInfo(invoice);
	}

	public void delInvoiceInfo(invoice invoice) {


		if(invoice.getId()==null){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"��Ʊ��Ϣ��������Ϊ�գ�");
		}
		
		invoicemapper.delInvoiceInfo(invoice);
		
	}

	public List<parkinglotsPay> findParkingPayInfo(parkinglotsPayCondition condition) {
		
		/*List<String> ids = new ArrayList<String>();
		for (String s : condition.getVeh_plates().toString().split(",")) {
			ids.add(s);
		}
		condition.setVehPlateLits(ids);*/
		
		if(StringUtils.isBlank(condition.getVeh_plates().toString())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"���ƺŲ���Ϊ�գ�");
		}
		//SqlUtil.escapeOrderBySql(condition.getVeh_plates().toString());
		//SqlUtil.filterKeyword(condition.getVeh_plates().toString());
		if(StringUtils.isBlank(condition.getVeh_plates_colours().toString())){
			List<String> ids = new ArrayList<String>();
			for (String s : condition.getVeh_plates().toString().split(",")) {
				ids.add(s);
			}
			condition.setVehPlateLits(ids);
			return invoicemapper.findParkingPayInfo(condition);
		}else{
			List<Map<String,String>> ids = new ArrayList<>();
			int veh_plates_count = condition.getVeh_plates().toString().split(",").length;
			if(condition.getVeh_plates().toString().split(",").length!=condition.getVeh_plates_colours().toString().split(",").length){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"�������ͳ�����ɫ����ƥ�䣬���ʵ��");
			}
			
			for(int i =0 ;i<veh_plates_count;i++){
				Map<String, String> map = new HashMap<String, String>();
				map.put("plate", condition.getVeh_plates().toString().split(",")[i].toString());
				map.put("colour", condition.getVeh_plates_colours().toString().split(",")[i].toString());
				ids.add(map);
			}
			operationOrderCondition orderCondition =  new  operationOrderCondition();
			orderCondition.setVeh_plate_color_txt(condition.getVeh_plates_colours());
			if(ids.size()!=0){
				orderCondition.setVehPlateMapLits(ids);
			}
			if(orderCondition==null){
				throw new BaseServiceException(
						StatusCode.MISSING_OPENID_ERROR.getCode(),
						"�޶�Ӧ�Ķ�����Ϣ�����ʵ��");
			}
			
			List<String>payNumberList=operationOrdermapper.findOrderPayNumberList(orderCondition);
			parkinglotsPayCondition condition2 = new parkinglotsPayCondition();
			condition2.setOut_trade_noList(payNumberList);
			condition2.setInvoice_status(condition.getInvoice_status());
			if(payNumberList.size()==0){
				List<parkinglotsPay> list = new ArrayList<>();
				return list;
			}else{
				return invoicemapper.findParkingPayInfo(condition2);
			}
			
		}
		
		/*return invoicemapper.findParkingPayInfo(condition);*/
	}

	public long findParkingPayInfoCount(parkinglotsPayCondition condition) {
		
		List<String> ids = new ArrayList<String>();
		for (String s : condition.getVeh_plates().toString().split(",")) {
			ids.add(s);
		}
		condition.setVehPlateLits(ids);
		
		return invoicemapper.findParkingPayInfoCount(condition);
	}
	
	
	
    /**
     * ����Ʊ
     * @param invoiceDto
     * @throws IOException 
     */
	public void doInvoice(doInvoiceDto invoiceDto) throws IOException {
		String userOpenId = invoiceDto.getUserOpenId();
		
		if(StringUtils.isBlank(invoiceDto.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"�û�userOpenId����Ϊ�գ�");
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
						"�������˺Ų���Ϊ�գ�");
			}*/
			
			if(StringUtils.isBlank(invoiceDto.getCompanyName())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"��Ʊ̧ͷ����Ϊ�գ�");
			}
			/*if(StringUtils.isBlank(invoiceDto.getTaxNumber())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"��Ʊ˰�Ų���Ϊ�գ�");
			}*/
			if(StringUtils.isBlank(invoiceDto.getEmail())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"�����ַ����Ϊ�գ�");
			}
			/*if(StringUtils.isBlank(invoiceDto.getAddressMobile())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"��ϵ��ַ�绰����Ϊ�գ�");
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
				Map<String, Object> sumPriceMap = invoicemapper.getSumPriceByOutTradeNoList(outTradeNoList);
				//String vehiclePates=invoicemapper.getVehiclesByOutTradeNoList(outTradeNoList);
				bluecardservice.doInvoiceSumPrice(outTradeNoList,invoice2,sumPriceMap);
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
				Map<String, Object> sumPriceMap = invoicemapper.getSumPriceByOutTradeNoList(outTradeNoList);
				bluecardservice.doInvoiceSumPrice(outTradeNoList,invoice2,sumPriceMap);
			}
			invoice2.setId(invoice.getId());
			invoicemapper.updateInvoiceInfo(invoice2);
		}
		
		
		
	}

	public List<Map<String, Object>> findCouponRecordListByCondition(couponCondition condition) {
		
		if(StringUtils.isBlank(condition.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"�û�userOpenId����Ϊ�գ�");
		}
		wechatUser user =wechatusermapper.findWechatUserInfoByOpenId(condition.getUserOpenId());
		if(user==null){
			throw new BaseServiceException(
					StatusCode.DATA_IS_EXISTS.getCode(),
					"�û���Ϣ�����ڣ����ʵ��");
		}
		condition.setR_user_id(user.getUserId());
		if(condition.getR_user_id()==null){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"�û�userId����Ϊ�գ�");
		}
		return invoicemapper.findCouponRecordListByCondition(condition);
	}

	public long findCouponRecordCountByCondition(couponCondition condition) {
		wechatUser user =wechatusermapper.findWechatUserInfoByOpenId(condition.getUserOpenId());
		if(user==null){
			throw new BaseServiceException(
					StatusCode.DATA_IS_EXISTS.getCode(),
					"�û���Ϣ�����ڣ����ʵ��");
		}
		condition.setR_user_id(user.getUserId());
		// TODO Auto-generated method stub
		return invoicemapper.findCouponRecordCountByCondition(condition);
	}

	public void doInvoiceByStore(storeInvoice invoice2) throws HttpException, IOException {
		
       String userOpenId = invoice2.getUserOpenId();
		
		if(StringUtils.isBlank(invoice2.getUserOpenId())){
			throw new BaseServiceException(
					StatusCode.MISSING_OPENID_ERROR.getCode(),
					"�û�userOpenId����Ϊ�գ�");
		}
		
		invoice invoice = invoicemapper.findInvoiceInfoByTaxNumber(invoice2.getTaxNumber(),invoice2.getUserOpenId());
		List<String> recordList = new ArrayList<>();
		for(String record : invoice2.getRecordIds().toString().split(",")){
		
			recordList.add(record);
		}
		String sumAmount=invoicemapper.getStoreSumPriceByRecordId(recordList);
		
		invoice invoice3 = new  invoice();
		invoice3.setUserOpenId(userOpenId);
		invoice3.setCompanyName(invoice2.getCompanyName());
		invoice3.setTaxNumber(invoice2.getTaxNumber());
		invoice3.setBankNumber(invoice2.getBankNumber());
		invoice3.setEmail(invoice2.getEmail());
		invoice3.setAddressMobile(invoice2.getAddressMobile());
		invoice3.setInvoiceAmount(sumAmount);
		/*if(sumAmount.equals(invoice2.getInvoiceAmount())){
			invoice3.setInvoiceAmount(invoice2.getInvoiceAmount());
		}else{
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"��Ʊ�����ʵ�ʲ�����һ�£�");
		}*/
		if(invoice==null){
			/*if(StringUtils.isBlank(invoiceDto.getBankNumber())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"�������˺Ų���Ϊ�գ�");
			}*/
			
			if(StringUtils.isBlank(invoice2.getCompanyName())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"��Ʊ̧ͷ����Ϊ�գ�");
			}
			/*if(StringUtils.isBlank(invoiceDto.getTaxNumber())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"��Ʊ˰�Ų���Ϊ�գ�");
			}*/
			if(StringUtils.isBlank(invoice2.getEmail())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"�����ַ����Ϊ�գ�");
			}
			/*if(StringUtils.isBlank(invoiceDto.getAddressMobile())){
				throw new BaseServiceException(
						StatusCode.PARAMETER_FORMATE_RROR.getCode(),
						"��ϵ��ַ�绰����Ϊ�գ�");
			}*/
			
			invoicemapper.addInvoiceInfo(invoice3);
			/*List<String> recordList = new ArrayList<>();
			for(String record : invoice2.getRecordIds().toString().split(",")){
			
				recordList.add(record);
			}*/
			String fpurl="";
			if(recordList.size()>0){
				
				fpurl=bluecardservice.doInvoiceByStore(invoice3,invoice2.getRecordIds());
			}
			if(fpurl.equals("")||fpurl!=null){
				invoicemapper.updateStoreChargeRecordBy(recordList,fpurl);
			}

		}else{
			/*List<String> recordList = new ArrayList<>();
			for(String record : invoice2.getRecordIds().toString().split(",")){
				
				recordList.add(record);
			}*/
			String fpurl="";
            if(recordList.size()>0){
				
				fpurl=bluecardservice.doInvoiceByStore(invoice3,invoice2.getRecordIds());
			}
			if(fpurl.equals("")||fpurl!=null){
				invoicemapper.updateStoreChargeRecordBy(recordList,fpurl);
			}
			
		}
		
		
		
		
	}

	public List<parkinglotsPay> findParkingPayInfoByPayId(Map<String, Object> map) {
		if(StringUtils.isBlank(map.get("payId").toString())){
			throw new BaseServiceException(
					StatusCode.PARAMETER_FORMATE_RROR.getCode(),
					"��ѯ��������Ϊ�գ�");
		}
		
		return invoicemapper.findParkingPayInfoByPayId(map.get("payId").toString());
	}

	

	

	
}
