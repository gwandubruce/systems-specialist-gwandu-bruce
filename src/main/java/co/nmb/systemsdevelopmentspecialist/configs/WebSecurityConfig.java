package co.nmb.systemsdevelopmentspecialist.configs;

import co.nmb.systemsdevelopmentspecialist.services.AuthorisedUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


import org.springframework.http.HttpMethod;

@Configuration

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public UserDetailsService userDetailsService() {
        return new AuthorisedUserService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests().antMatchers(HttpMethod.GET, "/home").authenticated()
                .antMatchers(HttpMethod.POST, "uploadFileFrontEnd").authenticated()
                .antMatchers(HttpMethod.GET, "/uploadStatus").authenticated()
                .antMatchers(HttpMethod.GET, "/uploadMessage").authenticated()
                .antMatchers(HttpMethod.GET, "/updateSubscriber").authenticated()
                .antMatchers(HttpMethod.POST, "/update_user").authenticated()
                .and()
                .formLogin().loginPage("/login")
                .usernameParameter("username")
                .defaultSuccessUrl("/home")
                .and()
                .logout().logoutSuccessUrl("/login").and().headers().cacheControl();
    }
}
