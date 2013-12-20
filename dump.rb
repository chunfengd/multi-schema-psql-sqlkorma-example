
per_user_table = "per_user_new.sql"
user_table = "users_new.sql"
@dump_path = "/tmp"
@psql = "psql"

def copy(tablename, columns, f, id)
  `sqlite3 -csv -noheader #{f} "select #{columns} from #{tablename}" | grep -v '^$' > #{@dump_path}/#{id}-#{tablename}.csv`
  `echo "COPY u#{id}.#{tablename} (#{columns}) FROM '#{@dump_path}/#{id}-#{tablename}.csv' DELIMITER ',' CSV;" | #{@psql}`
end

def copyuser(tablename, columns, f)
  `sqlite3 -csv -noheader #{f} "select #{columns} from #{tablename}" | grep -v '^$' > #{@dump_path}/#{tablename}.csv`
  `echo "COPY #{tablename} (#{columns}) FROM '#{@dump_path}/#{tablename}.csv' DELIMITER ',' CSV;" | #{@psql}`
end

fs = Dir["*.db"]

fs.each do |f|
  id = f.gsub(/(.*)\.db/, '\1')
  if id.match(/[0-9]+/)
    puts id
    # create table
    `echo "DROP SCHEMA IF EXISTS u#{id} CASCADE;CREATE SCHEMA u#{id};\nSET search_path TO u#{id},public;" | cat - #{per_user_table} | #{@psql}`

    # twitter_users
    columns = "id, at, followed_at, followed_date, unfollowed_at, unfollowed_date, flag"
    copy("twitter_users", columns, f, id)

    # competitor_followers
    columns = "competitor_id, follower_id, at"
    copy("competitor_followers", columns, f, id)

    # followers
    columns = "id, at"
    copy("followers", columns, f, id)

    # friends
    columns = "id, at, by_us, unfriended_at"
    copy("friends", columns, f, id)

    # follow_scores
    columns = "id, model_score, at"
    copy("follow_scores", columns, f, id)
  elsif (id == "users")
    puts id
    `echo "drop table if exists users; drop table if exists phrases; drop table if exists competitors" | #{@psql}`
    `cat #{user_table} | #{@psql}`

    # users
    columns = "id, referred_from_id, at, status, screen_name, email, usertoken, usersecret, apptoken, appsecret, json, daily_follow_limit, friends_followers_ratio, unfollow_days"
    copyuser("users", columns, f)

    # phrases
    columns = "phrase, user_id"
    copyuser("phrases", columns, f)

    #competitors
    columns = "login, twitter_user_id, profile_created_at, at, json, user_id"
    copyuser("competitors", columns, f)
  end
end
