/**
 * 
 */
package com.samsoft.uaa;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * @author sambhav.jain
 *
 */
public class ExternalAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private static final Logger log = LoggerFactory.getLogger(ExternalAuthenticationSuccessHandler.class);

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		super.onAuthenticationSuccess(request, response, authentication);
		if (authentication instanceof OAuth2Authentication) {
			OAuth2Authentication oauth = (OAuth2Authentication) authentication;

			if (request.getRequestURI().contains("google")) {
				findOrRegisterGoogle(oauth);
			}

		}
	}

	@SuppressWarnings("unchecked")
	private void findOrRegisterGoogle(OAuth2Authentication oauth) {
		Map<String, String> lhm = (Map<String, String>) oauth.getUserAuthentication().getDetails();
		log.debug(lhm + "");
		String email = lhm.get("email");
		String name = lhm.get("name");
		String googleId = lhm.get("id");
		log.debug("register the member here if not already registered. - {}", email);
	}
}
