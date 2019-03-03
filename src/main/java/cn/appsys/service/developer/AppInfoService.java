package cn.appsys.service.developer;


import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.DataDictionary;

import java.util.List;

public interface AppInfoService {
    /**
     * 按条件查询appinfo
     * @param softwareName
     * @param status
     * @param categoryLevel1
     * @param categoryLevel2
     * @param categoryLevel3
     * @param currentPageNo
     * @param pageSize
     * @return
     */
    List<AppInfo> getAppInfoList(String softwareName, String status,String flatformId, String categoryLevel1, String categoryLevel2,
                                 String categoryLevel3, Integer currentPageNo, Integer pageSize);

    /**
     * 获取appInfo的数量
     * @param softwareName
     * @param status
     * @param categoryLevel1
     * @param categoryLevel2
     * @param categoryLevel3
     * @return
     */
    int getAppInfoCount(String softwareName, String status,String flatformId, String categoryLevel1, String categoryLevel2,
                        String categoryLevel3);

    /**
     * 根据typeCode查询出相应的数据字典列表
     * @param typeCode
     * @return
     */
    List<DataDictionary> getDataDictionaryList(String typeCode);

    /**
     * 新增app基本信息
     * @param appInfo
     * @return
     */
    boolean addAppInfo(AppInfo appInfo);
    /**
     * 根据id、apkName查找appInfo
     * @param id
     * @return
     * @throws Exception
     */
    public AppInfo getAppInfo(Integer id,String APKName);
}
