(ns clojure_users_api.handler
  (:require
   [reitit.ring :as ring]
   [reitit.ring.middleware.parameters :as parameters]))

(defn make-handler []
  (reify
    com.stuartsierra.component/Lifecycle
    (start [this]
      (let [user-service (:user-service this)]
        (assoc this :handler
          (ring/ring-handler
            (ring/router
              [["/users/:id"
                {:get (fn [req]
                        (let [id (get-in req [:path-params :id])
                              user ((:get-user user-service) id)]
                          (if user
                            {:status 200 :body user}
                            {:status 404 :body {:error "User not found"}}))})]]
              {:data {:middleware [parameters/parameters-middleware]}})))))
    (stop [this] (dissoc this :handler))))