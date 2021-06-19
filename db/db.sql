-- auto-generated definition
create table acl_customer
(
    id           char(19)               not null comment '会员id'
        primary key,
    mobile       varchar(11) default '' null comment '手机号',
    password     varchar(255)           null comment '密码',
    room_id      varchar(32)            null comment '房间号',
    sex          tinyint(2) unsigned    null comment '性别 1 女，2 男',
    age          tinyint unsigned       null comment '年龄',
    avatar       varchar(255)           null comment '用户头像',
    sign         varchar(100)           null comment '用户签名',
    is_disabled  tinyint(1)  default 0  not null comment '是否禁用 1（true）已禁用，  0（false）未禁用',
    is_deleted   tinyint(1)  default 0  not null comment '逻辑删除 1（true）已删除， 0（false）未删除',
    gmt_create   datetime               not null comment '创建时间',
    gmt_modified datetime               not null comment '更新时间'
)
    comment '会员表' charset = utf8mb4;



CREATE TABLE `acl_user` (
                            `id` char(19) NOT NULL COMMENT '管理员id',
                            `username` varchar(20) NOT NULL DEFAULT '' COMMENT '管理员姓名',
                            `password` varchar(32) NOT NULL DEFAULT '' COMMENT '密码',
                            `role` varchar(32) NOT NULL DEFAULT '' COMMENT '角色',
                            `is_deleted` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除 1（true）已删除， 0（false）未删除',
                            `gmt_create` datetime NOT NULL COMMENT '创建时间',
                            `gmt_modified` datetime NOT NULL COMMENT '更新时间',
                            PRIMARY KEY (`id`)
                        )
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- auto-generated definition
create table room
(
    room_id     varchar(255)          not null
        primary key,
    temperature double     default 25 not null,
    wind_speed  int        default 25 not null comment '风速(高风为3，中风为2，低风为1)',
    state       int                   not null comment '空调模式(制冷模式为1，制热模式为2，关机模式为0)',
    is_disabled tinyint(1) default 0  null
);

-- auto-generated definition
create table log
(
    op_time    timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    room_id    varchar(255)                        not null,
    operation  int                                 not null comment '操作请求(请求送风为1、请求停止送风为0,办理入住为2，办理退房为3,start:4,end:5)',
    wind_speed int                                 not null comment '风速(高风为3，中风为2，低风为1)'
);



