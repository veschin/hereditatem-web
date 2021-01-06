(ns db
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [clojure.java.shell :refer [sh]]))

(def db-spec
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "db/database.db"})

(def patients [[:id :integer :primary :key :autoincrement]
               [:first_name :text]
               [:patronomyc :text]
               [:last_name :text]
               [:phone :text]
               [:birth_date :date]
               [:age :int]
               [:weight :int]
               [:height :int]
               [:foot_size :float]
               [:recommendation :text]
               [:anamnesis :text]
               [:images :blob]
               [:insole :text]
               [:appointment_date :date]])

(comment
  "init db"
  (do
    (sh "rm" "db/database.db")
    (jdbc/db-do-commands db-spec
                         (jdbc/create-table-ddl :patients patients)))
  "select all from patients"
  (jdbc/query db-spec (sql/format {:select [:*]
                                   :from [:patients]}))
  (sh "sqlite3" "db/database.db" ".schema")

  ;
  )