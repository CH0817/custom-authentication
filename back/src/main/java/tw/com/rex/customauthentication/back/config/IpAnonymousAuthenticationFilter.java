package tw.com.rex.customauthentication.back.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.stereotype.Component;
import tw.com.rex.customauthentication.back.mapper.model.WhiteIp;
import tw.com.rex.customauthentication.back.service.WhiteIpService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.UUID;

@Component
public class IpAnonymousAuthenticationFilter extends AnonymousAuthenticationFilter {

    private final WhiteIpService whiteIpService;

    @Autowired
    public IpAnonymousAuthenticationFilter(WhiteIpService whiteIpService) {
        super(UUID.randomUUID().toString());
        this.whiteIpService = whiteIpService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication) && !HttpMethod.OPTIONS.matches(request.getMethod())) {
            if (!isWhiteIp(request)) {
                returnError(request, (HttpServletResponse) res);
                return;
            }
        }
        super.doFilter(req, res, chain);
    }

    private boolean isWhiteIp(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        QueryWrapper<WhiteIp> wrapper = new QueryWrapper<>();
        wrapper.like("ip", clientIp);
        return whiteIpService.count(wrapper) > 0;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (StringUtils.isNotEmpty(ip)) {
            return ip.contains(",") ? ip.split(",")[0] : ip;
        }
        return request.getRemoteAddr();
    }

    private void returnError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.print("Is not allow IP: " + getClientIp(request));
        writer.flush();
        writer.close();
    }

}
