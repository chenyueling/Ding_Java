package cn.jpush.alertme.factory.plugins.failure_service;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * 验证失败的服务的资源类, 一般由于Server端删除对应的服务导致调用失败. 
 * 调用需要鉴权 
 * Created by ZeFanXie on 15-1-27.
 */
@Path("/failure_service")
public class FailureServiceResource {
    
    @Path("/")
    @GET
    public void getFailureServices() {
        
        
    }
    
    @Path("/{cid}")
    @DELETE
    public void removeFailureService() {
        
        
    }
    
}
