(ns sections.inputs
  (:require [semantic]
            [helpers :refer [create-margin set-value select]]
            [state :refer [ref'storage disabled?]]
            [clojure.string :refer [join trim]]))

(defn- update-subvecs [val subvecs join-str]
  (->> subvecs
       (map (comp #(apply str %) #(apply subvec val %)))
       (join join-str)))

(defn- update-phone []
  (let [valid? #(= (count %) 11)
        phone (select "#phone")
        phone-val (->> (.-value phone) (re-seq #"\d") vec)
        updated-phone (update-subvecs phone-val [[0 1] [1 4] [4 7] [7 9] [9 11]] "-")]
    (when (valid? phone-val)
      (swap! ref'storage assoc :phone updated-phone)
      (set-value phone updated-phone))))

(defn- update-name [field]
  (let [name (select (str "#" field))
        updated-name (-> name .-value clojure.string/capitalize)]
    (swap! ref'storage assoc (keyword field) updated-name)
    (set-value name updated-name)))


(defn- update-birth_date []
  (let [valid? #(= (count %) 8)
        date (select "#birth_date")
        date-val  (->> (.-value date) (re-seq #"\d") vec)
        updated-date (update-subvecs date-val [[0 2] [2 4] [4 8]] ".")]
    (when (valid? date-val)
      (swap! ref'storage assoc :birth_date updated-date)
      (set-value date updated-date))))

(defn inputs-section []
  (let [fields [:first_name :patronomyc :last_name
                :age :birth_date :weight :height :phone :foot_size]
        placeholders {:first_name "Имя*"
                      :patronomyc "Отчество*"
                      :last_name "Фамилия*"
                      :age "Возраст"
                      :weight "Вес"
                      :height "Рост"
                      :phone "Телефон*"
                      :birth_date "Дата рождения*"
                      :foot_size "Размер обуви"}
        important? #(#{:first_name :last_name :patronomyc :birth_date :phone} %)]
    (into
     [semantic/segment
      [semantic/header {:size "medium" :style {:margin 10}} "Данные о пациенте"]]
     (for [field fields]
       [semantic/grid-row
        [semantic/input
         {:id (name field)
          :style (if (important? field)
                   (merge (create-margin 10) {:border "solid 0.8px"
                                              :border-radius ".28571429rem"})
                   (create-margin 10))
          :disabled @disabled?
          :placeholder (field placeholders)
          :on-change (fn [_ ev]
                       (let [value (-> ev .-value trim)]
                         (swap! ref'storage assoc field value)
                         (case field
                           :phone (update-phone)
                           :birth_date (update-birth_date)
                           :first_name (update-name 'first_name)
                           :patronomyc (update-name 'patronomyc)
                           :last_name (update-name 'last_name))))}]]))))