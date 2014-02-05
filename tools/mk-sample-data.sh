#!/bin/bash

set -e


declare -r version='0.1.0.0'

printHelp()
{
    local -r progName="${0##*/}"

    cat << EOF
Usage:
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

main()
{
    local -r pathToUse="/bin:/usr/bin:/usr/local/bin"

    if [[ "$0" == */* ]]; then
        local -r parentDir="${0%/*}"
    else
        local -r parentDir="."
    fi

    local -r runStrace="$parentDir/run-strace.sh"

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
          *)
            error 1 '%s: Too many arguments' "$arg"
            ;;
        esac
    done

    {
        printf 'uname:\n  %s\n' "$(uname -srvm)" 
        printf 'lsb_relase:\n%s\n' "$(lsb_release -idrc | sed 's/\t/ /;s/^/  /')"
    } > system-info.txt

    find "$parentDir" -maxdepth 1 -type f -name '*.c' -print0 \
    | while IFS= read -d $'' file; do

        env --ignore-environment PATH="$pathToUse" "$runStrace" \
            --output="${file%.c}-strace.out" \
            "${file%.c}" \
            > "${file%.c}-stdout.out" \
            2> "${file%.c}-stderr.out"
        sed -r -e 's:/home/[^/"]+:/home/user:g' -i "${file%.c}-strace.out"
    done
}

main "$@"
