import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

//val filepath = "/home/klaus/IdeaProjects/SparkDemoA/src/main/resources/data/agent.log"

object Practice {
  def main(args: Array[String]): Unit = {
    //1.初始化spark配置信息并建立与spark的连接
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Practice")
    val sc: SparkContext = new SparkContext(sparkConf)

    //2.读取数据生成RDD：TS，Province，City，User，AD
    val lines: RDD[String] = sc.textFile("/home/klaus/IdeaProjects/SparkDemoA/src/main/resources/data/agent.log")

    //3.按照最小粒度聚合：((Province,AD),1)
    val provinceAdInit: RDD[((String, String), Int)] = lines.map(x => {
      val fields: Array[String] = x.split(" ")
      ((fields(1), fields(4)), 1)
    })

    //4.计算每个省中每个广告被点击的总数：((Province,AD),sum)
    val provinceAdCount: RDD[((String, String), Int)] = provinceAdInit.reduceByKey(_+_)

    //5.维度转换：将省份作为key，广告加点击数为value：(Province,(AD,sum))
    val provinceToAdCount: RDD[(String, (String, Int))] = provinceAdCount.map(x=>(x._1._1, (x._1._2, x._2)))

    //6.将同一个省份的所有广告进行聚合(Province,List((AD1,sum1),(AD2,sum2)...))
    val provinceToAdGroup: RDD[(String, Iterable[(String, Int)])] = provinceToAdCount.groupByKey()

    //7.对同一个省份所有广告的集合进行排序并取前3条，排序规则为广告点击总数
    //  对 Iterable[(String, Int)] 排序， 直接用 scala　语法，迭代器没有sort方法，需先转成list
    val top3: RDD[(String, List[(String, Int)])] = provinceToAdGroup.mapValues(x => {
      x.toList.sortWith((a, b) => a._2 > b._2).take(3)
    })

    // 打印结果
    top3.sortByKey().collect().foreach(println)

    // 关闭链接
    sc.stop()
  }
}
