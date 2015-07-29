package cn.jpush.alertme.factory.common.http;

/**
 * Created by ZeFanXie on 14-12-25.
 */
public class ErrorResponse extends BaseResponse {
    public static final ErrorResponse BAD_SERVER = new ErrorResponse(1000, "Bad Server");
    private int code;
    private String message;

    public ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
