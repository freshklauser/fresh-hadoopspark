

常用：

```
hdfs dfs -mkdir /dajiangtai       	//新建hdfs目录 （ -p, 可迭代创建多级目录）

hdfs dfs -rm   -r -f   /hbase       //删除hdfs不为空目录hbase
hdfs dfs -rm   -r -f   /windows     //删除hdfs不为空目录windows

# hdfs dfs -put <local_path> <hdfsdestpath>
hdfs dfs -put djt.txt /dajiangtai    //上传文件到hdfs上 本地路径:djt.txt; /dajiangtai:hdfs路径
hdfs dfs -put /home/zhangsf/aaa.txt  /input

# 从hdfs下载文件到本地 hdfs dfs -get <hdfs_path> <localdestpath>
[root@node4 ~]# hdfs dfs -get /word.txt /usr/hdp/2.6.3.0-235/spark2/upload-lyu/
# 从 hdfs 中删除文件
[hdfs@node4 root]$ hdfs dfs -rm -r /word.txt

hdfs dfs -ls /klaus					// 列出 hdfs目录klaus下的文件(夹)列表

hdfs dfs -cat /dajiangtai/djt.txt    
hdfs dfs -cat /spark/hellospark 	//查看hdfs上的文件
hdfs dfs -cat hdfs://192.168.10.201/spark/hellospark 
hdfs dfs -cat hdfs://192.168.10.201:8020/spark/hellospark7234	# 完整的hdfs目录
```



```
Usage: hadoop fs [generic options]
	[-appendToFile <localsrc> ... <dst>]
	[-cat [-ignoreCrc] <src> ...]
	[-checksum <src> ...]
	[-chgrp [-R] GROUP PATH...]
	[-chmod [-R] <MODE[,MODE]... | OCTALMODE> PATH...]
	[-chown [-R] [OWNER][:[GROUP]] PATH...]
	[-copyFromLocal [-f] [-p] [-l] <localsrc> ... <dst>]
	[-copyToLocal [-p] [-ignoreCrc] [-crc] <src> ... <localdst>]
	[-count [-q] [-h] [-v] [-t [<storage type>]] [-u] <path> ...]
	[-cp [-f] [-p | -p[topax]] <src> ... <dst>]
	[-createSnapshot <snapshotDir> [<snapshotName>]]
	[-deleteSnapshot <snapshotDir> <snapshotName>]
	[-df [-h] [<path> ...]]
	[-du [-s] [-h] <path> ...]
	[-expunge]
	[-find <path> ... <expression> ...]
	[-get [-p] [-ignoreCrc] [-crc] <src> ... <localdst>]
	[-getfacl [-R] <path>]
	[-getfattr [-R] {-n name | -d} [-e en] <path>]
	[-getmerge [-nl] <src> <localdst>]
	[-help [cmd ...]]
	[-ls [-C] [-d] [-h] [-q] [-R] [-t] [-S] [-r] [-u] [<path> ...]]
	[-mkdir [-p] <path> ...]
	[-moveFromLocal <localsrc> ... <dst>]
	[-moveToLocal <src> <localdst>]
	[-mv <src> ... <dst>]
	[-put [-f] [-p] [-l] <localsrc> ... <dst>]
	[-renameSnapshot <snapshotDir> <oldName> <newName>]
	[-rm [-f] [-r|-R] [-skipTrash] [-safely] <src> ...]
	[-rmdir [--ignore-fail-on-non-empty] <dir> ...]
	[-setfacl [-R] [{-b|-k} {-m|-x <acl_spec>} <path>]|[--set <acl_spec> <path>]]
	[-setfattr {-n name [-v value] | -x name} <path>]
	[-setrep [-R] [-w] <rep> <path> ...]
	[-stat [format] <path> ...]
	[-tail [-f] <file>]
	[-test -[defsz] <path>]
	[-text [-ignoreCrc] <src> ...]
	[-touchz <path> ...]
	[-truncate [-w] <length> <path> ...]
	[-usage [cmd ...]]

Generic options supported are
-conf <configuration file>     specify an application configuration file
-D <property=value>            use value for given property
-fs <local|namenode:port>      specify a namenode
-jt <local|resourcemanager:port>    specify a ResourceManager
-files <comma separated list of files>    specify comma separated files to be copied to the map reduce cluster
-libjars <comma separated list of jars>    specify comma separated jar files to include in the classpath.
-archives <comma separated list of archives>    specify comma separated archives to be unarchived on the compute machines.
```

