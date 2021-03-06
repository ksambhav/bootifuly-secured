/**
 * 
 */
package com.samsoft.uaa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.bind.annotation.RestController;

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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuthenticationServer.class, args);
	}

	@Autowired
	protected AuthenticationManager authenticationManager;

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		// @formatter:off
		endpoints
			.authenticationManager(authenticationManager)
			.tokenStore(tokenStore())
			.accessTokenConverter(jwtAccessTokenConverter());
		// @formatter:on
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

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

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey("sambhav"); // simple symmetric encryption key.
		return converter;
	}

	@Bean
	public TokenStore tokenStore() {
		TokenStore store = new JwtTokenStore(jwtAccessTokenConverter());
		return store;
	}

}
