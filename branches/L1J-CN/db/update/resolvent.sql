/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50524
Source Host           : localhost:3306
Source Database       : l1jtw

Target Server Type    : MYSQL
Target Server Version : 50524
File Encoding         : 65001

Date: 2012-10-30 21:32:41
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `resolvent`
-- ----------------------------
DROP TABLE IF EXISTS `resolvent`;
CREATE TABLE `resolvent` (
  `item_id` int(10) NOT NULL DEFAULT '0',
  `note` varchar(45) NOT NULL,
  `crystal_item` int(10) NOT NULL DEFAULT '41246',
  `crystal_count` int(10) NOT NULL DEFAULT '0',
  `crystal_chance` int(10) NOT NULL DEFAULT '50',
  PRIMARY KEY (`item_id`)
) ENGINE=MyISAM DEFAULT CHARSET=gbk;

-- ----------------------------
-- Records of resolvent
-- ----------------------------
INSERT INTO `resolvent` VALUES ('20140', '��������Ƥ����', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20141', '�������ĳ���', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20142', '���������ۼ�', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20143', '�������Ľ�������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20172', 'ˮ������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20177', '��������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20181', '��������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20189', '��������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20266', '��������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20284', '�ٻ����ƽ�ָ', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20306', 'С����������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20307', 'С���������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20308', 'С�;�������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20312', '��������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20316', '�������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20319', '��������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('20321', '����Ƥ��', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40014', '�¸�ҩˮ', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40044', '��ʯ', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40045', '�챦ʯ', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40046', '����ʯ', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40047', '�̱�ʯ', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40048', 'Ʒ����ʯ', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40049', 'Ʒ�ʺ챦ʯ', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40050', 'Ʒ������ʯ', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40051', 'Ʒ���̱�ʯ', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40090', '�յ�ħ������(�ȼ�1)', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40091', '�յ�ħ������(�ȼ�2)', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40092', '�յ�ħ������(�ȼ�3)', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40093', '�յ�ħ������(�ȼ�4)', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('40094', '�յ�ħ������(�ȼ�5)', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('120266', '��������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('120306', 'С����������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('120307', 'С���������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('120308', 'С�;�������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('120312', '��������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('120316', '�������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('120319', '��������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('120321', '����Ƥ��', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('61', '�森ڤ��ִ�н�', '60653', '50', '50');
INSERT INTO `resolvent` VALUES ('84', '����˫��', '60653', '50', '50');
INSERT INTO `resolvent` VALUES ('134', 'ʥ��ħ��', '60653', '50', '50');
INSERT INTO `resolvent` VALUES ('189', '����ʮ�ֹ�', '60653', '50', '50');
INSERT INTO `resolvent` VALUES ('20058', '��ٶ���', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20233', '���ħ����', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20113', '��ٻ���', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20129', '��ٷ���', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20067', '��ٶ���', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20070', '�ڰ�����', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20176', '�������', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20168', '�������', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20264', '��������', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20280', '��ħ��ָ', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20201', '��ٳ�ѥ', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20210', '�ڰ���ѥ', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20032', '�ڰ�ͷ��', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20208', '��ٳ�ѥ', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20098', '�ڰ������߿���', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20030', '���ͷ��', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20020', '���ͷ��', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20137', '��������', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20132', '�ڰ�����', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20228', '���֮��', '60653', '10', '50');
INSERT INTO `resolvent` VALUES ('20267', '��������', '41246', '10', '50');
INSERT INTO `resolvent` VALUES ('120267', '��������', '41246', '10', '50');
