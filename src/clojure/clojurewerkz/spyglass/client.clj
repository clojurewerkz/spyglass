(ns clojurewerkz.spyglass.client
  (:refer-clojure :exclude [set get])
  (:import [net.spy.memcached MemcachedClient DefaultConnectionFactory BinaryConnectionFactory AddrUtil]))




(defn- servers
  [^String server-list]
  (AddrUtil/getAddresses server-list))

(defn text-connection
  "Returns a new text protocol client that will use the provided list of servers."
  [^String server-list]
  (MemcachedClient. (DefaultConnectionFactory.) (servers server-list)))

(defn bin-connection
  "Returns a new binary protocol client that will use the provided list of servers."
  [^String server-list]
  (MemcachedClient. (BinaryConnectionFactory.) (servers server-list)))


(defn flush
  "Flush all caches from all servers. One-arity version flushes all caches
   immediately, two-arity version does it after the provided delay in seconds."
  ([^MemcachedClient client]
     (.flush client))
  ([^MemcachedClient client ^long delay]
     (.flush client delay)))

(defn set
  "Set an object in the cache (using the default transcoder) regardless of any existing value."
  [^MemcachedClient client ^String key ^long expiration value]
  (.set client key expiration value))

(defn get
  "Get with a single key and decode using the default transcoder."
  [^MemcachedClient client ^String key]
  (.get client key))

(defn get-multi
  "Get the values for multiple keys from the cache. Returns a map of results."
  [^MemcachedClient client ^java.util.Collection keys]
  (into {} (.getBulk client keys)))

(defn delete
  "Delete the given key from the cache."
  [^MemcachedClient client ^String key]
  (.delete client key))

(defn touch
  "Touch the given key to reset its expiration time."
  [^MemcachedClient client ^String key ^long expiration]
  (.touch client key expiration))

(defn add
  "Add an object to the cache (using the default transcoder) if it does not exist already.
   Returns a future that will return false if a mutation did not occur, true otherwise."
  [^MemcachedClient client ^String key ^long expiration value]
  (.add client key expiration value))

(defn replace
  "Replace an object to the cache (using the default transcoder) if there is already a value for the given key.
   Returns a future that will return false if a mutation did not occur (the key was missing), true otherwise."
  [^MemcachedClient client ^String key ^long expiration value]
  (.replace client key expiration value))

;; TODO: incr
;; TODO: decr
;; TODO: cas

(defn get-versions
  "Get the versions of all of the connected Memcached instances."
  [^MemcachedClient client]
  (into {} (.getVersions client)))

(defn shutdown
  "Shuts down the client. One-arity forces immediate shutdown. Three-arity shuts down gracefully
   (lets in-progress operations to finish, up to the given amount of time)."
  ([^MemcachedClient client]
     (.shutdown client))
  ([^MemcachedClient client ^long timeout ^java.util.concurrent.TimeUnit time-unit]
     (.shutdown client timeout time-unit)))
