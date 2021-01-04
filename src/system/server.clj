(ns system.server
  (:require [dotenv :refer [env]]

            [ring.middleware.session :refer [wrap-session]]
            [ring.middleware.session.memory :refer [memory-store]]

            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.util.response :refer [resource-response not-found]]
            [handlers.page-handler :refer [page-handler]]
            [bidi.ring :refer [make-handler ->Resources ->WrapMiddleware]]

            [mount.core :refer [defstate]]

            [org.httpkit.server :refer [run-server]]))

(def main-routes
  (let [store  (memory-store (atom {}))
        session #(wrap-session % {:cookie-name "application-session"
                                  :store       store})]
    (make-handler
     ["/" [["" (constantly (resource-response "templates/index.html"))]
           ["" (->WrapMiddleware
                [["main-page/" page-handler]]
                (comp
                 wrap-json-params
                 wrap-params
                 wrap-keyword-params
                 session))]
           ["js/" (->Resources {:prefix "public/js/"})]
           ["css/" (->Resources {:prefix "public/css/"})]
           [true (not-found "not found")]]])))

(defn- start []
  (let [cfg {:max-body           (* 1 1024 1024)
             :max-ws             (* 1 1024)
             :worker-name-prefix "application-"
             :ip                 "0.0.0.0"
             :port               (Integer/parseInt (-> "env" slurp (subs 9)))}]
    (run-server main-routes cfg)))

(defstate server
  :start (start)
  :stop  (server))
