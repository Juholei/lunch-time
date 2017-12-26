(ns lunch-time.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :get-start-time
  (fn [db _]
    (:start-time db)))

(reg-sub
  :get-end-time
  (fn [db _]
    (:end-time db)))

(reg-sub
  :get-error
  (fn [db _]
    (:error db)))
