package tw.com.rex.customauthentication.back.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @NonNull
    private final SsoiAuthenticationEntryPoint authenticationEntryPoint;
    @NonNull
    private final RedisAuthenticationFilter redisAuthenticationFilter;
    @NonNull
    private final SsoiLogoutHandler logoutHandler;
    @NonNull
    private final SsoiAuthenticationSuccessHandler authenticationSuccessHandler;
    @NonNull
    private final IpAnonymousAuthenticationFilter customAnonymousAuthenticationFilter;
    @Value("${cas.logout}")
    private String casLogoutUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().disable()
                .anonymous().authenticationFilter(customAnonymousAuthenticationFilter).and()
                .authorizeRequests().antMatchers("/public/**").permitAll().and()
                .authorizeRequests().anyRequest().authenticated().and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(redisAuthenticationFilter, SsoiAuthenticationFilter.class)
                .addFilterBefore(logoutFilter(), LogoutFilter.class);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/static/js/**", "/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**");
    }

    private SsoiAuthenticationFilter authenticationFilter() throws Exception {
        SsoiAuthenticationFilter filter = new SsoiAuthenticationFilter();
        filter.setAuthenticationManager(this.authenticationManager());
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        return filter;
    }

    private LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(casLogoutUrl, logoutHandler, new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl("/logout");
        return logoutFilter;
    }

}
