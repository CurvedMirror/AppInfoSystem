package cn.appsys.dao.devuser;


import cn.appsys.pojo.DevUser;
import org.apache.ibatis.annotations.Param;

/**
 * @author Administrator
 */
    public interface DevUserMapper {
    /**
     * 登录
     * @param devCode
     * @param devPassword
     * @return
     */
    DevUser doLogin(@Param("devCode") String devCode,
                    @Param("devPassword") String devPassword );
}
