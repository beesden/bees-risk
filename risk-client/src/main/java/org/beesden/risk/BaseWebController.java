package org.beesden.risk;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class BaseWebController {

	@RequestMapping("/home")
	String home() {
		return "index";
	}
}