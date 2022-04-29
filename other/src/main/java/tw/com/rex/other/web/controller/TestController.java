package tw.com.rex.other.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import tw.com.rex.other.model.TestModel;

@AllArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

    @NonNull
    private final ObjectMapper objectMapper;

    @PostMapping("/hello")
    public ResponseEntity<String> sayHello(@RequestBody TestModel testModel) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(testModel), headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity("http://localhost:8300/euisbq/public/hello", request, String.class);
    }

}
