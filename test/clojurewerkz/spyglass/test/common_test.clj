(ns clojurewerkz.spyglass.test.common-test
  (:require [clojurewerkz.spyglass.client  :as c])
  (:use clojure.test))


(def ci? (System/getenv "CI"))


(def tc (c/text-connection "localhost:11211"))
(def bc (c/bin-connection  "localhost:11211"))


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
      (is (false? (.get (c/add tc "x1" 25 v))))
      (is (c/add tc "x2" 25 v))
      (is (c/get tc "x1"))
      (is (c/get tc "x2"))))
  (when-not ci?
    (testing "with the binary protocol"
      (let [v "another-value"]
        (c/set bc "z1" 19 v)
        (is (= v (c/get bc "z1")))
        (is (nil? (c/get bc "z2")))
        (is (false? (.get (c/add bc "z1" 25 v))))
        (is (c/add bc "z2" 25 v))
        (is (c/get bc "z1"))
        (is (c/get bc "z2"))))))


(deftest test-replace
  (testing "with the text protocol"
    (let [v "some-value"]
      (c/set tc "y1" 19 v)
      (is (= v (c/get tc "y1")))
      (is (false? (.get (c/replace tc "z1" 25 v))))
      (is (.get (c/replace tc "y1" 25 "tc-new-value")))
      (is (= "tc-new-value" (c/get tc "y1")))))
  (when-not ci?
    (testing "with the binary protocol"
      (let [v "some-value"]
        (c/set bc "y1" 19 v)
        (is (= v (c/get bc "y1")))
        (is (false? (.get (c/replace bc "z1" 25 v))))
        (is (.get (c/replace bc "y1" 25 "bc-new-value")))
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