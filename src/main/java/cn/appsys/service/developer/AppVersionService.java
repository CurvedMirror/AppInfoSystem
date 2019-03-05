package cn.appsys.service.developer;


import cn.appsys.pojo.AppVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppVersionService {
    /**
     * 根据appid获取版本信息
     * @param id
     * @return
     */
    AppVersion getAppVersionById(@Param("id") Integer id);

    /**
     * 删除apk文件
     * @param id
     * @return
     */
    boolean deleteApkFile(@Param("id")Integer id);
    /**
     * 新增app版本信息
     * @param appVersion
     * @return
     */
    boolean appsysAdd(AppVersion appVersion);
    /**
     * 获取app版本信息
     * @param appId
     * @return
     */
    List<AppVersion> getAppVersionList(@Param("appId") Integer appId);
}
