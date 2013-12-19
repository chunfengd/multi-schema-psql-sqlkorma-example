(ns data
  (:use
    [korma.db]
    [korma.core]
    [cheshire.core]
    [clojure.java.shell :only [sh]]
    [clojure.java.io])
  (:import [java.io File]))

(def jtoy-id 14914457)
(def roy-id 16954668)
(def becca-id 4061601)
(def nate2-id 1924727874)

(defdb db
  (postgres {:db "chunfeng"}))

(defn create-user-db [id]
  (let [id (str id)]
    (if (not-empty id)
      (do (-> (sh "bash"
                  :in (str "echo -e \"CREATE SCHEMA u" id
                           ";\nSET search_path TO u" id
                           ",public;\" "
                           "| cat - resources/per_user.sql | psql")))))))

(defmacro defdynamicentity
  "dynamic entity replacing: https://github.com/korma/Korma/blob/master/src/korma/core.clj#L647"
  [ent & body]
  `(defn ~ent [id#]
     (let [table-name# (str "u" id# "." ~(name ent))]
       (-> (create-entity table-name#)
           (database db)
           ;; (table (keyword table-name#))
           ~@body))))

(defdynamicentity mytable)

(defn exec-sql [id conn? & sql]
  (let [schema (str "SET search_path TO u" id ", public;")]
    (transaction
     (if (map? conn?)
       (exec-raw conn? schema)
       (exec-raw schema))
     (apply exec-raw conn? sql))))

;; ; all the database stuff is stored here so any class can include and use this
;; (defn db
;;   ([id]
;;      (let [filename (str "dbs/" id ".db")]
;;       (if (.exists (File. filename))
;;        {:subprotocol "sqlite" :classname "org.sqlite.JDBC" :subname filename}
;;        (throw (Exception. (str "file \"" filename "\" doesn't exist!")))))))

;; (defonce users_db
;;   {:subprotocol ""
;;    :classname "org.sqlite.JDBC"
;;    :subname "dbs/users.db"})

;; (defn create-user-db [id]
;;   (if (not (.exists (File. (str "dbs/" id ".db"))))
;;       (do
;;         (-> (sh "bash" :in (str "cd dbs/ && sqlite3 " id ".db < ../resources/per_user.sql"))))
;;       (do
;;         {:exit 0, :out "", :err ""})))


;; (defmacro dynamic-belongs-to
;;   "from https://github.com/korma/Korma/blob/master/src/korma/core.clj#L574"
;;   [ent sub-ent & [opts]]
;;   `(dynamicrel ~ent ~sub-ent :belongs-to ~opts))

;; (defn dynamicrel [ent sub-ent type opts]
;;   (let [var-name (-> sub-ent :name symbol)
;;         cur-ns *ns*
;;         ]
;;     (assoc-in ent [:rel (name var-name)]
;;               (delay
;;                (let [resolved (ns-resolve cur-ns var-name)
;;                      ;sub-ent (when resolved (deref sub-ent))
;;                      ]
;;                  (when-not (map? sub-ent)
;;                    (throw (Exception. (format "Entity used in relationship does not exist: %s" (name var-name)))))
;;                  (create-relation ent sub-ent type opts))))))

;; (defdynamicentity twitter_users
;;   (pk :id)
;;   (table :twitter_users)
;;   (transform (fn [{json :json :as v}]
;;                (if json
;;                  (assoc v :json (cheshire.core/parse-string json true)) v))))


;; (defentity tweets
;;   (pk :id)
;;   (table :tweets)
;;   (database users_db)
;;   (transform (fn [{json :json :as v}]
;;                (if json
;;                  (assoc v :json (cheshire.core/parse-string json true)) v))))


;; (defdynamicentity follow_scores
;;   (pk :id)
;;   (table :follow_scores))

;; (defdynamicentity competitor_followers
;;   (pk :id)
;;   (table :competitor_followers))

;; (defdynamicentity favorites
;;   (pk :id)
;;   (table :favorites))

;; (defdynamicentity followers
;;   (table :followers))

;; (defdynamicentity friends
;;   (table :friends))

;; (defentity phrases
;;   (table :phrases)
;;   (database users_db))

;; (defentity competitors
;;   (table :competitors)
;;   (database users_db))

;; (defentity users
;;   (table :users)
;;   (database users_db)
;;   (transform (fn [{json :json :as v}]
;;                (if json
;;                  (assoc v :json (cheshire.core/parse-string json true)) v))))

;; (defn all_users
;;   ([] (select users  (order :at :desc))))

;; (defn update-json [user-id json]
;;   (update users (set-fields {:json json}) (where {:id user-id})))

;; (defn get-user
;;   "obtain the user map from the database"
;;   [id]
;;   (first (select users (where {:id id}))))

;; (defn get_email
;;   "obtain the email from the database, if no email return nil"
;;   [id]
;;   (when id
;;     (if-let [email (->> (select users (fields :email) (where {:id id}))
;;                         (map :email)
;;                         first)]
;;       (-> email
;;           clojure.string/trim
;;           (#(if (= "" %) nil %))))))

;; (defn insert_fav [tweet_or_fav id p experiment-id]
;;   (insert (favorites id) (values {:user_id id})))
