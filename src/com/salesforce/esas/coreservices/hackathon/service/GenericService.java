package com.salesforce.esas.coreservices.hackathon.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GenericService {

	public String getDesc() {

		System.out.println("getDesc() is executed!");

		return "Gradle + Spring MVC Hello World Example";

	}

	public String getTitle(String name) {

		System.out.println("getTitle() is executed! $name : {}" + name);

		if(StringUtils.isEmpty(name)){
			return "Hello World";
		}else{
			return "Hello " + name;
		}
		
	}
}
