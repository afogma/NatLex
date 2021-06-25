package team.natlex.NatLex.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http

                .anonymous()
                .and()
                .authorizeRequests()
//                .anyRequest()
                .antMatchers("/api/sections").permitAll()
                .antMatchers("/api/sections/by-code*").permitAll()
                .antMatchers("/api/classes/*").hasAnyAuthority("USER")
                .antMatchers("/api/sections/*").hasAnyAuthority("USER")
                .antMatchers("/api/section/**").hasAnyAuthority("USER")
                .antMatchers("/api/import/**").hasAnyAuthority("USER")
                .antMatchers("/api/export/**").hasAnyAuthority("USER")
                .and()
                .httpBasic()
                .and()
                .csrf().disable();
    }

    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("admin").password(passwordEncoder().encode("admin"))
                .authorities("USER");
    }
}