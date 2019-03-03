package cn.appsys.service.developer;


import cn.appsys.pojo.DevUser;

/**
 * @author Administrator
 */
public interface DevUserService {
    DevUser doLogin( String devCode, String devPassword );
}
