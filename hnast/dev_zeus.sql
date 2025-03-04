/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80015
 Source Host           : localhost:3306
 Source Schema         : dev_zeus

 Target Server Type    : MySQL
 Target Server Version : 80015
 File Encoding         : 65001

 Date: 19/02/2021 11:48:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'id',
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `label` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `del_flag` tinyint(4) NULL DEFAULT 0 COMMENT '删除标记',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_date` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新者',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序 升序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '数据字典表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES ('156a16eaf307471299ec143868e5c6a7', 'bill', '高铁票', 'speedrail', 0, '2019-08-06 17:42:07', '2019-08-10 17:35:03', NULL, NULL, '高铁票', 1);
INSERT INTO `sys_dict` VALUES ('2caa2e7825be4e6cb44a444719c6bf23', 'sex', '男', '1', 0, '2020-09-14 10:46:01', '2020-09-14 10:58:21', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', '的烦烦烦', 9);
INSERT INTO `sys_dict` VALUES ('8e62ca2f7a5a40a89ae0e6a614a5d16e', 'bill', '火车票', 'train', 0, '2019-08-06 17:40:30', '2019-08-10 17:35:10', NULL, NULL, '火车票', 1);
INSERT INTO `sys_dict` VALUES ('9d0cecb91f1a48d4888ae4a7fd7758ce', 'bill', '机票', 'planeticket', 0, '2019-08-06 17:42:50', '2019-08-06 17:42:50', NULL, NULL, '机票', 1);
INSERT INTO `sys_dict` VALUES ('a6f4d750c97fe5ac8d30a2d99484f5cf', 'sex', '女', '0', 0, '2020-09-14 10:46:23', '2020-09-14 10:46:23', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', '阿斯蒂芬', 5);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `code` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编码',
  `pid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '父菜单ID',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `icon` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序 升序',
  `is_show` int(11) NULL DEFAULT 1 COMMENT '菜单是否显示',
  `permission` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限',
  `del_flag` tinyint(4) NULL DEFAULT 0,
  `create_date` datetime(0) NOT NULL,
  `update_date` datetime(0) NOT NULL,
  `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '功能菜单表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('029599480429e715606ffec804100a8e', '001', '0', '首页', NULL, 1, 1, 'dashboards', 0, '2017-05-17 15:56:32', '2017-05-17 15:56:32', '338fdb483f1484750d4f6483e00120e4', '338fdb483f1484750d4f6483e00120e4', NULL);
INSERT INTO `sys_menu` VALUES ('07326b0b0c73282e5cd940cf587cd8fe', '002', '0', '系统管理', NULL, 9, 1, 'permission', 0, '2017-05-17 15:56:32', '2019-08-06 15:35:08', '338fdb483f1484750d4f6483e00120e4', '338fdb483f1484750d4f6483e00120e4', NULL);
INSERT INTO `sys_menu` VALUES ('26f3658888e709e61b9eb3e5527e84e6', '002000002', 'a17c71728b14035d176f815a63ddb4ea', '编辑', NULL, 7, 1, 'permission:user:edit', 0, '2020-09-11 19:52:05', '2020-09-11 19:52:05', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('33d6611c7853bf1895930584309a8bf0', '002002001', 'ff0ffa423dcfbdaff9742d43a8430078', '新增', NULL, 5, 1, 'permission:role:add', 0, '2020-09-11 19:55:26', '2020-09-11 19:55:26', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('3d48d93f3db8191aba341d2da53d756c', '002003002', '41dd737d1bb2f6d907ab362a4814c316', '编辑', NULL, 7, 1, 'permission:menu:edit', 0, '2020-09-11 19:57:29', '2020-09-11 19:57:29', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('41dd737d1bb2f6d907ab362a4814c316', '002003', '07326b0b0c73282e5cd940cf587cd8fe', '菜单管理', NULL, 9, 1, 'permission:menu', 0, '2020-09-11 19:56:22', '2020-09-11 19:56:22', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('43b71a9d76a911121b7c9204db47e323', '002004003', 'f60ed8f58d11686dadc6258c168ffa03', '删除', NULL, 7, 1, 'permission:dict:del', 0, '2020-09-14 10:03:49', '2020-09-14 10:03:49', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('45fa818bc5c841d48f58326cef9ab342', '002001000', 'dc347756cdfec8573c962b966af015ff', '查看', NULL, 3, 1, 'permission:office:view', 0, '2020-09-11 19:53:29', '2020-09-11 19:53:29', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('51764c6d4df47acb9c4a207303d678f0', '002001001', 'dc347756cdfec8573c962b966af015ff', '新增', NULL, 5, 1, 'permission:office:add', 0, '2020-09-11 19:53:39', '2020-09-11 19:53:39', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('589f140cf83f5986197834a7fdea4698', '002004002', 'f60ed8f58d11686dadc6258c168ffa03', '编辑', NULL, 5, 1, 'permission:dict:edit', 0, '2020-09-14 10:03:32', '2020-09-14 10:03:32', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('72e80058c7363e414dbad692fc78c036', '002001002', 'dc347756cdfec8573c962b966af015ff', '编辑', NULL, 7, 1, 'permission:office:edit', 0, '2020-09-11 19:53:52', '2020-09-11 19:53:52', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('843d3968760f97c30eaf7ed516c6dde2', '002002000', 'ff0ffa423dcfbdaff9742d43a8430078', '查看', NULL, 3, 1, 'permission:role:view', 0, '2020-09-11 19:55:14', '2020-09-11 19:55:14', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('86ccc90d5225e22d51d7b3224872972b', '002002003', 'ff0ffa423dcfbdaff9742d43a8430078', '删除', NULL, 9, 1, 'permission:role:del', 0, '2020-09-11 19:55:49', '2020-09-12 15:24:45', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('896bdcc85f701eec59799625334d254b', '002001003', 'dc347756cdfec8573c962b966af015ff', '删除', NULL, 9, 1, 'permission:office:del', 0, '2020-09-11 19:54:05', '2020-09-11 19:54:05', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('8ba8ce3536fe6501cf99bde8efecc5ed', '002003003', '41dd737d1bb2f6d907ab362a4814c316', '删除', NULL, 9, 1, 'permission:menu:del', 0, '2020-09-11 19:57:40', '2020-09-11 19:57:40', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('8e2d782cef9f4c5d99ebf600af86fd94', '002000001', 'a17c71728b14035d176f815a63ddb4ea', '新增', NULL, 5, 1, 'permission:user:add', 0, '2020-09-11 19:51:50', '2020-09-11 19:51:50', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('92c4c6dedb95552efdfec54055829675', '002003000', '41dd737d1bb2f6d907ab362a4814c316', '查看', NULL, 3, 1, 'permission:menu:view', 0, '2020-09-11 19:56:35', '2020-09-11 19:56:35', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('9b1e86cd07e2674c9754e1a8d0665203', '002004001', 'f60ed8f58d11686dadc6258c168ffa03', '新增', NULL, 3, 1, 'permission:dict:add', 0, '2020-09-14 10:02:54', '2020-09-14 10:02:54', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('a17c71728b14035d176f815a63ddb4ea', '002000', '07326b0b0c73282e5cd940cf587cd8fe', '用户管理', NULL, 3, 1, 'permission:user', 0, '2020-09-11 19:50:51', '2020-09-11 19:50:51', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('af03ed95da6d28e0eb4d4abe8a071894', '002004000', 'f60ed8f58d11686dadc6258c168ffa03', '查看', NULL, 1, 1, 'permission:dict:view', 0, '2020-09-14 10:02:39', '2020-09-14 10:02:39', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('c5b94f9fe1d1cd270005290df8872f39', '002000003', 'a17c71728b14035d176f815a63ddb4ea', '删除', NULL, 11, 1, 'permission:user:del', 0, '2020-09-11 19:52:17', '2020-09-11 19:52:17', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('cfc2667de969c9f153a762976f9d16ca', '002000000', 'a17c71728b14035d176f815a63ddb4ea', '查看', NULL, 3, 1, 'permission:user:view', 0, '2020-09-11 19:51:28', '2020-09-11 19:51:28', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('dc347756cdfec8573c962b966af015ff', '002001', '07326b0b0c73282e5cd940cf587cd8fe', '部门管理', NULL, 5, 1, 'permission:office', 0, '2020-09-11 19:53:17', '2020-09-11 19:53:17', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('e217ad75594fd35eebafb13f6f5796e9', '002003001', '41dd737d1bb2f6d907ab362a4814c316', '新增', NULL, 5, 1, 'permission:menu:add', 0, '2020-09-11 19:57:15', '2020-09-11 19:57:15', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('f43313d96c1fa2d497e84cbb7d192569', '002002002', 'ff0ffa423dcfbdaff9742d43a8430078', '编辑', NULL, 7, 1, 'permission:role:edit', 0, '2020-09-11 19:55:38', '2020-09-12 15:24:35', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('f60ed8f58d11686dadc6258c168ffa03', '002004', '07326b0b0c73282e5cd940cf587cd8fe', '字典管理', NULL, 13, 1, 'permission:dict', 0, '2020-09-14 10:02:28', '2020-09-14 10:02:28', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_menu` VALUES ('ff0ffa423dcfbdaff9742d43a8430078', '002002', '07326b0b0c73282e5cd940cf587cd8fe', '角色管理', NULL, 7, 1, 'permission:role', 0, '2020-09-11 19:55:02', '2020-09-11 19:55:02', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);

-- ----------------------------
-- Table structure for sys_office
-- ----------------------------
DROP TABLE IF EXISTS `sys_office`;
CREATE TABLE `sys_office`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `pid` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `code` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序 升序',
  `del_flag` tinyint(4) NULL DEFAULT 0,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '机构表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_office
-- ----------------------------
INSERT INTO `sys_office` VALUES ('0746c8380b0cfef4e5295197d4a90a46', '10d4db75294e056adfe9dcb8a8137688', '岳阳', '001000001', 3, 0, '2020-09-11 20:07:01', '2020-09-11 20:29:40', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_office` VALUES ('1', '0', '总部', '001', 1, 0, '2016-11-08 18:00:00', '2017-05-23 18:25:41', '', '338fdb483f1484750d4f6483e00120e4', NULL);
INSERT INTO `sys_office` VALUES ('10d4db75294e056adfe9dcb8a8137688', '1', '湖南', '001000', 1, 0, '2020-09-11 20:06:41', '2020-09-11 20:06:41', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_office` VALUES ('37cc5d3b26c404be2c79f175c2cb9a82', '10d4db75294e056adfe9dcb8a8137688', '湘潭', '001000002', 5, 0, '2020-09-11 20:07:10', '2020-09-11 20:07:10', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_office` VALUES ('53b0e22927fdb85be0bb46c0371da507', 'a47a5a9deda7a8ec1f88ad57fdd77e10', '武汉', '001001000', 1, 0, '2020-09-11 20:13:27', '2020-09-11 20:13:27', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_office` VALUES ('919fe189ea6a552ca2e1e7f0152621be', '10d4db75294e056adfe9dcb8a8137688', '长沙', '001000000', 1, 0, '2020-09-11 20:06:53', '2020-09-11 20:06:53', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_office` VALUES ('a47a5a9deda7a8ec1f88ad57fdd77e10', '1', '湖北', '001001', 3, 0, '2020-09-11 20:13:18', '2020-09-11 20:13:18', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_office` VALUES ('b587a19137cff9ff1c07a58f01dd106f', '10d4db75294e056adfe9dcb8a8137688', '衡阳', '001000003', 9, 0, '2020-09-11 20:52:23', '2020-09-11 20:52:23', '4d2f5b74c4e04285aa18ae408bfe7ded', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '记录id',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `del_flag` tinyint(4) NULL DEFAULT 0,
  `create_date` datetime(0) NOT NULL,
  `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_date` datetime(0) NOT NULL,
  `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('2d78a30e220ad79e9a220eb8f01ee095', '测试角色', 0, '2020-09-13 16:00:22', '4d2f5b74c4e04285aa18ae408bfe7ded', '2020-09-13 16:42:48', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_role` VALUES ('aca8d3043ea04c6da0fff218c0206464', '普通用户', 0, '2018-11-01 10:59:10', NULL, '2018-11-01 11:09:46', NULL, NULL);
INSERT INTO `sys_role` VALUES ('fa1a8ddf4b4d311fb1f38d2248f8548e', '管理员', 0, '2016-11-10 12:48:09', '338fdb483f1484750d4f6483e00120e4', '2019-08-06 17:32:12', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色ID',
  `menu_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单ID',
  `del_flag` tinyint(4) NULL DEFAULT 0,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色权限表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('3455c8ddb77a32768182a5ec70fc14a8', '2d78a30e220ad79e9a220eb8f01ee095', 'cfc2667de969c9f153a762976f9d16ca', 0, '2020-09-13 16:00:22', '4d2f5b74c4e04285aa18ae408bfe7ded', '2020-09-13 16:00:22', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_role_menu` VALUES ('3fc8f6f526bf0a5c057bbae09963e286', '2d78a30e220ad79e9a220eb8f01ee095', 'a17c71728b14035d176f815a63ddb4ea', 0, '2020-09-13 16:00:22', '4d2f5b74c4e04285aa18ae408bfe7ded', '2020-09-13 16:00:22', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);
INSERT INTO `sys_role_menu` VALUES ('4cadcb1443e2e777f465090c82fc1396', '2d78a30e220ad79e9a220eb8f01ee095', '07326b0b0c73282e5cd940cf587cd8fe', 0, '2020-09-13 16:00:22', '4d2f5b74c4e04285aa18ae408bfe7ded', '2020-09-13 16:00:22', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '记录id',
  `account` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '账号',
  `nickname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '昵称',
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号手机号码',
  `password` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `token` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'token',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最近一次登录时间',
  `last_visit_time` datetime(0) NULL DEFAULT NULL COMMENT '最后访问时间',
  `last_login_ip` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最近一次登录ip',
  `login_count` mediumint(8) UNSIGNED NULL DEFAULT 0 COMMENT '登录次数',
  `post_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '职位名',
  `email` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱地址',
  `is_admin` tinyint(1) NULL DEFAULT 0 COMMENT '是否超级管理员',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `enable` tinyint(4) NULL DEFAULT 1 COMMENT '是否可用',
  `del_flag` tinyint(4) NULL DEFAULT 0,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `mobile`(`mobile`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '后台管理账号信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('2810b835e698ab332fb1a37485544e32', 'test01', '测试', '14567898765', '542607bd827f2335c06743fc0cdf7d74', NULL, '2020-09-13 16:43:58', '2020-09-13 16:43:58', '127.0.0.1', 1, NULL, '', 0, NULL, 1, 0, '2020-09-13 16:43:38', '4d2f5b74c4e04285aa18ae408bfe7ded', '2020-09-13 16:49:13', '2810b835e698ab332fb1a37485544e32', NULL);
INSERT INTO `sys_user` VALUES ('4d2f5b74c4e04285aa18ae408bfe7ded', 'admin', '管理员', '15674875466', '57ce91870493f4095915ccf1617d06d0', '12a1496cf0934928abe910dc82171e39', '2021-02-19 11:42:24', '2021-02-19 11:42:24', '127.0.0.1', 18, NULL, NULL, 1, NULL, 1, 0, NULL, NULL, '2021-02-19 11:42:24', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);

-- ----------------------------
-- Table structure for sys_user_office
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_office`;
CREATE TABLE `sys_user_office`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `office_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门id',
  `del_flag` tinyint(4) NULL DEFAULT 0,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_user_office
-- ----------------------------
INSERT INTO `sys_user_office` VALUES ('556c93926ad1571c905449bdbbd31170', '2810b835e698ab332fb1a37485544e32', '53b0e22927fdb85be0bb46c0371da507', 0, '2020-09-13 16:43:38', '4d2f5b74c4e04285aa18ae408bfe7ded', '2020-09-13 16:43:38', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '后台账号id',
  `role_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色id',
  `del_flag` tinyint(4) NULL DEFAULT 0,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `create_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `update_date` datetime(0) NULL DEFAULT NULL,
  `update_by` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('dd4c6093b9c6e83438ac3ffaea0e2a64', '2810b835e698ab332fb1a37485544e32', '2d78a30e220ad79e9a220eb8f01ee095', 0, '2020-09-13 16:43:38', '4d2f5b74c4e04285aa18ae408bfe7ded', '2020-09-13 16:43:38', '4d2f5b74c4e04285aa18ae408bfe7ded', NULL);

SET FOREIGN_KEY_CHECKS = 1;
