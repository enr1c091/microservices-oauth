package com.enrico.microservices.got.utils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

import com.enrico.microservices.got.model.CustomPrincipal;

public class CustomUserAuthenticationConverter implements UserAuthenticationConverter {

	private final String EMAIL = "email";

	private Collection<? extends GrantedAuthority> defaultAuthorities;

	public void setDefaultAuthorities(String[] defaultAuthorities) {
		this.defaultAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList(StringUtils.arrayToCommaDelimitedString(defaultAuthorities));
	}

	@Override
	public Map<String, ?> convertUserAuthentication(Authentication userAuthentication) {
		Map<String, Object> response = new LinkedHashMap<String, Object>();
		response.put(USERNAME, userAuthentication.getName());

		if (userAuthentication.getAuthorities() != null && !userAuthentication.getAuthorities().isEmpty())
			response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(userAuthentication.getAuthorities()));

		return response;
	}

	@Override
	public Authentication extractAuthentication(Map<String, ?> map) {
		if (map.containsKey(USERNAME))
			return new UsernamePasswordAuthenticationToken(
					new CustomPrincipal(map.get(USERNAME).toString(), map.get(EMAIL).toString()), "N/A",
					getAuthorities(map));
		return null;
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
		if (!map.containsKey(AUTHORITIES))
			return defaultAuthorities;

		Object authorities = map.get(AUTHORITIES);

		if (authorities instanceof String)
			return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);

		if (authorities instanceof Collection)
			return AuthorityUtils.commaSeparatedStringToAuthorityList(
					StringUtils.collectionToCommaDelimitedString((Collection<?>) authorities));

		throw new IllegalArgumentException("Authorities must be either a String or a Collection");
	}

}
