package cn.jpush.alertme.factory.dao;

import cn.jpush.alertme.factory.model.ClientService;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Client Service Dao
 * Created by ZeFanXie on 14-12-17.
 */
public interface ClientServiceDao {
    @Insert("insert into tb_factory_client_service(id, sid, data) values(#{id}, #{service.id}, #{dataStr})")
    public void save(ClientService clientService);
    @Update("update tb_factory_client_service set data=#{dataStr} where id=#{id}")
    public void update(ClientService clientService);

    public ClientService findById(String id);
    public List<ClientService> findBySid(String sid);

    /**
     * 更具Service.Tag查账ClientService（不是JPush Tag）
     * @param tag
     * @return
     */
    public List<ClientService> findByTag(String tag);


}
