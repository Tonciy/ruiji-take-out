package cn.zero.reggie.common;

/**
 * @author Zero
 * @Description 自定义业务异常
 */
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}
