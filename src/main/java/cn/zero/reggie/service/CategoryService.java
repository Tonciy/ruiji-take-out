package cn.zero.reggie.service;

import cn.zero.reggie.entity.Category;

import com.baomidou.mybatisplus.extension.service.IService;


/**
 * @author Zero
 * @Description 描述此类
 */

public interface CategoryService extends IService<Category> {
    /**
     * 根据 id 删除对应分类信息，删除之前需要进行判断是否关联了菜品或者套餐
     * @param id
     */
    void remove(Long id);
}
