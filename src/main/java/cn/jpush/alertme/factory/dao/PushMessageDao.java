package cn.jpush.alertme.factory.dao;

import cn.jpush.alertme.factory.model.PushMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;


public interface PushMessageDao {
    
    @Update("set names utf8mb4")

    public void setCharsetToUtf8mb4();

    @Insert("insert into tb_factory_push_message (id, sid, cid, msg_type, push_time, link, title, summary, content ,tag)" +
            " values (#{id}, #{sid}, #{cid}, #{type}, #{pushTime}, #{link}, #{title}, #{summary}, #{content}, #{tag})")
    public void save(PushMessage record);
}
