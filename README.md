Description
===========

Framework and application for extensible multi-purpose log analysis.


Implementation Status
=====================

Currently Implemented
---------------------

* Dummy parser and dummy analyzer for debugging purposes, but might be useful
  in practice too.
* Partial strace parser implementation.
* Partial implementation of analyzer for strace messages that lists all opened
  files with their absolute value.


To Be Implemented
-----------------

* Syslog message parser with support for some variations, but traditional
  syslog format will be first.
* Path categorization analyzer. It will annotate file paths with additional
  information based on file path and file format (using libmagic).


Usage
=====

There is a shell wrapper provided for \*NIX/Linux systems:

    bin/unifiedloganalyzer

Just create symbolic link to it from any directory in your path or as an
alternative you can add the `bin` directory where this script is stored in to
your `$PATH`. Don't move it, because script uses its own path to find the rest.

With this wrapper we can now use this software more simply:

    $ unifiedloganalyzer --help
    Usage:

      unifiedloganalyzer [--dummy|--strace|--syslog] [{-o|--output} {FILE|-}] {FILE|-}

      unifiedloganalyzer [{-a|--algorithm} ALGORITHM] [{-i|--input-format} INPUT_FORMAT] [{-o|--output} {FILE|-}] {FILE|-}

      unifiedloganalyzer {--list-input-formats|--list-algorithms}

      unifiedloganalyzer {-h|--help}


Dependencies
============

* <https://github.com/trskop/snippets/tree/master/java>
* <https://github.com/trskop/magic-file-java-6>


Installation
============
Tested on Ubuntu 18.04 and Ubuntu 20.04

sudo apt-get install libmagic-dev libcommons-io-java junit4 ant openjdk-11-jdk-headless

git clone https://github.com/trskop/snippets/
pushd snippets/java
ant
popd

git clone https://github.com/trskop/magic-file-java-6
pushd magic-file-java-6
#edit nbproject/project.properties - replace commons-io-2.4 with commons-io-2.6 and add path to /usr/share/java/junit4.jar
ln -s /usr/share/java/commons-io-2.6.jar lib
make JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ant
popd

git clone https://github.com/ziri1/unified-log-analyzer
cd unified-log-analyzer
ln -s ../../magic-file-java-6/dist/magic-file-java-6.jar lib
ln -s ../../snippets/java/dest/jar/trskop-snippets.jar lib
ln -s /usr/share/java/commons-io-2.6.jar lib
ant -k
