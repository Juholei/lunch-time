(ns lunch-time.views.main-screen
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch]]
            [cljs-time.core :as time]
            [cljs-time.format :as time-format]
            [lunch-time.rn-utils :as rn]
            [lunch-time.views.components.common :as c]
            [lunch-time.events]
            [lunch-time.subs]))

(def logo-img (js/require "./images/cljs.png"))

(def format (time-format/formatter "HH:mm"))

(defn- lunch-complete? [start-time end-time]
  (and (not= @start-time nil) (not= @end-time nil)))

(def text-style {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}})

(def touchable-style {:background-color "#999" :padding 10 :border-radius 5})

(defn text-element [data string]
  (when @data
    [c/text text-style string (time-format/unparse format (time/to-default-time-zone data))]))

(defn lunch-button [visible? on-press string]
  (when visible?
    [c/touchable-highlight {:style touchable-style
                            :on-press on-press}
     [c/text {:style {:color "white" :text-align "center" :font-weight "bold"}} string]]))



(defn main-screen []
  (let [start-time (subscribe [:get-start-time])
        end-time (subscribe [:get-end-time])
        error (subscribe [:get-error])
        loading? (subscribe [:get-loading])]
    (fn []
      [c/view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [c/image {:source logo-img
                 :style  {:width 80 :height 80 :margin-bottom 30}}]
       [text-element start-time "Went to lunch at "]
       [text-element end-time "Came back from lunch at "]
       (when (lunch-complete? start-time end-time)
         [c/text text-style "You were at lunch for " (time/in-minutes (time/interval @start-time @end-time)) " minutes"])
       [lunch-button (= nil @start-time)
                     #(dispatch [:set-start-time (time/now)])
                     "Lunch time!"]
       [lunch-button (and @start-time (= @end-time nil))
                     #(do
                        (dispatch [:set-end-time (time/now)])
                        (dispatch [:save-to-server]))
                     "Back to work!"]
       [lunch-button (lunch-complete? start-time end-time)
                     #(do (dispatch [:set-end-time nil])
                          (dispatch [:set-start-time nil]))
                     "Reset"]
       [c/progress-indicator @loading? "Sending lunch to server..."]
       (when @error
         (rn/alert "Error communicating with server"
                   (str "Server returned status " (:status @error))
                   [{:text "OK" :onPress #(dispatch [:clear-error])}]))])))
