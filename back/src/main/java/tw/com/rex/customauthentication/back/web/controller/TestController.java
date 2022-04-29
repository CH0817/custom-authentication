package tw.com.rex.customauthentication.back.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.rex.customauthentication.back.config.SsoiUserDetailsService;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

    private final SsoiUserDetailsService service;

    @PostMapping("/connect")
    public ResponseEntity<Map<String, Object>> test(Principal principal) {
        Map<String, Object> map = new HashMap<>();
        map.put("principal", principal);
        map.put("userDetails", service.getUserDetails());
        return ResponseEntity.ok(map);
    }

}
