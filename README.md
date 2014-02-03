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

    $ java -cp lib/trskop-snippets.jar:dist/UnifiedLogAnalyzer.jar unifiedloganalyzer.UnifiedLogAnalyzer --help
    Usage:
    
      UnifiedLogAnalyzer [--dummy|--strace|--syslog] [{-o|--output} {FILE|-}] {FILE|-}
      UnifiedLogAnalyzer [{-a|--algorithm} ALGORITHM] [{-i|--input-format} INPUT_FORMAT] [{-o|--output} {FILE|-}] {FILE|-}
      UnifiedLogAnalyzer {--list-input-formats|--list-algorithms}
      UnifiedLogAnalyzer {-h|--help}


Dependencies
============

* <https://github.com/trskop/snippets/tree/master/java>
