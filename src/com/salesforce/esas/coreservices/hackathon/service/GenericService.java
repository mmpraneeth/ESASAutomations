package com.salesforce.esas.coreservices.hackathon.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GenericService {
	
	public final String postgreHost = "ec2-23-21-88-45.compute-1.amazonaws.com";
	public final String postgreDB = "dd9cmpsjagtepb";
	public final String postgreUsername = "ezfmmlebdzkskl";
	public final String postgrePassword = "7b4b3176116b6cff181c709a62d2e7b10e4561b26cf379e32120fa92303a6d82";

	public String getDesc() {

		System.out.println("getDesc() is executed!");

		return "Gradle + Spring MVC Hello World Example";

	}

	public String getTitle(String name) {

		System.out.println("getTitle() is executed! $name : {}" + name);
		getOrg62Analysis();
		if(StringUtils.isEmpty(name)){
			return "Hello World";
		}else{
			return "Hello " + name;
		}
		
	}
	
	public String getOrg62Analysis(){
		System.out.println("-------- PostgreSQL "
				+ "JDBC Connection Testing ------------");
		try {

			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return null;

		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;

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
			return null;

		}

		if (connection != null) {
			System.out.println("You made it, take control your database now!");
		} else {
			System.out.println("Failed to make connection!");
		}
		
		return null;
	}
}
