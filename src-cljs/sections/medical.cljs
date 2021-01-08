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
    [semantic/button {:style {:margin-right 30
                              :width 120}
                      :on-click #(swap! render-confirm? not)} "Нет"]
    [semantic/button {:style {:width 120}
                      :on-click remove-patient} "Да"]]])

(defn medical-section []
  (let [insoles ["Junior" "J2" "S" "M" "L" "XL" "XXL"]
        insole-value #(-> % (js->clj :keywordize-keys true) :value)
        insole-label (if-let [_ (:insole @ref'storage)] _ "Стелька не выбрана")
        disabled-color (reagent/atom (when (not @disabled?) "#D0EA2B"))
        date (subs (js/Date) 4 15)
        _ (swap! ref'storage assoc :appointment-date (js/Date.))
        save-date (when (-> @saved? zero? not)
                    [:p {:style {:margin-top 15}} (str "Сохранение номер " @saved? " от " date)])
        disabled-save (reagent/atom (->> [:first_name :patronomyc :last_name :birth_date :phone]
                                         (map #(% @ref'storage))
                                         (every? seq)
                                         not))]
    [semantic/segment

     [semantic/header {:style {:margin 10}
                       :size "medium"} "Выбор врача"]
     [semantic/grid-row {:style {:margin 10
                                 :display :inline-flex
                                 :gap 10}}

      [semantic/dropdown {:placeholder "Выбор стельки"
                          :selection true
                          :className "compact"
                          :style {:min-width 150}
                          :disabled @disabled?
                          :id "insole"
                          :on-change  #(swap! ref'storage assoc :insole (insole-value %2))
                          :options (map (fn [val] {:key val :value val :text val}) insoles)}]

      [semantic/label {:style {:width "35%"
                               :margin-left "5s%"}
                       :size "large"} insole-label]]
     [semantic/grid-row
      {:style {:margin 10}}
      [semantic/textarea {:style {:padding 10
                                  :width 300
                                  :height 100}
                          :id "anamnesis"
                          :disabled @disabled?
                          :on-change #(swap! ref'storage assoc :anamnesis (.-value %2))

                          :placeholder "Анамнез"}]]
     [semantic/grid-row
      {:style {:margin 10}}
      [semantic/textarea {:style {:padding 10
                                  :width 300
                                  :height 100}
                          :on-change #(swap! ref'storage assoc :recommendation (.-value %2))
                          :id "recommendation"
                          :disabled @disabled?
                          :placeholder "Врачебная рекомендация"}]]
     [semantic/grid-row
      {:style {:margin 15}}
      [semantic/header "Дата"]
      [:p date]
      [semantic/button {:on-click #(swap! disabled? not)
                        :style {:margin-bottom 10
                                :background-color @disabled-color}}
       [semantic/icon {:name  "pencil"}]
       "Изменить документ"]

      ;; [semantic/icon {:style {:margin 15
      ;;                         :color @disabled-color}
      ;;                 :name "circle" :size "large"}]

      [semantic/grid-row
       [semantic/button {:disabled @disabled-save

                         :on-click #((do
                                       (patient-insert)
                                       (swap! saved? inc)))}
        [semantic/icon {:name  "save"}]
        "Сохранить документ"]
       save-date]
      [semantic/button {:style {:margin-top 10}
                        :on-click #(swap! render-confirm? not)}
       [semantic/icon {:name  "delete"}]
       "Удалить документ"]
      (when @render-confirm? (confirm))]]))