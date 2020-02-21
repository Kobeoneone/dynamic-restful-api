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

 Date: 21/02/2020 15:56:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sm_sys_restfulapi
-- ----------------------------
DROP TABLE IF EXISTS `sm_sys_restfulapi`;
CREATE TABLE "sm_sys_restfulapi" (
  "id" int(10) NOT NULL AUTO_INCREMENT,
  "service_name" varchar(32) DEFAULT NULL COMMENT '数据库名称',
  "httpmethod_name" varchar(32) DEFAULT 'GET' COMMENT '请求方法(GET,POST)',
  "cors_uris" varchar(200) DEFAULT NULL COMMENT '可跨域uri集合(多个;隔开)',
  "db_name" varchar(32) DEFAULT NULL COMMENT '关联数据库',
  "flag" tinyint(1) DEFAULT '1',
  "status" tinyint(1) DEFAULT '1',
  "remark" varchar(50) DEFAULT NULL COMMENT '备注',
  "create_id" int(11) DEFAULT NULL COMMENT '创建者ID',
  "create_time" datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  "update_id" int(11) DEFAULT NULL COMMENT '更新人编号',
  "update_time" datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  "area_id" int(11) DEFAULT NULL COMMENT '区域id',
  "company_id" int(11) DEFAULT NULL COMMENT '公司编号',
  "catalogue_id" int(11) DEFAULT NULL COMMENT '编目ID',
  "tags" varchar(255) DEFAULT NULL COMMENT '标签',
  "dept_id" int(11) DEFAULT NULL COMMENT '部门编号',
  "map_service_code" varchar(255) NOT NULL COMMENT '服务编码-唯一',
  "table_name" varchar(255) NOT NULL COMMENT '表名',
  "server_port" int(10) DEFAULT '8085' COMMENT '端口',
  "is_sql" tinyint(1) DEFAULT '1' COMMENT '是否是sql来查询',
  "sqls" varchar(255) DEFAULT NULL COMMENT 'sql语句',
  "type" varchar(50) DEFAULT NULL COMMENT '查询类型(SELECT/UPATE/DELETE)',
  "url_path" varchar(255) DEFAULT NULL COMMENT 'url路径',
  "db_type" varchar(255) DEFAULT NULL COMMENT '数据库类型',
  "ip_address" varchar(255) DEFAULT NULL COMMENT 'ip地址',
  "code" varchar(255) DEFAULT NULL COMMENT 'dbinfo中的Name字段',
  "db_schema" varchar(255) DEFAULT 'public' COMMENT 'pg中的schema',
  PRIMARY KEY ("id") USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COMMENT='自动化接口列表';

-- ----------------------------
-- Records of sm_sys_restfulapi
-- ----------------------------
BEGIN;
INSERT INTO `sm_sys_restfulapi` VALUES (11, 'test', 'GET', '/query/cic/mm_mxd_info', 'cic', 1, 1, NULL, 1, '2019-07-23 17:27:57', 1, '2019-07-23 17:27:57', NULL, NULL, NULL, '', NULL, '7c3eb649-9d31-4359-949c-2592f795b818', 'mm_mxd_info', 8085, 0, '', 'SELECT', 'http://127.0.0.1:8085/dbapi/query/cic/mm_mxd_info', 'Mysql', '127.0.0.1:3306', 'cic', 'public');
INSERT INTO `sm_sys_restfulapi` VALUES (12, 'test', 'GET', '/exec/cic/7c3eb649-9d31-4359-949c-2592f795b819', 'cic', 1, 1, NULL, 1, '2019-07-23 17:27:57', 1, '2019-07-23 17:27:57', NULL, NULL, NULL, NULL, NULL, '7c3eb649-9d31-4359-949c-2592f795b811', 'mm_mxd_info', 8085, 1, ' SELECT\n			child.*, parent.name parent_name\n		FROM\n			bm_sys_catalogue child\n		LEFT JOIN bm_sys_catalogue parent ON child.parent_id = parent.id', 'SELECT', 'http://127.0.0.1:8085/dbapi/exec/cic/7c3eb649-9d31-4359-949c-2592f795b819', 'Mysql', '127.0.0.1:3306', 'cic', 'public');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
