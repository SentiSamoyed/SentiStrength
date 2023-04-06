#!/usr/bin/env bash

C_NAME=sentistrength
I_NAME=sentistrength:latest

# 删除已存在的 Docker container
if docker container ls | grep -q "$C_NAME"; then
  docker container stop "$C_NAME"; # 启动时 --rm，无需手动删除
fi;

# 建立新的 container
docker run --rm -d -p "8080:8080" --name "$C_NAME" "$I_NAME"
