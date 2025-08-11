(ns clojure-users-api.core
  (:gen-class)
  (:require
   [clojure_users_api.handler :refer [make-handler]]
   [clojure_users_api.db :refer [new-db]]
   [clojure_users_api.user_service :refer [new-user-service]]
   [com.stuartsierra.component :as component]
   [ring.adapter.jetty :refer [run-jetty]]))

(defrecord WebServer [handler]
  component/Lifecycle
  (start [this]
    (println ">> Starting WebServer on port 3000")
    (assoc this :server (run-jetty (:handler handler) {:port 3000 :join? false})))
  (stop [this]
    (println ">> Stopping WebServer")
    (.stop (:server this))
    (dissoc this :server)))

(defn system []
  (component/system-map
   :db (new-db)
   :user-service (component/using (new-user-service) [:db])
   :handler (component/using (make-handler) [:user-service])
   :web (component/using (map->WebServer {}) [:handler])))

(defn -main []
  (let [sys (component/start (system))]
    (println "✅ System started. Ctrl+C to exit.")))