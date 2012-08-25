## Changes between 1.0.0 and 1.1.0

### Clojure 1.4 By Default

Spyglass now depends on `org.clojure/clojure` version `1.4.0`. It is still compatible with Clojure 1.3 and if your `project.clj` depends
on 1.3, it will be used, but 1.4 is the default now.

We encourage all users to upgrade to 1.4, it is a drop-in replacement for the majority of projects out there.


### Recompiled for JDK 6

Spyglass 1.0.1 is compiled for JDK 6.


## Changes between 1.0.0-rc2 and 1.0.0

### Documentation Improvements

[Documentation guides](http://clojurememcached.info) were improved.

### Continuous Integration Against Couchbase

We now run [Continuous Integration](http://travis-ci.org/clojurewerkz/spyglass) against several versions of Couchbase Server.


## Changes between 1.0.0-rc1 and 1.0.0-rc2

### Future results now can be dereferenced

Many Spyglass functions return futures. Starting with the 1.0.0-rc2 release, it is now possible to
deref (`@future`) them.


## Changes between 1.0.0-beta1 and 1.0.0-rc1

### Documentation guides

Spyglass now has [documentation guides at clojurememcached.info](http://clojurememcached.info).



## 1.0.0-beta1

Initial release

Supported features:

 * get, async get
 * multi-get (bulk get), async multi-get
 * delete
 * replace
 * flush
 * add, incr, decr (need to use custom transcoders because of the SpyMemcached idiosyncracies)
 * gets, CAS, async CAS
