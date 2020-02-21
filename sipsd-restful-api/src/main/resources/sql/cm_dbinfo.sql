/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50638
 Source Host           : 127.0.0.1:3306
 Source Schema         : cic

 Target Server Type    : MySQL
 Target Server Version : 50638
 File Encoding         : 65001

 Date: 21/02/2020 15:55:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for cm_dbinfo
-- ----------------------------
DROP TABLE IF EXISTS `cm_dbinfo`;
CREATE TABLE "cm_dbinfo" (
  "Id" int(11) NOT NULL AUTO_INCREMENT COMMENT '自增编号',
  "Code" char(50) NOT NULL COMMENT '编码Guid',
  "Flag" int(11) NOT NULL DEFAULT '1' COMMENT '标识符号（1：有效；0：失效）',
  "Status" int(11) NOT NULL DEFAULT '1' COMMENT '状态符号（1：正常；0：删除）',
  "Name" varchar(200) NOT NULL COMMENT '数据库名称',
  "DBType" varchar(50) DEFAULT NULL COMMENT '数据库类型（SqlServer、Oracle）',
  "Alias" text COMMENT '数据库别名',
  "Version" varchar(100) DEFAULT NULL COMMENT '数据库版本',
  "Source" varchar(100) DEFAULT NULL COMMENT '数据库地址',
  "Remark" varchar(2000) DEFAULT NULL COMMENT '描述',
  "ConnStr" text COMMENT '数据库连接字符串',
  "AddCode" varchar(50) DEFAULT NULL COMMENT '创建人编号',
  "AddTime" datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  "SetCode" varchar(50) DEFAULT NULL COMMENT '修改人编号',
  "SetTime" datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  "Region" varchar(50) DEFAULT NULL COMMENT '区域',
  "area_id" int(11) DEFAULT NULL COMMENT '区域id',
  "source_type" int(11) DEFAULT NULL COMMENT '数据资源池：1；ArcGis：2；后台批量：3',
  "db_name" varchar(255) DEFAULT NULL COMMENT '数据库名称',
  "user_id" varchar(255) DEFAULT NULL COMMENT '用户名',
  "pwd" varchar(255) DEFAULT NULL COMMENT '密码',
  "tags" varchar(255) DEFAULT NULL COMMENT '标签',
  "catalogue_id" int(11) DEFAULT NULL COMMENT '编目ID',
  "ConnStr2" text COMMENT '数据库连接字符串-智能接口使用',
  "db_schema" varchar(255) DEFAULT 'public' COMMENT 'pg中的schema',
  PRIMARY KEY ("Id") USING BTREE,
  UNIQUE KEY "code" ("Code") USING BTREE COMMENT '唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='数据库信息';

-- ----------------------------
-- Records of cm_dbinfo
-- ----------------------------
BEGIN;
--mysql
INSERT INTO `cm_dbinfo` VALUES (115, '8A8CFCE4-45FF-11E9-8138-0050569D789U', 1, 1, 'cic', 'Mysql', '基础平台测试', 'Mysql5.6', '127.0.0.1:3306', NULL, 'jdbc:mysql://127.0.0.1:3306/cic?characterEncoding=UTF-8&useUnicode=true&useSSL=false', '1', '2019-03-14 10:19:05', '15', '2019-03-14 10:19:05', 'test', 12, 1, 'cic', 'root', 'root', NULL, NULL, 'jdbc:mysql://127.0.0.1:3306/cic?characterEncoding=UTF-8&useUnicode=true&useSSL=false', 'public');
--oracle
INSERT INTO `cm_dbinfo` VALUES (119, '55BB8ADA-A914-4134-8574-CC3B5E7D1CFaa', 1, 1, 'TRFF_APP', 'Oracle', 'TRFF_APP', 'Oracle', '127.0.0.1,1521', 'oracle测试数据库', 'Server=127.0.0.1,1421;Database=DBSNMP;Trusted_Connection=True;', '1', '2018-08-27 10:23:19', NULL, '2018-08-27 10:23:19', '', NULL, NULL, 'TRFF_APP', 'TRFF_APP', 'sipsd123', NULL, NULL, ' jdbc:oracle:thin:@//127.0.0.1:1521/TIS', 'public');
--sqlserver
INSERT INTO `cm_dbinfo` VALUES (120, '583f5fe2-b71b-11e9-8138-0050569d789bb', 1, 1, 'sqlservertest', 'SqlServer', 'sqlserver测试', 'SqlServer2012', '127.0.0.1,1433', NULL, 'jdbc:sqlserver://127.0.0.1:1433;DatabaseName=SCMDB_ZD', '1', '2019-08-05 08:53:00', '1', '2019-08-05 08:53:00', NULL, 3269, NULL, 'sqlservertest', 'sa', '!QAZ2wsx', '产业园', 369, 'jdbc:sqlserver://127.0.0.1:1433;DatabaseName=SCMDB_ZD', 'public');
--postgresql
INSERT INTO `cm_dbinfo` VALUES (121, '8A8CFCE4-45FF-11E9-8138-0050569D789cc', 1, 1, 'pgtest', 'PostgreSQL', 'pgtest', 'PostgreSQL 11.1', '127.0.0.1:8432', NULL, 'server=127.0.0.1:8432;database=sipioc;user id=sipsd;pwd=sipsd123;', '1', '2019-03-14 10:19:05', '1', '2019-03-14 10:19:05', '苏州高新区', 12, 1, 'sipioc', 'sipsd', 'sipsd123', NULL, NULL, 'jdbc:postgresql://127.0.0.1:8432/sipioc?currentSchema=sipioc', 'sipioc');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
