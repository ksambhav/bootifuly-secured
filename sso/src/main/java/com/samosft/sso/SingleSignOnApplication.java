/**
 * 
 */
package com.samosft.sso;

import java.security.Principal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author sambhav.jain
 *
 */
@SpringBootApplication
@EnableOAuth2Sso
@RestController
public class SingleSignOnApplication extends WebSecurityConfigurerAdapter {

	// @formatter:off
		private static final String[] IGNORE_STATIC_RESOURCES = { 
				"/favicon.ico", 
				"/**/*.html", 
				"/**/*.less", 
				"/**/*.css",
				"/**/*.js",
				"/index.html" 
			};
		// @formatter:on

	@RequestMapping(value = "/me", method = RequestMethod.GET)
	public Principal me(Principal principal) {
		return principal;
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		// @formatter:off
		web
			.ignoring()
			.antMatchers(HttpMethod.GET, IGNORE_STATIC_RESOURCES);
		// @formatter:on
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.antMatcher("/**").authorizeRequests()
			.anyRequest().authenticated()
			.and().logout()
			.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		// @formatter:on

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(SingleSignOnApplication.class, args);
	}

}
