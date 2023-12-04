package com.WeChatApi.bean.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class noVehiclePlate  {
	

	private String userOpenId;
	
	private String parkingCode;
	
	private Integer channelCode;
	
	private String vehiclePlate;
	
	private int vehPlateColour;
	

	public String getUserOpenId() {
		return userOpenId;
	}

	public void setUserOpenId(String userOpenId) {
		this.userOpenId = userOpenId;
	}

	public String getParkingCode() {
		return parkingCode;
	}

	public void setParkingCode(String parkingCode) {
		this.parkingCode = parkingCode;
	}

	public Integer getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(Integer channelCode) {
		this.channelCode = channelCode;
	}

	public String getVehiclePlate() {
		return vehiclePlate;
	}

	public void setVehiclePlate(String vehiclePlate) {
		this.vehiclePlate = vehiclePlate;
	}

	public int getVehPlateColour() {
		return vehPlateColour;
	}

	public void setVehPlateColour(int vehPlateColour) {
		this.vehPlateColour = vehPlateColour;
	}
	
	

}
