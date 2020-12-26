(ns routes.db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [clojure.java.shell :refer [sh]]))

(def db-spec
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"})

(def patiens [[:id :integer :primary :key :autoincrement]
              [:first_name :text]
              [:patronomyc :text]
              [:last_name :text]
              [:phone :text]
              [:birth_date :date]
              [:age :int]
              [:weigth :int]
              [:height :int]
              [:foot-size :float]])

(def medical)


(comment
  "init db"
  (do
    (sh "rm" "db/database.db")
    (jdbc/db-do-commands db-spec
                         (jdbc/create-table-ddl :patients patiens)))
  "select all from patients"
  (jdbc/query db-spec (sql/format {:select [:*]
                                   :from [:patients]}))
  ;
  )