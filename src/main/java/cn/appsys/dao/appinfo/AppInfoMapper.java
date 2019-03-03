package cn.appsys.dao.appinfo;


import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.DataDictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppInfoMapper {

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

    public AppInfo getAppInfo(@Param(value="id")Integer id,@Param(value="APKName")String APKName);
}
