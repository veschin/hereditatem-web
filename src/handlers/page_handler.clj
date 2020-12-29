(ns handlers.page-handler
  (:require [responses :refer [json-ok]]
            [db :refer [db-spec]]
            [honeysql.core :as sql]
            [clojure.java.jdbc :as jdbc]))

(defn update-or-insert!
  "Updates columns or inserts a new row in the specified table"
  [db table row where-clause]
  (jdbc/with-db-transaction [t-con db]
    (let [result (jdbc/update! t-con table row where-clause)]
      (if (zero? (first result))
        (jdbc/insert! t-con table row)
        result))))

(defn- patient-id [last-name phone birth-date]
  (->> (jdbc/query
        db-spec
        (sql/format {:select [:id]
                     :from [[:patients :p]]
                     :where [:and
                             [:= last-name :p.last_name]
                             [:= phone :p.phone]
                             [:= birth-date :p.birth_date]]}))
       sort
       last
       :id))

(defn page-handler [req]
  (let [{:keys [height last-name age phone
                first-name birth-date patronomyc
                recommendation foot-size
                weight appointment-date
                anamnesis insole images]} (:params req)]
    (jdbc/insert!
     db-spec :patients
     {:first_name first-name
      :patronomyc patronomyc
      :last_name last-name
      :phone phone
      :birth_date birth-date
      :age age
      :weight weight
      :height height
      :foot_size foot-size})

    (update-or-insert!
     db-spec :testimony
     {:recommendation recommendation
      :anamnesis anamnesis
      :images images
      :insole insole
      :appointment_date appointment-date
      :patient_id (patient-id last-name phone birth-date)}
     ["images = ?" images]))

  (json-ok))

(comment

  (jdbc/insert! db-spec :testimony))


