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

(defmacro defspyglasscache [type-name fields & specifics]
  `(cache/defcache ~type-name ~fields
     ~@specifics
     cache/CacheProtocol
     (has? [c# k#]
           (not (empty? (kv/get (.client c#) k#))))
     (hit [cache# k]
          cache#)
     (miss [cache# k# v#]
           (kv/set (.client cache#) k# (.ttl cache#) v#)
           cache#)
     (evict [cache# k#]
            (kv/delete (.client cache#) k#)
            cache#)
     (seed [cache# m#]
           (doseq [[k# v#] m#]
             (kv/set (.client cache#) k# (.ttl cache#) v#))
           cache#)))

(defspyglasscache AsyncSpyglassCache [^MemcachedClient client ttl]
  cache/CacheProtocol
  (lookup [cache k]
          (kv/async-get (.client cache) k)))

(defspyglasscache SyncSpyglassCache [^MemcachedClient client ttl]
  cache/CacheProtocol
  (lookup [cache k]
          (kv/get (.client cache) k)))

(defn sync-spyglass-cache-factory
  ([^MemcachedClient client]
     (SyncSpyglassCache. client 60000))
  ([^MemcachedClient client ^long ttl]
     (SyncSpyglassCache. client ttl))
  ([^MemcachedClient client ^long ttl base]
     (cache/seed (SyncSpyglassCache. client ttl) base)))

(defn async-spyglass-cache-factory
  ([^MemcachedClient client]
     (AsyncSpyglassCache. client 60000))
  ([^MemcachedClient client ^long ttl]
     (AsyncSpyglassCache. client ttl))
  ([^MemcachedClient client ^long ttl base]
     (cache/seed (AsyncSpyglassCache. client ttl) base)))
