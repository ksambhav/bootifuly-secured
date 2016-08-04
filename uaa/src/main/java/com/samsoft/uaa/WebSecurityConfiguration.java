/**
 * 
 */
package com.samsoft.uaa;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.filter.CompositeFilter;

/**
 * @author sambhav.jain
 *
 */
@Configuration
@Order(6)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter{


	/*@Autowired
	protected AuthenticationManager authenticationManager;*/

	@Autowired
	public OAuth2ClientContext oauth2ClientContext;

	/*@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.parentAuthenticationManager(authenticationManager);
	}*/

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
			http.formLogin().loginPage("/").loginProcessingUrl("/login")
			.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
		// @formatter:on
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
		oauth2ClientAuthFilter.setTokenServices(new UserInfoTokenServices(
				clientResource.getResource().getUserInfoUri(), clientResource.getClient().getClientId()));
		oauth2ClientAuthFilter.setAuthenticationSuccessHandler(successHandler());
		return oauth2ClientAuthFilter;
	}

	@Bean
	public AuthenticationSuccessHandler successHandler() {
		return new ExternalAuthenticationSuccessHandler();
	}


}
