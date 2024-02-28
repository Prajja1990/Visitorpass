package com.inops.visitorpass.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.inops.visitorpass.service.impl.UserServiceImpl;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "com.inops.visitorpass")
public class SecurityConfig {

	@Autowired
	private CustomLoginSucessHandler sucessHandler;

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// login
		http.formLogin().loginPage("/login.xhtml").permitAll().failureUrl("/login.xhtml?error=true")
				.successHandler(sucessHandler);// .usernameParameter("email") .passwordParameter("password");

		// logout
		http.logout().logoutSuccessUrl("/login.xhtml");
		// not needed as JSF 2.2 is implicitly protected against CSRF
		http.csrf().disable();

		/*
		 * http.authorizeRequests() // URL matching for accessibility
		 * .antMatchers("/login", "/register", "/javax.faces.resource/**").permitAll()
		 * //.antMatchers("/admin/**").hasAnyAuthority("ADMIN")
		 * //.antMatchers("/account/**").hasAnyAuthority("USER")
		 * .anyRequest().authenticated() .and() // form login
		 * .csrf().disable().formLogin() .loginPage("/login")
		 * .failureUrl("/login?error=true") .successHandler(sucessHandler)
		 * .usernameParameter("email") .passwordParameter("password") .and() // logout
		 * .logout() .logoutRequestMatcher(new AntPathRequestMatcher("/login"))
		 * .logoutSuccessUrl("/login") .and() .exceptionHandling()
		 * .accessDeniedPage("/access-denied");
		 */

		http.authenticationProvider(authenticationProvider());
		/* http.headers().frameOptions().sameOrigin(); */
		// require all requests to be authenticated except for the resources
		http.authorizeRequests().antMatchers("api/getMuster", "/api/register", "/javax.faces.resource/**").permitAll()
				.anyRequest().authenticated();

		return http.build();
	}
	/*
	 * @Autowired public void configureGlobal(AuthenticationManagerBuilder auth)
	 * throws Exception { auth.inMemoryAuthentication().withUser("john.doe")
	 * .password("{noop}1234").roles("USER").and()
	 * .withUser("admin").password("{noop}1234").roles("ADMIN"); }
	 */

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}
}