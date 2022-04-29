package tw.com.rex.customauthentication.back.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class RedisAuthenticationFilter extends OncePerRequestFilter {

    @NonNull
    private final RedisTemplate<String, String> redisTemplate;
    @NonNull
    private final SsoiUserDetailsService userDetailsService;
    @Value("${security.token-expire-time}")
    private long tokenExpireTime;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!HttpMethod.OPTIONS.matches(request.getMethod())) {
            checkRedisAuthentication(request);
        }
        filterChain.doFilter(request, response);
    }

    private void checkRedisAuthentication(HttpServletRequest request) {
        String ssoToken = request.getHeader("SSO-TOKEN");
        if (isAuthenticationExpire(ssoToken)) {
            resetTokenExpire(ssoToken);
            handleAuthentication(ssoToken);
        } else {
            SecurityContextHolder.getContext().setAuthentication(null);
            request.getSession().invalidate();
        }
    }

    private boolean isAuthenticationExpire(String ssoToken) {
        if (StringUtils.isBlank(ssoToken)) {
            return false;
        }
        boolean hasKey = Optional.ofNullable(redisTemplate.hasKey(ssoToken)).orElse(Boolean.FALSE);
        long expireTime = Optional.ofNullable(redisTemplate.getExpire(ssoToken, TimeUnit.SECONDS)).orElse(0L);
        return StringUtils.isNotBlank(ssoToken) && hasKey && expireTime > 0L;
    }

    private void handleAuthentication(String ssoToken) {
        if (needReloadAuthentication(ssoToken)) {
            String jwtToken = redisTemplate.opsForValue().get(ssoToken);
            UserDetails userDetails = userDetailsService.loadUserDetailsByJwtToken(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(new SsoiAuthenticationToken(userDetails, ssoToken, userDetails.getAuthorities()));
        }
    }

    private boolean needReloadAuthentication(String ssoToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            return true;
        }
        String oldToken = authentication.getCredentials().toString();
        if (!StringUtils.equals(oldToken, ssoToken)) {
            deleteOldToken(oldToken);
            return true;
        }
        return false;
    }

    private void resetTokenExpire(String ssoToken) {
        redisTemplate.expire(ssoToken, tokenExpireTime, TimeUnit.SECONDS);
    }

    private void deleteOldToken(String oldSsoToken) {
        if (Optional.ofNullable(redisTemplate.hasKey(oldSsoToken)).orElse(Boolean.FALSE)) {
            redisTemplate.delete(oldSsoToken);
        }
    }

}
