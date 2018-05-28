package com.enrico.microservices.auth.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
		implements LogoutSuccessHandler {

	private static final String BEARER_AUTHENTICATION = "Bearer ";
	private static final String HEADER_AUTHORIZATION = "authorization";

	@Autowired
	private TokenStore tokenStore;

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		String token = request.getHeader(HEADER_AUTHORIZATION);

		if (token != null && token.startsWith(BEARER_AUTHENTICATION)) {
			String jwt = token.substring("Bearer".length() + 1);
			OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(jwt);
			if (oAuth2AccessToken != null) {
				tokenStore.removeAccessToken(oAuth2AccessToken);
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);

	}
}
