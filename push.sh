#!/bin/bash
remotes=""
function listRemotes() {
    remotes=`git remote`
}

function push() {
        listRemotes
    for r in $remotes
    do
        echo "准备执行命令 git push $r $@"
        git push $r "$@"
    done
}

push "$@"