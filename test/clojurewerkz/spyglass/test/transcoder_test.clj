(ns clojurewerkz.spyglass.test.transcoder-test
  (:require [clojurewerkz.spyglass.client :as c]
            [clojurewerkz.spyglass.transcoder :refer [make-transcoder]])
  (:use clojure.test)
  (:import [clojure.lang PersistentQueue]))

(def memcached-host (or (System/getenv "MEMCACHED_HOST")
                        "localhost:11211"))

(def tc (c/text-connection memcached-host))
(def bc (c/bin-connection  memcached-host))

;;--test data
(def test-uuid (java.util.UUID/randomUUID))
(def test-date (java.util.Date.))
(def test-lazy-seq (repeatedly 1000 rand))

(defrecord StressRecord [state])

;;set and fetch different clojure datatypes
;;examples are taken from taoensso.nippy
;;uncommented examples are not supported by spyglass
(deftest test-get-set-with-different-data-types
  (are [k v]
    (do (c/set tc k 1 v)
        (is (= v (c/get tc k))))
  
    ;"nil"         nil
    "boolean"     true
    "char-utf8"   \ಬ
    ;"bytes"       (byte-array [(byte 1) (byte 2) (byte 3)])
    "ascii"       "abcdefghi"
    "utf-8"       "ಬಾ ಇಲ್ಲಿ ಸಂಭ"
    "long-string" (apply str (range 1000))
    "keyword"     :yep-its-valid
    "ns-keyword"  ::nskeyword
    "queue"       (-> (PersistentQueue/EMPTY) (conj :a :b :c :d :e :f :g)) 
    "queue-empty" (PersistentQueue/EMPTY)
    "sorted-set"  (sorted-set 1 2 3 4 5)
    "sorted-map"  (sorted-map :b 2 :a 1 :d 4 :c 3)
    "list"        (list 1 2 3 4 5 (list 6 7 8 (list 9 10)))
    "list-quoted" '(1 2 3 4 5 (6 7 8 (9 10)))
    "list-empty"  (list)
    "vector"      [1 2 3 4 5 [6 7 8 [9 10]]]
    "vector-empty" []
    "map"         {:a 1 :b 2 :c 3 :d {:e 4 :f {:g 5 :h 6 :i 7}}}
    "map-empty"   {}
    "set"         #{1 2 3 4 5 #{6 7 8 #{9 10}}}
    "set-empty"   #{}
    "meta"        (with-meta {:a :A} {:metakey :metaval})
    ;"lazy-seq"   test-lazy-seq 
    "byte"        (byte 16)
    "short"       (short 42)
    "integer"     (int 42)
    "long"        (long 42)
    "bigint"      (bigint 31415926535897932384626433832795)
    "float"       (float 3.14)
    "double"      (double 3.14)
    "bigdec"      (bigdec 3.1415926535897932384626433832795)
    "ratio"       22/7
    "uuid"        test-uuid 
    "date"        test-date 
    ;"record"      (->StressRecord "Hello, world!")
    ;"throwable"   (Throwable. "Yolo")
    ;"exception"   (try (/ 1 0) (catch Exception e e))
    ;"ex-info"     (ex-info "ExInfo" {:data "data"})
    ))

(def int-transcoder (make-transcoder :integer))
(deftest test-integer-transcoder
  (testing "roundtrips integer value correctly"
    (are [k v]
      (do (c/set tc k 1 v int-transcoder)
          (is (= v (c/get tc k int-transcoder))))
      "i32"   (int 32)
      "i0"    (int 0)
      "i-1"   (int -1)))

  (testing "fails to encode non-integer value"
    (is (thrown? java.lang.ClassCastException
          (c/set tc "k" 1 (long 42) int-transcoder))))
  
  (testing "returns nil when trying to decode non-integer value"
    (c/set tc "k" 1 (long 32))
    (is (nil? (c/get tc "k" int-transcoder)))))

(def long-transcoder (make-transcoder :long))
(deftest test-long-transcoder
  (testing "roundtrips long values correctly"
    (are [k v]
      (do (c/set tc k 1 v long-transcoder)
          (is (= v (c/get tc k long-transcoder))))
      "l42" 42
      "l0"  0
      "l-1" -1))
  
  (testing "fails to decode non-long value"
    (is (thrown? java.lang.ClassCastException
          (c/set tc "kl" 1 (int 3) long-transcoder))))
  
  (testing "returns nil when trying to decode non-long value"
    (c/set tc "kld" 1 (str "xyz123"))
    (is (nil? (c/get tc "kld" long-transcoder)))))

(def whalin-transcoder (make-transcoder :whalin))
(deftest test-whaling-transcoder
  (testing "roundtrips long values correctly"
    (are [k v]
      (do (c/set tc k 1 v whalin-transcoder)
          (is (= v (c/get tc k whalin-transcoder))))
      "l42" 42
      "l0"  0
      "l-1" -1
      "boolean"     true
      "char-utf8"   \ಬ
      ;"bytes"       (byte-array [(byte 1) (byte 2) (byte 3)])
      "ascii"       "abcdefghi"
      "utf-8"       "ಬಾ ಇಲ್ಲಿ ಸಂಭ"
      "long-string" (apply str (range 1000))
      "keyword"     :yep-its-valid
      "ns-keyword"  ::nskeyword
      "queue"       (-> (PersistentQueue/EMPTY) (conj :a :b :c :d :e :f :g)) 
      "queue-empty" (PersistentQueue/EMPTY)
      "sorted-set"  (sorted-set 1 2 3 4 5)
      "sorted-map"  (sorted-map :b 2 :a 1 :d 4 :c 3)
      "list"        (list 1 2 3 4 5 (list 6 7 8 (list 9 10)))
      "list-quoted" '(1 2 3 4 5 (6 7 8 (9 10)))
      "list-empty"  (list)
      "vector"      [1 2 3 4 5 [6 7 8 [9 10]]]
      "vector-empty" []
      "map"         {:a 1 :b 2 :c 3 :d {:e 4 :f {:g 5 :h 6 :i 7}}}
      "map-empty"   {}
      "set"         #{1 2 3 4 5 #{6 7 8 #{9 10}}}
      "set-empty"   #{}
      "meta"        (with-meta {:a :A} {:metakey :metaval})
      ;"lazy-seq"   test-lazy-seq 
      "byte"        (byte 16)
      "short"       (short 42)
      "integer"     (int 42)
      "long"        (long 42)
      "bigint"      (bigint 31415926535897932384626433832795)
      "float"       (float 3.14)
      "double"      (double 3.14)
      "bigdec"      (bigdec 3.1415926535897932384626433832795)
      "ratio"       22/7
      "uuid"        test-uuid 
      "date"        test-date )))

(def serializing-transcoder (make-transcoder :serializing))
(deftest test-serializing-transcoder
  (testing "roundtrips values correctly"
    (are [k v]
      (do (c/set tc k 1 v serializing-transcoder)
          (is (= v (c/get tc k serializing-transcoder))))
      "l42" 42
      "l0"  0
      "l-1" -1
      "boolean"     true
      "char-utf8"   \ಬ
      ;"bytes"       (byte-array [(byte 1) (byte 2) (byte 3)])
      "ascii"       "abcdefghi"
      "utf-8"       "ಬಾ ಇಲ್ಲಿ ಸಂಭ"
      "long-string" (apply str (range 1000))
      "keyword"     :yep-its-valid
      "ns-keyword"  ::nskeyword
      "queue"       (-> (PersistentQueue/EMPTY) (conj :a :b :c :d :e :f :g)) 
      "queue-empty" (PersistentQueue/EMPTY)
      "sorted-set"  (sorted-set 1 2 3 4 5)
      "sorted-map"  (sorted-map :b 2 :a 1 :d 4 :c 3)
      "list"        (list 1 2 3 4 5 (list 6 7 8 (list 9 10)))
      "list-quoted" '(1 2 3 4 5 (6 7 8 (9 10)))
      "list-empty"  (list)
      "vector"      [1 2 3 4 5 [6 7 8 [9 10]]]
      "vector-empty" []
      "map"         {:a 1 :b 2 :c 3 :d {:e 4 :f {:g 5 :h 6 :i 7}}}
      "map-empty"   {}
      "set"         #{1 2 3 4 5 #{6 7 8 #{9 10}}}
      "set-empty"   #{}
      "meta"        (with-meta {:a :A} {:metakey :metaval})
      ;"lazy-seq"   test-lazy-seq 
      "byte"        (byte 16)
      "short"       (short 42)
      "integer"     (int 42)
      "long"        (long 42)
      "bigint"      (bigint 31415926535897932384626433832795)
      "float"       (float 3.14)
      "double"      (double 3.14)
      "bigdec"      (bigdec 3.1415926535897932384626433832795)
      "ratio"       22/7
      "uuid"        test-uuid 
      "date"        test-date )))

