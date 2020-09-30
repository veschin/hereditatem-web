(ns manager.application.core
  (:require [reagent.dom :as rd]
            [accountant.core :as accountant]

            [manager.application.pages :refer [resolve-page]]
            [manager.application.router :as r]
            [manager.application.state :refer [!page]]

            [semantic]
            
            [routes :refer [generic-routes]]))

(defn main-page []
  [semantic/container {:fluid true
                       :style {:padding "1em"}}
   (when-let [page @!page]
     (resolve-page page))])

(defn ^:dev/after-load render []
  (rd/render [main-page] (.getElementById js/document "application")))

(defn hook-browser-navigation! []
  (let [current-path (fn []
                       (let [path (aget js/window "location" "hash")]
                         (if (empty? path)
                           "/"
                           (subs path 1))))]
    (accountant/configure-navigation!
     {:reload-same-path? true
      :nav-handler       (fn [_path]
                           (let [path         (current-path)
                                 match        (r/match-route path)
                                 route-key    (:handler match)
                                 route-params (merge (:route-params match)
                                                     (when-let [q (some->> (re-find #"\?(\S+)" path)
                                                                           (second)
                                                                           (new goog.Uri.QueryData))]
                                                       (zipmap (map keyword (.getKeys q)) (.getValues q))))]
                             (when route-key
                               (r/dispatch-route route-key route-params))))
      :path-exists?      (fn [_path]
                           (boolean (r/match-route (current-path))))}))
  (accountant/dispatch-current!))

(defn ^:export init []
  (r/register-routes! generic-routes)
  (hook-browser-navigation!)
  (render))
