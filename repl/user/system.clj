(ns user.system
  (:require [mount.core :refer [stop]]
            application))

(comment

  (do
    (ns user.system)
    (require
     '[mount.core :refer [stop]]
     'application
     '[clojure.tools.namespace.repl :as tn])
    (stop)
    (tn/set-refresh-dirs "src" "src-cljc" "repl")
    (tn/refresh-all :after 'mount.core/start))


  ;;
  )
