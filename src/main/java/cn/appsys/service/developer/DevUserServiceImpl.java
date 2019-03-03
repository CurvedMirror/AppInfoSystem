package cn.appsys.service.developer;


import cn.appsys.dao.devuser.DevUserMapper;
import cn.appsys.pojo.DevUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevUserServiceImpl implements DevUserService {

    @Autowired
    private DevUserMapper devUserMapper;

    @Override
    public DevUser doLogin(String devCode, String devPassword) {
        return devUserMapper.doLogin(devCode,devPassword);
    }
}
