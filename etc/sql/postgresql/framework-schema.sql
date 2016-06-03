
-----------------------------------------------------------------------------
-- UNIT
-----------------------------------------------------------------------------
DROP TABLE UNIT CASCADE;


CREATE TABLE UNIT
(
    ID int8 NOT NULL,
    NAME varchar (32) NOT NULL,
    DESCRIPTION varchar (64),
      -- REFERENCES UNIT (ID)
    BASE_UNIT_ID int8,
        RATE_VALUE int8 NOT NULL,
    RATE_SCALE integer NOT NULL,
    SCALE integer NOT NULL,
    VERSION int8 NOT NULL,
    PRIMARY KEY (ID)
);

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
DROP TABLE numbering CASCADE;


CREATE TABLE numbering
(
    id varchar (128) NOT NULL,
    next_number int8 NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE numbering IS 'ナンバリングテーブル';
COMMENT ON COLUMN numbering.id IS 'ＩＤ';
COMMENT ON COLUMN numbering.next_number IS '現在割り当てている番号';

