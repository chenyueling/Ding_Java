package cn.jpush.alertme.factory.dao;

import cn.jpush.alertme.factory.model.Service;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Service Dao
 * Created by ZeFanXie on 14-12-17.
 */
public interface ServiceDao {
    @Insert("insert into tb_factory_service (id, api_secret, title, data, tag) values (#{id}, #{apiSecret}, #{title}, #{dataStr}, #{tag})")
    public void save(Service service);

    @Update("update tb_factory_service set title=#{title}, data=#{dataStr} where id=#{id}")
    public void update(Service s);

    @Select("select * from tb_factory_service where id=#{sid}")
    @ResultMap("ServiceResultMap")
    public Service findById(String sid);

    @Select("select * from tb_factory_service where tag=#{tag}")
    @ResultMap("ServiceResultMap")
    public List<Service> findByTag(String tag);


}
