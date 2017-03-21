alter table sentence_collection add column type integer;
alter table sentence_collection add column title text;
alter table language add column done_count integer;

update sentence_collection set custom=0;
update language set done_count=0;
