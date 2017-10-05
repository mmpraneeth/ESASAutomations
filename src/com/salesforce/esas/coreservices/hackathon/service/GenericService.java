package com.salesforce.esas.coreservices.hackathon.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
	
	public LinkedList<FillRateAnalysis> getOrg62Analysis(String orgName){	
		LinkedList<FillRateAnalysis> dataList = new LinkedList<FillRateAnalysis>();
		try {
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery("Select * from salesforce.fill_rate_analysis where \"OrgName\"='"+orgName+"'");
			while(rs.next()){		
				FillRateAnalysis data = new FillRateAnalysis();
				data.setObjectName(rs.getString("ObjectName"));
				System.out.println();
				dataList.add(data);
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
		
		return dataList;
	}
}
