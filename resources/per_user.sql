CREATE TABLE twitter_users(
id INTEGER PRIMARY KEY,
json TEXT,
at INTEGER,
followed_at INTEGER,
followed_date INTEGER,
unfollowed_at INTEGER,
unfollowed_date INTEGER,
flag INTEGER -- nil or 0: not followed, 1: followed, -1: rejected
);
create index at_index on twitter_users(at);
create index followed_at_index on twitter_users(followed_at);
create index followed_date_index on twitter_users(followed_date);
create index unfollowed_at_index on twitter_users(unfollowed_at);
create index unfollowed_date_index on twitter_users(unfollowed_date);
