(ns clojure_users_api.db
  (:require [com.stuartsierra.component :as component]
            [next.jdbc.connection :as conn])
  (:import [liquibase.resource ClassLoaderResourceAccessor]
           [liquibase.database DatabaseFactory]
           [liquibase Liquibase]
           [liquibase.database.jvm JdbcConnection]
           [com.zaxxer.hikari HikariDataSource]))

(def db-spec
  {:dbtype "postgresql"
   :host "localhost"
   :port 5432
   :dbname "usersdb"
   :user (System/getenv "DB_USER")
   :password (System/getenv "DB_PASSWORD")})

(defn run-liquibase! [^javax.sql.DataSource datasource]
  (let [conn (.getConnection datasource)
        db (DatabaseFactory/getInstance
              (.findCorrectDatabaseImplementation (JdbcConnection. conn)))
        liquibase (Liquibase. "db/changelog/changelog.xml" (ClassLoaderResourceAccessor.) db)]
    (.update liquibase (String. "main"))))

(defrecord DB []
  component/Lifecycle
  (start [this]
    (let [datasource (conn/->pool HikariDataSource db-spec)]
      (run-liquibase! datasource)
      (assoc this :datasource datasource)))
  (stop [this]
    (when-let [^javax.sql.DataSource ds (:datasource this)]
      (.close ds))
    (dissoc this :datasource)))

(defn new-db []
  (map->DB {}))