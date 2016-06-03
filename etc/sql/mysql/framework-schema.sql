
# -----------------------------------------------------------------------
# UNIT
# -----------------------------------------------------------------------
drop table if exists UNIT;

CREATE TABLE UNIT
(
ID BIGINT NOT NULL COMMENT 'OID',
NAME VARCHAR (32) NOT NULL COMMENT '数量尺度名',
DESCRIPTION VARCHAR (64) COMMENT '単位説明',
BASE_UNIT_ID BIGINT COMMENT 'ベース数量尺度',
RATE_VALUE BIGINT NOT NULL COMMENT '変換係数 量(整数)',
RATE_SCALE INTEGER NOT NULL COMMENT '変換係数 小数点位置。正の数なら左へ、負の数なら右へ移動させる',
SCALE INTEGER NOT NULL COMMENT '変換時に保持する小数精度。負数を指定すると整数の切捨て判断する',
VERSION BIGINT NOT NULL COMMENT '楽観的ロッキングキー',
    PRIMARY KEY(ID));
ALTER TABLE UNIT COMMENT '数量尺度';

# -----------------------------------------------------------------------
# numbering
# -----------------------------------------------------------------------
drop table if exists numbering;

CREATE TABLE numbering
(
id VARCHAR (128) NOT NULL COMMENT 'ＩＤ',
next_number BIGINT NOT NULL COMMENT '現在割り当てている番号',
    PRIMARY KEY(id));
ALTER TABLE numbering COMMENT 'ナンバリングテーブル';
