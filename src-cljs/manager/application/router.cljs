(ns manager.application.router
  (:require [clojure.string :as string]
            [reagent.core :as reagent]
            [bidi.bidi :as bidi]))

(def ^:private !router (reagent/atom nil))

(defn register-routes! [routes]
  (reset! !router routes))

(defn match-route [path]
  (bidi/match-route @!router path))

(defn- with-params [path params]
  (let [query (->> (for [[k v] params]
                     (str (cond-> k (keyword? k) (name)) "=" v))
                   (string/join "&"))]
    (str path "?" query)))

(defn path-for [route-key & args]
  (let [with-params? (map? (last args))
        args* (if with-params? (butlast args) args)
        path (apply bidi/path-for @!router route-key args*)]
    (cond-> (str "#" path)
      with-params?
      (with-params (last args)))))

(defn redirect [route-key & args]
  (->> (apply path-for route-key args)
       (aset js/location "hash")))

(defmulti dispatch-route (fn [r & _] r))

(defmethod dispatch-route :default [r & _]
  (throw (js/Error (str "Route " r " not found!"))))