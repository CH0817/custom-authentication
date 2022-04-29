package tw.com.rex.customauthentication.back.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SsoiAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public SsoiAuthenticationFilter() {
        super("/sso/token");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String ssoToken = request.getHeader("SSO-TOKEN");
        if (!StringUtils.isNotBlank(ssoToken)) {
            log.debug("SSO-TOKEN is empty or null");
            throw new BadCredentialsException("Request not have sso-token");
        }
        return this.getAuthenticationManager().authenticate(new SsoiAuthenticationToken(ssoToken));
    }

}
