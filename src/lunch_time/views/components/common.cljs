(ns lunch-time.views.components.common
  (:require [reagent.core :as r]))

(def ReactNative (js/require "react-native"))

(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def activity-indicator (r/adapt-react-class (.-ActivityIndicator ReactNative)))

(defn progress-indicator [in-progress? progress-msg]
  [view
   [activity-indicator {:animating in-progress?
                        :style {:margin 10}}]
   (when in-progress?
     [text progress-msg])])
