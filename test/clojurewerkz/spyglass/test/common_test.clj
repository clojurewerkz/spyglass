(ns clojurewerkz.spyglass.test.common-test
  (:require [clojurewerkz.spyglass.client  :as c])
  (:use clojure.test)
  (:import java.util.UUID
           net.spy.memcached.transcoders.LongTranscoder))


(def ci? (System/getenv "CI"))


(def tc (c/text-connection "localhost:11211"))
(def bc (c/bin-connection  "localhost:11211"))
(c/set-log-level! "WARNING")

;;
;; Tests ported from https://github.com/dustin/memcached-test/blob/master/testClient.py
;;

(defn- flush-cache
  [f]
  (c/flush tc)
  (f)
  (c/flush tc))

(use-fixtures :each flush-cache)


(deftest test-version
  (testing "with the text protocol"
    (testing "that the version command returns something"
      (let [res (c/get-versions tc)]
        (is (not (empty? res))))))
  (when-not ci?
    (testing "with the binary protocol"
      (testing "that the version command returns something"
        (let [res (c/get-versions bc)]
          (is (not (empty? res))))))))

(deftest test-simple-set-and-get
  (testing "with the text protocol"
    (let [k "x"
          v "some-value"]
      (c/set tc k 19 v)
      (is (= v (c/get tc k)))))
  (when-not ci?
    (testing "with the binary protocol"
      (let [k "z"
            v "another-value"]
        (c/set tc k 19 v)
        (is (= v (c/get tc k)))))))


(deftest test-zero-expiration
  (testing "with the text protocol"
    (let [k "x"
          v "some-value"]
      (c/set tc k 0 v)
      (Thread/sleep 1200)
      (is (= v (c/get tc k)))))
  (when-not ci?
    (testing "with the binary protocol"
      (let [k "z"
            v "another-value"]
        (c/set tc k 19 v)
        (Thread/sleep 750)
        (is (= v (c/get tc k)))))))

(deftest test-delete
  (testing "with the text protocol"
    (let [k "x"
          v "some-value"]
      (c/set tc k 19 v)
      (is (= v (c/get tc k)))
      (c/delete tc k)
      (is (nil? (c/get tc k)))))
  (when-not ci?
    (testing "with the binary protocol"
      (let [k "z"
            v "another-value"]
        (c/set tc k 19 v)
        (is (= v (c/get tc k)))
        (c/delete tc k)
        (is (nil? (c/get tc k)))))))


(deftest test-flush
  (testing "with the text protocol"
    (let [v "some-value"]
      (c/set tc "x1" 19 v)
      (c/set tc "x2" 19 v)
      (is (= v (c/get tc "x1")))
      (is (= v (c/get tc "x2")))
      (c/flush tc)
      (is (nil? (c/get tc "x1")))
      (is (nil? (c/get tc "x2")))))
  (when-not ci?
    (testing "with the binary protocol"
      (let [v "some-value"]
        (c/set bc "x1" 19 v)
        (c/set bc "x2" 19 v)
        (is (= v (c/get bc "x1")))
        (is (= v (c/get bc "x2")))
        (c/flush bc)
        (is (nil? (c/get bc "x1")))
        (is (nil? (c/get bc "x2")))))))


(deftest test-add
  (testing "with the text protocol"
    (let [v "some-value"]
      (c/set tc "x1" 19 v)
      (is (= v (c/get tc "x1")))
      (is (nil? (c/get tc "x2")))
      (is (false? @(c/add tc "x1" 25 v)))
      (is (c/add tc "x2" 25 v))
      (is (c/get tc "x1"))
      (is (c/get tc "x2"))))
  (when-not ci?
    (testing "with the binary protocol"
      (let [v "another-value"]
        (c/set bc "z1" 19 v)
        (is (= v (c/get bc "z1")))
        (is (nil? (c/get bc "z2")))
        (is (false? @(c/add bc "z1" 25 v)))
        (is (c/add bc "z2" 25 v))
        (is (c/get bc "z1"))
        (is (c/get bc "z2"))))))


(deftest test-replace
  (testing "with the text protocol"
    (let [v "some-value"]
      (c/set tc "y1" 19 v)
      (is (= v (c/get tc "y1")))
      (is (false? @(c/replace tc "z1" 25 v)))
      (is @(c/replace tc "y1" 25 "tc-new-value"))
      (is (= "tc-new-value" (c/get tc "y1")))))
  (when-not ci?
    (testing "with the binary protocol"
      (let [v "some-value"]
        (c/set bc "y1" 19 v)
        (is (= v (c/get bc "y1")))
        (is (false? @(c/replace bc "z1" 25 v)))
        (is @(c/replace bc "y1" 25 "bc-new-value"))
        (is (= "bc-new-value" (c/get bc "y1")))))))


(deftest test-multiget
  (testing "with the text protocol"
    (c/set tc "key1" 20 "a-value")
    (c/set tc "key2" 20 "b-value")
    (c/set tc "key3" 20 "c-value")
    (is (= {"key3" "c-value", "key2" "b-value", "key1" "a-value"} (c/get-multi tc ["key1" "key2" "key3"]))))
  (when-not ci?
    (testing "with the binary protocol"
      (c/set bc "key1" 20 "a-value")
      (c/set bc "key2" 20 "b-value")
      (c/set bc "key3" 20 "c-value")
      (is (= {"key3" "c-value", "key2" "b-value", "key1" "a-value"} (c/get-multi bc ["key1" "key2" "key3"]))))))


(deftest   test-incr
  (testing "a case when a value does not exist (and is not initialized)"
    (is (= -1 (c/incr tc (str (UUID/randomUUID)) 77))))
  (testing "a case when a value does not exist (and IS initialized)"
    (is (= 88 (c/incr tc (str (UUID/randomUUID)) 77 88)))))


(deftest test-cas
  (let [key (str (UUID/randomUUID))
        val 123]
    (c/set tc key 60 val)
    (let [cid1 (:cas (c/gets tc key))
          _    (c/set tc key 60 889)
          cid2 (:cas (c/gets tc key))]
      (is (= :exists (c/cas tc key cid1 val)))
      (is (= :exists (c/cas tc key cid1 234)))
      (is (= :ok     (c/cas tc key cid2 val))))))

(deftest test-async-cas
  (let [key (str (UUID/randomUUID))
        val 123]
    (c/set tc key 60 val)
    (let [cid1 (:cas (c/gets tc key))
          _    (c/set tc key 60 889)
          cid2 (:cas (c/gets tc key))]
      (is (= "EXISTS" (str @(c/async-cas tc key cid1 val))))
      (is (= "EXISTS" (str @(c/async-cas tc key cid1 234))))
      (is (= "OK"     (str @(c/async-cas tc key cid2 val)))))))

(deftest test-get-future-being-derefed-with-timeout-expiring
  (let [key (str (UUID/randomUUID))
        val 123]
    (c/set tc key 60 val)
    (let [cache-future (c/async-get tc key)]
      (is (= :fail (deref cache-future 0 :fail))))))

(deftest test-get-future-being-derefed-with-timeout
  (let [key (str (UUID/randomUUID))
        val 123]
    (c/set tc key 60 val)
    (let [cache-future (c/async-get tc key)]
      (is (= 123 (deref cache-future 50 nil))))))

(deftest test-bulk-future-being-derefed-with-timeout
  (let [key (str (UUID/randomUUID))
        val 123]
    (c/set tc key 60 val)
    (let [cid (:cas (c/gets tc key))]
      (is (= "OK" (str (deref (c/async-cas tc key cid val) 60 nil)))))))

(deftest test-operation-future-being-derefed-with-timeout
  (let [future (c/flush tc)]
    (deref future 60 nil)))
