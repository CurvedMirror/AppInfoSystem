package cn.appsys.dao.appinfo;


import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.DataDictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 */
public interface AppInfoMapper {
    /**
     * 获取app列表
     * @param softwareName 软件名
     * @param status 状态
     * @param flatformId 所属平台
     * @param categoryLevel1 一级分类
     * @param categoryLevel2 二级分类
     * @param categoryLevel3 三级分类
     * @param currentPageNo 当前页
     * @param pageSize 页大小
     * @return
     */
    List<AppInfo> getAppInfoList(@Param("softwareName") String softwareName,
                                 @Param("status") String status,
                                 @Param("flatformId") String flatformId,
                                 @Param("categoryLevel1") String categoryLevel1,
                                 @Param("categoryLevel2") String categoryLevel2,
                                 @Param("categoryLevel3") String categoryLevel3,
                                 @Param("from") Integer currentPageNo,
                                 @Param("pageSize") Integer pageSize);


    int getAppInfoCount(@Param("softwareName") String softwareName,
                        @Param("status") String status,
                        @Param("flatformId") String flatformId,
                        @Param("categoryLevel1") String categoryLevel1,
                        @Param("categoryLevel2") String categoryLevel2,
                        @Param("categoryLevel3") String categoryLevel3);

    List<DataDictionary> getDataDictionaryList(String typeCode);

    boolean addAppInfo(AppInfo appInfo);

    AppInfo getAppInfo(@Param(value = "id") Integer id, @Param(value = "APKName") String APKName);

    boolean appinfomodifysave(AppInfo appInfo);

    AppInfo getAppInfoByID(@Param(value = "id") Integer id);

    boolean deleteAppLogo(@Param(value="id")Integer id);

    /**
     * 根据appId，更新最新versionId
     * @param versionId
     * @param appId
     * @return
     * @throws Exception
     */
    void updateVersionId(@Param(value = "versionId") Integer versionId, @Param(value = "id") Integer appId);

    /**
     * 获取app基础信息
     * @param id
     * @return
     */
    AppInfo getAppInfoById(@Param(value = "id") Integer id);

    /**
     * 删除app信息
     * @param id
     * @return
     */
    boolean delAppInfo(@Param("id") Integer id);

    /**
     * 更新app状态
     * @param appInfo
     * @return
     */
    boolean updateStatus(@Param(value = "appInfo") AppInfo appInfo);
}
