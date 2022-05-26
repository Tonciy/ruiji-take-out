package cn.zero.reggie.mapper;

import cn.zero.reggie.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Zero
 * @Description 菜单种类 Mapper接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
