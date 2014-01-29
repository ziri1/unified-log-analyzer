#!/bin/bash

set -e


declare -r version='0.1.0.0'

printHelp()
{
    local -r progName="${0##*/}"

    cat << EOF
Usage:

  ${progName} [OPTIONS] [--] COMMAND [ARGS]
  ${progName} {-h|--help|-V|--version}

Options:

  -o FILE, --output=FILE

    Save strace output in to FILE, default is \`strace.out'.
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
    local outputFile="$1"; shift

    strace -v -f -s 2048 -e trace=file -o "$outputFile" -- "$@"
}

main()
{
    local -a cmd=()
    local outputFile='strace.out'
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
          -o)
            if (( $# > 0 )); then
                outputFile="$1"; shift
            else
                error 1 '%s: %s' "$arg" "Missing argument."
            fi
            ;;
          --output=*)
            outputFile="${arg#--output=}"
            ;;
          -*)
            error 1 '%s: Unknown option' "$arg"
            ;;
          --)
            cmd=("$@")
            break
            ;;
          *)
            # Not an option, assuming it's a command.
            cmd=("$arg" "$@")
            break
            ;;
        esac
    done

    _strace "$outputFile" "${cmd[@]}"
}

main "$@"

# vim: tabstop=4 shiftwidth=4 expandtab filetype=sh
