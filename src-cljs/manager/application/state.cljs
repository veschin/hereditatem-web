(ns manager.application.state
  (:require [reagent.core :as reagent]))

(def !page (reagent/atom nil))
(defn set-page! [p]
  (reset! !page p))