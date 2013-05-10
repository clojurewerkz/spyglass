(ns clojurewerkz.spyglass.test.client-test
  (:require [clojurewerkz.spyglass.client :as c])
  (:use clojure.test))

(def ci? (System/getenv "CI"))


(def tc (c/text-connection "localhost:11211"))
(def bc (c/bin-connection  "localhost:11211"))

(c/set-log-level! "WARNING")

(deftest test-set-then-get
  (testing "with the text protocol"
    (are [k v]
         (do (c/set tc k 10 v)
             (is (= v (c/get tc k))))
         "s-key" "s-value"
         "l-key" 100000
         "kw-key" :memcached
         "ratio-key" 3/8))
  (when-not ci?
    (testing "with the binary protocol"
      (are [k v]
           (do (c/set bc k 10 v)
               (is (= v (c/get bc k))))
           "s-key" "s-value"
           "l-key" 100000
           "kw-key" :memcached
           "ratio-key" 3/8))))


(deftest test-set-then-async-get
  (testing "with the text protocol"
    (are [k v]
         (do (c/set tc k 10 v)
             (is (= v (.get (c/async-get tc k)))))
         "s-key" "s-value"
         "l-key" 100000
         "kw-key" :memcached
         "ratio-key" 3/8))
  (when-not ci?
    (testing "with the binary protocol"
      (are [k v]
           (do (c/set bc k 10 v)
               (is (= v (c/get bc k))))
           "s-key" "s-value"
           "l-key" 100000
           "kw-key" :memcached
           "ratio-key" 3/8))))


(deftest test-set-then-touch
  (testing "with the text protocol"
    (are [k v]
         (do (c/set tc k 10 v)
             ;; touch is not supported by the text protocol
             (is (thrown? UnsupportedOperationException
                          @(c/touch tc k 4))))
         "s-key" "s-value"
         "l-key" 100000
         "kw-key" :memcached
         "ratio-key" 3/8))
  (when-not ci?
    (testing "with the binary protocol"
      (are [k v]
           (do (c/set bc k 10 v)
               (is @(c/touch bc k 4)))
           "s-key" "s-value"
           "l-key" 100000
           "kw-key" :memcached
           "ratio-key" 3/8))))

(deftest test-async-multiget
  (testing "with the text protocol"
    (c/set tc "key1" 20 "a-value")
    (c/set tc "key2" 20 "b-value")
    (c/set tc "key3" 20 "c-value")
    (is (= {"key3" "c-value", "key2" "b-value", "key1" "a-value"} @(c/async-get-multi tc ["key1" "key2" "key3"]))))
  (when-not ci?
    (testing "with the binary protocol"
      (c/set bc "key1" 20 "a-value")
      (c/set bc "key2" 20 "b-value")
      (c/set bc "key3" 20 "c-value")
      (is (= {"key3" "c-value", "key2" "b-value", "key1" "a-value"} @(c/async-get-multi bc ["key1" "key2" "key3"]))))))

(deftest test-gets-key-that-does-not-exist
  (testing "with the text protocol"
    (let [{:keys [value cas]} (c/gets tc "ahs8d8s823u8u82u2")]
      (is (nil? value))
      (is (nil? cas))))
  (when-not ci?
    (testing "with the binary protocol"
      (let [{:keys [value cas]} (c/gets bc "ahs8d8s823u8u82u2")]
        (is (nil? value))
        (is (nil? cas))))))