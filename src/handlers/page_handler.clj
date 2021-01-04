(ns handlers.page-handler
  (:require [responses :refer [json-ok]]
            [db :refer [db-spec]]
            [honeysql.core :as sql]
            [clojure.java.jdbc :as jdbc]))

(defn- update-or-insert!
  [db table row where-clause]
  (jdbc/with-db-transaction [t-con db]
    (let [result (jdbc/update! t-con table row where-clause)]
      (if (zero? (first result))
        (jdbc/insert! t-con table row)
        result))))

(defn- patient-id [last_name phone birth_date]
  (->> (jdbc/query
        db-spec
        (sql/format {:select [:id]
                     :from [[:patients :p]]
                     :where [:and
                             [:= last_name :p.last_name]
                             [:= phone :p.phone]
                             [:= birth_date :p.birth_date]]}))
       sort
       last
       :id))

(defn- post-handler [req]
  (let [{:keys [height last_name age phone
                first_name birth_date patronomyc
                recommendation foot_size
                weight appointment_date
                anamnesis insole images]} (:params req)
        patient-id* (patient-id last_name phone birth_date)]
    (update-or-insert!
     db-spec :patients
     {:first_name first_name
      :patronomyc patronomyc
      :last_name last_name
      :phone phone
      :birth_date birth_date
      :age age
      :weight weight
      :height height
      :foot_size foot_size}
     ["phone = ?" phone])
    (update-or-insert!
     db-spec :indication
     {:recommendation recommendation
      :anamnesis anamnesis
      :images images
      :insole insole
      :appointment_date appointment_date
      :patient_id (patient-id last_name phone birth_date)}
     ["insole = ?" insole]))
  (json-ok))

(defn- patients-list []
  (->> (jdbc/query
        db-spec
        (sql/format {:select [:p.id
                              :p.first_name
                              :p.patronomyc
                              :p.last_name
                              :p.birth_date]
                     :from [[:patients :p]]}))
       (map (fn [{id :id :as patient}] {id (dissoc patient :id)}))
       (into {})))

(defn- patient-info [{params :params}]
  (let [{:keys [first_name patronomyc last_name birth_date]} params]
    (->> (jdbc/query
          db-spec
          (sql/format {:select [:p.* :i.*]
                       :from [[:patients :p]]
                       :join [[:indication :i] [:= :i.patient_id :p.id]]
                       :where [:and
                               [:= first_name :p.first_name]
                               [:= patronomyc :p.patronomyc]
                               [:= last_name :p.last_name]
                               [:= birth_date :p.birth_date]]}))
         (sort-by :id)
         last)))

(defn- get-handler [{params :params :as req}]
  (case (-> params :action keyword)
    :patients-list (json-ok (patients-list))
    :patient-info (-> req patient-info json-ok)))

(defn page-handler [{method :request-method :as req}]
  (def req' req)
  (case method
    :post (post-handler req)
    :get (get-handler req)))