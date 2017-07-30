(ns lunch-time.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [cljs-time.core :as time]
            [cljs-time.format :as time-format]
            [lunch-time.events]
            [lunch-time.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(def format (time-format/formatter "hh:mm"))

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defn lunch-complete? [start-time end-time]
  (and (not= @start-time nil) (not= @end-time nil)))

(defn app-root []
  (let [start-time (subscribe [:get-start-time])
        end-time (subscribe [:get-end-time])]
    (fn []
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       (when @start-time
         [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} "Went to lunch at " (time-format/unparse format @start-time)])
       (when @end-time
         [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} "Came back from lunch at " (time-format/unparse format @end-time)])
       (when (lunch-complete? start-time end-time)
         [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} "You were at lunch for " (time/in-minutes (time/interval @start-time @end-time)) " minutes"])
       [image {:source logo-img
               :style  {:width 80 :height 80 :margin-bottom 30}}]
       (when (= nil @start-time)
         [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                               :on-press #(dispatch [:set-start-time (time/now)])}
          [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Lunch time!"]])
       (when (and @start-time (= @end-time nil))
         [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                               :on-press #(dispatch [:set-end-time (time/now)])}
          [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Back to work!"]])
       (when (lunch-complete? start-time end-time)
         [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                               :on-press #(do
                                            (dispatch [:set-end-time nil])
                                            (dispatch [:set-start-time nil]))}
          [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "Reset"]])])))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "LunchTime" #(r/reactify-component app-root)))
