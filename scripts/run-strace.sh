#!/bin/bash

set -e


declare -r version='0.1.0.0'

printHelp()
{
    local -r progName="${0##*/}"

    cat << EOF
Usage:
  ${progName} [--] COMMAND [ARGS]
  ${progName} {-h|--help|-V|--version}
EOF
}

printNumericVersion()
{
    echo "$version"
}

printVersion()
{
    local -r progName="${0##*/}"

    echo "$progName $version"
}

error()
{
    local -r exitCode="$1"; shift
    local -r format="$1"; shift

    printf "Error: $format\n" "$@"

    if [[ "$exitCode" != '-' ]]; then
        exit "$exitCode"
    fi
}

_strace()
{
    strace -v -f -s 2048 -e trace=file -o strace.out -- "$@"
}

main()
{
    local -a cmd=()
    local arg=''

    while (( $# > 0 )); do
        arg="$1"; shift
        case "$arg" in
          -h|--help)
            printHelp
            exit 0
            ;;
          -V|--version)
            printVersion
            exit 0
            ;;
          --numeric-version)
            printNumericVersion
            exit 0
            ;;
          -*)
            error 1 '%s: Unknown option' "$arg"
            ;;
          --)
            cmd=("$@")
            break
            ;;
          *)
            cmd=("$arg" "$@")
            break
            ;;
        esac
    done

    _strace "${cmd[@]}"
}

main "$@"

# vim: tabstop=4 shiftwidth=4 expandtab filetype=sh
