CREATE TABLE `user` (
`id`  int NOT NULL AUTO_INCREMENT ,
`created_time`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`update_time`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`phone`  varchar(40) NOT NULL DEFAULT '' ,
`password`  varchar(200) NOT NULL DEFAULT '' COMMENT '加密后的密码' ,
`nick_name`  varchar(40) NOT NULL DEFAULT '' ,
`gender`  int NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
UNIQUE INDEX `phone_unique_index` (`phone`) USING BTREE
);

CREATE TABLE `seller` (
`id`  int NOT NULL AUTO_INCREMENT ,
`name`  varchar(80) NOT NULL DEFAULT '' ,
`created_at`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`updated_at`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`remark_score`  decimal(2,1) NOT NULL DEFAULT 0 ,
`disabled_flag`  int NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`)
);

CREATE TABLE `category` (
`id`  int NOT NULL AUTO_INCREMENT ,
`created_at`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`updated_at`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`name`  varchar(20) NOT NULL DEFAULT '' ,
`icon_url`  varchar(200) NOT NULL DEFAULT '' ,
`sort`  int NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
UNIQUE INDEX `name_unique_index` (`name`) USING BTREE
);

CREATE TABLE `shop` (
`id`  int NOT NULL AUTO_INCREMENT ,
`created_at`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`updated_at`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`name`  varchar(80) NOT NULL DEFAULT '' ,
`remark_score`  decimal(2,1) NOT NULL DEFAULT 0 ,
`price_per_man`  int NOT NULL DEFAULT 0 ,
`latitude`  decimal(10,6) NOT NULL DEFAULT 0 ,
`longitude`  decimal(10,6) NOT NULL DEFAULT 0 ,
`categord_id`  int NOT NULL DEFAULT 0 ,
`tags`  varchar(2000) ,
-- `tags`  varchar(2000) NOT NULL DEFAULT '' ,
`start_time`  varchar(200) NOT NULL DEFAULT '' ,
`end_time`  varchar(200) NOT NULL DEFAULT '' ,
`address`  varchar(200) NOT NULL DEFAULT '' ,
`seller_id`  int NOT NULL DEFAULT 0 ,
`icon_url`  varchar(100) NOT NULL DEFAULT '' ,
PRIMARY KEY (`id`)
);


