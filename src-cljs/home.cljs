(ns home
  (:require [reagent.core :as reagent]
            [ajax.core :refer [GET]]

            [manager.application.pages :refer [resolve-page]]
            [manager.application.router :as r]
            [manager.application.state :refer [set-page!]]
            
            [semantic]))

(defonce ref'state nil)

(defn- home-page []
  [semantic/segment
   [semantic/header {:size "large"}
    "Hello World"]])

(defmethod resolve-page ::home [& _]
  [(reagent/create-class
    {;;  :component-will-unmount #(reset! ref'state nil)
     :render home-page})])

(defmethod r/dispatch-route ::home [_]
  ; (GET "/rest-api/data"
  ;   {:response-format :json
  ;    :keywords?       true
  ;    :handler         (fn [res] (swap! ref'state assoc :data (:data res)))})
  (set-page! ::home))
