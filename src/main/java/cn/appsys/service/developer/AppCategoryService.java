package cn.appsys.service.developer;


import cn.appsys.pojo.AppCategory;

import java.util.List;

public interface AppCategoryService {
    List<AppCategory> getCategoryListByParentId(String parentId);
}
