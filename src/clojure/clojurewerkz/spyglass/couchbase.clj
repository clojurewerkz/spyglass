(ns clojurewerkz.spyglass.couchbase
  (:import java.net.URI
           java.util.List
           com.couchbase.client.CouchbaseClient))

;;
;; Implementation
;;

(defprotocol URILike
  (^java.net.URI to-uri [input] "Coerces the input to java.net.URI"))

(extend-protocol URILike
  URI
  (to-uri [^URI uri]
    uri)

  String
  (to-uri [^String s]
    (URI. s)))



;;
;; API
;;

(def ^{:const true} default-bucket   "default")
(def ^{:const true} default-password "")

(defn ^com.couchbase.client.CouchbaseClient connection
  ([^List bases]
     (CouchbaseClient. (map to-uri bases) default-bucket default-password))
  ([^List bases ^String bucket ^String password]
     (CouchbaseClient. (map to-uri bases) bucket password))
  ([^List bases ^String bucket ^String username ^String password]
     (CouchbaseClient. (map to-uri bases) bucket username password)))
