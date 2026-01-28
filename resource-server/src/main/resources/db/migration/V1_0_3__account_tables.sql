DROP PROCEDURE IF EXISTS createAccountTables;

DELIMITER //

CREATE PROCEDURE createAccountTables()
BEGIN

    DECLARE v_count INT;

    SELECT COUNT(*) INTO v_count FROM information_schema.TABLES where TABLE_SCHEMA = 'test' and TABLE_NAME = 'account';
    IF v_count = 0 THEN
        CREATE TABLE if not exists `account`
        (
            `id`             int          NOT NULL AUTO_INCREMENT primary key,
            `owner_id`       int          NOT NULL,
            `account_number` int          NOT NULL,
            `account_type`   varchar(100) NOT NULL,
            `branch_address` varchar(200) NOT NULL,
            created_at       datetime(6)    not null default current_timestamp(6),
            created_by       varchar(55)  not null default 'system',
            updated_at       datetime(6)    not null default current_timestamp(6) on update current_timestamp(6),
            updated_by       varchar(55)  not null default 'system',
            KEY `owner_id` (`owner_id`),
            CONSTRAINT `account_owner_fk` FOREIGN KEY (`owner_id`) REFERENCES `owner` (`id`)
        );

        CREATE TABLE if not exists `account_transaction`
        (
            `id`                  int          NOT NULL AUTO_INCREMENT primary key,
            `transaction_id`      varchar(200) NOT NULL,
            `account_id`          int          NOT NULL,
            `transaction_dt`       datetime(6)    not null default current_timestamp(6),
            `transaction_summary` varchar(200) NOT NULL,
            `transaction_type`    varchar(100) NOT NULL,
            `transaction_amt`     int          NOT NULL,
            `closing_balance`     int          NOT NULL,
            created_at             datetime(6)    not null default current_timestamp(6),
            created_by            varchar(55)  not null default 'system',
            updated_at             datetime(6)    not null default current_timestamp(6) on update current_timestamp(6),
            updated_by            varchar(55)  not null default 'system',
            KEY `account_id` (`account_id`),
            CONSTRAINT `accounts_account_transaction_fk` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE
        );

        CREATE TABLE if not exists `loans`
        (
            `id`                 int          NOT NULL AUTO_INCREMENT primary key,
            `owner_id`           int          NOT NULL,
            `start_dt`           date         NOT NULL,
            `loan_type`          varchar(100) NOT NULL,
            `total_loan`         int          NOT NULL,
            `amount_paid`        int          NOT NULL,
            `outstanding_amount` int          NOT NULL,
            created_at           datetime(6)    not null default current_timestamp(6),
            created_by           varchar(55)  not null default 'system',
            updated_at           datetime(6)    not null default current_timestamp(6) on update current_timestamp(6),
            updated_by           varchar(55)  not null default 'system'
        );

        CREATE TABLE if not exists `cards`
        (
            `id`               int          NOT NULL AUTO_INCREMENT primary key,
            `card_number`      varchar(100) NOT NULL,
            `account_id`       int          NOT NULL,
            `card_type`        varchar(100) NOT NULL,
            `total_limit`      int          NOT NULL,
            `amount_used`      int          NOT NULL,
            `available_amount` int          NOT NULL,
            created_at         datetime(6)    not null default current_timestamp(6),
            created_by         varchar(55)  not null default 'system',
            updated_at         datetime(6)    not null default current_timestamp(6) on update current_timestamp(6),
            updated_by         varchar(55)  not null default 'system',
            KEY `account_id` (`account_id`),
            CONSTRAINT `accounts_cards_fk` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE
        );


        CREATE TABLE if not exists `notice_details`
        (
            `id`             int          NOT NULL AUTO_INCREMENT primary key,
            `notice_summary` varchar(200) charset utf8mb4 NOT NULL,
            `notice_details` varchar(500) charset utf8mb4 NOT NULL,
            `notic_beg_dt`   date         NOT NULL,
            `notic_end_dt`   date                  DEFAULT NULL,
            created_at       datetime(6)    not null default current_timestamp(6),
            created_by       varchar(55)  not null default 'system',
            updated_at       datetime(6)    not null default current_timestamp(6) on update current_timestamp(6),
            updated_by       varchar(55)  not null default 'system'
        );

        CREATE TABLE if not exists `contact_messages`
        (
            `id`            int           NOT NULL AUTO_INCREMENT primary key,
            `contact_id`    varchar(50)   NOT NULL,
            `contact_name`  varchar(50)   NOT NULL,
            `contact_email` varchar(100)  NOT NULL,
            `subject`       varchar(500)  charset utf8mb4 NOT NULL,
            `message`       varchar(2000) charset utf8mb4 NOT NULL,
            created_at       datetime(6)     not null default current_timestamp(6),
            created_by      varchar(55)   not null default 'system',
            updated_at       datetime(6)     not null default current_timestamp(6) on update current_timestamp(6),
            updated_by      varchar(55)   not null default 'system'
        );

    END IF;
END;
//

DELIMITER ;

CALL createAccountTables();
DROP PROCEDURE IF EXISTS createAccountTables;