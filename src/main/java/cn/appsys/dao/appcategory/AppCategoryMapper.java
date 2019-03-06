package cn.appsys.dao.appcategory;


import cn.appsys.pojo.AppCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppCategoryMapper {
    /**
     * @param parentId 父级分类的id
     * @return 分类列表
     */
    List<AppCategory> getCategoryListByParentId(@Param("parentId") String parentId);
}
