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

### Artifact Repositories

Spyglass artifacts are [released to Clojars](https://clojars.org/clojurewerkz/spyglass).

### The Most Recent Release

With Leiningen:

    [clojurewerkz/spyglass "1.2.0"]

With Maven:

    <dependency>
      <groupId>clojurewerkz</groupId>
      <artifactId>spyglass</artifactId>
      <version>1.2.0</version>
    </dependency>



## Documentation & Examples

Spyglass has a [documentation site at clojurememcached.info](http://clojurememcached.info).

Our [test suite](https://github.com/clojurewerkz/spyglass/tree/master/test/) has plenty of code examples, too.



## Supported Clojure Versions

Spyglass requires Clojure 1.4+.




## Continuous Integration

[![Continuous Integration status](https://secure.travis-ci.org/clojurewerkz/spyglass.png)](http://travis-ci.org/clojurewerkz/spyglass)


CI is hosted by [travis-ci.org](http://travis-ci.org)

## Dependencies

[![Dependencies Status](https://jarkeeper.com/clojurewerkz/spyglass/status.png)](https://jarkeeper.com/clojurewerkz/spyglass)

## Development

Spyglass uses [Leiningen 2](https://github.com/technomancy/leiningen/blob/master/doc/TUTORIAL.md). Make sure you have it installed and then run tests against
all supported Clojure versions using

    lein all test

Then create a branch and make your changes on it. Once you are done with your changes and all tests pass, submit
a pull request on Github.


## License

Copyright (C) 2012-2016 [Michael S. Klishin](http://twitter.com/michaelklishin), Alex Petrov

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html), the same as Clojure.
