/**
 * 
 */
package com.samsoft.uaa;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CompositeFilter;

/**
 * @author sambhav.jain
 *
 */
@SpringBootApplication
@RestController
@EnableConfigurationProperties(value = { ClientResources.class })
@EnableAuthorizationServer
@EnableOAuth2Client
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
			.redirectUris("http://127.0.0.1:9999/#/home")
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

		@Autowired
		public OAuth2ClientContext oauth2ClientContext;

		@Bean
		@ConfigurationProperties("google")
		public ClientResources google() {
			return new ClientResources();
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.parentAuthenticationManager(authenticationManager);
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
			// filters.add(ssoFilter(facebook(), "/login/facebook"));
			// filters.add(ssoFilter(github(), "/login/github"));
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
			return oauth2ClientAuthFilter;
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
				http.formLogin().loginPage("/").loginProcessingUrl("/login")
				.and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.and().addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
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
