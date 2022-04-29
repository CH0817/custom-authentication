package tw.com.rex.customauthentication.back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import tw.com.rex.customauthentication.back.mapper.model.WhiteIp;

@Mapper
public interface WhiteIpMapper extends BaseMapper<WhiteIp> {
}
