CREATE TABLE users(
id INTEGER PRIMARY KEY,
referred_from_id INTEGER,
at INTEGER,
status INTEGER,
screen_name TEXT,
email TEXT,
usertoken TEXT,
usersecret TEXT,
apptoken TEXT,
appsecret TEXT,
json TEXT,
daily_follow_limit INTEGER,
friends_followers_ratio FLOAT,
unfollow_days INTEGER
);

CREATE TABLE phrases(
phrase TEXT,
user_id INTEGER,
UNIQUE(user_id,phrase)
);

CREATE TABLE competitors(
login TEXT,
twitter_user_id INTEGER,
profile_created_at INTEGER, 
at INTEGER,
json TEXT,
user_id INTEGER,
UNIQUE(user_id,twitter_user_id)
);

