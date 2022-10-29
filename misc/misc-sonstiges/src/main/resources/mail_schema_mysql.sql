-- create sequence MESSAGE_SEQ as bigint start with 1 increment by 1
-- create unique index MESSAGE_TOKEN_IDX on MESSAGE_TOKEN (MESSAGE_ID ASC, TOKEN ASC)
-- create index MESSAGE_IDX on MESSAGE_TOKEN (MESSAGE_ID ASC)
-- create database CONFLUENCE character set utf8 collate utf8_bin;
-- ENGINE=INNODB DEFAULT CHARSET=utf8;

--SET AUTOCOMMIT = 0;

--create database MAILS character set = 'utf8' collate = 'utf8_general_ci';

drop table if exists MESSAGE cascade;
drop table if exists TOKEN cascade;
drop table if exists MESSAGE_TOKEN cascade;

---------------------------------------------------------------------------------------------------
create table MESSAGE (
    MESSAGE_ID VARCHAR(255) NOT NULL,
    FOLDER_NAME VARCHAR(100) NOT NULL,
    SUBJECT VARCHAR(255) NOT NULL,
    IS_SPAM BOOLEAN NOT NULL,
    RECEIVED_DATE TIMESTAMP NOT NULL,
    SENDER VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

alter table MESSAGE comment 'Einzelne Mails';
--comment on column MESSAGE.IS_SPAM is 'SPAM oder HAM'; -- Column Comments werden von mariadb/mysql nicht unterstützt.

alter table MESSAGE add constraint MESSAGE_PK primary key (MESSAGE_ID);
alter table MESSAGE add constraint MESSAGE_CHK check (MESSAGE_ID > 0);
alter table MESSAGE add constraint MESSAGE_UQ unique (MESSAGE_ID, FOLDER_NAME);

---------------------------------------------------------------------------------------------------
create table TOKEN (
    VALUE VARCHAR(50) NOT NULL,
    HAM_COUNT INTEGER NOT NULL,
    SPAM_COUNT INTEGER NOT NULL
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

alter table TOKEN comment 'Tabelle aller vorhandenen Values';
--comment on column TOKEN.HAM_COUNT is 'Anzahl Vorkommen in HAM-Mails';
--comment on column TOKEN.SPAM_COUNT is 'Anzahl Vorkommen in SPAM-Mails';

alter table TOKEN add constraint TOKEN_PK primary key (VALUE);
alter table TOKEN add constraint TOKEN_TOKEN_CHK check (length(trim(VALUE)) > 0);
alter table TOKEN add constraint TOKEN_HAM_CHK check (HAM_COUNT >= 0);
alter table TOKEN add constraint TOKEN_SPAM_CHK check (SPAM_COUNT >= 0);

---------------------------------------------------------------------------------------------------
create table MESSAGE_TOKEN (
    MESSAGE_ID VARCHAR(255) NOT NULL,
    VALUE VARCHAR(50) NOT NULL,
    COUNT INTEGER NOT NULL
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;

alter table MESSAGE_TOKEN comment 'Zu einer Mail gehörenden Values';
--comment on column MESSAGE_TOKEN.COUNT is 'Häufigkeit des Values in der Mail';

alter table MESSAGE_TOKEN add constraint MT_MESSAGE_ID_FK foreign key (MESSAGE_ID) references MESSAGE (MESSAGE_ID);
alter table MESSAGE_TOKEN add constraint MT_TOKEN_FK foreign key (VALUE) references TOKEN (VALUE);
alter table MESSAGE_TOKEN add constraint MT_MESSAGETOKEN_UQ unique (MESSAGE_ID, VALUE);
alter table MESSAGE_TOKEN add constraint MT_MESSAGE_ID_CHK check (MESSAGE_ID > 0);
alter table MESSAGE_TOKEN add constraint MT_TOKEN_CHK check (length(trim(VALUE)) > 0);
create index MT_MESSAGE_KEY on MESSAGE_TOKEN (MESSAGE_ID);
