package cn.appsys.service.backend;


import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.DataDictionary;

import java.util.List;

public interface AppService {
    /**
     * 按条件查询appinfo
     * @param softwareName 软件名
     * @param categoryLevel1 一级分类
     * @param categoryLevel2 二级分类
     * @param categoryLevel3 三级分类
     * @param currentPageNo 当前页
     * @param pageSize 页大小
     * @return app信息的list集合
     */
    List<AppInfo> getAppInfoList(String softwareName, String flatformId, String categoryLevel1, String categoryLevel2,
                                 String categoryLevel3, Integer currentPageNo, Integer pageSize);

    /**
     * 获取appInfo的数量
     * @param softwareName 软件名
     * @param categoryLevel1 一级分类
     * @param categoryLevel2 二级分类
     * @param categoryLevel3 三级分类
     * @return 获取app信息数量
     */
    int getAppInfoCount(String softwareName, String flatformId, String categoryLevel1, String categoryLevel2,
                        String categoryLevel3);
    /**
     * 根据typeCode查询出相应的数据字典列表
     * @param typeCode typeCode
     * @return 数据字典列表
     */
    List<DataDictionary> getDataDictionaryList(String typeCode);

    /**
     * 根据id、apkName查找appInfo
     * @param id id
     * @return appInfo
     */
    AppInfo getAppInfo(Integer id, String APKName);

    /**
     * 更新app状态
     * @return boolean
     */
    boolean updateStatus(AppInfo appInfo);

}
