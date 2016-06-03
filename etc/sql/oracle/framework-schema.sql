
-----------------------------------------------------------------------------
-- UNIT
-----------------------------------------------------------------------------
DROP TABLE UNIT CASCADE CONSTRAINTS PURGE;

CREATE TABLE UNIT
(
  ID NUMBER (20, 0) NOT NULL,
  NAME VARCHAR2 (32) NOT NULL,
  DESCRIPTION VARCHAR2 (64),
  BASE_UNIT_ID NUMBER (20, 0),
    RATE_VALUE NUMBER (20, 0) NOT NULL,
  RATE_SCALE NUMBER (10,0) NOT NULL,
  SCALE NUMBER (10,0) NOT NULL,
  VERSION NUMBER (20, 0) NOT NULL
);
ALTER TABLE UNIT
    ADD CONSTRAINT UNIT_PK
PRIMARY KEY (ID);



COMMENT ON TABLE UNIT IS '���ʎړx';
COMMENT ON COLUMN UNIT.ID IS 'OID';
COMMENT ON COLUMN UNIT.NAME IS '���ʎړx��';
COMMENT ON COLUMN UNIT.DESCRIPTION IS '�P�ʐ���';
COMMENT ON COLUMN UNIT.BASE_UNIT_ID IS '�x�[�X���ʎړx';
COMMENT ON COLUMN UNIT.RATE_VALUE IS '�ϊ��W�� ��(����)';
COMMENT ON COLUMN UNIT.RATE_SCALE IS '�ϊ��W�� �����_�ʒu�B���̐��Ȃ獶�ցA���̐��Ȃ�E�ֈړ�������';

COMMENT ON COLUMN UNIT.SCALE IS '�ϊ����ɕێ����鏬�����x�B�������w�肷��Ɛ����̐؎̂Ĕ��f����';
COMMENT ON COLUMN UNIT.VERSION IS '�y�ϓI���b�L���O�L�[';


-----------------------------------------------------------------------------
-- numbering
-----------------------------------------------------------------------------
DROP TABLE numbering CASCADE CONSTRAINTS PURGE;

CREATE TABLE numbering
(
  id VARCHAR2 (128) NOT NULL,
  next_number NUMBER (20, 0) NOT NULL
);
ALTER TABLE numbering
    ADD CONSTRAINT numbering_PK
PRIMARY KEY (id);



COMMENT ON TABLE numbering IS '�i���o�����O�e�[�u��';
COMMENT ON COLUMN numbering.id IS '�h�c';
COMMENT ON COLUMN numbering.next_number IS '���݊��蓖�ĂĂ���ԍ�';

