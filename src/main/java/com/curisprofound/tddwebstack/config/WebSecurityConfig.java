package com.curisprofound.tddwebstack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.anonymous()
                .and().authorizeRequests().antMatchers("/h2**").permitAll()
                .and().logout().permitAll();
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }
}
