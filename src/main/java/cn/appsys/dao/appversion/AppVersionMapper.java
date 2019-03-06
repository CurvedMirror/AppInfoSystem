package cn.appsys.dao.appversion;


import cn.appsys.pojo.AppVersion;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppVersionMapper {
    /**
     * 根据id获取版本信息
     * @param id
     * @return
     */
     AppVersion getAppVersionById(@Param("id") Integer id);
    /**
     * 删除appapk文件
     * @param id
     * @return
     */
     boolean deleteApkFile(@Param("id")Integer id);
    /**
     * 新增app版本信息
     * @param appVersion
     * @return
     */
    boolean add(AppVersion appVersion);

    /**
     * 获取app版本信息
     * @param appId
     * @return
     */
    List<AppVersion> getAppVersionList(@Param("appId") Integer appId);

    /**
     * 修改版本信息
     * @param appVersion
     * @return
     */
    boolean modifyVersion(AppVersion appVersion);

    /**
     * 删除app版本信息
     * @param appId
     * @return
     */
    boolean delVersion(@Param("appId") Integer appId);
}
