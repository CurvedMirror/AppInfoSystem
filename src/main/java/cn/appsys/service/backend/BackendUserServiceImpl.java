package cn.appsys.service.backend;


import cn.appsys.dao.backenduser.BackendUserMapper;
import cn.appsys.pojo.*;
import cn.appsys.service.developer.AppCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BackendUserServiceImpl implements BackendUserService {

    @Autowired
    private BackendUserMapper backendUserMapper;

    @Override
    public BackendUser login(String userCode, String userPassword) {
        BackendUser user = backendUserMapper.getLoginUser(userCode);
        //匹配密码
        if(null != user){
            if(!user.getUserPassword().equals(userPassword)) {
                user = null;
            }
        }
        return user;
    }

}
