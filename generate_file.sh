#!/bin/bash

for i in {1..20}
do
    FILE_NAME="file_$i.txt"

    if [ -f "$FILE_NAME" ]; then
        # 获取当前日期和时间
        CURRENT_TIME=$(date '+%Y-%m-%d %H:%M:%S')

        # 定义追加内容
        RANDOM_STRING="在 $CURRENT_TIME 添加的新内容"

        # 将内容追加到文件末尾
        echo "$RANDOM_STRING" >> "$FILE_NAME"

        echo "文件 $FILE_NAME 已更新，追加内容：$RANDOM_STRING"
    else
        echo "文件 $FILE_NAME 不存在，跳过。"
    fi
done
