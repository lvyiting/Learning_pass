package cn.race.admin.config;


import cn.race.admin.security.*;
import cn.race.common.MD5.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //管理权限校验
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	LoginFailureHandler loginFailureHandler;

	@Autowired
	LoginSuccessHandler loginSuccessHandler;

	@Autowired
	CaptchaFilter captchaFilter;

	@Autowired
	JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	JwtAccessDeniedHandler jwtAccessDeniedHandler;

	@Autowired
	UserDetailServiceImpl userDetailService;

	@Autowired
	JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

	@Bean
	JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
		return jwtAuthenticationFilter;
	}

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//白名单
	private static final String[] URL_WHITELIST = {

			"/login",
			"/logout",
			"/captcha",
			"/favicon.ico",

	};


	protected void configure(HttpSecurity http) throws Exception {

		http.cors().and().csrf().disable()

				// 登录配置
				.formLogin()
				.successHandler(loginSuccessHandler)
				.failureHandler(loginFailureHandler)

				.and()
				.logout()
				.logoutSuccessHandler(jwtLogoutSuccessHandler)

				// 禁用session
				.and()
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				// 配置拦截规则
				.and()
				.authorizeRequests()
				.antMatchers(URL_WHITELIST).permitAll()
				.anyRequest().authenticated()

				// 异常处理器
				.and()
				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler)

				// 配置自定义的过滤器
				.and()
				.addFilter(jwtAuthenticationFilter())
				.addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)

		;

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService).passwordEncoder(new PasswordEncoder(){

			@Override
			public String encode(CharSequence rawPassword) {
				return MD5Util.encode((String)rawPassword);
			}

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				return encodedPassword.equals(MD5Util.encode((String)rawPassword));
			}}); //user Details Service验证
	}
}
