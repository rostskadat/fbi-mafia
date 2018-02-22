package com.stratio.fbi.mafia.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class IndexController implements ErrorController {

	private static final String PATH = "/error";

	@RequestMapping(value = PATH)
	public ModelAndView error(ModelMap model) {
		return new ModelAndView("forward:/", model);
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}
}
