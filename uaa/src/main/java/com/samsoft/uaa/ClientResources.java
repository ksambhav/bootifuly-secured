package com.samsoft.uaa;

/**
 * 
 */

import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Kumar Sambhav Jain
 *
 */
@Getter
@Setter
@ConfigurationProperties
public class ClientResources {
	private OAuth2ProtectedResourceDetails client = new AuthorizationCodeResourceDetails();
	private ResourceServerProperties resource = new ResourceServerProperties();

}
