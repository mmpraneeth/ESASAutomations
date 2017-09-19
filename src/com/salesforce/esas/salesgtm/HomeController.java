package com.salesforce.esas.salesgtm;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {	

	@RequestMapping(method = RequestMethod.GET)
	public String uploadFile(ModelMap map){
		return "";
	}

}
