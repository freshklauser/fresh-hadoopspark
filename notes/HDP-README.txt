hdp大大数据平台
211/212/213/214/215
该目录下的hadoop-local是本地装的hadoop, hadoop是从集群node1节点直接复制过来的hadoop


1.ambai ResourceManager webui： node1:8080
        hadoop namenode webui： node1:50070

2.spark 位于 node3节点 
  kafka 主节点在 node3 有(node1中不是主节点)
		broker端口：6667 (设置了listener则是以listener的端口为准)
  zookeeper 主节点在 node2 和 node4 中，默认端口 clientPort=2181
		topic的元信息是保存在zk中，因此--create是在 --zookeeper {ip}:{port}中创建目录	
		producer是在--broker-list中生产
		consumer是在--bootstrap-server中消费

3.spark history server（node3）
	webui界面： node3:18081
4.hadoop cluster webui：node2:8088	
5.hdfs完整路徑(ip:port)：hdfs://node1:8020/		# 根目录   wifiprobe的示例中默認設置的是9000，需要改成8020

6.Hbase RegionServer node4:16030
  Hbase页面：http://192.168.1.211:16010


 
1-4:
root   --->  xxxxxxx
centos --->  centosxxxxxxx
1-4:mysql
root   --->  root
5:
root   ---> admin
centos ---> asd123456
5:mysql
root   ---> root

写代码的pc上的hosts文件中需添加 服务器各个节点的ip与hostname的映射关系,以为ip都是通过映射的域名访问，不设置的话在本机上访问服务器的 "node4:18081"等会无法访问



idea打包-->提交yarn集群运算
1) idea利用maven打包成jar

2) jar上传到服务器任一个节点（xftp上传），这里选择上传至node4节点（spark安装的节点）

3) node4节点中上传文件到hdfs文件管理系统
	# hdfs 中创建目录
	[hdfs@node1 /]$ hdfs dfs -mkdir -p /klaus/input
	[hdfs@node1 /]$ hdfs dfs -mkdir -p /klaus/output
	# 将文件上传到hdfs的 klaus/input目录下
	hdfs dfs -put /usr/local/spark/word.txt /klaus		
	# hdfs改文件夹名
	hdfs dfs -mv /klaus /lyu
	# hdfs删除文件夹
	hdfs dfs -rm -r -f /output
	
4) spark所在节点进入spark根目录 ./bin/spark-submit xxxxx
	其中 文件路径必须为hdfs的路径 hdfs:/xxxxx	
	bin/spark-submit \
	--class com.lyu.test.WordCount \	# 全类名 .scala 程序中object名 右键 CopyReference
	--master yarn \
	--deploy-mode cluster 
	./upload-lyu/WordCount-jar-with-dependencies.jar \
	/lyu/input/word.txt \			# hdfs的文件目录 <----------------- 注意这里的文件路径是hdfs路径
	/lyu/output/wordcount-output	# hdfs的文件目录，wordcount-output在运行前必须不能存在
	
	bin/spark-submit --class func.gender.core.GenderStatisticV1 --master yarn --deploy-mode cluster ./upload-lyu/ShoppingRealTimeDashBorad-1.0.jar 192.168.1.212:2181 1 gender-shopping 1

	

集群中安装spark的节点node4上以 yarn 模式打开spark-shell:
	[hdfs@node4 spark2]$ bin/spark-shell --master yarn
	Setting default log level to "WARN".
	To adjust logging level use sc.setLogLevel(newLevel). For SparkR, use setLogLevel(newLevel).
	Spark context Web UI available at http://node4:4040
	Spark context available as 'sc' (master = yarn, app id = application_1571802404618_0011).
	Spark session available as 'spark'.
	Welcome to
		  ____              __
		 / __/__  ___ _____/ /__
		_\ \/ _ \/ _ `/ __/  '_/
	   /___/ .__/\_,_/_/ /_/\_\   version 2.2.0.2.6.3.0-235
		  /_/
			 
	Using Scala version 2.11.8 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_112)
	Type in expressions to have them evaluated.
	Type :help for more information.
	scala> 


node5--docker 镜像挂载路径
mysql:   /opt/mysqldatastore/gracesql --> /var/lib/mysql    port:3306
mongodb: /opt/mongodatastore/mongodb  --> /data/db			port:27017
redis:   /opt/redisdatastore/data     --> /data


# >>>>>>>>>>>>>>>>>>>>>>>>>>>>> docker-compose <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
















