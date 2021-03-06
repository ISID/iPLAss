DROP TABLE LOB_STORE CASCADE CONSTRAINT;
CREATE TABLE "LOB_STORE"
 (	"TENANT_ID" NUMBER(7,0),
	"LOB_DATA_ID" NUMBER(16,0),
	"CRE_DATE" TIMESTAMP (3),
	"REF_COUNT" NUMBER(10,0),
	"B_DATA" BLOB,
	"LOB_SIZE" NUMBER(16,0),
    CONSTRAINT "LOB_STORE_PK" PRIMARY KEY ("TENANT_ID", "LOB_DATA_ID") USING INDEX LOCAL
 )
 LOB(B_DATA) STORE AS SECUREFILE(
    -- TABLESPACE LOBTBS
    -- COMPRESS MEDIUM
    NOCACHE
    DISABLE STORAGE IN ROW
   )
    PARTITION BY RANGE (TENANT_ID) INTERVAL(1)
(
    PARTITION LOB_STORE_0 VALUES LESS THAN (1)
)
;
