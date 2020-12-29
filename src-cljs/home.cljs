(ns home
  (:require [reagent.core :as reagent]
            [ajax.core :refer [GET POST]]
            [clojure.string :refer [join trim]]
            [manager.application.pages :refer [resolve-page]]
            [manager.application.router :as r]
            [manager.application.state :refer [set-page!]]

            [semantic]))

(defonce ref'state nil)
(def ref'storage (reagent/atom {}))
(def disabled? (reagent/atom false))
(def saved? (reagent/atom 0))

(defn- create-margin
  ([value] (create-margin "" value))
  ([key value]
   (let [margin-base (if (symbol? key) 'margin- 'margin)
         key (-> (str margin-base key) keyword)]
     {key value})))

(defn select [selector]
  (-> js/document (.querySelector selector)))

(defn- update-subvecs [val subvecs join-str]
  (->> subvecs
       (map (comp #(apply str %) #(apply subvec val %)))
       (join join-str)))

(def set* #(set! (.-value %) %2))

(defn- update-phone []
  (let [valid? #(= (count %) 11)
        phone (select "#phone")
        phone-val (->> (.-value phone) (re-seq #"\d") vec)
        updated-phone (update-subvecs phone-val [[0 1] [1 4] [4 7] [7 9] [9 11]] "-")]
    (when (valid? phone-val)
      (swap! ref'storage assoc :phone updated-phone)
      (set* phone updated-phone))))

(defn- update-name [field]
  (let [name (select (str "#" field))
        updated-name (-> name .-value clojure.string/capitalize)]
    (swap! ref'storage assoc (keyword field) updated-name)
    (set* name updated-name)))


(defn- update-birth-date []
  (let [valid? #(= (count %) 8)
        date (select "#birth-date")
        date-val  (->> (.-value date) (re-seq #"\d") vec)
        updated-date (update-subvecs date-val [[0 2] [2 4] [4 8]] ".")]
    (when (valid? date-val)
      (swap! ref'storage assoc :birth-date updated-date)
      (set* date updated-date))))

(defn- inputs []
  (let [fields [:first-name :patronomyc :last-name
                :age :birth-date :weight :height :phone :foot-size]
        placeholders {:first-name "Имя"
                      :patronomyc "Отчество"
                      :last-name "Фамилия"
                      :age "Возраст"
                      :weight "Вес"
                      :height "Рост"
                      :phone "Телефон"
                      :birth-date "Дата рождения"
                      :foot-size "Размер обуви"}]
    (into
     [semantic/segment
      [semantic/header {:size "medium" :style {:margin 10}} "Данные о пациенте"]]
     (for [field fields]
       [semantic/grid-row
        [semantic/input
         {:id (name field)
          :style (create-margin 10)
          :disabled @disabled?
          :placeholder (field placeholders)
          :on-change (fn [_ ev]
                       (let [value (-> ev .-value trim)]
                         (swap! ref'storage assoc field value)
                         (prn field value)
                         (case field
                           :phone (update-phone)
                           :birth-date (update-birth-date)
                           :first-name (update-name 'first-name)
                           :patronomyc (update-name 'patronomyc)
                           :last-name (update-name 'last-name))))}]]))))

(defn- save-page []
  (POST "/save-page/"
    {:params @ref'storage
     :format :json}))

(defn- medical-section []
  (let [insoles ["Junior" "J2" "S" "M" "L" "XL" "XXL"]
        insole-value #(-> % (js->clj :keywordize-keys true) :value)
        disabled-color (reagent/atom (when (not @disabled?) "#D0EA2B"))
        date (subs (js/Date) 4 15)
        _ (swap! ref'storage assoc :appointment-date (js/Date.))
        save-date (when (-> @saved? zero? not)
                    [:p {:style (create-margin 'top 15)} (str "Сохранение номер " @saved? " от " date)])]
    [semantic/segment
     [semantic/header {:style (create-margin 10)
                       :size "medium"} "Выбор врача"]
     [semantic/grid-row
      {:style (create-margin 10)}
      [semantic/dropdown {:placeholder "Выбор стельки"
                          :selection true
                          :disabled @disabled?
                          :on-change  #(swap! ref'storage assoc :insole (insole-value %2))
                          :options (map (fn [val] {:key val :value val :text val}) insoles)}]]
     [semantic/grid-row
      {:style (create-margin 10)}
      [semantic/textarea {:style {:padding 10
                                  :width 300
                                  :height 100}
                          :disabled @disabled?
                          :on-change #(swap! ref'storage assoc :anamnesis (.-value %2))

                          :placeholder "Анамнез"}]]
     [semantic/grid-row
      {:style (create-margin 10)}
      [semantic/textarea {:style {:padding 10
                                  :width 300
                                  :height 100}
                          :on-change #(swap! ref'storage assoc :recommendation (.-value %2))
                          :disabled @disabled?
                          :placeholder "Врачебная рекомендация"}]]
     [semantic/grid-row
      {:style (create-margin 15)}
      [semantic/header {:size "mini"} "Дата"]
      [:p date]
      [semantic/button {:on-click #(swap! disabled? not)} "Изменить документ"]
      [semantic/icon {:style {:margin 15
                              :color @disabled-color}
                      :name "circle" :size "large"}]
      [semantic/grid-row
       [semantic/button {:on-click #((do
                                       (save-page)
                                       (swap! saved? inc)))} "Сохранить документ"]
       save-date]]]))

(def images (reagent/atom {}))
(def images-counter (reagent/atom nil))

(defn render-image [file-added-event]
  (let [file (-> (.. file-added-event -target -files) array-seq first)
        file-reader (js/FileReader.)]
    (prn file)
    (set! (.-onload file-reader) #(do
                                    (swap! images assoc (swap! images-counter inc) (-> % .-target .-result))
                                    (swap! ref'storage assoc :images (vals @images))))
    (.readAsDataURL file-reader file)))

(defn- images-section []
  (into
   [semantic/segment
    [semantic/header {:style (create-margin 10)
                      :size "medium"} "Фотографии"]
    [semantic/grid-row
     {:style (create-margin 10)}
     [semantic/input {:type "file"
                      :id "images"
                      :style (create-margin 'bottom 15)
                      :multiple true
                      :on-change render-image
                      :disabled @disabled?}]]]
   (for [[key img] @images]
     [semantic/image {:src img
                      :centered true
                      :on-click (fn [_] (swap! images dissoc key))
                      :className "rounded"
                      :style {:width 350
                              :height 350}}])))

(defn- patiens-list []
  [semantic/segment
   [semantic/header {:style (create-margin 10)
                     :size "medium"} "Поиск пациентов"]
   [semantic/search]
   [semantic/list]])

; 
(defn- home-page []
  [semantic/grid
   [semantic/grid-row {}
    [semantic/grid-column {:style {:height 650} :className "four wide"} (medical-section)]
    [semantic/grid-column {:style {:height 600} :className "three wide"} (inputs)]
    [semantic/grid-column {:style {:height 600} :className "four wide"} (images-section)]
    [semantic/grid-column {:style {:height 600} :className "four wide"} (patiens-list)]]])



(defmethod resolve-page ::home [& _]
  [(reagent/create-class
    {;;  :component-will-unmount #(reset! ref'state nil)
     :render home-page})])

(defmethod r/dispatch-route ::home [_]
  ; (GET "/rest-api/data"
  ;   {:response-format :json
  ;    :keywords?       true
  ;    :handler         (fn [res] (swap! ref'state assoc :data (:data res)))})
  (set-page! ::home))

