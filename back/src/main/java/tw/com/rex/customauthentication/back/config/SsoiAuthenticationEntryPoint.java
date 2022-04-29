package tw.com.rex.customauthentication.back.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SsoiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        SecurityContextHolder.getContext().setAuthentication(null);
        request.getSession().invalidate();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

}