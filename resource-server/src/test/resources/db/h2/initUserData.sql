SET REFERENTIAL_INTEGRITY FALSE;

INSERT INTO `CLIENT` (id, name, category, service_account_id)
VALUES (1, 'eazypublicclient', 'AUTH_CODE_PKCE', null),
       (2, 'eazybankapi', 'CLIENT_CRED', '79247510-00eb-4665-9963-d4ac4d77e1f1');

INSERT INTO `CUSTOMER` (username, mobile_number, email, enabled)
VALUES ('user', '+301234567890', 'user@email.com', 1),
       ('admin', '+301234567890', 'admin@email.com', 1),
       ('manager', '+301234567890', 'manager@email.com', 1),
       ('owner', '+301234567890', 'owner@email.com', 1),
       ('guest', '+301234567890', 'guest@email.com', 1);

INSERT INTO `OWNER` (id, uuid, name, type, customer_id, client_id, privilege_level)
VALUES (1, '4348cfa4-11c9-4cf7-ba26-cf7d52df3b15', 'eazypublicclient', 'CLIENT', null,  1, 1),
       (2, '25601bfd-1dae-4fc5-a3ec-89b5cf585ce5', 'eazybankapi', 'CLIENT', null,  2, 0),
       (3, 'f3e9ad83-6f1f-4af2-9d28-e5e21160b9f5', 'user@email.com', 'CUSTOMER', 1, null, 3),
       (4, 'eb9e2f08-b611-4eb0-b014-4c755477ee3e', 'admin@email.com', 'CUSTOMER',  2, null, 1),
       (5, 'a00b13fd-fd10-4645-97b4-ee54d003e367', 'manager@email.com', 'CUSTOMER',  3, null, 2),
       (6, '04f8c014-8c0b-4912-82ef-10df1f037859', 'owner@email.com', 'CUSTOMER',  4, null, 0),
       (7, 'fe4f3015-e41b-45b9-8a10-49bd20edfa11', 'guest@email.com', 'CUSTOMER',  5, null, 4);

insert into owner_roles (owner_id, role_id)
select id, (select id from roles where role = 'ROLE_GUEST')
from `OWNER`
where name = 'ROLE_GUEST';

insert into owner_roles (owner_id, role_id)
select id, (select id from roles where role = 'ROLE_USER')
from `OWNER`
where name in ('user@email.com', 'admin@email.com', 'manager@email.com', 'eazypublicclient', 'eazybankapi', 'owner@email.com');

insert into owner_roles (owner_id, role_id)
select id, (select id from roles where role = 'ROLE_ADMIN')
from `OWNER`
where name = 'admin@email.com'
   or name = 'eazybankapi'
   or name = 'eazypublicclient'
   or name = 'owner@email.com';

insert into owner_roles (owner_id, role_id)
select id, (select id from roles where role = 'ROLE_MANAGER')
from `OWNER`
where name = 'manager@email.com'
   or name = 'admin@email.com'
   or name = 'eazybankapi'
   or name = 'eazypublicclient'
   or name = 'owner@email.com';

insert into owner_roles (owner_id, role_id)
select id, (select id from roles where role = 'ROLE_OWNER')
from `OWNER`
where name = 'owner@email.com' or name = 'eazybankapi';

SET REFERENTIAL_INTEGRITY TRUE;