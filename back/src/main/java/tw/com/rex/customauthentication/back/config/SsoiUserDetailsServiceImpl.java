package tw.com.rex.customauthentication.back.config;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import tw.com.rex.customauthentication.back.util.JsonUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Component
public class SsoiUserDetailsServiceImpl implements SsoiUserDetailsService {

    @NonNull
    private final JsonUtils jsonUtils;
    @Value("${system.code}")
    private String systemCode;

    @Override
    public UserDetails loadUserDetailsByJwtToken(String jwtToken) throws UsernameNotFoundException {
        JsonNode userProfile = getUserProfileJsonNode(jwtToken);
        SsoiUserDetails userDetails = new SsoiUserDetails();
        userDetails.setUserId(jsonUtils.getStringValue(userProfile, "cn"));
        userDetails.setUsername(jsonUtils.getStringValue(userProfile, "sn"));
        userDetails.setEmployeeNumber(jsonUtils.getStringValue(userProfile, "employeeNumber"));
        userDetails.setDisplayName(jsonUtils.getStringValue(userProfile, "displayName"));
        userDetails.setDepartmentCode(jsonUtils.getStringValue(userProfile, "departmentNumber"));
        userDetails.setDepartmentName(jsonUtils.getStringValue(userProfile, "department"));
        userDetails.setDescription(jsonUtils.getStringValue(userProfile, "description"));
        userDetails.setAuthorities(getGrantedAuthorities(userProfile));
        if (log.isDebugEnabled()) {
            log.debug("login user details: {}", userDetails);
        }
        return userDetails;
    }

    @Override
    public SsoiUserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) || !authentication.getPrincipal().getClass().isAssignableFrom(SsoiUserDetails.class)) {
            return null;
        }
        return (SsoiUserDetails) authentication.getPrincipal();
    }

    public JsonNode getUserProfileJsonNode(String jwtToken) {
        JsonNode privatePayload = jsonUtils.readTree(JWT.decode(jwtToken).getClaim("privatePayload").asString());
        if (Objects.isNull(privatePayload)) {
            throw new RuntimeException("cannot read user profile from JWT token!");
        }
        return jsonUtils.readTree(privatePayload.get("userProfile").asText());
    }

    private List<GrantedAuthority> getGrantedAuthorities(JsonNode userProfile) {
        String memberOf = jsonUtils.getStringValue(userProfile, "memberOf");
        if (StringUtils.isBlank(memberOf)) {
            return Collections.emptyList();
        }
        String prefix = getRolePrefix();
        return Stream.of(memberOf)
                .filter(StringUtils::isNoneBlank)
                .map(StringUtils::trimToEmpty)
                .flatMap(m -> Arrays.stream(m.split(",")))
                .filter(m -> m.contains(prefix))
                .map(m -> StringUtils.remove(m, prefix))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private String getRolePrefix() {
        return ("CN=" + systemCode + "_").toUpperCase();
    }

}
