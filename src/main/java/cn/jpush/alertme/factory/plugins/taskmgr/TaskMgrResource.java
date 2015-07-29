package cn.jpush.alertme.factory.plugins.taskmgr;

import cn.jpush.alertme.factory.common.FreeMarkerHelper;
import cn.jpush.alertme.factory.common.SpringBeanFactory;
import cn.jpush.alertme.factory.common.exception.ServiceException;
import cn.jpush.alertme.factory.common.http.HttpContextHelper;
import org.quartz.SchedulerException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenyueling on 2015/1/27.
 */

@Path("/api/taskmgr")
public class TaskMgrResource {

    private TaskMgrService taskMgrService;

    public TaskMgrResource(){
        taskMgrService = SpringBeanFactory.getBean(TaskMgrServiceImpl.class);
    }

    @GET
    @Path("/tasks")
    public void task(@Context HttpServletRequest request , @Context HttpServletResponse response) {
        System.out.println("task");
        Map<String, Object> data = new HashMap<>();
        try {
            List<Map<String, String>> tasks = taskMgrService.getTasks();
            data.put("tasks" , tasks);
            data.put("base","baseV");
            String html = FreeMarkerHelper.createHtml(data, FreeMarkerHelper.QUARTZ_JOBS, request.getServletContext());
            HttpContextHelper.writeHTML(response,html);
        } catch (ServiceException e) {
            e.printStackTrace();
            HttpContextHelper.writeBadServerResponse(response);
        } catch (SchedulerException e) {
            e.printStackTrace();
            HttpContextHelper.writeBadServerResponse(response);
        }
    }


    @POST
    @Path("/update/cron")
    public void updateTaskCron(@Context HttpServletResponse response,MultivaluedMap<String, String> formParams){
        String name = formParams.get("job_name").get(0);
        String group = formParams.get("job_group").get(0);
        String cron = formParams.get("cron").get(0);
        String star_now = formParams.get("star_now").get(0);

        boolean isStarNow  = false;

        if("true".equals(star_now)){
            isStarNow = true;
        }
        try {
            taskMgrService.updateTaskCron(name,group,cron,isStarNow);
            HttpContextHelper.writeSuccessResponse(response);
        } catch (SchedulerException e) {
            HttpContextHelper.writeBadServerResponse(response);
            e.printStackTrace();
        }
    }

    @POST
    @Path("/delete/task")
    public void delTask(@Context HttpServletResponse response,MultivaluedMap<String, String> formParams){
        String name = formParams.get("job_name").get(0);
        String group = formParams.get("job_group").get(0);
        try {
            taskMgrService.deleteTask(name,group);
            HttpContextHelper.writeSuccessResponse(response);
        } catch (SchedulerException e) {
            HttpContextHelper.writeBadServerResponse(response);
            e.printStackTrace();
        }

    }
}
