use ding;
# 修改推送消息列表的title的长度
alter table tb_factory_push_message modify column title varchar(500) ;