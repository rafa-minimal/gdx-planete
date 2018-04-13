#!/bin/bash

if [ -d ".idea" ]; then
    if [ -d ".idea-as" ]; then
        echo "Switched to android"
        mv .idea .idea-id
        mv .idea-as .idea
    elif [ -d ".idea-id" ]; then
        echo "Switched to desktop"
        mv .idea .idea-as
        mv .idea-id .idea
    fi
fi
sleep 1