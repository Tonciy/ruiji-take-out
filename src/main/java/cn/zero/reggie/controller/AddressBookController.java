package cn.zero.reggie.controller;

import cn.zero.reggie.common.BaseContext;
import cn.zero.reggie.common.R;
import cn.zero.reggie.entity.AddressBook;
import cn.zero.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Zero
 * @Description 描述此类
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        // 设置地址的用户 id
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }


    /**
     * 更新用户地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook) {
        // 设置地址的用户 id
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }
    /**
     * 设置默认地址
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setFault(@RequestBody AddressBook addressBook){
        // 先将此用户的所有地址信息中默认值均设为0
        LambdaUpdateWrapper<AddressBook> queryWrapper = new LambdaUpdateWrapper<AddressBook>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.set(AddressBook::getIsDefault, 0);
        addressBookService.update(queryWrapper);
        // 设置当前地址为默认地址
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getFault(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getIsDefault,1);
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if(addressBook == null){
            return R.error("当前用户查无默认地址");
        }else {
            return R.success(addressBook);
        }

    }

    /**
     * 根据 id 查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook == null){
            return R.error("查无此用户");
        }else {
            return R.success(addressBook);
        }
    }

    /**
     * 根据 id 删除地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        boolean b = addressBookService.removeById(ids);
        if(b){
            return R.success("删除成功");
        }else {
            return R.success("删除失败");
        }
    }

    /**
     * 查询指定用户的全部地址
     * @param addressBook
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId() != null, AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        return R.success(addressBookService.list(queryWrapper));
    }
}
