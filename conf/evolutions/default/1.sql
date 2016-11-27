# --- !Ups
create table disease (
  id SERIAL PRIMARY KEY ,
  name VARCHAR(255) NOT NULL ,
  what_it_does TEXT NOT NULL ,
  why_and_where_it_occurs TEXT NOT NULL ,
  how_to_identify TEXT NOT NULL ,
  image_source TEXT NOT NULL
);

create table member (
  id SERIAL PRIMARY KEY ,
  username VARCHAR(20) UNIQUE UNIQUE ,
  hashed_password VARCHAR(255) NOT NULL ,
  email VARCHAR(255) UNIQUE NOT NULL ,
  rice_variety VARCHAR(255) NOT NULL ,
  latitude DOUBLE PRECISION NOT NULL ,
  longtitude DOUBLE PRECISION NOT NULL
);

create table issue (
  id  SERIAL PRIMARY KEY ,
  image_source  TEXT NOT NULL ,
  color INTEGER NOT NULL ,
  shape INTEGER NOT NULL ,
  part INTEGER NOT NULL ,
  additional_info TEXT ,
  answer TEXT,
  user_id INTEGER REFERENCES member(id)
);