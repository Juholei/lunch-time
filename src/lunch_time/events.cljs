(ns lunch-time.events
  (:require
   [ajax.core :as ajax]
   [cljs-time.coerce :refer [to-long]]
   [day8.re-frame.http-fx]
   [re-frame.core :refer [reg-event-db reg-event-fx after]]
   [clojure.spec.alpha :as s]
   [lunch-time.db :as db :refer [app-db]]))

;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db [event]]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check after " event " failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
 :initialize-db
 validate-spec
 (fn [_ _]
   app-db))

(reg-event-db
 :set-start-time
 ;validate-spec
 (fn [db [_ value]]
   (assoc db :start-time value)))

(reg-event-db
 :set-end-time
 ;validate-spec
 (fn [db [_ value]]
   (assoc db :end-time value)))

(reg-event-db
 :success-response
 (fn [db [_ response]]
   (assoc db :loading? false)))

(reg-event-db
 :failure-response
 (fn [db [_ response]]
   (-> db
     (assoc :loading? false)
     (assoc :error response))))

(reg-event-db
 :clear-error
 (fn [db _]
   (assoc db :error nil)))

(reg-event-fx
 :save-to-server
 (fn
   [{db :db} _]
   (if-let [backend (:backend db)]
     {:http-xhrio {:method :post
                   :params {:start (to-long (:start-time db))
                            :end   (to-long (:end-time db))}
                   :uri backend
                   :format (ajax/transit-request-format)
                   :response-format (ajax/transit-response-format)
                   :on-success [:success-response]
                   :on-failure [:failure-response]}
      :db (assoc db :loading? true)}
     {:db db})))
