(ns sections.images
  (:require [semantic]
            [state :refer [images-counter images disabled?]]
            [helpers :refer [create-margin]]))

(defn render-image [file-added-event]
  (let [file (-> (.. file-added-event -target -files) array-seq first)
        file-reader (js/FileReader.)]
    (set! (.-onload file-reader) #(swap! images assoc (swap! images-counter inc) (-> % .-target .-result)))
    (.readAsDataURL file-reader file)))

(defn images-section []
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
                      :on-click (fn [_] (when (not @disabled?) (swap! images dissoc key)))
                      :className "rounded"
                      :style {:width 350
                              :height 350}}])))