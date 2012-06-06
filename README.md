# What is Spyglass

Spyglass is a very fast Clojure client for Memcached built on top of [SpyMemcached](http://code.google.com/p/spymemcached/)



## Community

[Spyglass has a mailing list](https://groups.google.com/forum/#!forum/clojure-memcached). Feel free to join it and ask any questions you may have.

To subscribe for announcements of releases, important changes and so on, please follow [@ClojureWerkz](https://twitter.com/#!/clojurewerkz) on Twitter.



## Project Maturity

Spyglass is *very* young and still not 100% feature complete. It is, however, built on a very solid Java client, [SpyMemcached](http://code.google.com/p/spymemcached/) and
has good test coverage (using adapted [Memcached client test suite](https://github.com/dustin/memcached-test/blob/master/testClient.py) by Dustin Sallings).

As such, you can confidently use it, although it is not yet ready for 1.0 or even RC releases.



## Artifacts

### The Most Recent Release

With Leiningen:

    [clojurewerkz/spyglass "1.0.0-beta1"]


With Maven:

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>spyglass</artifactId>
      <version>1.0.0-beta1</version>
    </dependency>



## Documentation & Examples

Our documentation site is not yet live, sorry. Our [test suite](https://github.com/clojurewerkz/spyglass/tree/master/test/) has plenty of code examples.



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
