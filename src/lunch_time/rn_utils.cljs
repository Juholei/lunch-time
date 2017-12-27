(ns lunch-time.rn-utils
 (:require [re-frame.core :refer [dispatch]]))

(def ReactNative (js/require "react-native"))

(defn alert
  "Wraps ReactNative.Alert.alert function.
   Buttons is a vector with buttons represented as maps.
   Accepted keys for buttons are :text and :onPress."
  [title message buttons]
  (.alert ReactNative.Alert
          title
          message
          (clj->js buttons)))
