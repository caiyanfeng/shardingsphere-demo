/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50734
Source Host           : 127.0.0.1:3306
Source Database       : sharding_data1

Target Server Type    : MYSQL
Target Server Version : 50734
File Encoding         : 65001

Date: 2021-11-12 15:11:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_business
-- ----------------------------
DROP TABLE IF EXISTS `t_business`;
CREATE TABLE `t_business` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  `type` int(2) NOT NULL,
  `create_by` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_business
-- ----------------------------
INSERT INTO `t_business` VALUES ('1', '修改商品1', '1', 'admin', '2021-11-12 12:22:05', '修改数据');
INSERT INTO `t_business` VALUES ('4', '修改商品1', '1', 'admin', '2021-11-12 12:22:05', '修改数据');
INSERT INTO `t_business` VALUES ('5', '修改商品1', '1', 'admin', '2021-11-12 12:22:05', '修改数据');

-- ----------------------------
-- Table structure for t_order
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `type` int(4) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES ('1', '1', '订单1', '1', '1', '1');
