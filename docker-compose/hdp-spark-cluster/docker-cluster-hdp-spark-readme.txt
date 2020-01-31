基于uhopper的hadoop和spark镜像，hadoop版本：2.8.1， spark版本：2.1.2
refer: https://www.jianshu.com/p/3ca4c759d3d8
refer: https://www.jianshu.com/p/e44e948b8d5f   (宿主机spark-yarn配置)


挂载路径会在宿主机中自动创建

hadoop端口：
50070: namenode 		--> namode-50070.png
50075: datanode1 		--> datanode1-50075.png
50072: datanode2		--> datanode2-50072.png
50073: datanode3		--> datanode3-50073.png
8088： resourcemanager  --> resourcemanager-8088.png
8042:  nodemanager 		--> nodemanager-8042.png



------------------------- 启动 -------------------------------
# docker-compose.yml 当前目录 --> uhopper/
$ docker-compose up -d

------------------------- 宿主机spark以远程服务器的yarn模式运行 -------------------------------
--> 宿主机 spark 连接 docker 中的 hadoop 集群：
	不管是使用 spark 还是其他服务，只要我们要连接使用 docker 搭建的 hadoop 集群，我们需要配置一些参数。如果使用 spark，宿主机需要有 spark 和 hadoop 的完整程序包。

1. 宿主机配置docker假域名
在 /etc/hosts 下配置上述 docker 容器中出现的 hostname，将这些域名都指向本机(宿主机) IP，192.168.13.128 为本机 ip：
	192.168.13.128 namenode
	192.168.13.128 resourcemanager
	192.168.13.128 nodemanager
	192.168.13.128 datanode1
	192.168.13.128 datanode2
	192.168.13.128 datanode3
	
2.配置 $HADOOP_HOME/etc/hadoop 目录下 core-site.xml 和 yarn-site.xml
core-site.xml 增加指向 namenode 的配置，其中 hdfs://namenode:8020 指向 docker 容器中的 namenode host，因此这里我们需要在 /etc/hosts 中加入此域名，并配置为宿主机 ip
	<configuration>
		<property>
			<name>fs.defaultFS</name>
			<value>hdfs://namenode:8020</value>
		</property>
		
	</configuration>

3.配置 $SPARK_HOME/conf/spark-env.sh，暴露 hadoop_home 目录
	export HADOOP_CONF_DIR=/home/maple/app/hadoop-2.8.5/etc/hadoop
	
4. local模式提交
klaus@messi spark2.1.2]$ spark-submit --class org.apache.spark.examples.SparkPi --master local examples/jars/spark-examples_2.11-2.1.2.jar 50

>>>>>>>>> 以上配置后如果使用yarn模式 spark-shell --master yarn 会报 WARN 如下：
	WARN yarn.Client: Neither spark.yarn.jars nor spark.yarn.archive is set, falling back to uploading libraries under SPARK_HOME.
>>>>>>>>> 
解决方法如5 ，refer: https://www.jianshu.com/p/e44e948b8d5f   (宿主机spark-yarn配置)

5.spark-yarn jars上传至hdfs
1)、HDFS上创建存放spark jar的目录
[klaus@messi ~]$ hdfs dfs -mkdir -p /spark-yarn/jars

2)、将$SPARK_HOME/jars下的jar包上传至刚建的HDFS路径
[klaus@messi ~]$ cd /usr/local/spark2.1.2/jars/
[klaus@messi jars]$ hdfs dfs -put * /spark-yarn/jars/

3)、在 spark-defaults.conf中添加
spark.yarn.jars=hdfs://hadoop000:8020/spark-yarn/jars/*.jar

注意：
	(1) 采用该docker部署集群后，spark on yarn 还是会报错，其中一只是jvm虚拟内存超限，暂时没管，使用local模式本机测试




