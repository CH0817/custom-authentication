package tw.com.rex.customauthentication.back.config;

import org.springframework.security.core.userdetails.UserDetails;

public interface SsoiUserDetailsService {

    UserDetails loadUserDetailsByJwtToken(String jwtToken);

    SsoiUserDetails getUserDetails();

}
