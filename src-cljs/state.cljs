(ns state
  (:require [reagent.core :as reagent]))

(def ref'storage (reagent/atom {}))
(def patients-list (reagent/atom []))
(def buffer-for-patient-list (reagent/atom []))
(def disabled? (reagent/atom true))
(def render-confirm? (reagent/atom false))
(def saved? (reagent/atom 0))
(def images (reagent/atom {}))
(def images-counter (reagent/atom nil))