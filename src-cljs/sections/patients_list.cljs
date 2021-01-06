(ns sections.patients-list
  (:require [semantic]
            [ajax.core :refer [GET]]
            [cljs.reader :as reader]
            [clojure.string :refer [includes? lower-case]]
            [state :refer [ref'storage images disabled?
                           patients-list buffer-for-patient-list]]
            [helpers :refer [create-margin select-by select set-value select-all]]))

;; (defn- update-insole [insole value]
;;   (let [items (select-by insole ".menu")
;;         remove-selected #(-> % .-classList (.remove "selected" "active"))
;;         add-selected #(-> % .-classList (.add "selected" "active"))
;;         return (fn [func val] (func val) val)
;;         valid? #(= (-> % (select-by "span") .-innerText) value)]
;;     ;; (def items items)
;;     (set! (.-innerText (-> insole .-children array-seq first)) value)
;;     (doall (->> items
;;                 .-children
;;                 array-seq
;;                 (map #(return remove-selected %))
;;                 (map #(when (valid? %) (add-selected %)))))))

(defn- clear-inputs []
  ;; (reset! ref'storage {})
  (let [valid? #(not (= "#search" (.-id %)))
        inputs (->> ["textarea" "input"] (map select-all)
                    (map array-seq)
                    (apply concat))]
    (doseq [input inputs]
      (.setAttribute input "autocomplete" "off"))
    (->> inputs
         (filter valid?)
         (map #(set-value % "")))))

(defn- fill-inputs [[selector value]]
  (clear-inputs)
  (let [selector* (->> selector name (str "#") select)]
    (when selector*
      (set-value selector* value))))

(defn- patient-info [patient]
  (reset! disabled? true)
  (GET "/main-page/"
    {:response-format :json
     :params (merge
              {:action :patient-info}
              patient)
     :keywords?       true
     :handler         (fn [{data :data}]
                        (let [{:keys [images-vec patient]} data
                              images* (->> images-vec
                                           reader/read-string
                                           (map (fn [[key value]] {(int key) value}))
                                           (into {}))]
                          (doseq [selector<->value patient]
                            (fill-inputs selector<->value))
                          (reset! images images*)
                          (reset! ref'storage patient)))}))

(def recur-id
  #(loop [el %]
     (if (-> el .-id seq)
       el
       (recur (.-parentElement el)))))

(defn- custom-search [query]
  (let [patients @patients-list
        includes?* (fn [[_ value]] (includes? (lower-case value) query))
        valid? (fn [[_ fields]] (->> fields
                                     (map includes?*)
                                     doall
                                     (some true?)))]
    (->> patients (filter valid?) (into {}) (reset! buffer-for-patient-list))))

(defn patients-list-section []
  (clear-inputs)
  (let [patients @buffer-for-patient-list
        search-value #(-> % .-target .-value)]
    [semantic/segment
     [semantic/header {:style (create-margin 10)
                       :size "medium"} "Поиск пациентов"]
     [semantic/input {:icon "search"
                      :id "search"
                      :style (create-margin 10)
                      :on-change #(-> % search-value custom-search)}]
     (into
      [semantic/list]
      (for [[id patient] patients]
        [semantic/list-item {:id (str "patient_" id)
                             :on-click #(->> % .-target recur-id .-id last keyword (get @patients-list) patient-info)}
         [semantic/grid {:className "custom-grid"}
          (into
           [semantic/grid-row
            {:style (create-margin 5)}]
           (for [[_ key] patient]
             [semantic/grid-column {:className "four wide"}
              [:h3 key]]))]]))]))