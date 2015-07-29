package cn.jpush.alertme.factory.plugins.earth_quake;

import cn.jpush.alertme.factory.common.BaseResource;

import javax.ws.rs.Path;

/**
 * Created by chenyueling on 2015/1/30.
 */
@Path("earth_quake")
public class EarthQuakeResource extends BaseResource{

    public static final String Tag = "EarthQuake";






    @Override
    protected String getTag() {
        return Tag;
    }
}
