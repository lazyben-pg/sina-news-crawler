create table news
(
id bigint primary key auto_increment,
title text,
content text,
url varchar(500),
created_at timestamp,
modified_at timestamp
);

create table link_already_processed (link varchar(500));

create table link_to_be_processed (link varchar(500));
