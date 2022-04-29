package tw.com.rex.customauthentication.back.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class SsoiAuthenticationToken extends AbstractAuthenticationToken {

    private Object principal;
    private Object credentials;

    public SsoiAuthenticationToken(Object credentials) {
        super(Collections.emptyList());
        this.credentials = credentials;
        super.setAuthenticated(false);
    }

    public SsoiAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

}
