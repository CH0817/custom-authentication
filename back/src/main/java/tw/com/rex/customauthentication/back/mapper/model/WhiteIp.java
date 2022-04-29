package tw.com.rex.customauthentication.back.mapper.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WhiteIp implements Serializable {

    private String ip;
    private String name;
    private String createdUser;
    private Date createdDate;
    private String modifiedUser;
    private Date modifiedDate;

}
