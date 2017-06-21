## Changes between 1.2.0 and 1.3.0 (unreleased)

No changes yet.


## Changes between 1.1.0 and 1.2.0 (June 21st, 2017)

### `async-get-and-touch`

`async-get-and-touch` is a new function available for binary
protocol connections.

Contributed by Ryan Wilson.


### Transcoder Support

Contributed by Timo Sulg.


### Clojure 1.8 by Default

This project now depends on Clojure 1.8 by default.


## Changes between 1.1.0-beta3 and 1.1.0

### Clojure 1.4 Requirement

Spyglass `1.1.0` **drops support for Clojure 1.3**.


### Heroku Add-on Support

By using SpyMemcached `2.8.9`, you now can use Spyglass with Heroku
Memcached add-ons:

``` clojure
(defproject my-great-project "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clojurewerkz/spyglass "1.1.0-beta6-SNAPSHOT"
                  :exclusions [spy/spymemcached]]
                 [spy/spymemcached "2.8.9"]]
  :repositories {"spy-memcached" {:url "http://files.couchbase.com/maven2/"}})
```

Contributed by Connor Mendenhall.


## Changes between 1.1.0-beta2 and 1.1.0-beta3

Warning: `1.1.0-beta3` has **breaking changes** in `clojurewerkz.spyglass.cache`.

### Clojure 1.5 By Default

Spyglass now depends on `org.clojure/clojure` version `1.5.1`. It is
still compatible with Clojure 1.3+ and if your `project.clj` depends
on a different version, it will be used, but 1.5 is the default now.

We encourage all users to upgrade to 1.5, it is a drop-in replacement
for the majority of projects out there.

### Asynchronous Cache Store

Spyglass now ships both sync and async implementations of [clojure.core.cache](https://github.com/clojure/core.cache).

To instantiate async store, use `clojurewerkz.spyglass.cache/async-spyglass-cache-factory`.
`clojurewerkz.spyglass.cache/spyglass-cache-factory` was renamed to `clojurewerkz.spyglass.cache/sync-spyglass-cache-factory`.

Contributed by [Joseph Wilk](https://github.com/josephwilk).

### Fix Authentication Support

`clojurewerkz.spyglass.client/text-connection` and `clojurewerkz.spyglass.client/bin-connection`
no longer fail when credentials are passed in.


### Empty gets Responses

`clojurewerkz.spyglass.client/gets` now correctly handles responses for
keys that do not exist.

GH issue: #4.


## Changes between 1.1.0-beta1 and 1.1.0-beta2

### SASL (Authentication) Support

`clojurewerkz.spyglass.client/text-connection` and `clojurewerkz.spyglass.client/bin-connection`
now support credentials:

(ns my.service
  (:require [clojurewerkz.spyglass.client :as c]))

;; uses credentials from environment variables, e.g. on Heroku:
(c/text-connection "127.0.0.1:11211" (System/getenv "MEMCACHE_USERNAME")
                                     (System/getenv "MEMCACHE_PASSWORD"))

When you need to fine tune things and want to use a custom connection factory, you need
to instantiate *auth descriptor* and pass it explicitly, like so:

``` clojure
(ns my.service
  (:require [clojurewerkz.spyglass.client :as c])
  (:import [net.spy.memcached.auth AuthDescriptor]))

(let [ad (AuthDescriptor/typical (System/getenv "MEMCACHE_USERNAME")
                                 (System/getenv "MEMCACHE_PASSWORD"))]
  (c/text-connection "127.0.0.1:11211" (c/text-connection-factory :failure-mode :redistribute
                                                                  :aut-descriptor ad)))
```



## Changes between 1.0.0 and 1.1.0-beta1

### Blocking Deref for Futures

Futures returned by async Spyglass operations now implement "blocking dereferencing":
they can be dereferenced with a timeout and default value, just like futures created
with `clojure.core/future` and similar.

Contributed by Joseph Wilk.



### Support For Configurable Connections

New functions `clojurewerkz.spyglass.client/text-connection-factory` and
`clojurewerkz.spyglass.client/bin-connection-factory` provide a Clojuric
way of instantiating connection factories. Those factories, in turn, can be
passed to new arities of `clojurewerkz.spyglass.client/text-connection` and
`clojurewerkz.spyglass.client/bin-connection` to control failure mode,
default transcoder and so on:

``` clojure
(ns my.service
  (:require [clojurewerkz.spyglass.client :as c]))

(c/text-connection "127.0.0.1:11211" (c/text-connection-factory :failure-mode :redistribute))
```


### core.cache Implementation

`clojurewerkz.spyglass.cache` now provides a `clojure.core.cache` implementation on top of
Memcached:

``` clojure
(ns my.service
  (:require [clojurewerkz.spyglass.client :as sg]
            [clojurewerkz.spyglass.cache  :as sc]
            [clojure.core.cache           :as cc]))

(let [client (sg/text-connection)
      cache  (sc/sync-spyglass-cache-factory)]
      (cc/has? cache "a-key")
      (cc/lookup cache "a-key"))
```

`SyncSpyglassCache` uses synchronous operations from `clojurewerkz.spyglass.client`. Asynchronous implementation
that returns futures will be added in the future.


### SpyMemcached 2.8.4

SpyMemcached has been upgraded to `2.8.4`.


### Improved Couchbase Support

`clojurewerkz.spyglass.couchbase/connection` is a new function that connects to Couchbase with the given
bucket and credentials. It returns a client that regular `clojurewerkz.spyglass.memcached` functions can
use.


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
