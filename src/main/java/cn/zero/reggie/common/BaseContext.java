package cn.zero.reggie.common;

/**
 * @author Zero
 * @Description 基于 ThreadLocal 封装工具类，用户保存和获取当前登录用户 id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 存入当前用户 id
     * @param id
     */
    public static void  setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取当前用户 id
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
