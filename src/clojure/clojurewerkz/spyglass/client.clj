(ns clojurewerkz.spyglass.client
  (:refer-clojure :exclude [set get])
  (:import [net.spy.memcached MemcachedClient DefaultConnectionFactory BinaryConnectionFactory AddrUtil]))




(defn- servers
  [^String server-list]
  (AddrUtil/getAddresses server-list))

(defn text-connection
  "Returns a new text protocol client that will use the provided list of servers"
  [^String server-list]
  (MemcachedClient. (DefaultConnectionFactory.) (servers server-list)))

(defn bin-connection
  "Returns a new binary protocol client that will use the provided list of servers"
  [^String server-list]
  (MemcachedClient. (BinaryConnectionFactory.) (servers server-list)))


(defn set
  "Set an object in the cache (using the default transcoder) regardless of any existing value"
  [^MemcachedClient client ^String key ^long expiration value]
  (.set client (name key) expiration value))

(defn get
  "Get with a single key and decode using the default transcoder"
  [^MemcachedClient client ^String key]
  (.get client (name key)))

(defn touch
  "Touch the given key to reset its expiration time"
  [^MemcachedClient client ^String key ^long expiration]
  (.touch client (name key) expiration))