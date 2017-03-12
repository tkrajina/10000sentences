alter table sentence_collection add column custom integer;
alter table sentence_collection add column title text;
update sentence_collection set custom=0;

