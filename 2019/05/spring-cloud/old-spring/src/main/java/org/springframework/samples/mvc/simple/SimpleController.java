package org.springframework.samples.mvc.simple;

import feign.Feign;
import org.springframework.samples.mvc.PepLogoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SimpleController {

	@RequestMapping("/simple")
	public @ResponseBody String simple() {
		PepLogoService service = Feign.builder().target(PepLogoService.class, "http://192.168.1.111/pep/develop/");
		String banner = service.banner();
//		String banner = "";
		return "Hello world!<br>" + banner;
	}

}
