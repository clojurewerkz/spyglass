(ns ^{:doc "clojure.core.cache implementation(s) on top of Memcached."
      :author "Michael S. Klishin"}
  clojurewerkz.spyglass.cache
  (:require [clojurewerkz.spyglass.client :as kv]
            [clojure.core.cache    :as cache])
  (:import clojure.core.cache.CacheProtocol
           net.spy.memcached.MemcachedClient))


;;
;; API
;;

(cache/defcache SyncSpyglassCache [^MemcachedClient client ttl]
  cache/CacheProtocol
  (lookup [cache k]
          (kv/get (.client cache) k))
  (has? [c k]
    (not (empty? (kv/get (.client c) k))))
  (hit [cache k]
    cache)
  (miss [cache k v]
    (kv/set (.client cache) k (.ttl cache) v)
    cache)
  (evict [cache k]
    (kv/delete (.client cache) k)
    cache)
  (seed [cache m]
    (doseq [[k v] m]
      (kv/set (.client cache) k (.ttl cache) v))
    cache))

(defn sync-spyglass-cache-factory
  ([^MemcachedClient client]
     (SyncSpyglassCache. client 60000))
  ([^MemcachedClient client ^long ttl]
     (SyncSpyglassCache. client ttl))
  ([^MemcachedClient client ^long ttl base]
     (cache/seed (SyncSpyglassCache. client ttl) base)))
