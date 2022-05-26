package cn.zero.reggie.dto;


import cn.zero.reggie.entity.Setmeal;
import cn.zero.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
