DROP PROCEDURE IF EXISTS createUserAndRoleTables;

DELIMITER //

CREATE PROCEDURE createUserAndRoleTables()
BEGIN

    DECLARE v_count INT;

    SELECT COUNT(*) INTO v_count FROM information_schema.TABLES where TABLE_SCHEMA = 'test' and TABLE_NAME = 'owner';
    IF v_count = 0 THEN
        SET FOREIGN_KEY_CHECKS = 0;

        create table if not exists `owner`
        (
            id              int          not null auto_increment primary key,
            uuid            varchar(37)  not null unique,
            name            varchar(55)  not null unique,
            type            varchar(55)  not null,
            customer_id     int,
            client_id       int,
            privilege_level int unsigned not null,
            created_at      timestamp    not null default current_timestamp,
            created_by      varchar(55)  not null default 'system',
            updated_at      timestamp    not null default current_timestamp on update current_timestamp,
            updated_by      varchar(55)  not null default 'system',
            KEY `fk_customer_owner_idx` (customer_id),
            CONSTRAINT `fk_customer_owner` FOREIGN KEY (customer_id)
                REFERENCES `customer` (id),
            KEY `fk_client_owner_idx` (client_id),
            CONSTRAINT `fk_client_owner` FOREIGN KEY (client_id)
                REFERENCES `client` (id)
        ) ENGINE = InnoDB
          DEFAULT CHARSET = latin1;

        create table if not exists `client`
        (
            id                 int         not null auto_increment primary key,
            service_account_id varchar(37),
            name               varchar(55) not null unique,
            category           varchar(55) not null,
            created_at         timestamp   not null default current_timestamp,
            created_by         varchar(55) not null default 'system',
            updated_at         timestamp   not null default current_timestamp on update current_timestamp,
            updated_by         varchar(55) not null default 'system'
        ) ENGINE = InnoDB
          DEFAULT CHARSET = latin1;

        create table if not exists `customer`
        (
            id                      int          not null auto_increment primary key,
            username                varchar(255) not null unique,
            mobile_number           varchar(13),
            email                   varchar(255) not null unique,
            first_name              varchar(110),
            last_name               varchar(110),
            enabled                 boolean      not null,
            account_non_expired     boolean      not null default true,
            account_non_locked      boolean      not null default true,
            credentials_non_expired boolean      not null default true,
            created_at              timestamp    not null default current_timestamp,
            created_by              varchar(55)  not null default 'system',
            updated_at              timestamp    not null default current_timestamp on update current_timestamp,
            updated_by              varchar(55)  not null default 'system'
        ) ENGINE = InnoDB
          DEFAULT CHARSET = latin1;

        CREATE TABLE if not exists `roles`
        (
            id            int         not null auto_increment primary key,
            role          varchar(50) not null,
            privilege_lvl int         not null default 2147483647,
            UNIQUE (role)
        ) ENGINE = InnoDB
          DEFAULT CHARSET = latin1;

        create table if not exists owner_roles
        (
            owner_id int not null,
            role_id  int not null,
            PRIMARY KEY (owner_id, role_id),
            KEY `fk_owner_owner_roles_idx` (owner_id),
            CONSTRAINT `fk_owner_owner_roles` FOREIGN KEY (owner_id)
                REFERENCES `owner` (id),
            KEY `fk_role_owner_roles_idx` (role_id),
            CONSTRAINT `fk_role_owner_roles` FOREIGN KEY (role_id)
                REFERENCES `roles` (id)
        );

        CREATE TABLE if not exists `role_authorities`
        (
            role_id      int not null,
            authority_id int not null,
            PRIMARY KEY (role_id, authority_id),
            KEY `fk_role_role_authorities_idx` (role_id),
            CONSTRAINT `fk_role_role_authorities` FOREIGN KEY (role_id)
                REFERENCES `roles` (id),
            KEY `fk_authority_role_authorities_idx` (authority_id),
            CONSTRAINT `fk_authority_role_authorities` FOREIGN KEY (authority_id)
                REFERENCES `authorities` (id)
        ) ENGINE = InnoDB
          DEFAULT CHARSET = latin1;

        CREATE TABLE if not exists `authorities`
        (
            id          int                                                         not null auto_increment primary key,
            name        varchar(50)                                                 not null,
            access_type enum ('READ', 'UPDATE', 'CREATE', 'PATCH', 'DELETE', 'ALL') not null,
            uri         varchar(255)                                                not null,
            expression  varchar(255),
            UNIQUE (name)
        ) ENGINE = InnoDB
          DEFAULT CHARSET = latin1;

        CREATE TABLE if not exists `task_run`
        (
            id   int         not null auto_increment primary key,
            name varchar(50) not null,
            time timestamp,
            UNIQUE (name)
        ) ENGINE = InnoDB
          DEFAULT CHARSET = latin1;

        SET FOREIGN_KEY_CHECKS = 1;

    END IF;
END;
//

DELIMITER ;

CALL createUserAndRoleTables();
DROP PROCEDURE IF EXISTS createUserAndRoleTables;