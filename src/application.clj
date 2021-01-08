(ns application
  (:gen-class)
  (:require [mount.core :refer [start]]
            db
            system.server))

(defn -main [& args]
  (when args (db/init-db))
  (prn (start)))