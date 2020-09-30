(ns responses
  (:require [ring.util.response :refer [response]]
            [cheshire.core :as json]))

(def json-response 
  (comp response json/generate-string))

(defn json-ok [& [data]]
  (json-response 
   (cond-> {:status "ok"}
     (some? data)
     (assoc :data data))))

(defn json-error [data]
  (json-response 
   {:status   "error"
    :err-data data}))
