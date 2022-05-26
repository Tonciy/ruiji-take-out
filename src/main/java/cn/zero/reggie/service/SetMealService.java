package cn.zero.reggie.service;

import cn.zero.reggie.common.R;
import cn.zero.reggie.dto.SetmealDto;
import cn.zero.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Zero
 * @Description 描述此类
 */
public interface SetMealService extends IService<Setmeal> {

    /**
     * 新增套餐以及对应菜品信息
     * @param setmealDto
     */
    void addSetMealWithDish(SetmealDto setmealDto);


    /**
     * 分页查询套餐信息，以及携带套餐属于哪个分类
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    R<Page> pageWithCategory(int page, int pageSize, String name );

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteWithDish(List<Long> ids);

    /**
     * 批量修改套餐信息
     * @param ids
     * @param status
     */
    void setStatus(List<Long> ids, int status);
}
