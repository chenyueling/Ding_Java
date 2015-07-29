use ding;
drop table if exists tb_factory_service;
create table tb_factory_service (
  id varchar(36) primary key,
  api_secret varchar(50) not null,
  title varchar(200),
  data varchar(2000),
  tag varchar(50) not null
);


drop table if exists tb_factory_client_service;
create table tb_factory_client_service (
  id varchar(36) primary key,
  sid varchar(36),
  data varchar(2000)
);


DROP TABLE if EXISTS tb_factory_push_message;
CREATE TABLE tb_factory_push_message(
  id VARCHAR(36) PRIMARY KEY,
  sid VARCHAR(36),
  cid VARCHAR(36),
  msg_type VARCHAR(36),
  push_time datetime,
  content VARCHAR(500),
  title VARCHAR(200),
  link VARCHAR(500),
  summary VARCHAR(500),
  tag VARCHAR (50)
);