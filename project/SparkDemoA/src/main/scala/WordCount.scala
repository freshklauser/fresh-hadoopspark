import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    // 创建SparkConf
    val sparkConf: SparkConf = new SparkConf().setAppName("WordCount").setMaster("local[*]")

    // 创建　SparkContext　对象
    val sc = new SparkContext(sparkConf)

    // 读取一个文件
    //val line: RDD[String] = sc.textFile(args(0))
    val line: RDD[String] = sc.textFile("/home/klaus/klaus_docs/word.txt")

    // 压平
    val words: RDD[String] = line.flatMap(_.split(" "))

    // 将单词转化为元祖
    val wordAndOne: RDD[(String, Int)] = words.map((_,1))

    // 统计单词总数
    val wordAndCount: RDD[(String, Int)] = wordAndOne.aggregateByKey(0)(_+_, _+_)
    //val wordAndCount: RDD[(String, Int)] = wordAndOne.reduceByKey(_+_)

    // 保存到文件中
    //wordAndCount.saveAsTextFile(args(1))
    wordAndCount.saveAsTextFile("/home/klaus/klaus_docs/wordCount.txt")

    // 打印结果
    //wordAndCount.foreach(c => println(c._1 + "appeared" + c._2 + "times."))

    // 关闭连接
    sc.stop()
  }
}
