% OpenStage Build Logs Analysis
% Notes taken by Peter
% 17 January 2014


Strace poziadavky
=================

- Find a way how strace could send its output to specified file descriptor.
- Relative path handling.
- Symbolic link handling, e.g. will open() give us path to link or to its
  target?
- `strace -v -f -s 2048 -e trace=file <command>`
    * `-v` -- Verbose output.
    * `-f` -- Follow forks.
    * `-s 2048` -- Use 2kB buffer for printed strings.
    * `-e trace=file` -- Print only file related system calls.
- Use timestamps?
- What are the cases when it won't work?
- What about resument system calls?


Obecne poziadavky
=================

- More then one source, otuput, and parser. Make it generic so we can extend it
  for various purposes.
- Parser can be choosed using version number of strace or using command line
  option.
- Start it all with file input and file output. It's a simple case and it also
  can be already used for real work.

vim: tabstop=4 shiftwidth=4 expandtab filetype=markdown
