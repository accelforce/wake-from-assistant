CREATE TABLE device
(
    id                identity,
    device_name       varchar not null,
    mac_address       bigint  not null,
    ip_address        integer not null,
    broadcast_address integer null
);
