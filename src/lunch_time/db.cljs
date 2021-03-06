(ns lunch-time.db
  (:require [clojure.spec.alpha :as s]
            [lunch-time.config :as config]))

;; spec of app-db
(s/def ::greeting string?)
(s/def ::app-db
  (s/keys :req-un [::greeting]))

;; initial state of app-db
(def app-db {:greeting "There's quite much crap in me"
             :start-time nil
             :end-time nil
             :error nil
             :loading? false
             :backend config/backend
             :secret config/secret})
