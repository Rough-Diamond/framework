
# -----------------------------------------------------------------------
# UNIT
# -----------------------------------------------------------------------
drop table if exists UNIT;

CREATE TABLE UNIT
(
ID BIGINT NOT NULL COMMENT 'OID',
NAME VARCHAR (32) NOT NULL COMMENT '���ʎړx��',
DESCRIPTION VARCHAR (64) COMMENT '�P�ʐ���',
BASE_UNIT_ID BIGINT COMMENT '�x�[�X���ʎړx',
RATE_VALUE BIGINT NOT NULL COMMENT '�ϊ��W�� ��(����)',
RATE_SCALE INTEGER NOT NULL COMMENT '�ϊ��W�� �����_�ʒu�B���̐��Ȃ獶�ցA���̐��Ȃ�E�ֈړ�������',
SCALE INTEGER NOT NULL COMMENT '�ϊ����ɕێ����鏬�����x�B�������w�肷��Ɛ����̐؎̂Ĕ��f����',
VERSION BIGINT NOT NULL COMMENT '�y�ϓI���b�L���O�L�[',
    PRIMARY KEY(ID));
ALTER TABLE UNIT COMMENT '���ʎړx';

# -----------------------------------------------------------------------
# numbering
# -----------------------------------------------------------------------
drop table if exists numbering;

CREATE TABLE numbering
(
id VARCHAR (128) NOT NULL COMMENT '�h�c',
next_number BIGINT NOT NULL COMMENT '���݊��蓖�ĂĂ���ԍ�',
    PRIMARY KEY(id));
ALTER TABLE numbering COMMENT '�i���o�����O�e�[�u��';
