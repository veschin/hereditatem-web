(ns home
  (:require [reagent.core :as reagent]
            [ajax.core :refer [GET]]
            [manager.application.pages :refer [resolve-page]]
            [manager.application.router :as r]
            [manager.application.state :refer [set-page!]]
            [semantic]
            [sections.patients-list :refer [patients-list-section]]
            [sections.medical :refer [medical-section]]
            [sections.images :refer [images-section]]
            [sections.inputs :refer [inputs-section]]
            [state :refer [patients-list]]))

(defn- home-page []
  [semantic/grid
   [semantic/grid-row {}
    [semantic/grid-column {:style {:height 650} :className "four wide"} (medical-section)]
    [semantic/grid-column {:style {:height 600} :className "three wide"} (inputs-section)]
    [semantic/grid-column {:style {:height 600} :className "four wide"} (images-section)]
    [semantic/grid-column {:style {:height 600} :className "five wide"} (patients-list-section)]]])

(defmethod resolve-page ::home [& _]
  [(reagent/create-class
    {;;  :component-will-unmount #(reset! ref'state nil)
     :render home-page})])

(defmethod r/dispatch-route ::home [_]
  (GET "/main-page/"
    {:response-format :json
     :params {:action :patients-list}
     :keywords?       true
     :handler         (fn [res] (reset! patients-list (:data res)))})
  (set-page! ::home))

