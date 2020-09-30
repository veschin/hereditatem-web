(ns manager.application.pages)

(defmulti resolve-page (fn [r & _] r))

(defmethod resolve-page :default [r & _]
  (throw (js/Error (str "Page " r " not found!"))))