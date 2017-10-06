package com.salesforce.esas.coreservices.hackathon.web;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;	
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.salesforce.esas.coreservices.hackathon.service.GenericService;

@Controller
public class HomeController {	

	private final GenericService genericService;

	@Autowired
	public HomeController(GenericService genericService) {
		this.genericService = genericService;
	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Map<String, Object> model) {

		//genericService.getOrg62Analysis("Org62");
		
		model.put("org62", genericService.getOrg62Analysis("Org62"));
		model.put("supportforce", genericService.getOrg62Analysis("Supportforce"));
		
		return "index";
	}

}
