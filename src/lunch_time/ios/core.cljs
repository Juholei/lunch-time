(ns lunch-time.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [dispatch-sync]]
            [lunch-time.events]
            [lunch-time.subs]
            [lunch-time.views.main-screen :refer [main-screen]]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))

(defn app-root []
 [main-screen])

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "LunchTime" #(r/reactify-component app-root)))
