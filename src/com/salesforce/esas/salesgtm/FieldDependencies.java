/**
 * 
 */
package com.salesforce.esas.salesgtm;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.opencsv.CSVReader;

/**
 * @author mmallampalli
 *
 */
public class FieldDependencies {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			CSVReader reader = new CSVReader(new FileReader("C:\\Users\\mmallampalli\\Downloads\\VFRM1-01.13.2017_1.csv")); 
			 String [] nextLine;
			 String[] header = reader.readNext();
		        LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> dataList = new LinkedHashMap();
		        LinkedHashMap<String, ArrayList<String>> parentValueSet = new LinkedHashMap<>();
		        for(int i=0; i<header.length-1; i++){
		        	LinkedHashMap<String, ArrayList<String>> valueSet1 = new LinkedHashMap<>();
		        	int dependent = i+1;
		        	int controlling = i;
		        	ArrayList<String> data = null;
		        	String controllingField = null;
		        	CSVReader reader1 = new CSVReader(new FileReader("C:\\Users\\mmallampalli\\Downloads\\VFRM1-01.13.2017_1.csv"));
		        	reader1.readNext();
		        	while ((nextLine = reader1.readNext()) != null){
		        		boolean flag = false;
			        	if((null != nextLine[dependent] && nextLine[dependent].length() > 0) && valueSet1.containsKey(nextLine[dependent].trim())){
			        		data = valueSet1.get(nextLine[dependent].trim());
			        		controllingField = null == nextLine[controlling] || nextLine[controlling].length() == 0 ? controllingField : nextLine[controlling];
			        		if(!data.contains(controllingField))
			        			data.add(controllingField);
			        		flag = true;
			        	}else if(null != nextLine[dependent] && nextLine[dependent].length() > 0){
			        		data = new ArrayList();
			        		controllingField = null == nextLine[controlling] || nextLine[controlling].length() == 0 ? controllingField : nextLine[controlling];
			        		if(!data.contains(controllingField))
			        			data.add(controllingField);
				        	flag = true;
			        	}
			        	
			        	if(controlling ==0){
		        			parentValueSet.put(nextLine[controlling].trim(), new ArrayList());
		        		}
			        	
			        	if(flag)
			        		valueSet1.put(nextLine[dependent].trim(), data);
		        	}
		        	if(controlling == 0)
		        		dataList.put(header[controlling], parentValueSet);
		        	
		        	dataList.put(header[dependent]+"-"+header[controlling],valueSet1);
		       	
		        }
		     new CreateXMLFile().generateXML(dataList);
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
