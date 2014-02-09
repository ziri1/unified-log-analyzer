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
