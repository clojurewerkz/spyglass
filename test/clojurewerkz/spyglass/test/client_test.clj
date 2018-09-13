(ns clojurewerkz.spyglass.test.client-test
  (:require [clojurewerkz.spyglass.client :as c])
  (:use clojure.test))

(def ci? (System/getenv "CI"))

(def memcached-host (or (System/getenv "MEMCACHED_HOST")
                        "localhost:11211"))

(def tc (c/text-connection memcached-host))
(def bc (c/bin-connection  memcached-host))
(def kc (c/text-connection memcached-host (c/ketama-connection-factory)))

(c/set-log-level! "WARNING")

(deftest test-set-original-future
  (let [^clojurewerkz.spyglass.OperationFuture set-future (c/set tc "s-key" 10 "s-value")
        orig-future (.getOriginalFuture set-future)]
    (is (instance? net.spy.memcached.internal.OperationFuture orig-future))
    (is (= @set-future @orig-future))))

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
           "ratio-key" 3/8)))
  (when-not ci?
    (testing "with ketama hashing"
      (are [k v]
           (do (c/set kc k 10 v)
               (is (= v (c/get kc k))))
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

(deftest test-async-get-original-future
  (let [async-get-future (c/async-get tc "s-key")
        orig-future (.getOriginalFuture async-get-future)]
    (is (instance? net.spy.memcached.internal.GetFuture orig-future))
    (is (= @async-get-future @orig-future))))

(deftest test-set-then-touch
  (testing "with the text protocol"
    (are [k v]
         (do (c/set tc k 10 v)
             (is @(c/touch tc k 4)))
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

;; get-and-touch is only supported in binary protocol
(deftest test-set-then-get-and-touch
  (when-not ci?
    (testing "with the binary protocol"
      (are [k v]
           (do (c/set bc k 10 v)
               (is (= v (:value (c/get-and-touch bc k 10)))))
           "s-key" "s-value"
           "l-key" 100000
           "kw-key" :memcached
           "ratio-key" 3/8))))

;; async-get-and-touch is only supported in binary protocol
(deftest test-set-then-async-get-and-touch
  (when-not ci?
    (testing "with the binary protocol"
      (are [k v]
           (do (c/set bc k 10 v)
               (is (= v (.getValue @(c/async-get-and-touch bc k 10)))))
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

(deftest test-async-multiget-original-future
  (let [async-get-multi-future (c/async-get-multi tc ["key1" "key2"])
        orig-future (.getOriginalFuture async-get-multi-future)]
    (is (instance? net.spy.memcached.internal.BulkGetFuture orig-future))
    (is (= @async-get-multi-future @orig-future))))

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
