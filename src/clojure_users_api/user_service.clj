(ns clojure_users_api.user_service
  (:require [com.stuartsierra.component :as component]
            [next.jdbc :as jdbc]))

(defrecord UserService [db]
  component/Lifecycle
  (start [this]
    (assoc this
           :get-user
           (fn [id]
             (let [ds (:datasource db)
                   query ["SELECT id, name FROM users WHERE id = ?" (parse-long id)]
                   result (jdbc/execute-one! ds query)]
               result))))
  (stop [this] (dissoc this :get-user)))

(defn new-user-service []
  (map->UserService {}))


System.out.println(teste)
