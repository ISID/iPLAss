use mtdb
GO

/* drop/create OBJ_STORE */
DROP TABLE OBJ_STORE
GO

CREATE TABLE OBJ_STORE
(
    TENANT_ID NUMERIC(7,0) NOT NULL,
    OBJ_DEF_ID VARCHAR(128) NOT NULL,
    PG_NO NUMERIC(2,0) NOT NULL,
    OBJ_ID VARCHAR(64) NOT NULL,
    OBJ_VER NUMERIC(10,0) DEFAULT 0 NOT NULL,
    OBJ_DEF_VER NUMERIC(10,0),
    STATUS CHAR(1),
    OBJ_NAME NVARCHAR(256),
    OBJ_DESC NVARCHAR(1024),
    CRE_DATE DATETIME2(3),
    UP_DATE DATETIME2(3),
    S_DATE DATETIME2(3),
    E_DATE DATETIME2(3),
    LOCK_USER VARCHAR(64),
    CRE_USER VARCHAR(64),
    UP_USER VARCHAR(64),
    USTR_1 NVARCHAR(380),
    USTR_1_TD VARCHAR(139),
    UNUM_1 NUMERIC,
    UNUM_1_TD VARCHAR(139),
    UTS_1 DATETIME2(3),
    UTS_1_TD VARCHAR(139),
    UDBL_1 FLOAT,
    UDBL_1_TD VARCHAR(139),
    USTR_2 NVARCHAR(380),
    USTR_2_TD VARCHAR(139),
    UNUM_2 NUMERIC,
    UNUM_2_TD VARCHAR(139),
    UTS_2 DATETIME2(3),
    UTS_2_TD VARCHAR(139),
    UDBL_2 FLOAT,
    UDBL_2_TD VARCHAR(139),
    ISTR_1 NVARCHAR(380),
    ISTR_1_TD VARCHAR(139),
    ISTR_2 NVARCHAR(380),
    ISTR_2_TD VARCHAR(139),
    INUM_1 NUMERIC,
    INUM_1_TD VARCHAR(139),
    ITS_1 DATETIME2(3),
    ITS_1_TD VARCHAR(139),
    IDBL_1 FLOAT,
    IDBL_1_TD VARCHAR(139),
    ISTR_3 NVARCHAR(380),
    ISTR_3_TD VARCHAR(139),
    ISTR_4 NVARCHAR(380),
    ISTR_4_TD VARCHAR(139),
    INUM_2 NUMERIC,
    INUM_2_TD VARCHAR(139),
    ITS_2 DATETIME2(3),
    ITS_2_TD VARCHAR(139),
    IDBL_2 FLOAT,
    IDBL_2_TD VARCHAR(139),
    ISTR_5 NVARCHAR(380),
    ISTR_5_TD VARCHAR(139),
    ISTR_6 NVARCHAR(380),
    ISTR_6_TD VARCHAR(139),
    INUM_3 NUMERIC,
    INUM_3_TD VARCHAR(139),
    ITS_3 DATETIME2(3),
    ITS_3_TD VARCHAR(139),
    IDBL_3 FLOAT,
    IDBL_3_TD VARCHAR(139),
    ISTR_7 NVARCHAR(380),
    ISTR_7_TD VARCHAR(139),
    ISTR_8 NVARCHAR(380),
    ISTR_8_TD VARCHAR(139),
    INUM_4 NUMERIC,
    INUM_4_TD VARCHAR(139),
    ITS_4 DATETIME2(3),
    ITS_4_TD VARCHAR(139),
    IDBL_4 FLOAT,
    IDBL_4_TD VARCHAR(139),
    STR_1 NVARCHAR(4000),
    STR_2 NVARCHAR(4000),
    STR_3 NVARCHAR(4000),
    STR_4 NVARCHAR(4000),
    NUM_1 NUMERIC,
    TS_1 DATETIME2(3),
    DBL_1 FLOAT,
    STR_5 NVARCHAR(4000),
    STR_6 NVARCHAR(4000),
    STR_7 NVARCHAR(4000),
    STR_8 NVARCHAR(4000),
    NUM_2 NUMERIC,
    TS_2 DATETIME2(3),
    DBL_2 FLOAT,
    STR_9 NVARCHAR(4000),
    STR_10 NVARCHAR(4000),
    STR_11 NVARCHAR(4000),
    STR_12 NVARCHAR(4000),
    NUM_3 NUMERIC,
    TS_3 DATETIME2(3),
    DBL_3 FLOAT,
    STR_13 NVARCHAR(4000),
    STR_14 NVARCHAR(4000),
    STR_15 NVARCHAR(4000),
    STR_16 NVARCHAR(4000),
    NUM_4 NUMERIC,
    TS_4 DATETIME2(3),
    DBL_4 FLOAT,
    STR_17 NVARCHAR(4000),
    STR_18 NVARCHAR(4000),
    STR_19 NVARCHAR(4000),
    STR_20 NVARCHAR(4000),
    NUM_5 NUMERIC,
    TS_5 DATETIME2(3),
    DBL_5 FLOAT,
    STR_21 NVARCHAR(4000),
    STR_22 NVARCHAR(4000),
    STR_23 NVARCHAR(4000),
    STR_24 NVARCHAR(4000),
    NUM_6 NUMERIC,
    TS_6 DATETIME2(3),
    DBL_6 FLOAT,
    STR_25 NVARCHAR(4000),
    STR_26 NVARCHAR(4000),
    STR_27 NVARCHAR(4000),
    STR_28 NVARCHAR(4000),
    NUM_7 NUMERIC,
    TS_7 DATETIME2(3),
    DBL_7 FLOAT,
    STR_29 NVARCHAR(4000),
    STR_30 NVARCHAR(4000),
    STR_31 NVARCHAR(4000),
    STR_32 NVARCHAR(4000),
    NUM_8 NUMERIC,
    TS_8 DATETIME2(3),
    DBL_8 FLOAT,
    STR_33 NVARCHAR(4000),
    STR_34 NVARCHAR(4000),
    STR_35 NVARCHAR(4000),
    STR_36 NVARCHAR(4000),
    NUM_9 NUMERIC,
    TS_9 DATETIME2(3),
    DBL_9 FLOAT,
    STR_37 NVARCHAR(4000),
    STR_38 NVARCHAR(4000),
    STR_39 NVARCHAR(4000),
    STR_40 NVARCHAR(4000),
    NUM_10 NUMERIC,
    TS_10 DATETIME2(3),
    DBL_10 FLOAT,
    STR_41 NVARCHAR(4000),
    STR_42 NVARCHAR(4000),
    STR_43 NVARCHAR(4000),
    STR_44 NVARCHAR(4000),
    NUM_11 NUMERIC,
    TS_11 DATETIME2(3),
    DBL_11 FLOAT,
    STR_45 NVARCHAR(4000),
    STR_46 NVARCHAR(4000),
    STR_47 NVARCHAR(4000),
    STR_48 NVARCHAR(4000),
    NUM_12 NUMERIC,
    TS_12 DATETIME2(3),
    DBL_12 FLOAT,
    STR_49 NVARCHAR(4000),
    STR_50 NVARCHAR(4000),
    STR_51 NVARCHAR(4000),
    STR_52 NVARCHAR(4000),
    NUM_13 NUMERIC,
    TS_13 DATETIME2(3),
    DBL_13 FLOAT,
    STR_53 NVARCHAR(4000),
    STR_54 NVARCHAR(4000),
    STR_55 NVARCHAR(4000),
    STR_56 NVARCHAR(4000),
    NUM_14 NUMERIC,
    TS_14 DATETIME2(3),
    DBL_14 FLOAT,
    STR_57 NVARCHAR(4000),
    STR_58 NVARCHAR(4000),
    STR_59 NVARCHAR(4000),
    STR_60 NVARCHAR(4000),
    NUM_15 NUMERIC,
    TS_15 DATETIME2(3),
    DBL_15 FLOAT,
    STR_61 NVARCHAR(4000),
    STR_62 NVARCHAR(4000),
    STR_63 NVARCHAR(4000),
    STR_64 NVARCHAR(4000),
    NUM_16 NUMERIC,
    TS_16 DATETIME2(3),
    DBL_16 FLOAT,
    STR_65 NVARCHAR(4000),
    STR_66 NVARCHAR(4000),
    STR_67 NVARCHAR(4000),
    STR_68 NVARCHAR(4000),
    NUM_17 NUMERIC,
    TS_17 DATETIME2(3),
    DBL_17 FLOAT,
    STR_69 NVARCHAR(4000),
    STR_70 NVARCHAR(4000),
    STR_71 NVARCHAR(4000),
    STR_72 NVARCHAR(4000),
    NUM_18 NUMERIC,
    TS_18 DATETIME2(3),
    DBL_18 FLOAT,
    STR_73 NVARCHAR(4000),
    STR_74 NVARCHAR(4000),
    STR_75 NVARCHAR(4000),
    STR_76 NVARCHAR(4000),
    NUM_19 NUMERIC,
    TS_19 DATETIME2(3),
    DBL_19 FLOAT,
    STR_77 NVARCHAR(4000),
    STR_78 NVARCHAR(4000),
    STR_79 NVARCHAR(4000),
    STR_80 NVARCHAR(4000),
    NUM_20 NUMERIC,
    TS_20 DATETIME2(3),
    DBL_20 FLOAT,
    STR_81 NVARCHAR(4000),
    STR_82 NVARCHAR(4000),
    STR_83 NVARCHAR(4000),
    STR_84 NVARCHAR(4000),
    NUM_21 NUMERIC,
    TS_21 DATETIME2(3),
    DBL_21 FLOAT,
    STR_85 NVARCHAR(4000),
    STR_86 NVARCHAR(4000),
    STR_87 NVARCHAR(4000),
    STR_88 NVARCHAR(4000),
    NUM_22 NUMERIC,
    TS_22 DATETIME2(3),
    DBL_22 FLOAT,
    STR_89 NVARCHAR(4000),
    STR_90 NVARCHAR(4000),
    STR_91 NVARCHAR(4000),
    STR_92 NVARCHAR(4000),
    NUM_23 NUMERIC,
    TS_23 DATETIME2(3),
    DBL_23 FLOAT,
    STR_93 NVARCHAR(4000),
    STR_94 NVARCHAR(4000),
    STR_95 NVARCHAR(4000),
    STR_96 NVARCHAR(4000),
    NUM_24 NUMERIC,
    TS_24 DATETIME2(3),
    DBL_24 FLOAT,
    STR_97 NVARCHAR(4000),
    STR_98 NVARCHAR(4000),
    STR_99 NVARCHAR(4000),
    STR_100 NVARCHAR(4000),
    NUM_25 NUMERIC,
    TS_25 DATETIME2(3),
    DBL_25 FLOAT,
    STR_101 NVARCHAR(4000),
    STR_102 NVARCHAR(4000),
    STR_103 NVARCHAR(4000),
    STR_104 NVARCHAR(4000),
    NUM_26 NUMERIC,
    TS_26 DATETIME2(3),
    DBL_26 FLOAT,
    STR_105 NVARCHAR(4000),
    STR_106 NVARCHAR(4000),
    STR_107 NVARCHAR(4000),
    STR_108 NVARCHAR(4000),
    NUM_27 NUMERIC,
    TS_27 DATETIME2(3),
    DBL_27 FLOAT,
    STR_109 NVARCHAR(4000),
    STR_110 NVARCHAR(4000),
    STR_111 NVARCHAR(4000),
    STR_112 NVARCHAR(4000),
    NUM_28 NUMERIC,
    TS_28 DATETIME2(3),
    DBL_28 FLOAT,
    STR_113 NVARCHAR(4000),
    STR_114 NVARCHAR(4000),
    STR_115 NVARCHAR(4000),
    STR_116 NVARCHAR(4000),
    NUM_29 NUMERIC,
    TS_29 DATETIME2(3),
    DBL_29 FLOAT,
    STR_117 NVARCHAR(4000),
    STR_118 NVARCHAR(4000),
    STR_119 NVARCHAR(4000),
    STR_120 NVARCHAR(4000),
    NUM_30 NUMERIC,
    TS_30 DATETIME2(3),
    DBL_30 FLOAT,
    STR_121 NVARCHAR(4000),
    STR_122 NVARCHAR(4000),
    STR_123 NVARCHAR(4000),
    STR_124 NVARCHAR(4000),
    NUM_31 NUMERIC,
    TS_31 DATETIME2(3),
    DBL_31 FLOAT,
    STR_125 NVARCHAR(4000),
    STR_126 NVARCHAR(4000),
    STR_127 NVARCHAR(4000),
    STR_128 NVARCHAR(4000),
    NUM_32 NUMERIC,
    TS_32 DATETIME2(3),
    DBL_32 FLOAT,
    CONSTRAINT OBJ_STORE_PK PRIMARY KEY (OBJ_DEF_ID, TENANT_ID, OBJ_ID, OBJ_VER, PG_NO)
)
GO

CREATE UNIQUE INDEX OBJ_STORE_USTR_UNIQUE_1 ON OBJ_STORE (USTR_1_TD, USTR_1) WHERE USTR_1_TD IS NOT NULL AND USTR_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE_UNUM_UNIQUE_1 ON OBJ_STORE (UNUM_1_TD, UNUM_1) WHERE UNUM_1_TD IS NOT NULL AND UNUM_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE_UTS_UNIQUE_1 ON OBJ_STORE (UTS_1_TD, UTS_1) WHERE UTS_1_TD IS NOT NULL AND UTS_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE_UDBL_UNIQUE_1 ON OBJ_STORE (UDBL_1_TD, UDBL_1) WHERE UDBL_1_TD IS NOT NULL AND UDBL_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE_USTR_UNIQUE_2 ON OBJ_STORE (USTR_2_TD, USTR_2)  WHERE USTR_2_TD IS NOT NULL AND USTR_2 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE_UNUM_UNIQUE_2 ON OBJ_STORE (UNUM_2_TD, UNUM_2) WHERE UNUM_2_TD IS NOT NULL AND UNUM_2 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE_UTS_UNIQUE_2 ON OBJ_STORE (UTS_2_TD, UTS_2) WHERE UTS_2_TD IS NOT NULL AND UTS_2 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE_UDBL_UNIQUE_2 ON OBJ_STORE (UDBL_2_TD, UDBL_2) WHERE UDBL_2_TD IS NOT NULL AND UDBL_2 IS NOT NULL
GO

CREATE INDEX OBJ_STORE_ISTR_INDEX_1 ON OBJ_STORE (ISTR_1, ISTR_1_TD)
GO
CREATE INDEX OBJ_STORE_ISTR_INDEX_2 ON OBJ_STORE (ISTR_2, ISTR_2_TD)
GO
CREATE INDEX OBJ_STORE_INUM_INDEX_1 ON OBJ_STORE (INUM_1, INUM_1_TD)
GO
CREATE INDEX OBJ_STORE_ITS_INDEX_1 ON OBJ_STORE (ITS_1, ITS_1_TD)
GO
CREATE INDEX OBJ_STORE_IDBL_INDEX_1 ON OBJ_STORE (IDBL_1, IDBL_1_TD)
GO
CREATE INDEX OBJ_STORE_ISTR_INDEX_3 ON OBJ_STORE (ISTR_3, ISTR_3_TD)
GO
CREATE INDEX OBJ_STORE_ISTR_INDEX_4 ON OBJ_STORE (ISTR_4, ISTR_4_TD)
GO
CREATE INDEX OBJ_STORE_INUM_INDEX_2 ON OBJ_STORE (INUM_2, INUM_2_TD)
GO
CREATE INDEX OBJ_STORE_ITS_INDEX_2 ON OBJ_STORE (ITS_2, ITS_2_TD)
GO
CREATE INDEX OBJ_STORE_IDBL_INDEX_2 ON OBJ_STORE (IDBL_2, IDBL_2_TD)
GO
CREATE INDEX OBJ_STORE_ISTR_INDEX_5 ON OBJ_STORE (ISTR_5, ISTR_5_TD)
GO
CREATE INDEX OBJ_STORE_ISTR_INDEX_6 ON OBJ_STORE (ISTR_6, ISTR_6_TD)
GO
CREATE INDEX OBJ_STORE_INUM_INDEX_3 ON OBJ_STORE (INUM_3, INUM_3_TD)
GO
CREATE INDEX OBJ_STORE_ITS_INDEX_3 ON OBJ_STORE (ITS_3, ITS_3_TD)
GO
CREATE INDEX OBJ_STORE_IDBL_INDEX_3 ON OBJ_STORE (IDBL_3, IDBL_3_TD)
GO
CREATE INDEX OBJ_STORE_ISTR_INDEX_7 ON OBJ_STORE (ISTR_7, ISTR_7_TD)
GO
CREATE INDEX OBJ_STORE_ISTR_INDEX_8 ON OBJ_STORE (ISTR_8, ISTR_8_TD)
GO
CREATE INDEX OBJ_STORE_INUM_INDEX_4 ON OBJ_STORE (INUM_4, INUM_4_TD)
GO
CREATE INDEX OBJ_STORE_ITS_INDEX_4 ON OBJ_STORE (ITS_4, ITS_4_TD)
GO
CREATE INDEX OBJ_STORE_IDBL_INDEX_4 ON OBJ_STORE (IDBL_4, IDBL_4_TD)
GO

CREATE INDEX OBJ_STORE_CRE_USER_INDEX ON OBJ_STORE (CRE_USER, OBJ_DEF_ID, TENANT_ID)
GO

/* drop/create OBJ_STORE */
DROP TABLE OBJ_STORE__MTP
GO

CREATE TABLE OBJ_STORE__MTP
(
    TENANT_ID NUMERIC(7,0) NOT NULL,
    OBJ_DEF_ID VARCHAR(128) NOT NULL,
    PG_NO NUMERIC(2,0) NOT NULL,
    OBJ_ID VARCHAR(64) NOT NULL,
    OBJ_VER NUMERIC(10,0) DEFAULT 0 NOT NULL,
    OBJ_DEF_VER NUMERIC(10,0),
    STATUS CHAR(1),
    OBJ_NAME NVARCHAR(256),
    OBJ_DESC NVARCHAR(1024),
    CRE_DATE DATETIME2(3),
    UP_DATE DATETIME2(3),
    S_DATE DATETIME2(3),
    E_DATE DATETIME2(3),
    LOCK_USER VARCHAR(64),
    CRE_USER VARCHAR(64),
    UP_USER VARCHAR(64),
    USTR_1 NVARCHAR(380),
    USTR_1_TD VARCHAR(139),
    UNUM_1 NUMERIC,
    UNUM_1_TD VARCHAR(139),
    UTS_1 DATETIME2(3),
    UTS_1_TD VARCHAR(139),
    UDBL_1 FLOAT,
    UDBL_1_TD VARCHAR(139),
    USTR_2 NVARCHAR(380),
    USTR_2_TD VARCHAR(139),
    UNUM_2 NUMERIC,
    UNUM_2_TD VARCHAR(139),
    UTS_2 DATETIME2(3),
    UTS_2_TD VARCHAR(139),
    UDBL_2 FLOAT,
    UDBL_2_TD VARCHAR(139),
    ISTR_1 NVARCHAR(380),
    ISTR_1_TD VARCHAR(139),
    ISTR_2 NVARCHAR(380),
    ISTR_2_TD VARCHAR(139),
    INUM_1 NUMERIC,
    INUM_1_TD VARCHAR(139),
    ITS_1 DATETIME2(3),
    ITS_1_TD VARCHAR(139),
    IDBL_1 FLOAT,
    IDBL_1_TD VARCHAR(139),
    ISTR_3 NVARCHAR(380),
    ISTR_3_TD VARCHAR(139),
    ISTR_4 NVARCHAR(380),
    ISTR_4_TD VARCHAR(139),
    INUM_2 NUMERIC,
    INUM_2_TD VARCHAR(139),
    ITS_2 DATETIME2(3),
    ITS_2_TD VARCHAR(139),
    IDBL_2 FLOAT,
    IDBL_2_TD VARCHAR(139),
    ISTR_5 NVARCHAR(380),
    ISTR_5_TD VARCHAR(139),
    ISTR_6 NVARCHAR(380),
    ISTR_6_TD VARCHAR(139),
    INUM_3 NUMERIC,
    INUM_3_TD VARCHAR(139),
    ITS_3 DATETIME2(3),
    ITS_3_TD VARCHAR(139),
    IDBL_3 FLOAT,
    IDBL_3_TD VARCHAR(139),
    ISTR_7 NVARCHAR(380),
    ISTR_7_TD VARCHAR(139),
    ISTR_8 NVARCHAR(380),
    ISTR_8_TD VARCHAR(139),
    INUM_4 NUMERIC,
    INUM_4_TD VARCHAR(139),
    ITS_4 DATETIME2(3),
    ITS_4_TD VARCHAR(139),
    IDBL_4 FLOAT,
    IDBL_4_TD VARCHAR(139),
    STR_1 NVARCHAR(4000),
    STR_2 NVARCHAR(4000),
    STR_3 NVARCHAR(4000),
    STR_4 NVARCHAR(4000),
    NUM_1 NUMERIC,
    TS_1 DATETIME2(3),
    DBL_1 FLOAT,
    STR_5 NVARCHAR(4000),
    STR_6 NVARCHAR(4000),
    STR_7 NVARCHAR(4000),
    STR_8 NVARCHAR(4000),
    NUM_2 NUMERIC,
    TS_2 DATETIME2(3),
    DBL_2 FLOAT,
    STR_9 NVARCHAR(4000),
    STR_10 NVARCHAR(4000),
    STR_11 NVARCHAR(4000),
    STR_12 NVARCHAR(4000),
    NUM_3 NUMERIC,
    TS_3 DATETIME2(3),
    DBL_3 FLOAT,
    STR_13 NVARCHAR(4000),
    STR_14 NVARCHAR(4000),
    STR_15 NVARCHAR(4000),
    STR_16 NVARCHAR(4000),
    NUM_4 NUMERIC,
    TS_4 DATETIME2(3),
    DBL_4 FLOAT,
    STR_17 NVARCHAR(4000),
    STR_18 NVARCHAR(4000),
    STR_19 NVARCHAR(4000),
    STR_20 NVARCHAR(4000),
    NUM_5 NUMERIC,
    TS_5 DATETIME2(3),
    DBL_5 FLOAT,
    STR_21 NVARCHAR(4000),
    STR_22 NVARCHAR(4000),
    STR_23 NVARCHAR(4000),
    STR_24 NVARCHAR(4000),
    NUM_6 NUMERIC,
    TS_6 DATETIME2(3),
    DBL_6 FLOAT,
    STR_25 NVARCHAR(4000),
    STR_26 NVARCHAR(4000),
    STR_27 NVARCHAR(4000),
    STR_28 NVARCHAR(4000),
    NUM_7 NUMERIC,
    TS_7 DATETIME2(3),
    DBL_7 FLOAT,
    STR_29 NVARCHAR(4000),
    STR_30 NVARCHAR(4000),
    STR_31 NVARCHAR(4000),
    STR_32 NVARCHAR(4000),
    NUM_8 NUMERIC,
    TS_8 DATETIME2(3),
    DBL_8 FLOAT,
    STR_33 NVARCHAR(4000),
    STR_34 NVARCHAR(4000),
    STR_35 NVARCHAR(4000),
    STR_36 NVARCHAR(4000),
    NUM_9 NUMERIC,
    TS_9 DATETIME2(3),
    DBL_9 FLOAT,
    STR_37 NVARCHAR(4000),
    STR_38 NVARCHAR(4000),
    STR_39 NVARCHAR(4000),
    STR_40 NVARCHAR(4000),
    NUM_10 NUMERIC,
    TS_10 DATETIME2(3),
    DBL_10 FLOAT,
    STR_41 NVARCHAR(4000),
    STR_42 NVARCHAR(4000),
    STR_43 NVARCHAR(4000),
    STR_44 NVARCHAR(4000),
    NUM_11 NUMERIC,
    TS_11 DATETIME2(3),
    DBL_11 FLOAT,
    STR_45 NVARCHAR(4000),
    STR_46 NVARCHAR(4000),
    STR_47 NVARCHAR(4000),
    STR_48 NVARCHAR(4000),
    NUM_12 NUMERIC,
    TS_12 DATETIME2(3),
    DBL_12 FLOAT,
    STR_49 NVARCHAR(4000),
    STR_50 NVARCHAR(4000),
    STR_51 NVARCHAR(4000),
    STR_52 NVARCHAR(4000),
    NUM_13 NUMERIC,
    TS_13 DATETIME2(3),
    DBL_13 FLOAT,
    STR_53 NVARCHAR(4000),
    STR_54 NVARCHAR(4000),
    STR_55 NVARCHAR(4000),
    STR_56 NVARCHAR(4000),
    NUM_14 NUMERIC,
    TS_14 DATETIME2(3),
    DBL_14 FLOAT,
    STR_57 NVARCHAR(4000),
    STR_58 NVARCHAR(4000),
    STR_59 NVARCHAR(4000),
    STR_60 NVARCHAR(4000),
    NUM_15 NUMERIC,
    TS_15 DATETIME2(3),
    DBL_15 FLOAT,
    STR_61 NVARCHAR(4000),
    STR_62 NVARCHAR(4000),
    STR_63 NVARCHAR(4000),
    STR_64 NVARCHAR(4000),
    NUM_16 NUMERIC,
    TS_16 DATETIME2(3),
    DBL_16 FLOAT,
    STR_65 NVARCHAR(4000),
    STR_66 NVARCHAR(4000),
    STR_67 NVARCHAR(4000),
    STR_68 NVARCHAR(4000),
    NUM_17 NUMERIC,
    TS_17 DATETIME2(3),
    DBL_17 FLOAT,
    STR_69 NVARCHAR(4000),
    STR_70 NVARCHAR(4000),
    STR_71 NVARCHAR(4000),
    STR_72 NVARCHAR(4000),
    NUM_18 NUMERIC,
    TS_18 DATETIME2(3),
    DBL_18 FLOAT,
    STR_73 NVARCHAR(4000),
    STR_74 NVARCHAR(4000),
    STR_75 NVARCHAR(4000),
    STR_76 NVARCHAR(4000),
    NUM_19 NUMERIC,
    TS_19 DATETIME2(3),
    DBL_19 FLOAT,
    STR_77 NVARCHAR(4000),
    STR_78 NVARCHAR(4000),
    STR_79 NVARCHAR(4000),
    STR_80 NVARCHAR(4000),
    NUM_20 NUMERIC,
    TS_20 DATETIME2(3),
    DBL_20 FLOAT,
    STR_81 NVARCHAR(4000),
    STR_82 NVARCHAR(4000),
    STR_83 NVARCHAR(4000),
    STR_84 NVARCHAR(4000),
    NUM_21 NUMERIC,
    TS_21 DATETIME2(3),
    DBL_21 FLOAT,
    STR_85 NVARCHAR(4000),
    STR_86 NVARCHAR(4000),
    STR_87 NVARCHAR(4000),
    STR_88 NVARCHAR(4000),
    NUM_22 NUMERIC,
    TS_22 DATETIME2(3),
    DBL_22 FLOAT,
    STR_89 NVARCHAR(4000),
    STR_90 NVARCHAR(4000),
    STR_91 NVARCHAR(4000),
    STR_92 NVARCHAR(4000),
    NUM_23 NUMERIC,
    TS_23 DATETIME2(3),
    DBL_23 FLOAT,
    STR_93 NVARCHAR(4000),
    STR_94 NVARCHAR(4000),
    STR_95 NVARCHAR(4000),
    STR_96 NVARCHAR(4000),
    NUM_24 NUMERIC,
    TS_24 DATETIME2(3),
    DBL_24 FLOAT,
    STR_97 NVARCHAR(4000),
    STR_98 NVARCHAR(4000),
    STR_99 NVARCHAR(4000),
    STR_100 NVARCHAR(4000),
    NUM_25 NUMERIC,
    TS_25 DATETIME2(3),
    DBL_25 FLOAT,
    STR_101 NVARCHAR(4000),
    STR_102 NVARCHAR(4000),
    STR_103 NVARCHAR(4000),
    STR_104 NVARCHAR(4000),
    NUM_26 NUMERIC,
    TS_26 DATETIME2(3),
    DBL_26 FLOAT,
    STR_105 NVARCHAR(4000),
    STR_106 NVARCHAR(4000),
    STR_107 NVARCHAR(4000),
    STR_108 NVARCHAR(4000),
    NUM_27 NUMERIC,
    TS_27 DATETIME2(3),
    DBL_27 FLOAT,
    STR_109 NVARCHAR(4000),
    STR_110 NVARCHAR(4000),
    STR_111 NVARCHAR(4000),
    STR_112 NVARCHAR(4000),
    NUM_28 NUMERIC,
    TS_28 DATETIME2(3),
    DBL_28 FLOAT,
    STR_113 NVARCHAR(4000),
    STR_114 NVARCHAR(4000),
    STR_115 NVARCHAR(4000),
    STR_116 NVARCHAR(4000),
    NUM_29 NUMERIC,
    TS_29 DATETIME2(3),
    DBL_29 FLOAT,
    STR_117 NVARCHAR(4000),
    STR_118 NVARCHAR(4000),
    STR_119 NVARCHAR(4000),
    STR_120 NVARCHAR(4000),
    NUM_30 NUMERIC,
    TS_30 DATETIME2(3),
    DBL_30 FLOAT,
    STR_121 NVARCHAR(4000),
    STR_122 NVARCHAR(4000),
    STR_123 NVARCHAR(4000),
    STR_124 NVARCHAR(4000),
    NUM_31 NUMERIC,
    TS_31 DATETIME2(3),
    DBL_31 FLOAT,
    STR_125 NVARCHAR(4000),
    STR_126 NVARCHAR(4000),
    STR_127 NVARCHAR(4000),
    STR_128 NVARCHAR(4000),
    NUM_32 NUMERIC,
    TS_32 DATETIME2(3),
    DBL_32 FLOAT,
    CONSTRAINT OBJ_STORE__MTP_PK PRIMARY KEY (OBJ_DEF_ID, TENANT_ID, OBJ_ID, OBJ_VER, PG_NO)
)
GO

CREATE UNIQUE INDEX OBJ_STORE__MTP_USTR_UNIQUE_1 ON OBJ_STORE__MTP (USTR_1_TD, USTR_1) WHERE USTR_1_TD IS NOT NULL AND USTR_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__MTP_UNUM_UNIQUE_1 ON OBJ_STORE__MTP (UNUM_1_TD, UNUM_1) WHERE UNUM_1_TD IS NOT NULL AND UNUM_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__MTP_UTS_UNIQUE_1 ON OBJ_STORE__MTP (UTS_1_TD, UTS_1) WHERE UTS_1_TD IS NOT NULL AND UTS_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__MTP_UDBL_UNIQUE_1 ON OBJ_STORE__MTP (UDBL_1_TD, UDBL_1) WHERE UDBL_1_TD IS NOT NULL AND UDBL_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__MTP_USTR_UNIQUE_2 ON OBJ_STORE__MTP (USTR_2_TD, USTR_2)  WHERE USTR_2_TD IS NOT NULL AND USTR_2 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__MTP_UNUM_UNIQUE_2 ON OBJ_STORE__MTP (UNUM_2_TD, UNUM_2) WHERE UNUM_2_TD IS NOT NULL AND UNUM_2 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__MTP_UTS_UNIQUE_2 ON OBJ_STORE__MTP (UTS_2_TD, UTS_2) WHERE UTS_2_TD IS NOT NULL AND UTS_2 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__MTP_UDBL_UNIQUE_2 ON OBJ_STORE__MTP (UDBL_2_TD, UDBL_2) WHERE UDBL_2_TD IS NOT NULL AND UDBL_2 IS NOT NULL
GO

CREATE INDEX OBJ_STORE__MTP_ISTR_INDEX_1 ON OBJ_STORE__MTP (ISTR_1, ISTR_1_TD)
GO
CREATE INDEX OBJ_STORE__MTP_ISTR_INDEX_2 ON OBJ_STORE__MTP (ISTR_2, ISTR_2_TD)
GO
CREATE INDEX OBJ_STORE__MTP_INUM_INDEX_1 ON OBJ_STORE__MTP (INUM_1, INUM_1_TD)
GO
CREATE INDEX OBJ_STORE__MTP_ITS_INDEX_1 ON OBJ_STORE__MTP (ITS_1, ITS_1_TD)
GO
CREATE INDEX OBJ_STORE__MTP_IDBL_INDEX_1 ON OBJ_STORE__MTP (IDBL_1, IDBL_1_TD)
GO
CREATE INDEX OBJ_STORE__MTP_ISTR_INDEX_3 ON OBJ_STORE__MTP (ISTR_3, ISTR_3_TD)
GO
CREATE INDEX OBJ_STORE__MTP_ISTR_INDEX_4 ON OBJ_STORE__MTP (ISTR_4, ISTR_4_TD)
GO
CREATE INDEX OBJ_STORE__MTP_INUM_INDEX_2 ON OBJ_STORE__MTP (INUM_2, INUM_2_TD)
GO
CREATE INDEX OBJ_STORE__MTP_ITS_INDEX_2 ON OBJ_STORE__MTP (ITS_2, ITS_2_TD)
GO
CREATE INDEX OBJ_STORE__MTP_IDBL_INDEX_2 ON OBJ_STORE__MTP (IDBL_2, IDBL_2_TD)
GO
CREATE INDEX OBJ_STORE__MTP_ISTR_INDEX_5 ON OBJ_STORE__MTP (ISTR_5, ISTR_5_TD)
GO
CREATE INDEX OBJ_STORE__MTP_ISTR_INDEX_6 ON OBJ_STORE__MTP (ISTR_6, ISTR_6_TD)
GO
CREATE INDEX OBJ_STORE__MTP_INUM_INDEX_3 ON OBJ_STORE__MTP (INUM_3, INUM_3_TD)
GO
CREATE INDEX OBJ_STORE__MTP_ITS_INDEX_3 ON OBJ_STORE__MTP (ITS_3, ITS_3_TD)
GO
CREATE INDEX OBJ_STORE__MTP_IDBL_INDEX_3 ON OBJ_STORE__MTP (IDBL_3, IDBL_3_TD)
GO
CREATE INDEX OBJ_STORE__MTP_ISTR_INDEX_7 ON OBJ_STORE__MTP (ISTR_7, ISTR_7_TD)
GO
CREATE INDEX OBJ_STORE__MTP_ISTR_INDEX_8 ON OBJ_STORE__MTP (ISTR_8, ISTR_8_TD)
GO
CREATE INDEX OBJ_STORE__MTP_INUM_INDEX_4 ON OBJ_STORE__MTP (INUM_4, INUM_4_TD)
GO
CREATE INDEX OBJ_STORE__MTP_ITS_INDEX_4 ON OBJ_STORE__MTP (ITS_4, ITS_4_TD)
GO
CREATE INDEX OBJ_STORE__MTP_IDBL_INDEX_4 ON OBJ_STORE__MTP (IDBL_4, IDBL_4_TD)
GO

CREATE INDEX OBJ_STORE__MTP_CRE_USER_INDEX ON OBJ_STORE__MTP (CRE_USER, OBJ_DEF_ID, TENANT_ID)
GO

/* drop/create OBJ_STORE */
DROP TABLE OBJ_STORE__USER
GO

CREATE TABLE OBJ_STORE__USER
(
    TENANT_ID NUMERIC(7,0) NOT NULL,
    OBJ_DEF_ID VARCHAR(128) NOT NULL,
    PG_NO NUMERIC(2,0) NOT NULL,
    OBJ_ID VARCHAR(64) NOT NULL,
    OBJ_VER NUMERIC(10,0) DEFAULT 0 NOT NULL,
    OBJ_DEF_VER NUMERIC(10,0),
    STATUS CHAR(1),
    OBJ_NAME NVARCHAR(256),
    OBJ_DESC NVARCHAR(1024),
    CRE_DATE DATETIME2(3),
    UP_DATE DATETIME2(3),
    S_DATE DATETIME2(3),
    E_DATE DATETIME2(3),
    LOCK_USER VARCHAR(64),
    CRE_USER VARCHAR(64),
    UP_USER VARCHAR(64),
    USTR_1 NVARCHAR(380),
    USTR_1_TD VARCHAR(139),
    UNUM_1 NUMERIC,
    UNUM_1_TD VARCHAR(139),
    UTS_1 DATETIME2(3),
    UTS_1_TD VARCHAR(139),
    UDBL_1 FLOAT,
    UDBL_1_TD VARCHAR(139),
    USTR_2 NVARCHAR(380),
    USTR_2_TD VARCHAR(139),
    UNUM_2 NUMERIC,
    UNUM_2_TD VARCHAR(139),
    UTS_2 DATETIME2(3),
    UTS_2_TD VARCHAR(139),
    UDBL_2 FLOAT,
    UDBL_2_TD VARCHAR(139),
    ISTR_1 NVARCHAR(380),
    ISTR_1_TD VARCHAR(139),
    ISTR_2 NVARCHAR(380),
    ISTR_2_TD VARCHAR(139),
    INUM_1 NUMERIC,
    INUM_1_TD VARCHAR(139),
    ITS_1 DATETIME2(3),
    ITS_1_TD VARCHAR(139),
    IDBL_1 FLOAT,
    IDBL_1_TD VARCHAR(139),
    ISTR_3 NVARCHAR(380),
    ISTR_3_TD VARCHAR(139),
    ISTR_4 NVARCHAR(380),
    ISTR_4_TD VARCHAR(139),
    INUM_2 NUMERIC,
    INUM_2_TD VARCHAR(139),
    ITS_2 DATETIME2(3),
    ITS_2_TD VARCHAR(139),
    IDBL_2 FLOAT,
    IDBL_2_TD VARCHAR(139),
    ISTR_5 NVARCHAR(380),
    ISTR_5_TD VARCHAR(139),
    ISTR_6 NVARCHAR(380),
    ISTR_6_TD VARCHAR(139),
    INUM_3 NUMERIC,
    INUM_3_TD VARCHAR(139),
    ITS_3 DATETIME2(3),
    ITS_3_TD VARCHAR(139),
    IDBL_3 FLOAT,
    IDBL_3_TD VARCHAR(139),
    ISTR_7 NVARCHAR(380),
    ISTR_7_TD VARCHAR(139),
    ISTR_8 NVARCHAR(380),
    ISTR_8_TD VARCHAR(139),
    INUM_4 NUMERIC,
    INUM_4_TD VARCHAR(139),
    ITS_4 DATETIME2(3),
    ITS_4_TD VARCHAR(139),
    IDBL_4 FLOAT,
    IDBL_4_TD VARCHAR(139),
    STR_1 NVARCHAR(4000),
    STR_2 NVARCHAR(4000),
    STR_3 NVARCHAR(4000),
    STR_4 NVARCHAR(4000),
    NUM_1 NUMERIC,
    TS_1 DATETIME2(3),
    DBL_1 FLOAT,
    STR_5 NVARCHAR(4000),
    STR_6 NVARCHAR(4000),
    STR_7 NVARCHAR(4000),
    STR_8 NVARCHAR(4000),
    NUM_2 NUMERIC,
    TS_2 DATETIME2(3),
    DBL_2 FLOAT,
    STR_9 NVARCHAR(4000),
    STR_10 NVARCHAR(4000),
    STR_11 NVARCHAR(4000),
    STR_12 NVARCHAR(4000),
    NUM_3 NUMERIC,
    TS_3 DATETIME2(3),
    DBL_3 FLOAT,
    STR_13 NVARCHAR(4000),
    STR_14 NVARCHAR(4000),
    STR_15 NVARCHAR(4000),
    STR_16 NVARCHAR(4000),
    NUM_4 NUMERIC,
    TS_4 DATETIME2(3),
    DBL_4 FLOAT,
    STR_17 NVARCHAR(4000),
    STR_18 NVARCHAR(4000),
    STR_19 NVARCHAR(4000),
    STR_20 NVARCHAR(4000),
    NUM_5 NUMERIC,
    TS_5 DATETIME2(3),
    DBL_5 FLOAT,
    STR_21 NVARCHAR(4000),
    STR_22 NVARCHAR(4000),
    STR_23 NVARCHAR(4000),
    STR_24 NVARCHAR(4000),
    NUM_6 NUMERIC,
    TS_6 DATETIME2(3),
    DBL_6 FLOAT,
    STR_25 NVARCHAR(4000),
    STR_26 NVARCHAR(4000),
    STR_27 NVARCHAR(4000),
    STR_28 NVARCHAR(4000),
    NUM_7 NUMERIC,
    TS_7 DATETIME2(3),
    DBL_7 FLOAT,
    STR_29 NVARCHAR(4000),
    STR_30 NVARCHAR(4000),
    STR_31 NVARCHAR(4000),
    STR_32 NVARCHAR(4000),
    NUM_8 NUMERIC,
    TS_8 DATETIME2(3),
    DBL_8 FLOAT,
    STR_33 NVARCHAR(4000),
    STR_34 NVARCHAR(4000),
    STR_35 NVARCHAR(4000),
    STR_36 NVARCHAR(4000),
    NUM_9 NUMERIC,
    TS_9 DATETIME2(3),
    DBL_9 FLOAT,
    STR_37 NVARCHAR(4000),
    STR_38 NVARCHAR(4000),
    STR_39 NVARCHAR(4000),
    STR_40 NVARCHAR(4000),
    NUM_10 NUMERIC,
    TS_10 DATETIME2(3),
    DBL_10 FLOAT,
    STR_41 NVARCHAR(4000),
    STR_42 NVARCHAR(4000),
    STR_43 NVARCHAR(4000),
    STR_44 NVARCHAR(4000),
    NUM_11 NUMERIC,
    TS_11 DATETIME2(3),
    DBL_11 FLOAT,
    STR_45 NVARCHAR(4000),
    STR_46 NVARCHAR(4000),
    STR_47 NVARCHAR(4000),
    STR_48 NVARCHAR(4000),
    NUM_12 NUMERIC,
    TS_12 DATETIME2(3),
    DBL_12 FLOAT,
    STR_49 NVARCHAR(4000),
    STR_50 NVARCHAR(4000),
    STR_51 NVARCHAR(4000),
    STR_52 NVARCHAR(4000),
    NUM_13 NUMERIC,
    TS_13 DATETIME2(3),
    DBL_13 FLOAT,
    STR_53 NVARCHAR(4000),
    STR_54 NVARCHAR(4000),
    STR_55 NVARCHAR(4000),
    STR_56 NVARCHAR(4000),
    NUM_14 NUMERIC,
    TS_14 DATETIME2(3),
    DBL_14 FLOAT,
    STR_57 NVARCHAR(4000),
    STR_58 NVARCHAR(4000),
    STR_59 NVARCHAR(4000),
    STR_60 NVARCHAR(4000),
    NUM_15 NUMERIC,
    TS_15 DATETIME2(3),
    DBL_15 FLOAT,
    STR_61 NVARCHAR(4000),
    STR_62 NVARCHAR(4000),
    STR_63 NVARCHAR(4000),
    STR_64 NVARCHAR(4000),
    NUM_16 NUMERIC,
    TS_16 DATETIME2(3),
    DBL_16 FLOAT,
    STR_65 NVARCHAR(4000),
    STR_66 NVARCHAR(4000),
    STR_67 NVARCHAR(4000),
    STR_68 NVARCHAR(4000),
    NUM_17 NUMERIC,
    TS_17 DATETIME2(3),
    DBL_17 FLOAT,
    STR_69 NVARCHAR(4000),
    STR_70 NVARCHAR(4000),
    STR_71 NVARCHAR(4000),
    STR_72 NVARCHAR(4000),
    NUM_18 NUMERIC,
    TS_18 DATETIME2(3),
    DBL_18 FLOAT,
    STR_73 NVARCHAR(4000),
    STR_74 NVARCHAR(4000),
    STR_75 NVARCHAR(4000),
    STR_76 NVARCHAR(4000),
    NUM_19 NUMERIC,
    TS_19 DATETIME2(3),
    DBL_19 FLOAT,
    STR_77 NVARCHAR(4000),
    STR_78 NVARCHAR(4000),
    STR_79 NVARCHAR(4000),
    STR_80 NVARCHAR(4000),
    NUM_20 NUMERIC,
    TS_20 DATETIME2(3),
    DBL_20 FLOAT,
    STR_81 NVARCHAR(4000),
    STR_82 NVARCHAR(4000),
    STR_83 NVARCHAR(4000),
    STR_84 NVARCHAR(4000),
    NUM_21 NUMERIC,
    TS_21 DATETIME2(3),
    DBL_21 FLOAT,
    STR_85 NVARCHAR(4000),
    STR_86 NVARCHAR(4000),
    STR_87 NVARCHAR(4000),
    STR_88 NVARCHAR(4000),
    NUM_22 NUMERIC,
    TS_22 DATETIME2(3),
    DBL_22 FLOAT,
    STR_89 NVARCHAR(4000),
    STR_90 NVARCHAR(4000),
    STR_91 NVARCHAR(4000),
    STR_92 NVARCHAR(4000),
    NUM_23 NUMERIC,
    TS_23 DATETIME2(3),
    DBL_23 FLOAT,
    STR_93 NVARCHAR(4000),
    STR_94 NVARCHAR(4000),
    STR_95 NVARCHAR(4000),
    STR_96 NVARCHAR(4000),
    NUM_24 NUMERIC,
    TS_24 DATETIME2(3),
    DBL_24 FLOAT,
    STR_97 NVARCHAR(4000),
    STR_98 NVARCHAR(4000),
    STR_99 NVARCHAR(4000),
    STR_100 NVARCHAR(4000),
    NUM_25 NUMERIC,
    TS_25 DATETIME2(3),
    DBL_25 FLOAT,
    STR_101 NVARCHAR(4000),
    STR_102 NVARCHAR(4000),
    STR_103 NVARCHAR(4000),
    STR_104 NVARCHAR(4000),
    NUM_26 NUMERIC,
    TS_26 DATETIME2(3),
    DBL_26 FLOAT,
    STR_105 NVARCHAR(4000),
    STR_106 NVARCHAR(4000),
    STR_107 NVARCHAR(4000),
    STR_108 NVARCHAR(4000),
    NUM_27 NUMERIC,
    TS_27 DATETIME2(3),
    DBL_27 FLOAT,
    STR_109 NVARCHAR(4000),
    STR_110 NVARCHAR(4000),
    STR_111 NVARCHAR(4000),
    STR_112 NVARCHAR(4000),
    NUM_28 NUMERIC,
    TS_28 DATETIME2(3),
    DBL_28 FLOAT,
    STR_113 NVARCHAR(4000),
    STR_114 NVARCHAR(4000),
    STR_115 NVARCHAR(4000),
    STR_116 NVARCHAR(4000),
    NUM_29 NUMERIC,
    TS_29 DATETIME2(3),
    DBL_29 FLOAT,
    STR_117 NVARCHAR(4000),
    STR_118 NVARCHAR(4000),
    STR_119 NVARCHAR(4000),
    STR_120 NVARCHAR(4000),
    NUM_30 NUMERIC,
    TS_30 DATETIME2(3),
    DBL_30 FLOAT,
    STR_121 NVARCHAR(4000),
    STR_122 NVARCHAR(4000),
    STR_123 NVARCHAR(4000),
    STR_124 NVARCHAR(4000),
    NUM_31 NUMERIC,
    TS_31 DATETIME2(3),
    DBL_31 FLOAT,
    STR_125 NVARCHAR(4000),
    STR_126 NVARCHAR(4000),
    STR_127 NVARCHAR(4000),
    STR_128 NVARCHAR(4000),
    NUM_32 NUMERIC,
    TS_32 DATETIME2(3),
    DBL_32 FLOAT,
    CONSTRAINT OBJ_STORE__USER_PK PRIMARY KEY (OBJ_DEF_ID, TENANT_ID, OBJ_ID, OBJ_VER, PG_NO)
)
GO

CREATE UNIQUE INDEX OBJ_STORE__USER_USTR_UNIQUE_1 ON OBJ_STORE__USER (USTR_1_TD, USTR_1)  WHERE USTR_1_TD IS NOT NULL AND USTR_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__USER_UNUM_UNIQUE_1 ON OBJ_STORE__USER (UNUM_1_TD, UNUM_1) WHERE UNUM_1_TD IS NOT NULL AND UNUM_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__USER_UTS_UNIQUE_1 ON OBJ_STORE__USER (UTS_1_TD, UTS_1) WHERE UTS_1_TD IS NOT NULL AND UTS_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__USER_UDBL_UNIQUE_1 ON OBJ_STORE__USER (UDBL_1_TD, UDBL_1) WHERE UDBL_1_TD IS NOT NULL AND UDBL_1 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__USER_USTR_UNIQUE_2 ON OBJ_STORE__USER (USTR_2_TD, USTR_2)  WHERE USTR_2_TD IS NOT NULL AND USTR_2 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__USER_UNUM_UNIQUE_2 ON OBJ_STORE__USER (UNUM_2_TD, UNUM_2) WHERE UNUM_2_TD IS NOT NULL AND UNUM_2 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__USER_UTS_UNIQUE_2 ON OBJ_STORE__USER (UTS_2_TD, UTS_2) WHERE UTS_2_TD IS NOT NULL AND UTS_2 IS NOT NULL
GO
CREATE UNIQUE INDEX OBJ_STORE__USER_UDBL_UNIQUE_2 ON OBJ_STORE__USER (UDBL_2_TD, UDBL_2) WHERE UDBL_2_TD IS NOT NULL AND UDBL_2 IS NOT NULL
GO

CREATE INDEX OBJ_STORE__USER_ISTR_INDEX_1 ON OBJ_STORE__USER (ISTR_1, ISTR_1_TD)
GO
CREATE INDEX OBJ_STORE__USER_ISTR_INDEX_2 ON OBJ_STORE__USER (ISTR_2, ISTR_2_TD)
GO
CREATE INDEX OBJ_STORE__USER_INUM_INDEX_1 ON OBJ_STORE__USER (INUM_1, INUM_1_TD)
GO
CREATE INDEX OBJ_STORE__USER_ITS_INDEX_1 ON OBJ_STORE__USER (ITS_1, ITS_1_TD)
GO
CREATE INDEX OBJ_STORE__USER_IDBL_INDEX_1 ON OBJ_STORE__USER (IDBL_1, IDBL_1_TD)
GO
CREATE INDEX OBJ_STORE__USER_ISTR_INDEX_3 ON OBJ_STORE__USER (ISTR_3, ISTR_3_TD)
GO
CREATE INDEX OBJ_STORE__USER_ISTR_INDEX_4 ON OBJ_STORE__USER (ISTR_4, ISTR_4_TD)
GO
CREATE INDEX OBJ_STORE__USER_INUM_INDEX_2 ON OBJ_STORE__USER (INUM_2, INUM_2_TD)
GO
CREATE INDEX OBJ_STORE__USER_ITS_INDEX_2 ON OBJ_STORE__USER (ITS_2, ITS_2_TD)
GO
CREATE INDEX OBJ_STORE__USER_IDBL_INDEX_2 ON OBJ_STORE__USER (IDBL_2, IDBL_2_TD)
GO
CREATE INDEX OBJ_STORE__USER_ISTR_INDEX_5 ON OBJ_STORE__USER (ISTR_5, ISTR_5_TD)
GO
CREATE INDEX OBJ_STORE__USER_ISTR_INDEX_6 ON OBJ_STORE__USER (ISTR_6, ISTR_6_TD)
GO
CREATE INDEX OBJ_STORE__USER_INUM_INDEX_3 ON OBJ_STORE__USER (INUM_3, INUM_3_TD)
GO
CREATE INDEX OBJ_STORE__USER_ITS_INDEX_3 ON OBJ_STORE__USER (ITS_3, ITS_3_TD)
GO
CREATE INDEX OBJ_STORE__USER_IDBL_INDEX_3 ON OBJ_STORE__USER (IDBL_3, IDBL_3_TD)
GO
CREATE INDEX OBJ_STORE__USER_ISTR_INDEX_7 ON OBJ_STORE__USER (ISTR_7, ISTR_7_TD)
GO
CREATE INDEX OBJ_STORE__USER_ISTR_INDEX_8 ON OBJ_STORE__USER (ISTR_8, ISTR_8_TD)
GO
CREATE INDEX OBJ_STORE__USER_INUM_INDEX_4 ON OBJ_STORE__USER (INUM_4, INUM_4_TD)
GO
CREATE INDEX OBJ_STORE__USER_ITS_INDEX_4 ON OBJ_STORE__USER (ITS_4, ITS_4_TD)
GO
CREATE INDEX OBJ_STORE__USER_IDBL_INDEX_4 ON OBJ_STORE__USER (IDBL_4, IDBL_4_TD)
GO

CREATE INDEX OBJ_STORE__USER_CRE_USER_INDEX ON OBJ_STORE__USER (CRE_USER, OBJ_DEF_ID, TENANT_ID)
GO
