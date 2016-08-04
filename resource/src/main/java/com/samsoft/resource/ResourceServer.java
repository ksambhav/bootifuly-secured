/**
 * 
 */
package com.samsoft.resource;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sambhav.jain
 *
 */
@SpringBootApplication
@EnableResourceServer
@RestController
public class ResourceServer {

	@CrossOrigin(origins =  "http://localhost:9999")
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public Map<String, String> test(Principal principal) {
		Map<String, String> map = new HashMap<>(2);
		map.put("a", "100");
		map.put("b", principal.getName());
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(ResourceServer.class, args);
	}

}
