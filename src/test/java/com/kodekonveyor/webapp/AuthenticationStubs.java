package com.kodekonveyor.webapp;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationStubs {

	public static SecurityContext securityContext;
	public static Authentication authentication;

	public static void nullAuthentication() {
		createSecurityContext();
		doReturn(null).when(securityContext).getAuthentication();
		SecurityContextHolder.setContext(securityContext);
	}

	public static void authenticated() {
		createSecurityContext();
		authentication = mock(Authentication.class);
		doReturn(true).when(authentication).isAuthenticated();
		doReturn(authentication).when(securityContext).getAuthentication();
		SecurityContextHolder.setContext(securityContext);
	}

	private static void createSecurityContext() {
		securityContext = mock(SecurityContext.class);
	}

	public static void anonymous() {
		createSecurityContext();
		authentication = mock(Authentication.class);
		doReturn(false).when(authentication).isAuthenticated();
		doReturn(authentication).when(securityContext).getAuthentication();
		SecurityContextHolder.setContext(securityContext);
	}

}
