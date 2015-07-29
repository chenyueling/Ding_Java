package cn.jpush.alertme.factory.common.http;

/**
 * Request Success
 * Created by ZeFanXie on 14-12-19.
 */
public class RightResponse extends BaseResponse {
    public static final RightResponse SUCCESS = new RightResponse(3000, "Success");
    public static final int MESSAGE_RESPONSE = 1101;

    private int code;
    private String message;

    public RightResponse(int code, String message) {
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
