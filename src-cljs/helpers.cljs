(ns helpers)

(defn select [selector]
  (-> js/document (.querySelector selector)))

(defn select-all [selector]
  (-> js/document (.querySelectorAll selector)))

(defn select-by [el selector]
  (.querySelector el selector))

(defn create-margin
  ([value] (create-margin "" value))
  ([key value]
   (let [margin-base (if (symbol? key) 'margin- 'margin)
         key (-> (str margin-base key) keyword)]
     {key value})))

(def set-value #(set! (.-value %) %2))