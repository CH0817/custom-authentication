package tw.com.rex.customauthentication.back.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

@Component
public class SsoiLogoutHandler implements LogoutHandler {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (Objects.nonNull(authentication)) {
            String ssoToken = (String) authentication.getCredentials();
            if (Optional.ofNullable(redisTemplate.hasKey(ssoToken)).orElse(false)) {
                redisTemplate.delete(ssoToken);
            }
        }
        request.getSession().invalidate();
    }

}
