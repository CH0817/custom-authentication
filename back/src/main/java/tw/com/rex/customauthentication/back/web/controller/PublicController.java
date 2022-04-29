package tw.com.rex.customauthentication.back.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.com.rex.customauthentication.back.model.TestModel;

@AllArgsConstructor
@RestController
@RequestMapping("/public")
public class PublicController {

    @PostMapping("/hello")
    public ResponseEntity<String> anonymous(@RequestBody TestModel testModel) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);
        return ResponseEntity.ok("Hello~ anonymous " + testModel.getName());
    }

}
