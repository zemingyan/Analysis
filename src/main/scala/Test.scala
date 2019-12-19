import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

object Test extends App {
  val conf = new SparkConf().setAppName("Analysis").setMaster("local")
  val ssc = new StreamingContext(conf, Seconds(10))

  ssc.checkpoint("/home/fan/checkpoint")

  val topics = Set("data")

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "linux:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "Analysis",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean))

  val kafkaStream = KafkaUtils.createDirectStream[String, String](
    ssc,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaParams))

  val input = kafkaStream.map(_.value)

  input.print()

  ssc.start()
  ssc.awaitTermination()
}
