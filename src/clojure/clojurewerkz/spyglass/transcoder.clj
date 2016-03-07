(ns clojurewerkz.spyglass.transcoder
  "Transcoder is an interface for classes that convert between byte arrays
  and objects for storage in the cache."
  (:require [taoensso.nippy :as nippy])
  (:import [net.spy.memcached CachedData]
           [net.spy.memcached.transcoders
              Transcoder IntegerTranscoder LongTranscoder
              SerializingTranscoder WhalinTranscoder]))

(defmulti make-transcoder identity)

(defmethod make-transcoder :integer [_]
  (IntegerTranscoder.))

(defmethod make-transcoder :long [_]
  (LongTranscoder.))

(defmethod make-transcoder :whalin [_]
  (WhalinTranscoder.))

(defmethod make-transcoder :serializing [_]
  (SerializingTranscoder.))


;;experimental Nippy transcoder
(deftype NippyTranscoder []
  net.spy.memcached.transcoders.Transcoder
  (asyncDecode [this dt] false)
  (getMaxSize [this]
    (int CachedData/MAX_SIZE))

  (decode [this dt]
    (-> dt (.getData) nippy/thaw))

  (encode [this clj-obj]
    (CachedData. (int 0)
                 (nippy/freeze clj-obj)
                 CachedData/MAX_SIZE)))

(defmethod make-transcoder :nippy [_]
  (NippyTranscoder.))

(comment
  (require '[clojurewerkz.spyglass.client :as c])
  (require '[clojurewerkz.spyglass.transcoder :as t] :reload)
  (def tc (c/text-connection "192.168.99.100:11211"))
  
  (def nippler (t/make-transcoder :nippy))
  @(c/set tc "abc" 30 (long 123) nippler)
  (c/get tc "abc" nippler)


  )

