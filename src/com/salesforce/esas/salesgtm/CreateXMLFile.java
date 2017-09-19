package com.salesforce.esas.salesgtm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CreateXMLFile {
	
	   HashMap<String, String> defaultChildsToParents = new HashMap<>();
	
	   public CreateXMLFile(){
		   defaultChildsToParents.put("externalId", "false");
		   defaultChildsToParents.put("trackFeedHistory", "false");
		   defaultChildsToParents.put("trackHistory", "true");
		   defaultChildsToParents.put("trackTrending", "false");
		   defaultChildsToParents.put("type", "Picklist");
	   }
	
	   public static void main(String argv[]) {		  
		   		   
		   
		   HashMap<String, ArrayList<String>> valueSet1 = new HashMap<>();
		   ArrayList<String> valueData1 = new ArrayList();
		   valueData1.add("Corporate Sales");
		   valueData1.add("Enterprise Sales");
		   
		   
		   ArrayList<String> valueData2 = new ArrayList();
		   valueData2.add("Corporate Sales");
		   
		   ArrayList<String> valueData3 = new ArrayList();
		   valueData3.add("Corporate Sales");
		   
		   ArrayList<String> valueData4 = new ArrayList();
		   valueData4.add("Enterprise Sales");
		   
		   
		   valueSet1.put("5154", valueData1);
		   valueSet1.put("5155", valueData2);
		   valueSet1.put("5156", valueData3);
		   valueSet1.put("15155", valueData4);
		   
	   }
	   
	   public void generateXML(LinkedHashMap<String, LinkedHashMap<String, ArrayList<String>>> dataMap){
		   try {
			   
		         DocumentBuilderFactory dbFactory =
		         DocumentBuilderFactory.newInstance();
		         DocumentBuilder dBuilder = 
		            dbFactory.newDocumentBuilder();
		         Document doc = dBuilder.newDocument();
		         // root element
		         Element rootElement = doc.createElement("CustomObject");
		         rootElement.setAttribute("xmlns", "http://soap.sforce.com/2006/04/metadata");
		         doc.appendChild(rootElement);

		         Element defaultChildsToRoot = doc.createElement("enableEnhancedLookup");
		         defaultChildsToRoot.appendChild(doc.createTextNode("true"));
		         rootElement.appendChild(defaultChildsToRoot);
		         
		         ArrayList<String> dataList = new ArrayList();
		         dataList.addAll(dataMap.keySet());
		         
		         for(int i=0; i<dataList.size(); i++){
		        	 LinkedHashMap<String, ArrayList<String>> valueSet1 = dataMap.get(dataList.get(i));
			         Element child = doc.createElement("fields");
			         rootElement.appendChild(child);
			         
			         defaultChildsToParents.put("fullName", dataList.get(i).split("-")[0].split("##")[0]);
			         defaultChildsToParents.put("label", dataList.get(i).split("-")[0].split("##")[1]);
			         ArrayList<String> list = new ArrayList();
			         list.addAll(defaultChildsToParents.keySet());
			         
			         for(String str : list){
			        	 Element defaultChildsToParent = doc.createElement(str);
				         defaultChildsToParent.appendChild(doc.createTextNode(defaultChildsToParents.get(str)));
				         child.appendChild(defaultChildsToParent);
			         }
			         
			         Element picklist = doc.createElement("picklist");	         
			         child.appendChild(picklist);
			         
			         if(dataList.get(i).split("-").length > 1){
				         Element controllingField = doc.createElement("controllingField");
				         controllingField.appendChild(doc.createTextNode(dataList.get(i).split("-")[1].split("##")[0]));
				         picklist.appendChild(controllingField);
			         }
			         
			         Element defaultVal = doc.createElement("default");
			         //defaultVal.appendChild(doc.createTextNode("false"));
			         //picklist.appendChild(defaultVal);
			         
			         ArrayList<String> valueSetList = new ArrayList();
			         valueSetList.addAll(valueSet1.keySet());
			         
			         for(String str: valueSetList){
			        	 Element picklistValues = doc.createElement("picklistValues");	         
			        	 picklist.appendChild(picklistValues);
				         
			        	 Element fullName = doc.createElement("fullName");	
			        	 fullName.appendChild(doc.createTextNode(str));
			        	 picklistValues.appendChild(fullName);
			        	 
			        	 defaultVal = doc.createElement("default");
				         defaultVal.appendChild(doc.createTextNode("false"));
				         picklistValues.appendChild(defaultVal);
			        	 
			        	 ArrayList<String> ControllingValues = valueSet1.get(str);
			        	 
			        	 for(String controlStr: ControllingValues){
				        	 
				        	 Element controllingFieldValue = doc.createElement("controllingFieldValues");
				        	 controllingFieldValue.appendChild(doc.createTextNode(controlStr));
				        	 picklistValues.appendChild(controllingFieldValue);
				         
			        	 }
				         
			         }
		         
		         }		         
		         // write the content into xml file
		         TransformerFactory transformerFactory =
		         TransformerFactory.newInstance();
		         Transformer transformer =
		         transformerFactory.newTransformer();
		         DOMSource source = new DOMSource(doc);
		         StreamResult result =
		         new StreamResult(new File("d:\\Employee_Model__c.object"));
		         transformer.transform(source, result);
		         // Output to console for testing
		         StreamResult consoleResult =
		         new StreamResult(System.out);
		         transformer.transform(source, consoleResult);
		      } catch (Exception e) {
		         e.printStackTrace();
		      }
	   }
	}
