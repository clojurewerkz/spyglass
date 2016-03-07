(ns clojurewerkz.spyglass.transcoder
  "Transcoder is an interface for classes that convert between byte arrays
  and objects for storage in the cache."
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
