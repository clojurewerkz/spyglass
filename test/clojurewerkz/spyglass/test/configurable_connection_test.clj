(ns clojurewerkz.spyglass.test.configurable-connection-test
  (:require [clojurewerkz.spyglass.client :as c])
  (:use clojure.test)
  (:import [net.spy.memcached FailureMode]))


(deftest test-to-failure-mode
  (are [alias mode] (is (= (c/to-failure-mode alias) mode))
    "redistribute" FailureMode/Redistribute
    "retry"        FailureMode/Retry
    "cancel"       FailureMode/Cancel
    :redistribute  FailureMode/Redistribute
    :retry         FailureMode/Retry
    :cancel        FailureMode/Cancel))


(deftest test-text-connection-factory
  (let [cf (c/text-connection-factory :failure-mode :redistribute)]
    (is (= FailureMode/Redistribute (.getFailureMode cf))))
  (let [cf (c/text-connection-factory :failure-mode :cancel)]
    (is (= FailureMode/Cancel (.getFailureMode cf)))))

(deftest test-connection-with-custom-failure-mode
  (let [conn (c/text-connection "127.0.0.1:11211" (c/text-connection-factory :failure-mode :redistribute))]
    (c/set conn "a" 1000 "1")
    (is (= (c/get conn "a") "1"))))
