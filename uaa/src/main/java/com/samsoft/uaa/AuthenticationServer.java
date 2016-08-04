/**
 * 
 */
package com.samsoft.uaa;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sambhav.jain
 *
 */
@SpringBootApplication
@RestController
@EnableConfigurationProperties(value = { ClientResources.class })
@EnableAuthorizationServer
public class AuthenticationServer extends AuthorizationServerConfigurerAdapter {

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// @formatter:off
		clients.inMemory()
			.withClient("acme")
			.secret("acmesecret")
			.accessTokenValiditySeconds(36000)
			.autoApprove(true)
			.scopes("read","write","profile","openid")
			.authorizedGrantTypes("authorization_code","password"); 
		// @formatter:on

	}

	@RequestMapping(value = { "/me", "/user" }, method = RequestMethod.GET)
	public Map<String, String> user(Principal principal) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("name", principal.getName());
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServer.class, args);

	}

	@Configuration
	@Order(6)
	public static class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

		@Autowired
		protected AuthenticationManager authenticationManager;

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.parentAuthenticationManager(authenticationManager);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
				http.formLogin();//.loginPage("/")
//				.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
			// @formatter:on

		}

	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/me").authorizeRequests().anyRequest().authenticated();
		}
	}

}
