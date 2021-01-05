(ns sections.patients-list
  (:require [semantic]
            [ajax.core :refer [GET]]
            [cljs.reader :as reader]
            [state :refer [ref'storage images disabled? patients-list]]
            [helpers :refer [create-margin select-by select set-value]]))

(defn update-insole [insole value]
  (let [items (select-by insole ".menu")
        remove-selected #(-> % .-classList (.remove "selected" "active"))
        add-selected #(-> % .-classList (.add "selected" "active"))
        return (fn [func val] (func val) val)
        valid? #(= (-> % (select-by "span") .-innerText) value)]
    ;; (def items items)
    (set! (.-innerText (-> insole .-children array-seq first)) value)
    (doall (->> items
                .-children
                array-seq
                (map #(return remove-selected %))
                (map #(when (valid? %) (add-selected %)))))))

(defn- fill-inputs [[selector value]]
  (let [selector* (->> selector name (str "#") select)]
    (case selector
      :insole (update-insole selector* value)
      (when selector*
        (set-value selector* value)))))

(defn patient-info [patient]
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

(defn patients-list-section []
  (let [patients @patients-list]
    [semantic/segment
     [semantic/header {:style (create-margin 10)
                       :size "medium"} "Поиск пациентов"]
     [semantic/search]
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