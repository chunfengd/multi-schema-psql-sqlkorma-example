(ns config
  (:require [cheshire.core :refer :all]))


;the app-config is stored in a clojure map on file in resources
(def app-config
 (read-string (slurp (clojure.java.io/resource "config"))))


(def environment (get (System/getenv) "APP_ENV" "development"))

(def production?
  (= "production" environment))

(def test?
  (= "test" environment))

;default is development
(def development?
  (= "development" environment))

(def app-time-zone
  (if (:time-zone app-config)
    (:time-zone app-config)
    -5))

