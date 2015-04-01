package com.justbootup.blouda.controller;




import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class HomeController {
	
	
	
	@RequestMapping(value = "/sample", method = RequestMethod.GET)
	public String sample() {
		
		System.out.println("sample page");
		return "sample";
	}
	
}
