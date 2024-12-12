
CREATE DATABASE if not exists learn_room;

USE learn_room;

CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                        `userName` varchar(255) NOT NULL COMMENT '用户名称',
                        `userAccount` varchar(255) NOT NULL COMMENT '用户账号',
                        `userPassword` varchar(1024) NOT NULL COMMENT '用户密码',
                        `userPhone` char(11) DEFAULT NULL COMMENT '用户手机号',
                        `userRole` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '用户权限: 0:普通用户/1:管理员',
                        `userPicture` varchar(1024) DEFAULT NULL COMMENT '用户头像',
                        `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                        `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                        `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `question` (
                            `id` bigint NOT NULL COMMENT 'id',
                            `title` varchar(512) NOT NULL COMMENT '题目标题',
                            `context` text COMMENT '题目内容',
                            `userId` bigint NOT NULL COMMENT '创建人id',
                            `answer` text NOT NULL COMMENT '推荐答案',
                            `tags` varchar(1024) DEFAULT NULL COMMENT '标签(json 数组)',
                            `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                            `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `question_bank` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                 `title` varchar(512) NOT NULL COMMENT '题库名称',
                                 `description` text COMMENT '题库描述',
                                 `picture` varchar(1024) DEFAULT NULL COMMENT '题库图片',
                                 `userId` bigint NOT NULL COMMENT '创建人id',
                                 `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                 `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `question_bank_question` (
                                          `id` bigint NOT NULL COMMENT 'id',
                                          `questionId` bigint NOT NULL COMMENT '题目id',
                                          `questionBandId` bigint NOT NULL COMMENT '题库id',
                                          `userId` bigint NOT NULL COMMENT '创建用户id',
                                          `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `questionAndBankIndex` (`questionBandId`,`questionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;