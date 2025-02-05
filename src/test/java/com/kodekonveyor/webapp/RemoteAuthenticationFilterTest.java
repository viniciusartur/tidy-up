package com.kodekonveyor.webapp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;

import com.kodekonveyor.authentication.UserRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RunWith(MockitoJUnitRunner.class)
public class RemoteAuthenticationFilterTest {

	@InjectMocks
	private RemoteAuthenticationFilter remoteAuthenticationFilter;

	@Mock
	private UserRepository userRepository;

	@Mock
	private LoggerService loggerService;

	@Mock
	private ServletResponse servletResponse;

	@Mock
	private FilterChain filterChain;

	private WebappTestData testData;

	@Captor
	private ArgumentCaptor<Authentication> newAuthentication;

	@Captor
	private ArgumentCaptor<String> stringCaptor;

	@BeforeEach
	public void setUp() {
		testData = new WebappTestData();
		doReturn(testData.USER_LIST).when(userRepository).findByAuth0id(testData.AUTH0ID);
	}

	@DisplayName("if authenticated, does not set authenticaed user")
	@Test
	public void test() throws IOException, ServletException {
		AuthenticationStubs.authenticated();
		remoteAuthenticationFilter.doFilter(testData.REQUEST, servletResponse, filterChain);
		verify(AuthenticationStubs.securityContext, never()).setAuthentication(newAuthentication.capture());
	}

	@DisplayName("if authenticated, calls the filter chain")
	@Test
	public void test01() throws IOException, ServletException {
		AuthenticationStubs.authenticated();
		remoteAuthenticationFilter.doFilter(testData.REQUEST, servletResponse, filterChain);
		verify(filterChain).doFilter(testData.REQUEST, servletResponse);
	}

	@DisplayName("if the filter authenticates someone, logs the authentication")
	@Test
	public void test02() throws IOException, ServletException {
		AuthenticationStubs.nullAuthentication();
		remoteAuthenticationFilter.doFilter(testData.REQUEST, servletResponse, filterChain);
		verify(loggerService).call(stringCaptor.capture());
		TestHelper.assertContains("authenticated", stringCaptor.getValue());
	}

	@DisplayName("if Authentication is null, sets the remote user as authenticated")
	@Test
	public void test1() throws IOException, ServletException {
		AuthenticationStubs.nullAuthentication();
		remoteAuthenticationFilter.doFilter(testData.REQUEST, servletResponse, filterChain);
		assertRemoteUserIsCorrectlySet();
	}

	@DisplayName("if Authentication is anonymous, sets the remote user as authenticated")
	@Test
	public void test2() throws IOException, ServletException {
		AuthenticationStubs.anonymous();
		remoteAuthenticationFilter.doFilter(testData.REQUEST, servletResponse, filterChain);
		assertRemoteUserIsCorrectlySet();
	}

	@DisplayName("if Authentication is null and the remote user exists, sets the remote user as authenticated")
	@Test
	public void test3() throws IOException, ServletException {
		AuthenticationStubs.nullAuthentication();
		remoteAuthenticationFilter.doFilter(testData.REQUEST, servletResponse, filterChain);
		assertRemoteUserIsCorrectlySet();
	}

	private void assertRemoteUserIsCorrectlySet() {
		verify(AuthenticationStubs.securityContext).setAuthentication(newAuthentication.capture());
		assertEquals(testData.AUTH0ID, newAuthentication.getValue().getCredentials());
	}

	@DisplayName("if Authentication is null and the remote user does not exists, does not set an authenticated user")
	@Test
	public void test4() throws IOException, ServletException {
		AuthenticationStubs.nullAuthentication();
		remoteAuthenticationFilter.doFilter(testData.REQUEST_WITH_UNKNOWN_USER, servletResponse, filterChain);
		verify(AuthenticationStubs.securityContext, never()).setAuthentication(newAuthentication.capture());
	}

}
