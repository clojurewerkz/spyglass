(ns clojurewerkz.spyglass.test.client-test
  (:require [clojurewerkz.spyglass.client :as c])
  (:use clojure.test))

(def ci? (System/getenv "CI"))


(def tc (c/text-connection "localhost:11211"))
(def bc (c/bin-connection  "localhost:11211"))

(deftest test-set-then-get
  (testing "with text protocol"
    (are [k v]
       (do (c/set tc k 10 v)
           (is (= v (c/get tc k))))
       "s-key" "s-value"
       "l-key" 100000
       "kw-key" :memcached
       :sym 'symbol
       "ratio-key" 3/8))
  (when-not ci?
    (testing "with binary protocol"
      (are [k v]
           (do (c/set bc k 10 v)
               (is (= v (c/get bc k))))
           "s-key" "s-value"
           "l-key" 100000
           "kw-key" :memcached
           :sym 'symbol
           "ratio-key" 3/8))))


(deftest test-set-then-touch
  (testing "with text protocol"
    (are [k v]
       (do (c/set tc k 10 v)
           ;; touch is not supported by the text protocol
           (is (thrown? UnsupportedOperationException
                        (.get (c/touch tc k 4)))))
       "s-key" "s-value"
       "l-key" 100000
       "kw-key" :memcached
       :sym 'symbol
       "ratio-key" 3/8))
  (when-not ci?
    (testing "with binary protocol"
      (are [k v]
           (do (c/set bc k 10 v)
               (is (.get (c/touch bc k 4))))
           "s-key" "s-value"
           "l-key" 100000
           "kw-key" :memcached
           :sym 'symbol
           "ratio-key" 3/8))))
