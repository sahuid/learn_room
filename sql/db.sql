
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
                            `viewCount` int NOT NULL DEFAULT '0' COMMENT '浏览量',
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

# 点赞记录表
CREATE TABLE `likes` (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                         `targetId` bigint NOT NULL COMMENT '点赞实体 id',
                         `targetType` tinyint NOT NULL COMMENT '点赞实体类型: 0-题目',
                         `userId` bigint NOT NULL COMMENT '用户id',
                         `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updateTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `targetType_targetId_userId` (`targetId`,`targetType`,`userId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# 点赞计数表
CREATE TABLE `likes_count` (
                               `targetId` bigint NOT NULL COMMENT '点赞实体id',
                               `targetType` tinyint NOT NULL COMMENT '点赞实体类型:0-题目',
                               `count` int DEFAULT '0' COMMENT '点赞数',
                               UNIQUE KEY `targetId_targetType` (`targetId`,`targetType`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# 浏览记录
CREATE TABLE `question_view` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                 `questionId` bigint NOT NULL COMMENT '题目id',
                                 `userId` bigint NOT NULL COMMENT '用户id',
                                 `viewTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
                                 `viewCount` int DEFAULT '1' COMMENT '浏览次数',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `userId_targetId` (`userId`,`questionId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1873242391907950594 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# 收藏计数表
CREATE TABLE `collect_count` (
                                 `questionId` bigint NOT NULL COMMENT '题目id',
                                 `collectCount` int NOT NULL DEFAULT '0' COMMENT '收藏数',
                                 PRIMARY KEY (`questionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# 收藏记录表
CREATE TABLE `question_collect` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                    `questionId` bigint NOT NULL COMMENT '题目id',
                                    `userId` bigint NOT NULL COMMENT '用户id',
                                    `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

# 评论表
CREATE TABLE `comment` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                           `content` text NOT NULL COMMENT '内容',
                           `userId` bigint NOT NULL COMMENT '用户id',
                           `targetId` bigint NOT NULL COMMENT '目标id',
                           `parentId` bigint DEFAULT NULL COMMENT '父评论Id',
                           `rootId` bigint DEFAULT NULL COMMENT '根评论Id',
                           `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                           `isDelete` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除',
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


# 聊天记录表
CREATE TABLE `chat_history` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                   `userId` bigint NOT NULL COMMENT '用户id',
                                   `content` varchar(2048) NOT NULL COMMENT '聊天内容',
                                   `role` tinyint NOT NULL COMMENT '角色- 0：ai，1：用户',
                                   `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


# 本地消息表
CREATE TABLE `message` (
                           `messageId` bigint NOT NULL COMMENT '消息id',
                           `content` text NOT NULL COMMENT '消息内容',
                           `status` varchar(10) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL DEFAULT 'init' COMMENT '消息状态',
                           `messageType` varchar(128) DEFAULT NULL COMMENT '消息类型',
                           `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           PRIMARY KEY (`messageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;