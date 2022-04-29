package tw.com.rex.customauthentication.back.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Component
public class SsoiAuthenticationProvider implements AuthenticationProvider {

    @NonNull
    private final SsoiUserDetailsService userDetailsService;
    @NonNull
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${security.jwt-verify-url}")
    private String jwtVerifyUrl;
    @Value("${security.token-expire-time}")
    private long tokenExpireTime;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String ssoToken = (String) authentication.getCredentials();
        SsoiTokenVerifyResponse ssoTokenVerifyResponse = verifySsoToken(ssoToken);
        setSsoTokenInfo(ssoToken, ssoTokenVerifyResponse);
        UserDetails userDetails = userDetailsService.loadUserDetailsByJwtToken(ssoTokenVerifyResponse.getJwtToken());
        return new SsoiAuthenticationToken(userDetails, ssoToken, userDetails.getAuthorities());
    }

    private SsoiTokenVerifyResponse verifySsoToken(String ssoToken) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("ssoToken", ssoToken);
        SsoiTokenVerifyResponse ssoTokenVerifyResponse = new RestTemplate().getForEntity(jwtVerifyUrl,
                SsoiTokenVerifyResponse.class, uriVariables).getBody();
        if (!"01".equals(ssoTokenVerifyResponse.getStsCd())) {
            log.debug("SSO-TOKEN: {} is invalid", ssoToken);
            throw new BadCredentialsException("SSO-TOKEN is invalid");
        }
        return ssoTokenVerifyResponse;
    }

    private void setSsoTokenInfo(String ssoToken, SsoiTokenVerifyResponse ssoTokenVerifyResponse) {
        redisTemplate.opsForValue().set(ssoToken, ssoTokenVerifyResponse.getJwtToken());
        redisTemplate.expire(ssoToken, tokenExpireTime, TimeUnit.SECONDS);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SsoiAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
