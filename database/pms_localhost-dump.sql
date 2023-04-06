-- MySQL dump 10.13  Distrib 8.0.25, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: pms
-- ------------------------------------------------------
-- Server version	8.0.25

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `pd`
--

DROP TABLE IF EXISTS `pd`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pd`
(
    `pdid`         int          NOT NULL AUTO_INCREMENT COMMENT '采购需求编号',
    `p_uid`        varchar(50)  NOT NULL COMMENT '采购员id',
    `product_name` varchar(100) NOT NULL COMMENT '产品名',
    `amount`       int          NOT NULL COMMENT '采购数量\n',
    `expire_time`  datetime     NOT NULL COMMENT '过期时间',
    `start_time`   datetime     NOT NULL COMMENT '开始时间',
    `remark`       varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`pdid`),
    KEY `pd_purchaser_uid_fk` (`p_uid`),
    CONSTRAINT `pd_purchaser_uid_fk` FOREIGN KEY (`p_uid`) REFERENCES `purchaser` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `pd_chk_1` CHECK ((`expire_time` > `start_time`))
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='采购需求表 procurement_demand';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `processing`
--

DROP TABLE IF EXISTS `processing`;
/*!50001 DROP VIEW IF EXISTS `processing`*/;
SET @saved_cs_client = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `processing` AS
SELECT 1 AS `pid`,
       1 AS `qid`,
       1 AS `pdid`,
       1 AS `status`,
       1 AS `start_time`,
       1 AS `p_uid`,
       1 AS `p_name`,
       1 AS `v_uid`,
       1 AS `v_name`,
       1 AS `product_name`,
       1 AS `amount`,
       1 AS `total_price`,
       1 AS `cname`,
       1 AS `cpath`,
       1 AS `cexpire_time`,
       1 AS `remark`
        */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `procurement`
--

DROP TABLE IF EXISTS `procurement`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `procurement`
(
    `pid`          int      NOT NULL AUTO_INCREMENT COMMENT '采购编号',
    `qid`          int      NOT NULL COMMENT '报价编号',
    `status`       tinyint      DEFAULT '1' COMMENT '采购状态',
    `start_time`   datetime NOT NULL COMMENT '采购发起的时间',
    `cname`        varchar(100) DEFAULT NULL COMMENT '合同文件名',
    `cpath`        varchar(500) DEFAULT NULL COMMENT '合同存储位置',
    `cexpire_time` datetime     DEFAULT NULL COMMENT '合同过期时间',
    `csize`        double       DEFAULT NULL COMMENT '合同文件大小',
    PRIMARY KEY (`pid`),
    KEY `procurement_quote_qid_fk` (`qid`),
    CONSTRAINT `procurement_quote_qid_fk` FOREIGN KEY (`qid`) REFERENCES `quote` (`qid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 5
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='采购表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `purchaser`
--

DROP TABLE IF EXISTS `purchaser`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchaser`
(
    `uid`      varchar(50)   NOT NULL COMMENT '编号',
    `uname`    varchar(50)   NOT NULL COMMENT '用户名',
    `email`    varchar(50)   NOT NULL COMMENT '邮箱',
    `pwd`      varchar(200)  NOT NULL COMMENT '密码',
    `rate`     decimal(3, 2) NOT NULL DEFAULT '0.00' COMMENT '评分',
    `rate_num` int           NOT NULL DEFAULT '0' COMMENT '评分人数',
    `rid`      smallint      NOT NULL DEFAULT '1' COMMENT '角色id',
    PRIMARY KEY (`uid`),
    UNIQUE KEY `purchaser_email_uindex` (`email`),
    KEY `purchaser_role_rid_fk` (`rid`),
    CONSTRAINT `purchaser_role_rid_fk` FOREIGN KEY (`rid`) REFERENCES `role` (`rid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='采购员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `quote`
--

DROP TABLE IF EXISTS `quote`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `quote`
(
    `qid`         int         NOT NULL AUTO_INCREMENT,
    `p_id`        int         NOT NULL COMMENT '采购需求编号',
    `v_uid`       varchar(50) NOT NULL COMMENT '供应商编号',
    `amount`      int         NOT NULL COMMENT '数量',
    `total_price` float       NOT NULL COMMENT '总价',
    `remark`      varchar(500) DEFAULT NULL COMMENT '备注',
    `start_time`  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`qid`),
    KEY `quote_vendor_uid_fk` (`v_uid`),
    KEY `quote_pd_pdid_fk` (`p_id`),
    CONSTRAINT `quote_pd_pdid_fk` FOREIGN KEY (`p_id`) REFERENCES `pd` (`pdid`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `quote_vendor_uid_fk` FOREIGN KEY (`v_uid`) REFERENCES `vendor` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  AUTO_INCREMENT = 11
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='报价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `request_log`
--

DROP TABLE IF EXISTS `request_log`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request_log`
(
    `request_id` bigint       NOT NULL AUTO_INCREMENT COMMENT '请求编号',
    `method`     varchar(20)  NOT NULL DEFAULT 'GET' COMMENT '请求方法',
    `param`      varchar(500)          DEFAULT NULL COMMENT '请求参数',
    `path`       varchar(500) NOT NULL COMMENT '请求目标地址',
    `time`       datetime     NOT NULL COMMENT '时间',
    `ip`         varchar(150)          DEFAULT NULL COMMENT '请求ip',
    `uid`        varchar(50)           DEFAULT NULL COMMENT '用户id',
    PRIMARY KEY (`request_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1063
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='请求日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role`
(
    `rid`        smallint    NOT NULL AUTO_INCREMENT COMMENT '角色编号',
    `rname`      varchar(50) NOT NULL COMMENT '角色名',
    `permission` varchar(50) DEFAULT NULL COMMENT '权限',
    PRIMARY KEY (`rid`),
    UNIQUE KEY `role_rname_uindex` (`rname`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Temporary view structure for view `user`
--

DROP TABLE IF EXISTS `user`;
/*!50001 DROP VIEW IF EXISTS `user`*/;
SET @saved_cs_client = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `user` AS
SELECT 1 AS `uid`,
       1 AS `uname`,
       1 AS `email`,
       1 AS `pwd`,
       1 AS `rid`,
       1 AS `rate`,
       1 AS `rate_num`
        */;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `vendor`
--

DROP TABLE IF EXISTS `vendor`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendor`
(
    `uid`      varchar(50)   NOT NULL COMMENT '编号',
    `uname`    varchar(50)   NOT NULL COMMENT '用户名',
    `email`    varchar(50)   NOT NULL COMMENT '邮箱',
    `pwd`      varchar(200)  NOT NULL COMMENT '密码',
    `phone`    varchar(20)   NOT NULL COMMENT '手机号',
    `address`  varchar(150)  NOT NULL COMMENT '公司地址',
    `rate`     decimal(3, 2) NOT NULL DEFAULT '0.00' COMMENT '评分',
    `rate_num` int           NOT NULL DEFAULT '0' COMMENT '评分人数',
    `rid`      smallint      NOT NULL DEFAULT '2' COMMENT '角色id',
    PRIMARY KEY (`uid`),
    UNIQUE KEY `vendor_email_uindex` (`email`),
    KEY `vendor_role_rid_fk` (`rid`),
    CONSTRAINT `vendor_role_rid_fk` FOREIGN KEY (`rid`) REFERENCES `role` (`rid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='供应商表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `processing`
--

/*!50001 DROP VIEW IF EXISTS `processing`*/;
/*!50001 SET @saved_cs_client = @@character_set_client */;
/*!50001 SET @saved_cs_results = @@character_set_results */;
/*!50001 SET @saved_col_connection = @@collation_connection */;
/*!50001 SET character_set_client = utf8mb4 */;
/*!50001 SET character_set_results = utf8mb4 */;
/*!50001 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM = UNDEFINED */ /*!50013 DEFINER =`root`@`localhost` SQL SECURITY DEFINER */ /*!50001 VIEW `processing` AS
select `t1`.`pid`          AS `pid`,
       `t1`.`qid`          AS `qid`,
       `t2`.`pdid`         AS `pdid`,
       `t1`.`status`       AS `status`,
       `t1`.`start_time`   AS `start_time`,
       `t2`.`p_uid`        AS `p_uid`,
       `t2`.`pname`        AS `p_name`,
       `t1`.`v_uid`        AS `v_uid`,
       `t1`.`vname`        AS `v_name`,
       `t2`.`product_name` AS `product_name`,
       `t1`.`amount`       AS `amount`,
       `t1`.`total_price`  AS `total_price`,
       `t1`.`cname`        AS `cname`,
       `t1`.`cpath`        AS `cpath`,
       `t1`.`cexpire_time` AS `cexpire_time`,
       `t1`.`remark`       AS `remark`
from ((select `p`.`pid`          AS `pid`,
              `p`.`qid`          AS `qid`,
              `p`.`status`       AS `status`,
              `p`.`start_time`   AS `start_time`,
              `p`.`cname`        AS `cname`,
              `p`.`cpath`        AS `cpath`,
              `p`.`cexpire_time` AS `cexpire_time`,
              `p`.`p_id`         AS `p_id`,
              `v`.`v_uid`        AS `v_uid`,
              `p`.`amount`       AS `amount`,
              `p`.`total_price`  AS `total_price`,
              `p`.`remark`       AS `remark`,
              `v`.`vname`        AS `vname`
       from ((select `procurement`.`pid`          AS `pid`,
                     `procurement`.`qid`          AS `qid`,
                     `procurement`.`status`       AS `status`,
                     `procurement`.`start_time`   AS `start_time`,
                     `procurement`.`cname`        AS `cname`,
                     `procurement`.`cpath`        AS `cpath`,
                     `procurement`.`cexpire_time` AS `cexpire_time`,
                     `q`.`p_id`                   AS `p_id`,
                     `q`.`v_uid`                  AS `v_uid`,
                     `q`.`amount`                 AS `amount`,
                     `q`.`total_price`            AS `total_price`,
                     `q`.`remark`                 AS `remark`
              from (`procurement` join `quote` `q` on ((`procurement`.`qid` = `q`.`qid`)))) `p` join (select `vendor`.`uid` AS `v_uid`, `vendor`.`uname` AS `vname`
                                                                                                      from `vendor`) `v`
             on ((`p`.`v_uid` = `v`.`v_uid`)))) `t1` join (select `pd`.`pdid`         AS `pdid`,
                                                                  `pd`.`p_uid`        AS `p_uid`,
                                                                  `p2`.`uname`        AS `pname`,
                                                                  `pd`.`product_name` AS `product_name`
                                                           from (`pd` join `purchaser` `p2` on ((`pd`.`p_uid` = `p2`.`uid`)))) `t2`
      on ((`t1`.`p_id` = `t2`.`pdid`)))
        */;
/*!50001 SET character_set_client = @saved_cs_client */;
/*!50001 SET character_set_results = @saved_cs_results */;
/*!50001 SET collation_connection = @saved_col_connection */;

--
-- Final view structure for view `user`
--

/*!50001 DROP VIEW IF EXISTS `user`*/;
/*!50001 SET @saved_cs_client = @@character_set_client */;
/*!50001 SET @saved_cs_results = @@character_set_results */;
/*!50001 SET @saved_col_connection = @@collation_connection */;
/*!50001 SET character_set_client = utf8mb4 */;
/*!50001 SET character_set_results = utf8mb4 */;
/*!50001 SET collation_connection = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM = UNDEFINED */ /*!50013 DEFINER =`root`@`localhost` SQL SECURITY DEFINER */ /*!50001 VIEW `user` AS
select `pepr`.`uid`      AS `uid`,
       `pepr`.`uname`    AS `uname`,
       `pepr`.`email`    AS `email`,
       `pepr`.`pwd`      AS `pwd`,
       `pepr`.`rid`      AS `rid`,
       `pepr`.`rate`     AS `rate`,
       `pepr`.`rate_num` AS `rate_num`
from (select `purchaser`.`uid`      AS `uid`,
             `purchaser`.`uname`    AS `uname`,
             `purchaser`.`email`    AS `email`,
             `purchaser`.`pwd`      AS `pwd`,
             `purchaser`.`rid`      AS `rid`,
             `purchaser`.`rate`     AS `rate`,
             `purchaser`.`rate_num` AS `rate_num`
      from `purchaser`
      union
      select `vendor`.`uid`      AS `uid`,
             `vendor`.`uname`    AS `uname`,
             `vendor`.`email`    AS `email`,
             `vendor`.`pwd`      AS `pwd`,
             `vendor`.`rid`      AS `rid`,
             `vendor`.`rate`     AS `rate`,
             `vendor`.`rate_num` AS `rate_num`
      from `vendor`) `pepr`
        */;
/*!50001 SET character_set_client = @saved_cs_client */;
/*!50001 SET character_set_results = @saved_cs_results */;
/*!50001 SET collation_connection = @saved_col_connection */;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2023-04-06 15:09:50
