DROP PROCEDURE IF EXISTS addEventTables;

DELIMITER //

CREATE PROCEDURE addEventTables()
BEGIN

    DECLARE v_count INT;

    SELECT COUNT(*) INTO v_count FROM information_schema.TABLES where TABLE_SCHEMA = 'test' and TABLE_NAME = 'keycloak_event';
    IF v_count = 0 THEN
        SET FOREIGN_KEY_CHECKS = 0;

        create table if not exists `keycloak_event`
        (
            id        int         not null auto_increment primary key,
            uuid      varchar(37) not null,
            time      datetime(6)   not null,
            status    varchar(55) not null,
            realm_id  varchar(37) not null,
            error     varchar(500),
            type      varchar(55) not null,
            client_id varchar(37) not null,
            user_id   varchar(37) not null
        ) ENGINE = InnoDB
          DEFAULT CHARSET = latin1;

        create table if not exists `keycloak_event_details`
        (
            id       int         not null auto_increment primary key,
            name     varchar(55) not null,
            value    varchar(500),
            event_id int         not null,
            KEY `fk_keycloak_event_keycloak_event_details_idx` (event_id),
            CONSTRAINT `fk_keycloak_event_keycloak_event_details_idx` FOREIGN KEY (event_id)
                REFERENCES `keycloak_event` (id)
        ) ENGINE = InnoDB
          DEFAULT CHARSET = latin1;

        create table if not exists `keycloak_admin_event`
        (
            id             int           not null auto_increment primary key,
            uuid           varchar(37)   not null,
            time           datetime(6)     not null,
            status         varchar(55)   not null,
            realm_id       varchar(37)   not null,
            error          varchar(500),
            client_id      varchar(37),
            user_id        varchar(37),
            operation_type varchar(55)   not null,
            resource_type  varchar(55)   not null,
            resource_path  varchar(2000) not null
        ) ENGINE = InnoDB
          DEFAULT CHARSET = latin1;

        create table if not exists `keycloak_role`
        (
            id             int         not null auto_increment primary key,
            uuid           varchar(37) not null,
            admin_event_id int         not null,
            name           varchar(55) not null,
            client_role    bool        not null,
            KEY `fk_keycloak_admin_event_keycloak_role_idx` (admin_event_id),
            CONSTRAINT `fk_keycloak_admin_event_keycloak_role_idx` FOREIGN KEY (admin_event_id)
                REFERENCES `keycloak_event` (id)
        ) ENGINE = InnoDB
          DEFAULT CHARSET = latin1;

        SET FOREIGN_KEY_CHECKS = 1;

    END IF;
END;
//

DELIMITER ;

CALL addEventTables();
DROP PROCEDURE IF EXISTS addEventTables;