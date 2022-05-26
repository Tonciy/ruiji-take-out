package cn.zero.reggie.service;

import cn.zero.reggie.dto.DishDto;
import cn.zero.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author Zero
 * @Description 描述此类
 */
public interface DishService extends IService<Dish> {

    /**
     * 新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 通过 id 查询菜品信息及口味
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);


    /**
     * 更新菜品信息，同时更新对应的口味信息
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);
}
