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
core-site.xml:
core-site.xml 增加指向 namenode 的配置，其中 hdfs://namenode:8020 指向 docker 容器中的 namenode host，因此这里我们需要在 /etc/hosts 中加入此域名，并配置为宿主机 ip
	<configuration>
		<property>
			<name>fs.defaultFS</name>
			<value>hdfs://namenode:8020</value>
		</property>
		
	</configuration>

yarn-site.xml:
	<configuration>
	<!-- Site specific YARN configuration properties -->

		<property>
			<name>yarn.nodemanager.pmem-check-enabled</name>
			<value>false</value>
		</property>

		<property>
			<name>yarn.nodemanager.vmem-check-enabled</name>
			<value>false</value>
		</property>

		<property>
			<name>yarn.nodemanager.vmem-pmem-ratio</name>
			<value>3</value>
		</property>
		
		<property>
			<!-- refer中有这个没有上述3个，我的虚拟机配置中没加hostname这个 -->
			<name>yarn.resourcemanager.hostname</name>
			<value>resourcemanager</value>
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


>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> hive install  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
# 基于uhopper镜像使用docker搭建的集群安装hive
refer: http://hive.apache.org/downloads.html  官网可以查看hive与hadoop的版本兼容信息
此处版本hive-1.2.1,解压 重命名至 /usr/local/hive1.2.1 安装目录
1． Hive 安装及配置
（1）把 apache-hive-1.2.1-bin.tar.gz 上传到 linux 的/usr/local 目录下
（2）解压 apache-hive-1.2.1-bin.tar.gz 到/usr/local目录下面
	 $ sudo tar -zxvf apache-hive-1.2.1-bin.tar.gz -C /usr/local/
（3）修改 apache-hive-1.2.1-bin.tar.gz 的名称为 hive
     $ sudo mv apache-hive-1.2.1-bin/ hive1.2.1
（4）修改/usr/local/hive/conf 目录下的 hive-env.sh.template 名称为 hive-env.sh
	 $ mv hive-env.sh.template hive-env.sh
（5）配置 hive-env.sh 文件
	（a）配置 HADOOP_HOME 路径
	export HADOOP_HOME=/usr/local/hadoop-2.8.1
	（b）配置 HIVE_CONF_DIR 路径
	export HIVE_CONF_DIR=/usr/local/hive/conf
==> 启动 hive出现问题如下：
		启动hive出现：cannot access /usr/local/spark/lib/spark-assembly-*.jar: No such file or directory
	原因：
		这个jar包在新版本的spark中的位置已经改变！我们要做的只是将hive中的启动文件中的sparkAssemblyPath这一行更改为你安装的spark的jar包路径即可
	修改：
		到Hive的bin目录下编辑hive, 将 sparkAssemblyPath=`ls ${SPARK_HOME}/lib/spark-assembly-*.jar` 修改为 sparkAssemblyPath=`ls ${SPARK_HOME}/jars/*.jar`
		
2. mysql 安装和配置 参考 <mysql-redis.txt>中的内容

3. 创建用于存储hive元数据库的数据库、账号密码
	使用mysql的root账号进入mysql后，创建数据库 hivedb，账号 hive，密码 hive123456
	(1) 创建 hive 账号(该用户可以从任意远程主机登陆)
		mysql> create user 'hive'@'%' identified by 'hive123456';
		Query OK, 0 rows affected (0.00 sec)

		mysql> select User, Host from user;
		+---------------+-----------+
		| User          | Host      |
		+---------------+-----------+
		| hive          | %         |
		| klaus         | %         |
		| mysql.session | %         |
		| mysql.sys     | %         |
		| root          | %         |
		| root          | localhost |
		+---------------+-----------+
		6 rows in set (0.00 sec)
	
	(2) 创建数据库 hivedb (删除：DROP DATABASE hivedb;)
		mysql> create database hivedb;
		Query OK, 1 row affected (0.00 sec)

	(3) 授权用户 hive 对 hivedb开头的(或者对所有)数据库的所有权限
		GRANT ALL ON hivedb.* TO 'hive'@'%' IDENTIFIED BY 'hive123456';
		mysql> GRANT ALL PRIVILEGES ON *.* TO 'hive'@'%' IDENTIFIED BY 'hive123456';
		Query OK, 0 rows affected, 1 warning (0.12 sec)

		mysql> flush privileges;
		Query OK, 0 rows affected (0.06 sec)

		mysql> SHOW GRANTS FOR hive;
		+--------------------------------------------------+
		| Grants for hive@%                                |
		+--------------------------------------------------+
		| GRANT USAGE ON *.* TO 'hive'@'%'                 |
		| GRANT ALL PRIVILEGES ON `hivedb`.* TO 'hive'@'%' |
		+--------------------------------------------------+
		2 rows in set (0.00 sec)
		

4. 配置hive-site.xml --> Hive 元数据配置到 MySql
	(1) 驱动拷贝到 hive/lib/ 目录下 （8.0和5.0的mysql驱动有不一样的地方）
		mysql8.0身份验证问题是caching_sha2_password， 5.x 采用的是 mysql_native_password， user表中的 plugin 字段
	(2) 配置 Metastore 到 MySql	
		1) conf目录下新建文件 hive-site.xml 或者 复制 hive-default-xxx.xml 为 hive-site.xml均可
		2) 根据官方文档配置参数，拷贝配置到 hive-site.xml 文件中，主要是如下配置：
			配置中的用户名和密码根据自己mysql的用户名和密码修改
	配置文件如下：
		<?xml version="1.0"?>
		<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
		<configuration>
			<property>
				<name>javax.jdo.option.ConnectionURL</name>
				<value>jdbc:mysql://192.168.13.128:3306/hivedb?createDatabaseIfNotExist=true</value>
				<description>
				  JDBC connect string for a JDBC metastore.
				  To use SSL to encrypt/authenticate the connection, provide database-specific SSL flag in the connection URL.
				  For example, jdbc:postgresql://myhost/db?ssl=true for postgres database.
				</description>
			</property>

			<property>
				<name>javax.jdo.option.ConnectionDriverName</name>
				<value>com.mysql.jdbc.Driver</value>
				<description>Driver class name for a JDBC metastore</description>
			</property>

			<property>
				<name>javax.jdo.option.ConnectionUserName</name>
				<value>hive</value>
				<description>Username to use against metastore database</description>
			</property>

			<property>
				<name>javax.jdo.option.ConnectionPassword</name>
				<value>hive123456</value>
				<description>password to use against metastore database</description>
			</property>

			<property>
				<!-- hdfs的路径 Hive 默认的数据文件存储路径，通常为 HDFS 可写的路径，数据库会存放在这里 -->
				<name>hive.metastore.warehouse.dir</name>
				<value>/usr/hive/warehouse</value>
				<description>location of default database for the warehouse</description>
			</property>

			<property>
				<!-- hdfs的路径 , 用于存储不同 map/reduce 阶段的执行计划和这些阶段的中间输出结果-->
				<name>hive.exec.scratchdir</name>
				<value>/usr/hive/tmp</value>
			</property>

			<property>
				<!-- 解决多并发读取失败的问题（HIVE-4762） -->
				<name>datanucleus.autoStartMechanism</name>
				<value>SchemaTable</value>
			</property>

			<property>
				<!-- 配置元数据认证为false，否则异常 Caused by: MetaException(message:Version information not found in metastore. ) -->
				<name>hive.metastore.schema.verification</name>
				<value>false</value>
				<description>
				Enforce metastore schema version consistency.
				True: Verify that version information stored in metastore matches with one from Hive jars.  Also disable automatic schema migration attempt. Users are required to manully migrate schema after Hive upgrade which ensures proper metastore schema migration. (Default)
				False: Warn if the version information stored in metastore doesn't match with one from in Hive jars.
				</description>
			</property>

			<property>
				<!--  指定 io 临时目录的路径, 需要提前创建好该本地目录 -->
				<name>system:java.io.tmpdir</name>
				<value>/home/klaus/opt/hive/tmp</value>
			</property>
			<property>
				<name>system:user.name</name>
				<value>hive</value>
			</property>
			
			<property>
				<!--  显示查询表的头信息 -->
				<name>hive.cli.print.header</name>
				<value>true</value>
			</property>
		   
			<property>
				<!--  显示当前数据库 -->
				<name>hive.cli.print.current.db</name>
				<value>true</value>
			</property>
			
		</configuration>
			
5. 初始化 hive 元数据库
[klaus@messi bin]$ ./schematool -dbType mysql -initSchema
	Metastore connection URL:	 jdbc:mysql://192.168.13.128:3306/hivedb?createDatabaseIfNotExist=true
	Metastore Connection Driver :	 com.mysql.jdbc.Driver
	Metastore connection User:	 hive
	Starting metastore schema initialization to 1.2.0
	Initialization script hive-schema-1.2.0.mysql.sql
	Initialization script completed
	schemaTool completed

6. 启动hive
	[klaus@messi hive1.2.1]$ ./bin/hive

	Logging initialized using configuration in jar:file:/usr/local/hive1.2.1/lib/hive-common-1.2.1.jar!/hive-log4j.properties
	hive (default)>  
	hive (default)> use hive2;
	OK
	Time taken: 0.035 seconds
	hive (hive2)> 

	
7. 测试hive
	hive> create database hive2;
	OK
	Time taken: 0.922 seconds
	---> 在hdfs的路径中会出现：/usr/hive/warehouse/hive2.db

8. log日志配置：hive-log4j.properties
	# 提前创建目录，设置后重启hive
	hive.log.dir=${java.io.tmpdir}/${user.name} --> hive.log.dir=/home/klaus/opt/hive/logs
	注意：
		hive.log.dir=/home/klaus/opt/hive/logs	--> log存放的位置
		hive.log.file=hive.log					--> log文件的名字
	
