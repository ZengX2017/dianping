CREATE TABLE `NewTable` (
`id`  int NOT NULL AUTO_INCREMENT ,
`created_time`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`update_time`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' ,
`phone`  varchar(40) NOT NULL DEFAULT '' ,
`password`  varchar(200) NOT NULL DEFAULT '' COMMENT '加密后的密码' ,
`nick_name`  varchar(40) NOT NULL DEFAULT '' ,
`gender`  int NOT NULL DEFAULT 0 ,
PRIMARY KEY (`id`),
UNIQUE INDEX `phone_unique_index` (`phone`) USING BTREE
)
;

