package com.enrico.microservices.got.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessTokenConverter implements AccessTokenConverter, JwtAccessTokenConverterConfigurer {

	private boolean includeGrantType;

	private UserAuthenticationConverter userTokenConverter = new CustomUserAuthenticationConverter();

	@Override
	public void configure(JwtAccessTokenConverter converter) {
		converter.setAccessTokenConverter(this);
	}

	public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
		DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(value);
		Map<String, Object> info = new HashMap<String, Object>(map);

		info.remove(EXP);
		info.remove(AUD);
		info.remove(CLIENT_ID);
		info.remove(SCOPE);

		if (map.containsKey(EXP))
			token.setExpiration(new Date((Long) map.get(EXP) * 1000L));

		if (map.containsKey(JTI))
			info.put(JTI, map.get(JTI));

		token.setScope(extractScope(map));
		token.setAdditionalInformation(info);
		return token;
	}

	@Override
	public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
		Set<String> scope = extractScope(map);
		Map<String, String> parameters = new HashMap<String, String>();
		Authentication user = userTokenConverter.extractAuthentication(map);

		String clientId = (String) map.get(CLIENT_ID);
		parameters.put(CLIENT_ID, clientId);

		if (includeGrantType && map.containsKey(GRANT_TYPE))
			parameters.put(GRANT_TYPE, (String) map.get(GRANT_TYPE));

		Set<String> resourceIds = new LinkedHashSet<String>(
				map.containsKey(AUD) ? getAudience(map) : Collections.<String>emptySet());

		Collection<? extends GrantedAuthority> authorities = null;

		if (user == null && map.containsKey(AUTHORITIES)) {
			@SuppressWarnings("unchecked")
			String[] roles = ((Collection<String>) map.get(AUTHORITIES)).toArray(new String[0]);
			authorities = AuthorityUtils.createAuthorityList(roles);
		}

		OAuth2Request request = new OAuth2Request(parameters, clientId, authorities, true, scope, resourceIds, null,
				null, null);

		return new OAuth2Authentication(request, user);
	}

	public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		Map<String, Object> response = new HashMap<String, Object>();
		OAuth2Request clientToken = authentication.getOAuth2Request();

		if (!authentication.isClientOnly())
			response.putAll(userTokenConverter.convertUserAuthentication(authentication.getUserAuthentication()));
		else if (clientToken.getAuthorities() != null && !clientToken.getAuthorities().isEmpty())
			response.put(UserAuthenticationConverter.AUTHORITIES,
					AuthorityUtils.authorityListToSet(clientToken.getAuthorities()));

		if (token.getScope() != null)
			response.put(SCOPE, token.getScope());

		if (token.getAdditionalInformation().containsKey(JTI))
			response.put(JTI, token.getAdditionalInformation().get(JTI));

		if (token.getExpiration() != null)
			response.put(EXP, token.getExpiration().getTime() / 1000);

		if (includeGrantType && authentication.getOAuth2Request().getGrantType() != null)
			response.put(GRANT_TYPE, authentication.getOAuth2Request().getGrantType());

		response.putAll(token.getAdditionalInformation());

		response.put(CLIENT_ID, clientToken.getClientId());
		if (clientToken.getResourceIds() != null && !clientToken.getResourceIds().isEmpty())
			response.put(AUD, clientToken.getResourceIds());

		return response;
	}

	private Collection<String> getAudience(Map<String, ?> map) {
		Object auds = map.get(AUD);

		if (auds instanceof Collection) {
			@SuppressWarnings("unchecked")
			Collection<String> result = (Collection<String>) auds;
			return result;
		}

		return Collections.singleton((String) auds);
	}

	private Set<String> extractScope(Map<String, ?> map) {
		Set<String> scope = Collections.emptySet();

		if (map.containsKey(SCOPE)) {
			Object scopeObj = map.get(SCOPE);

			if (String.class.isInstance(scopeObj))
				scope = new LinkedHashSet<String>(Arrays.asList(String.class.cast(scopeObj).split(" ")));
			else if (Collection.class.isAssignableFrom(scopeObj.getClass())) {
				@SuppressWarnings("unchecked")
				Collection<String> scopeColl = (Collection<String>) scopeObj;
				scope = new LinkedHashSet<String>(scopeColl);
			}
		}
		return scope;
	}

	public void setUserTokenConverter(UserAuthenticationConverter userTokenConverter) {
		this.userTokenConverter = userTokenConverter;
	}

	public void setIncludeGrantType(boolean includeGrantType) {
		this.includeGrantType = includeGrantType;
	}
}
