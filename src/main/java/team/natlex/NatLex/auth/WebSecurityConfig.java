package team.natlex.NatLex.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
                .antMatchers("/api/sections").permitAll()
                .antMatchers("/api/sections/by-code*").permitAll()
                .antMatchers("/api/classes/*").permitAll()
                .antMatchers("/api/class/**").hasAuthority("USER")
                .antMatchers("/api/sections/*").hasAuthority("USER")
                .antMatchers("/api/section/**").hasAuthority("USER")
                .antMatchers("/api/import/**").hasAuthority("USER")
                .antMatchers("/api/export/**").hasAuthority("USER")
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