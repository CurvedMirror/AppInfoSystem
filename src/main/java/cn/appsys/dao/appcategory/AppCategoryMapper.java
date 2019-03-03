package cn.appsys.dao.appcategory;


import cn.appsys.pojo.AppCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppCategoryMapper {

    List<AppCategory> getCategoryListByParentId(@Param("parentId") String parentId);
}
