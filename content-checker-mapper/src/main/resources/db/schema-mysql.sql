drop table  if exists  paper_info ;
create table paper_info
(
    paper_id     bigint auto_increment comment '论文主键',
    paper_no     VARCHAR(256)                       null comment '论文编号',
    data_type    char(2)                            not null comment '数据类型',
    title        varchar(400)                       null comment '标题',
    author       varchar(150)                       null comment '作者',
    content      text                               null comment '内容',
    publish_year char(4)                            null,
    hash LONG                            null comment 'hash值',
    create_time  DATETIME default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  DATETIME default CURRENT_TIMESTAMP null comment '更新时间',
    constraint paper_info_pk
        primary key (paper_id)
)
    comment '论文信息';

drop table  if exists  paper_ext ;
create table paper_ext
(
    paper_id     bigint comment '论文主键',
    data_type    char(2)                            not null comment '数据类型',
    content      text                               null comment '内容',
    create_time  DATETIME default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  DATETIME default CURRENT_TIMESTAMP null comment '更新时间',
    constraint paper_ext_pk
        primary key (paper_id)
)
    comment '论文扩展信息';


drop table  if exists  paper_paragraph ;
create table paper_paragraph
(
    paragraph_id     bigint auto_increment comment '段落主键',
    paragraph_num     int comment '段落号',
    paper_id     bigint comment '论文主键',
    content      text                               null comment '文本内容',
    create_time  DATETIME default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  DATETIME default CURRENT_TIMESTAMP null comment '更新时间',
    hash LONG                            null comment 'hash值',
    constraint paper_paragraph_pk
        primary key (paragraph_id)
)
    comment '论文段落';

drop table  if exists  paper_sentence ;
create table paper_sentence
(
    sentence_id     bigint auto_increment comment '句子主键',
    sentence_num     int comment '句子号',
    paragraph_id     bigint comment '段落主键',
    content      text                               null comment '文本内容',
    create_time  DATETIME default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  DATETIME default CURRENT_TIMESTAMP null comment '更新时间',
    hash LONG                            null comment 'hash值',
    constraint paper_sentence_pk
        primary key (sentence_id)
)
    comment '论文句子';



drop table  if exists  paper_token ;
create table paper_token
(
    token_id     bigint auto_increment comment '分词主键',
    token_num     int comment '分词序号',
    sentence_id     bigint comment '句子主键',
    content      text                               null comment '文本内容',
    create_time  DATETIME default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  DATETIME default CURRENT_TIMESTAMP null comment '更新时间',
    hash LONG                            null comment 'hash值',
    constraint paper_token_pk
        primary key (token_id)
)
    comment '论文分词';