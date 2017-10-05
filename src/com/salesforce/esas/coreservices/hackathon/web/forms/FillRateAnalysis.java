package com.salesforce.esas.coreservices.hackathon.web.forms;

public class FillRateAnalysis {

	private String orgName;
	private String objectName;
	private String fieldName;
	private double fillRate;
	
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getObjectName() {
		return objectName;
	}
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public double getFillRate() {
		return fillRate;
	}
	public void setFillRate(double fillRate) {
		this.fillRate = fillRate;
	}
	
	
}
