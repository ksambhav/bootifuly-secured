/**
 * 
 */
package com.samsoft.uaa;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CompositeFilter;

/**
 * @author sambhav.jain
 *
 */
@SpringBootApplication
@EnableOAuth2Client
@RestController
@EnableConfigurationProperties(value = { ClientResources.class })
public class AuthenticationServer extends WebSecurityConfigurerAdapter {

	@Autowired
	public OAuth2ClientContext oauth2ClientContext;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		 http.formLogin()
	      .and().formLogin()
	      .and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
		// @formatter:on

	}

	@Bean
	@ConfigurationProperties("facebook")
	public ClientResources facebook() {
		return new ClientResources();
	}

	@Bean
	@ConfigurationProperties("github")
	public ClientResources github() {
		return new ClientResources();
	}

	@Bean
	@ConfigurationProperties("google")
	public ClientResources google() {
		return new ClientResources();
	}

	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

	private Filter ssoFilter() {
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		filters.add(ssoFilter(facebook(), "/login/facebook"));
		filters.add(ssoFilter(github(), "/login/github"));
		filters.add(ssoFilter(google(), "/login/google"));
		filter.setFilters(filters);
		return filter;
	}

	/**
	 * Filter to execute the OAuth2 dance.
	 * 
	 * @param clientResource
	 *            {@link ClientResources} having
	 *            {@link OAuth2ProtectedResourceDetails} and
	 *            {@link ResourceServerProperties}
	 * @param path
	 *            Path for which this filter comes into play.
	 * @return {@link Filter}
	 */
	private Filter ssoFilter(ClientResources clientResource, String path) {
		OAuth2ClientAuthenticationProcessingFilter oauth2ClientAuthFilter = new OAuth2ClientAuthenticationProcessingFilter(
				path);
		OAuth2RestTemplate socialTemplate = new OAuth2RestTemplate(clientResource.getClient(), oauth2ClientContext);
		oauth2ClientAuthFilter.setRestTemplate(socialTemplate);
		oauth2ClientAuthFilter.setTokenServices(new UserInfoTokenServices(clientResource.getResource().getUserInfoUri(),
				clientResource.getClient().getClientId()));
		return oauth2ClientAuthFilter;
	}

	@RequestMapping(value = "/me", method = RequestMethod.GET)
	public Principal user(Principal principal) {
		return principal;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServer.class, args);

	}

}
