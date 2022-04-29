package tw.com.rex.customauthentication.back.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import tw.com.rex.customauthentication.back.mapper.WhiteIpMapper;
import tw.com.rex.customauthentication.back.mapper.model.WhiteIp;

@Service
public class WhiteIpServiceImpl extends ServiceImpl<WhiteIpMapper, WhiteIp> implements WhiteIpService {
}
