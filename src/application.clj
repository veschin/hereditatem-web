(ns application
  (:gen-class)
  (:require [mount.core :refer [start]]
            system.server))

(defn -main [& args]
  (prn (start)))