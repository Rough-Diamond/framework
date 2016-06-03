
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



COMMENT ON TABLE UNIT IS '数量尺度';
COMMENT ON COLUMN UNIT.ID IS 'OID';
COMMENT ON COLUMN UNIT.NAME IS '数量尺度名';
COMMENT ON COLUMN UNIT.DESCRIPTION IS '単位説明';
COMMENT ON COLUMN UNIT.BASE_UNIT_ID IS 'ベース数量尺度';
COMMENT ON COLUMN UNIT.RATE_VALUE IS '変換係数 量(整数)';
COMMENT ON COLUMN UNIT.RATE_SCALE IS '変換係数 小数点位置。正の数なら左へ、負の数なら右へ移動させる';

COMMENT ON COLUMN UNIT.SCALE IS '変換時に保持する小数精度。負数を指定すると整数の切捨て判断する';
COMMENT ON COLUMN UNIT.VERSION IS '楽観的ロッキングキー';


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



COMMENT ON TABLE numbering IS 'ナンバリングテーブル';
COMMENT ON COLUMN numbering.id IS 'ＩＤ';
COMMENT ON COLUMN numbering.next_number IS '現在割り当てている番号';

