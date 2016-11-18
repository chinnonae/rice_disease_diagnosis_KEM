# --- !Ups
create table issue (
  id  SERIAL PRIMARY KEY ,
  image_source  TEXT NOT NULL ,
  color INTEGER NOT NULL ,
  shape INTEGER NOT NULL ,
  part INTEGER NOT NULL ,
  additional_info TEXT ,
  answer TEXT
);

create table disease (
  id SERIAL PRIMARY KEY ,
  name VARCHAR(255) NOT NULL ,
  what_it_does TEXT NOT NULL ,
  why_and_where_it_occurs TEXT NOT NULL ,
  how_to_identify TEXT NOT NULL
);