1. 实际生产中，离线的job因为都会放在夜里执行
   因此，通常需要设置在0点滚动文件 （比24小时滚动一次更合理）
   但是 hdfs.roundUnit 只有 Second, Minute 和 Hour 级别，没有在0点round的参数，可以修改jar包源码自定义flag后重新打包,替换
   flume/lib中的 flume-hdfs-sink-1.8.0.jar
   refer: https://blog.csdn.net/simonchi/article/details/45365377
   
2. 