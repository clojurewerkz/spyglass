# What is Spyglass

Spyglass is a very fast Clojure client for Memcached built on top of [SpyMemcached](http://code.google.com/p/spymemcached/)



## Community

[Spyglass has a mailing list](https://groups.google.com/forum/#!forum/clojure-memcached). Feel free to join it and ask any questions you may have.

To subscribe for announcements of releases, important changes and so on, please follow [@ClojureWerkz](https://twitter.com/#!/clojurewerkz) on Twitter.



## Project Maturity

Spyglass is no longer a young project. It is almost 100% feature complete. Built on a very solid Java client, [SpyMemcached](http://code.google.com/p/spymemcached/) and
has good test coverage (using adapted [Memcached client test suite](https://github.com/dustin/memcached-test/blob/master/testClient.py) by Dustin Sallings).

As such, you can confidently use it, the API is small and locked down.



## Artifacts

Spyglass artifacts are released to Clojars. Spyglass relies on a recent SpyMemcached release so
you may need to add [SpyMemcached repository](https://code.google.com/p/spymemcached/wiki/Maven) first:

### Dependencies

With Leiningen:

``` clojure
;; in project.clj
:repositories {"spy-memcached" {:url "http://files.couchbase.com/maven2/"}}
```

With Maven:

``` xml
<repositories>
  <repository>
    <id>spy</id>
    <name>Spy Repository</name>
    <layout>default</layout>
    <url>http://files.couchbase.com/maven2/</url>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
  </repository>
</repositories>
```

### The Most Recent Stable Release

With Leiningen:

    [clojurewerkz/spyglass "1.0.2"]

With Maven:

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>spyglass</artifactId>
      <version>1.0.2</version>
    </dependency>


### The Most Recent Preview Release

With Leiningen:

    [clojurewerkz/spyglass "1.1.0-beta2"]

With Maven:

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>spyglass</artifactId>
      <version>1.1.0-beta2</version>
    </dependency>



## Documentation & Examples

Spyglass has a [documentation site at clojurememcached.info](http://clojurememcached.info).

Our [test suite](https://github.com/clojurewerkz/spyglass/tree/master/test/) has plenty of code examples, too.



## Supported Clojure versions

Spyglass is built from the ground up for Clojure 1.3 and up.




## Continuous Integration

[![Continuous Integration status](https://secure.travis-ci.org/clojurewerkz/spyglass.png)](http://travis-ci.org/clojurewerkz/spyglass)


CI is hosted by [travis-ci.org](http://travis-ci.org)



## Development

Spyglass uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make sure you have it installed and then run tests against
all supported Clojure versions using

    lein2 all test

Then create a branch and make your changes on it. Once you are done with your changes and all tests pass, submit
a pull request on Github.


## License

Copyright (C) 2012 Michael S. Klishin, Alex Petrov

Distributed under the Eclipse Public License, the same as Clojure.
