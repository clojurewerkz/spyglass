(defproject clojurewerkz/spyglass "1.1.0-SNAPSHOT"
  :description "A Clojure client for Memcached implemented as a very thin layer on top of SpyMemcached"
  :url "http://github.com/clojurewerkz/spyglass"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure        "1.4.0"]
                 [spy/spymemcached           "2.8.1"]
                 [couchbase/couchbase-client "1.0.3"]]
  :repositories {"spy-memcached" {:url "http://files.couchbase.com/maven2/"
                                  :checksum :ignore}
                 "sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}}
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"]
  :javac-options     ["-target" "1.6" "-source" "1.6"]  
  :warn-on-reflection true
  :profiles       {:1.4 {:dependencies [[org.clojure/clojure "1.4.0"]]}
                   :1.5 {:dependencies [[org.clojure/clojure "1.5.0-master-SNAPSHOT"]]}}
  :aliases        {"all" ["with-profile" "dev:1.4:1.5"]}
    :test-selectors {:default     (fn [m]
                                  (and (not (:couchbase m))))
                   :focus         :focus
                   :couchbase     :couchbase
                   :all           (constantly true)})
