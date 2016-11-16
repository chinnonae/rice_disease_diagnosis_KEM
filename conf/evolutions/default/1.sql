# Issue SCHEMA

# --- !Ups
create table issue (
  id  SERIAL PRIMARY KEY ,
  image_source  TEXT NOT NULL ,
  color INTEGER NOT NULL ,
  shape INTEGER NOT NULL ,
  part INTEGER NOT NULL ,
  additional_info TEXT ,
  answer TEXT
)

# --- !Downs
DROP TABLE issue;