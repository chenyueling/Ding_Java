package cn.jpush.alertme.factory.common.exception;

/**
 * Created by ZeFanXie on 15-1-27.
 */
public class SpiderRuntimeError extends Exception {
    public static final SpiderRuntimeError ACCESS_REFUSED_ERROR = new SpiderRuntimeError(1000, "访问被拒绝");
    public static final SpiderRuntimeError REQUEST_FAIL_ERROR = new SpiderRuntimeError(1001, "请求失败");
    public static final SpiderRuntimeError PARSE_FAIL_ERROR = new SpiderRuntimeError(1002, "数据解析失败");
    private int code;
    
    public SpiderRuntimeError(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public SpiderRuntimeError(String message) {
        super(message);
    }

    public SpiderRuntimeError(String message, Throwable cause) {
        super(message, cause);
    }

    public SpiderRuntimeError(Throwable cause) {
        super(cause);
    }
    
   

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
