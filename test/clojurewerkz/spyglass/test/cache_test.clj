(ns clojurewerkz.spyglass.test.cache-test
  (:require [clojurewerkz.spyglass.client :as kv])
  (:use clojure.core.cache clojure.test clojurewerkz.spyglass.cache)
  (:import [clojure.core.cache BasicCache FIFOCache LRUCache TTLCache]
           java.util.UUID))

(def tc (kv/text-connection "localhost:11211"))

(deftest ^{:cache true}
  test-has?-with-basic-cache
  (testing "that has? returns false for misses"
    (let [c (BasicCache. {})]
      (are [v] (is (false? (has? c v)))
           :missing-key
           "missing-key"
           (gensym "missing-key"))))
  (testing "that has? returns true for hits"
    (let [c (BasicCache. {:skey "Value" :lkey (Long/valueOf 10000) "kkey" :keyword})]
      (are [v] (is (has? c v))
           :skey
           :lkey
           "kkey"))))


(deftest ^{:cache true}
  test-lookup-with-basic-cache
  (testing "that lookup returns nil for misses"
    (let [c (BasicCache. {})]
      (are [v] (is (nil? (lookup c v)))
           :missing-key
           "missing-key"
           (gensym "missing-key"))))
  (testing "that lookup returns cached values for hits"
    (let [l (Long/valueOf 10000)
          c (BasicCache. {:skey "Value" :lkey l "kkey" :keyword})]
      (are [v k] (is (= v (lookup c k)))
           "Value"   :skey
           l         :lkey
           :keyword  "kkey"))))

(deftest ^{:cache true}
  test-evict-with-basic-cache
  (testing "that evict has no effect for keys that do not exist"
    (let [c (atom (BasicCache. {:a 1 :b 2}))]
      (swap! c evict :missing-key)
      (is (has? @c :a))
      (is (has? @c :b))))
  (testing "that evict removes keys that did exist"
    (let [c (atom (BasicCache. {:skey "Value" "kkey" :keyword}))]
      (is (has? @c :skey))
      (is (= "Value"  (lookup @c :skey)))
      (swap! c evict :skey)
      (is (not (has? @c :skey)))
      (is (= nil  (lookup @c :skey)))
      (is (has? @c "kkey"))
      (is (= :keyword (lookup @c "kkey"))))))

(deftest ^{:cache true}
  test-seed-with-basic-cache
  (testing "that seed returns a new value"
    (let [c (atom (BasicCache. {}))]
      (swap! c seed {:a 1 :b "b" "c" :d})
      (are [k v] (do
                   (is (has? @c k))
                   (is (= v (lookup @c k))))
           :a 1
           :b "b"
           "c" :d))))


;;
;; Tests
;;

(use-fixtures :each (fn [f]
                      (kv/flush tc)
                      (f)
                      (kv/flush tc)))


(deftest ^{:cache true}
  test-has?-with-sync-spyglass-cache
  (testing "that has? returns false for misses"
    (let [c    (sync-spyglass-cache-factory tc)]
      (is (not (has? c (str (UUID/randomUUID)))))
      (is (not (has? c (str (UUID/randomUUID)))))))
  (testing "that has? returns true for hits"
    (let [c    (sync-spyglass-cache-factory tc 1000 {"a" "1" "b" "cache" "c" "3/4"})]
      (is (has? c "a"))
      (is (has? c "b"))
      (is (has? c "c"))
      (is (not (has? c "d"))))))


(deftest ^{:cache true}
  test-lookup-with-sync-spyglass-cache
  (testing "that lookup returns nil for misses"
    (let [c    (sync-spyglass-cache-factory tc)]
      (are [v] (is (nil? (lookup c v)))
           (str (UUID/randomUUID))
           "missing-key"
           (str (gensym "missing-key")))))
  (testing "that lookup returns cached values for hits"
    (let [c (sync-spyglass-cache-factory tc 1000 {"skey" "Value"})]
      (are [k v] (is (= v (lookup c k)))
           "skey" "Value"))))

(deftest ^{:cache true}
  test-lookup-with-async-spyglass-cache
  (testing "that lookup returns a future thats drefs to nil for misses"
    (let [c    (async-spyglass-cache-factory tc)]
      (are [v] (is (nil? @(lookup c v)))
           (str (UUID/randomUUID))
           "missing-key"
           (str (gensym "missing-key")))))
  (testing "that lookup returns a future which derefs to cached values for hits"
    (let [c (async-spyglass-cache-factory tc 1000 {"skey" "Value"})]
      (are [k v] (is (= v @(lookup c k)))
           "skey" "Value"))))
