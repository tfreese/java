-- h2: set mode MySQL;
-- hsqldb: SET DATABASE SQL SYNTAX MYS TRUE;
-- or the equivalent URL property sql.syntax_mys=true
-- for "INSERT INTO ... ON DUPLICATE KEY UPDATE" Syntax

drop table if exists MESSAGE;
drop table if exists TOKEN;
drop table if exists MESSAGE_TOKEN;

---------------------------------------------------------------------------------------------------
create table MESSAGE (
    MESSAGE_ID BIGINT NOT NULL,
    FOLDER_NAME VARCHAR(100) NOT NULL,
    SUBJECT VARCHAR(255) NOT NULL,
    IS_SPAM BOOLEAN NOT NULL,
    RECEIVED_DATE TIMESTAMP NOT NULL,
    SENDER VARCHAR(100)
);

comment on table MESSAGE is 'Einzelne Mails';
comment on column MESSAGE.IS_SPAM is 'SPAM oder HAM';

alter table MESSAGE add constraint MESSAGE_PK primary key (MESSAGE_ID);
alter table MESSAGE add constraint MESSAGE_CHK check (MESSAGE_ID > 0);
alter table MESSAGE add constraint MESSAGE_UQ unique (MESSAGE_ID, FOLDER_NAME);

---------------------------------------------------------------------------------------------------
create table TOKEN (
    VALUE VARCHAR(50) NOT NULL,
    HAM_COUNT INTEGER NOT NULL,
    SPAM_COUNT INTEGER NOT NULL
);

comment on table TOKEN is 'Tabelle aller vorhandenen Values';
comment on column TOKEN.HAM_COUNT is 'Anzahl Vorkommen in HAM-Mails';
comment on column TOKEN.SPAM_COUNT is 'Anzahl Vorkommen in SPAM-Mails';

alter table TOKEN add constraint TOKEN_PK primary key (VALUE);
alter table TOKEN add constraint TOKEN_TOKEN_CHK check (length(trim(VALUE)) > 0);
alter table TOKEN add constraint TOKEN_HAM_CHK check (HAM_COUNT >= 0);
alter table TOKEN add constraint TOKEN_SPAM_CHK check (SPAM_COUNT >= 0);

---------------------------------------------------------------------------------------------------
create table MESSAGE_TOKEN (
    MESSAGE_ID BIGINT NOT NULL,
    VALUE VARCHAR(50) NOT NULL,
    COUNT INTEGER NOT NULL
);

comment on table MESSAGE_TOKEN is 'Zu einer Mail gehörenden Values';
comment on column MESSAGE_TOKEN.COUNT is 'Häufigkeit des Values in der Mail';

alter table MESSAGE_TOKEN add constraint MT_MESSAGE_ID_FK foreign key (MESSAGE_ID) references MESSAGE (MESSAGE_ID);
alter table MESSAGE_TOKEN add constraint MT_TOKEN_FK foreign key (VALUE) references TOKEN (VALUE);
alter table MESSAGE_TOKEN add constraint MT_MESSAGETOKEN_UQ unique (MESSAGE_ID, VALUE);
alter table MESSAGE_TOKEN add constraint MT_MESSAGE_ID_CHK check (MESSAGE_ID > 0);
alter table MESSAGE_TOKEN add constraint MT_TOKEN_CHK check (length(trim(VALUE)) > 0);
create index MT_MESSAGE_KEY on MESSAGE_TOKEN (MESSAGE_ID);
