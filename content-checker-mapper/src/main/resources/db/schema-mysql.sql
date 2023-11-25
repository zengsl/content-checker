create schema `content-checker`;


create table `content-checker`.check_paragraph
(
    paragraph_id  bigint auto_increment comment '段落主键'
        primary key,
    paragraph_num int                                null comment '段落号',
    paper_no      varchar(256)                       null comment '论文编号',
    task_id       bigint                             null comment '检测任务主键',
    check_id      bigint                             null comment '检测数据主键',
    content       text                               null comment '文本内容',
    similarity    double   default -1                null comment '相似度',
    status        char     default '0'               null comment '状态: 0 待处理,  1 处理中， 2 已完成 3 失败',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    hash          bigint                             null comment 'hash值',
    hash1         varchar(20)                        null comment '第一段hash值',
    hash2         varchar(20)                        null comment '第二段hash值',
    hash3         varchar(20)                        null comment '第三段hash值',
    hash4         varchar(20)                        null comment '第四段hash值'
)
    comment '检测文本段落';

create table `content-checker`.check_paragraph_pair
(
    id             bigint auto_increment comment '主键'
        primary key,
    task_id        bigint                             null comment '验证任务主键',
    check_para_id  bigint                             null comment '验证段落主键',
    target_para_id bigint                             null comment '疑似段落主键',
    similarity     double                             null comment '相似度',
    status         char     default '0'               null comment '状态: 0 待处理,  1 处理中， 2 已完成 3 失败',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null comment '更新时间'
)
    comment '验证段落对';

create table `content-checker`.check_request
(
    check_id     bigint auto_increment comment '验证请求主键'
        primary key,
    check_no     varchar(50)                        null comment '验证标号',
    paper_no     varchar(256)                       null comment '论文编号',
    req_source   char     default '1'               null comment '请求来源: 1 web端调用 2 系统API',
    title        varchar(400)                       null comment '标题',
    author       varchar(150)                       null comment '作者',
    publish_year char(4)                            null,
    task_num     int      default 0                 null comment '任务数: check_task',
    status       char     default '0'               null comment '状态: 0 待处理,  1 处理中， 2 已完成',
    similarity   double   default -1                not null comment '相似度',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null comment '更新时间'
)
    comment '验证请求';

create table `content-checker`.check_sentence
(
    sentence_id    bigint auto_increment comment '句子主键'
        primary key,
    sentence_num   int                                null comment '句子号',
    paragraph_id   bigint                             null comment '段落主键',
    origin_content text                               null comment '原始文本内容',
    content        text                               null comment '文本内容',
    similarity     double   default -1                null comment '相似度',
    status         char     default '0'               null comment '状态: 0 待处理,  1 处理中， 2 已完成 3 失败',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null comment '更新时间'
)
    comment '检测文本句子';

create table `content-checker`.check_sentence_pair
(
    id                 bigint auto_increment comment '主键'
        primary key,
    task_id            bigint                             null comment '验证任务主键',
    check_para_id      bigint                             null comment '验证段落主键',
    target_para_id     bigint                             null comment '疑似段落主键',
    check_sentence_id  bigint                             null comment '验证句子主键',
    target_sentence_id bigint                             null comment '疑似句子主键',
    similarity         double   default -1                null comment '相似度',
    status             char     default '0'               null comment '状态: 0 待处理,  1 处理中， 2 已完成 3 失败',
    create_time        datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time        datetime default CURRENT_TIMESTAMP null comment '更新时间'
)
    comment '验证句子对';

create table `content-checker`.check_task
(
    task_id     bigint auto_increment comment '验证任务主键'
        primary key,
    check_id    bigint                             null comment '验证请求主键',
    check_no    varchar(50)                        null comment '验证标号',
    paper_no    varchar(256)                       null comment '论文编号',
    check_type  char     default '1'               null comment '验证类型: 1 内容 2 title',
    content     text                               not null comment '内容',
    similarity  double   default (-(1))            null comment '相似度',
    status      char     default '0'               null comment '状态: 0 待处理,  1 处理中， 2 已完成 3 失败 4 取消',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null comment '更新时间'
)
    comment '验证任务';

create table `content-checker`.paper_ext
(
    paper_id    bigint                             not null comment '论文主键'
        primary key,
    data_type   char(2)                            not null comment '数据类型',
    content     text                               null comment '内容',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null comment '更新时间'
)
    comment '论文扩展信息';

create table `content-checker`.paper_info
(
    paper_id     bigint auto_increment comment '论文主键'
        primary key,
    paper_no     varchar(256)                       null comment '论文编号',
    data_type    char(2)                            not null comment '数据类型',
    title        varchar(400)                       null comment '标题',
    author       varchar(150)                       null comment '作者',
    content      text                               null comment '内容',
    publish_year char(4)                            null,
    data_source  char(2)                            not null comment '数据来源',
    hash         bigint                             null comment 'hash值',
    hash1        varchar(20)                        null comment '第一段hash值',
    hash2        varchar(20)                        null comment '第二段hash值',
    hash3        varchar(20)                        null comment '第三段hash值',
    hash4        varchar(20)                        null comment '第四段hash值',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null comment '更新时间'
)
    comment '论文信息';

create table `content-checker`.paper_paragraph
(
    paragraph_id  bigint auto_increment comment '段落主键'
        primary key,
    paragraph_num int                                null comment '段落号',
    paper_id      bigint                             null comment '论文主键',
    paper_no      varchar(256)                       null comment '论文编号',
    content       text                               null comment '文本内容',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null comment '更新时间',
    hash          bigint                             null comment 'hash值',
    hash1         varchar(20)                        null comment '第一段hash值',
    hash2         varchar(20)                        null comment '第二段hash值',
    hash3         varchar(20)                        null comment '第三段hash值',
    hash4         varchar(20)                        null comment '第四段hash值'
)
    comment '论文段落';

create table `content-checker`.paper_sentence
(
    sentence_id    bigint auto_increment comment '句子主键'
        primary key,
    sentence_num   int                                null comment '句子号',
    paragraph_id   bigint                             null comment '段落主键',
    origin_content text                               null comment '原始文本内容',
    content        text                               null comment '文本内容',
    hash           bigint                             null comment 'hash值',
    hash1          varchar(20)                        null comment '第一段hash值',
    hash2          varchar(20)                        null comment '第二段hash值',
    hash3          varchar(20)                        null comment '第三段hash值',
    hash4          varchar(20)                        null comment '第四段hash值',
    create_time    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP null comment '更新时间'
)
    comment '论文句子';

create table `content-checker`.paper_token
(
    token_id     bigint auto_increment comment '分词主键'
        primary key,
    token_num    int                                null comment '分词序号',
    sentence_id  bigint                             null comment '句子主键',
    paragraph_id bigint                             null comment '段落主键',
    content      text                               null comment '文本内容',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null comment '更新时间'
)
    comment '论文分词';
