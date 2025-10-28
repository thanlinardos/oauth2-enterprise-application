insert into roles (role, privilege_lvl)
values ('ROLE_GUEST', 4),
       ('ROLE_USER', 3),
       ('ROLE_MANAGER', 2),
       ('ROLE_ADMIN', 1),
       ('ROLE_OWNER', 0);

insert into authorities (name, access_type, uri)
values ('READ_ACCOUNT', 'READ', '/myAccount'),
       ('READ_CARDS', 'READ', '/myCards'),
       ('READ_LOANS', 'READ', '/myLoans'),
       ('READ_BALANCE', 'READ', '/myBalance'),
       ('READ_DASHBOARD', 'READ', '/dashboard'),
       ('READ_USER', 'READ', '/user'),
       ('READ_LOGOUT', 'READ', '/logout'),
       ('READ_HOLIDAYS', 'READ', '/holidays/**');

INSERT INTO role_authorities (role_id, authority_id)
select roles.id, authorities.id
from roles,
     authorities
where role = 'ROLE_GUEST';

insert into authorities (name, access_type, uri)
values ('READ_OWNER', 'READ', '/owner'),
       ('READ_CUSTOMERS_USERNAME', 'READ', '/customers/{username}');


INSERT INTO role_authorities (role_id, authority_id)
select roles.id, authorities.id
from roles,
     authorities
where role = 'ROLE_USER';

insert into authorities (name, access_type, uri)
values ('READ_CUSTOMERS', 'READ', '/customers'),
       ('CREATE_CUSTOMER', 'CREATE', '/user');

INSERT INTO role_authorities (role_id, authority_id)
select roles.id, authorities.id
from roles,
     authorities
where role = 'ROLE_MANAGER';

insert into authorities (name, access_type, uri)
values ('ALL_ADMIN', 'ALL', '/admin/**'),
       ('READ_CLIENT', 'READ', '/client');

INSERT INTO role_authorities (role_id, authority_id)
select roles.id, authorities.id
from roles,
     authorities
where role = 'ROLE_ADMIN';

insert into authorities (name, access_type, uri)
values ('READ_CLIENTS', 'READ', '/clients'),
       ('READ_CLIENTS_NAME', 'READ', '/clients/{name}');

INSERT INTO role_authorities (role_id, authority_id)
select roles.id, authorities.id
from roles,
     authorities
where role = 'ROLE_OWNER';