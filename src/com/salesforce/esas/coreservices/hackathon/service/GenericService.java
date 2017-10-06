package com.salesforce.esas.coreservices.hackathon.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.salesforce.esas.coreservices.hackathon.web.forms.FillRateAnalysis;

@Service
public class GenericService {
	
	public final String postgreHost = "ec2-23-21-88-45.compute-1.amazonaws.com";
	public final String postgreDB = "dd9cmpsjagtepb";
	public final String postgreUsername = "ezfmmlebdzkskl";
	public final String postgrePassword = "7b4b3176116b6cff181c709a62d2e7b10e4561b26cf379e32120fa92303a6d82";

	Connection connection = null;
	
	GenericService(){
		System.out.println("-------- PostgreSQL "+ "JDBC Connection Testing ------------");
		try {

			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();		

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		try {

			connection = DriverManager.getConnection(
					"jdbc:postgresql://"+postgreHost+"/"+postgreDB+"?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", postgreUsername, postgrePassword);

			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery("Select * from salesforce.fill_rate_analysis");
			while(rs.next()){			
				System.out.println(rs.getString(1));
			}
		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();			

		}
	}	
	
	public LinkedHashMap<String, LinkedList<FillRateAnalysis>> getOrg62Analysis(String orgName){
		LinkedHashMap<String, LinkedList<FillRateAnalysis>> dataMap = new LinkedHashMap<String, LinkedList<FillRateAnalysis>>();
		LinkedList<FillRateAnalysis> dataList = null;
		try {
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery("Select * from salesforce.fill_rate_analysis where \"OrgName\"='"+orgName+"' order by \"FillRate\",\"ObjectName\",\"FieldName\"");
			while(rs.next()){		
				FillRateAnalysis data = new FillRateAnalysis();
				data.setObjectName(rs.getString("ObjectName").trim());
				data.setFieldName(rs.getString("FieldName").trim());
				data.setFillRate(rs.getDouble("FillRate"));
				if(dataMap.get(data.getObjectName()) != null)
				{
					dataList = dataMap.get(data.getObjectName());
					dataList.add(data);
					dataMap.put(data.getObjectName(), dataList);
				}else{
					dataList = new LinkedList<FillRateAnalysis>();
					dataList.add(data);
					dataMap.put(data.getObjectName(), dataList);
				}
				
			}
		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;

		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		
		return dataMap;
	}
}
