package tw.com.rex.customauthentication.back.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class SsoiTokenVerifyResponse implements Serializable {

    private String msg;
    private String stsCd;
    private String jwtToken;

}
