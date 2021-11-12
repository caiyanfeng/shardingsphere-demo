/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50734
Source Host           : 127.0.0.1:3306
Source Database       : sharding_user

Target Server Type    : MYSQL
Target Server Version : 50734
File Encoding         : 65001

Date: 2021-11-12 15:11:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'admin', '123456', '测试用户');
