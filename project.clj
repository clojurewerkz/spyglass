(defproject clojurewerkz/spyglass "1.2.0-SNAPSHOT"
  :description "A Clojure client for Memcached implemented as a very thin layer on top of SpyMemcached"
  :url "http://github.com/clojurewerkz/spyglass"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [net.spy/spymemcached "2.11.4"]
                 [com.couchbase.client/java-client "2.0.0"]]
  :repositories {"sonatype" {:url "http://oss.sonatype.org/content/repositories/releases"
                             :snapshots false
                             :releases {:checksum :fail :update :always}}
                 "sonatype-snapshots" {:url "http://oss.sonatype.org/content/repositories/snapshots"
                                       :snapshots true
                                       :releases {:checksum :fail :update :always}}}
  :source-paths      ["src/clojure"]
  :java-source-paths ["src/java"]
  :javac-options     ["-target" "1.6" "-source" "1.6"]
  :warn-on-reflection true
  :profiles       {:1.5 {:dependencies [[org.clojure/clojure "1.5.1"]]}
                   :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
                   :master {:dependencies [[org.clojure/clojure "1.8.0-master-SNAPSHOT"]]}
                   :dev {:resource-paths ["test/resources"]
                         :dependencies [[org.clojure/core.cache "0.6.2" :exclusions [org.clojure/clojure]]]
                         :plugins [[codox "0.8.10"]]
                         :codox {:sources ["src/clojure"]
                                 :output-dir "doc/api"}}}
  :aliases        {"all" ["with-profile" "dev:dev,1.5:dev,1.7:dev,master"]}
  :test-selectors {:default     (fn [m]
                                  (and (not (:couchbase m))))
                   :focus         :focus
                   :couchbase     :couchbase
                   :all           (constantly true)})
