package cn.jpush.alertme.factory.common.ding;

import cn.jpush.alertme.factory.util.StringUtil;

/**
 * Created by ZeFanXie on 14-12-18.
 */
public class Text {
    private String content;

    public Text() {}

    public Text(String content) {
        this.content = content;
    }

    public boolean validate() {
        return !StringUtil.isEmpty(content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
