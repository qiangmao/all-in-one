#!/bin/bash

# 循环生成 20 个文件
for i in {1..20}
do
    # 生成一个随机字符串（长度为16，由大小写字母和数字组成）
    RANDOM_STRING=$(head /dev/urandom | tr -dc 'A-Za-z0-9' | head -c 16)
    
    # 定义文件名，例如 file_1.txt 到 file_20.txt
    FILE_NAME="file_$i.txt"
    
    # 将随机字符串写入文件
    echo "$RANDOM_STRING" > "$FILE_NAME"
    
    # 输出创建的文件信息（可选）
    echo "创建了文件 $FILE_NAME，内容为：$RANDOM_STRING"
done
