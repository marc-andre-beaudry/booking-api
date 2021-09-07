package com.marc.campsite.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// NOTE: This is an example project, we can disable security, even if the
		// endpoints are public, we would ideally want security for admin / actuator
		// endpoints
		http.csrf().disable().authorizeRequests().anyRequest().permitAll();
	}
}
