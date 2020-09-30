(ns user.system
  (:require [mount.core :refer [stop]]
            system.server))

(comment
  
  (do
    (require '[clojure.tools.namespace.repl :as tn])
    (stop)
    (tn/set-refresh-dirs "src" "src-cljc" "repl")
    (tn/refresh-all :after 'mount.core/start))
  
  ;;
  )
