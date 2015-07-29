package cn.jpush.alertme.factory.plugins.finance;

import cn.jpush.alertme.factory.common.ding.Text;

/**
 * Created by chenyueling on 2015/1/6.
 */
public abstract class FinanceTip {



    public final Text financeTip() {
        String data = getData();
        String threashold = getThreshold();
        float dataF = Float.parseFloat(data);
        float threasholdF = Float.parseFloat(threashold);
        Text text = buildPush(dataF,threasholdF);
        return text;
    }

    /**
     * 获取数据
     *
     * @return
     */
    public abstract String getData();

    /**
     * 获取阀值,重写这个方法时，设定这个类所需要的阀值
     *
     * @return
     */
    public abstract String getThreshold();

    /**
     * 自定义推送格式
     *
     * @return
     */
    public abstract Text buildPush(float dataF , float threasholdF);

    /**
     * 分辨是来自哪一类的调用
     * @return
     */
    public abstract String getTag();

}
