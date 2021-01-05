(ns sections.medical
  (:require [semantic]
            [reagent.core :as reagent]
            [ajax.core :refer [POST]]
            [state :refer [ref'storage disabled?
                           saved? images render-confirm?]]
            [helpers :refer [create-margin]]))

(defn patient-insert []
  (POST "/main-page/"
    {:params (-> @ref'storage (merge {:images @images}) (assoc :action :insert-patient))
     :format :json})
  (js/document.location.reload))

(defn remove-patient []
  (POST "/main-page/"
    {:params (-> @ref'storage
                 (select-keys [:last_name :phone :birth_date])
                 (assoc :action :remove-patient))
     :format :json})
  (js/document.location.reload))

(defn confirm []
  [semantic/grid {:style (->> [['top 5] ['bottom 0] ['left 0]] (map #(apply create-margin %)) (into {}))}
   [semantic/grid-row
    [semantic/header "Вы точно хотите удалить документ?"]]
   [semantic/grid-row
    [semantic/button {:style (merge (create-margin 'right 30) {:width 120})
                      :on-click #(swap! render-confirm? not)} "Нет"]
    [semantic/button {:style {:width 120}
                      :on-click remove-patient} "Да"]]])

(defn medical-section []
  (let [insoles ["Junior" "J2" "S" "M" "L" "XL" "XXL"]
        insole-value #(-> % (js->clj :keywordize-keys true) :value)
        disabled-color (reagent/atom (when (not @disabled?) "#D0EA2B"))
        date (subs (js/Date) 4 15)
        _ (swap! ref'storage assoc :appointment-date (js/Date.))
        save-date (when (-> @saved? zero? not)
                    [:p {:style (create-margin 'top 15)} (str "Сохранение номер " @saved? " от " date)])
        disabled-save (reagent/atom (->> [:first_name :patronomyc :last_name :birth_date :phone]
                                         (map #(% @ref'storage))
                                         (every? seq)
                                         not))]
    [semantic/segment
     [semantic/header {:style (create-margin 10)
                       :size "medium"} "Выбор врача"]
     [semantic/grid-row
      {:style (create-margin 10)}
      [semantic/dropdown {:placeholder "Выбор стельки"
                          :selection true
                          :disabled @disabled?
                          :id "insole"
                          :on-change  #(swap! ref'storage assoc :insole (insole-value %2))
                          :options (map (fn [val] {:key val :value val :text val}) insoles)}]]
     [semantic/grid-row
      {:style (create-margin 10)}
      [semantic/textarea {:style {:padding 10
                                  :width 300
                                  :height 100}
                          :id "anamnesis"
                          :disabled @disabled?
                          :on-change #(swap! ref'storage assoc :anamnesis (.-value %2))

                          :placeholder "Анамнез"}]]
     [semantic/grid-row
      {:style (create-margin 10)}
      [semantic/textarea {:style {:padding 10
                                  :width 300
                                  :height 100}
                          :on-change #(swap! ref'storage assoc :recommendation (.-value %2))
                          :id "recommendation"
                          :disabled @disabled?
                          :placeholder "Врачебная рекомендация"}]]
     [semantic/grid-row
      {:style (create-margin 15)}
      [semantic/header "Дата"]
      [:p date]
      [semantic/button {:on-click #(swap! disabled? not)} "Изменить документ"]

      [semantic/icon {:style {:margin 15
                              :color @disabled-color}
                      :name "circle" :size "large"}]

      [semantic/grid-row
       [semantic/button {:disabled @disabled-save
                         :on-click #((do
                                       (patient-insert)
                                       (swap! saved? inc)))} "Сохранить документ"]
       save-date]
      [semantic/button {:style (create-margin 'top 10)
                        :on-click #(swap! render-confirm? not)} "Удалить документ"]
      (when @render-confirm? (confirm))]]))