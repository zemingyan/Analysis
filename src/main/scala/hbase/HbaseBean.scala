package hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes


/**
  * 一个单例
  * 封装了Hbase 的增删查改以及创建删除数据表
  */
object HbaseBean {
  val conf: Configuration = HBaseConfiguration.create

  conf.set("hbase.rootdir", "hdfs://master:8020/hbase")
  conf.set("hbase.zookeeper.quorum", "master,slave1")

  //val executor: ExecutorService = Executors.newFixedThreadPool(5000)

  implicit val connection: Connection = ConnectionFactory.createConnection(conf)

  val admin: Admin = connection.getAdmin

  def createTable(tableName: String, columnFamily: String*): Unit = {
    val tableNameObj = TableName.valueOf(tableName)
    if (admin.tableExists(tableNameObj)) {
      println(s"Table : $tableName already exists !")
    } else {
      val td = new HTableDescriptor(tableNameObj)
      for (idx <- columnFamily) {
        val family = new HColumnDescriptor(idx)
        td.addFamily(family)
      }
      admin.createTable(td)
      println(s"Create table $tableName successfully..")
    }
  }

  def dropTable(tableName: String): Unit = {
    val tableNameObj = TableName.valueOf(tableName)
    if (admin.tableExists(tableNameObj)) {
      admin.disableTable(tableNameObj)
      admin.deleteTable(tableNameObj)
      println(s"Delete table $tableName successfully")
    } else {
      println(s"Table $tableName is not existed!")
    }
  }

  def insertBatchRecord(tableName: String, rowKey: String, columnFamily: String, data: Map[String, String])(implicit connection: Connection): Unit = {
    val table = connection.getTable(TableName.valueOf(tableName))
    val put = new Put(rowKey.getBytes)
    data foreach { case (k, v) => put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(k), Bytes.toBytes(v)) }
    table.put(put)
    table.close()
  }

  def insertRecord(tableName: String, rowKey: String, columnFamily: String, qualifier: String, value: String)(implicit connection: Connection): Unit = {
    val table = connection.getTable(TableName.valueOf(tableName))
    val put = new Put(rowKey.getBytes)
    put.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifier), Bytes.toBytes(value))
    table.put(put)
    table.close()
  }

  def getNewConnection: Connection = {
    val hbaseConf = HBaseConfiguration.create
    hbaseConf.set("hbase.rootdir", "hdfs://master:8020/hbase")
    hbaseConf.set("hbase.zookeeper.quorum", "master,slave1")
    ConnectionFactory.createConnection(hbaseConf)
  }

  def getOneRecord(tableName: String, rowKey: String)(implicit connection: Connection): Result = {
    val table = connection.getTable(TableName.valueOf(tableName))
    val get = new Get(rowKey.getBytes)
    val rs = table.get(get)
    rs
  }

  def deleteOneRecord(tableName: String, rowKey: String): Unit = {
    val table = connection.getTable(TableName.valueOf(tableName))
    val del = new Delete(rowKey.getBytes)
    table.delete(del)
    println(s"Delete rowKey $rowKey successfully..")
  }

  def getAll(tableName: String): ResultScanner = {
    val table = connection.getTable(TableName.valueOf(tableName))
    val scan = new Scan
    val scanner = table.getScanner(scan)
    scanner forEach { rs =>
      println(new String(rs.getRow))
      rs.listCells forEach { cell =>
        println(new String(CellUtil.cloneFamily(cell)) + " " + new String(CellUtil.cloneQualifier(cell)) + " " + new String(CellUtil.cloneValue(cell)))
      }
    }
    scanner
  }

  def main(args: Array[String]): Unit = {
    //    createTable("AIV", "brand", "model", "system_version", "resolution", "net_status", "language", "ISP")
    //    createTable("SINGLE_APP", "click_num", "version", "day_period", "week_period", "user", "duration")
    //    createTable("APP_VERSION", "click_num")
    //    createTable("APP_USAGE", "1", "2", "3", "4", "5", "6")
    //    createTable("USER", "usage_duration", "usage_statistics", "usage_history", "hobby", "usage_times")
    //    createTable("USER_GROUP", "usage", "hobby", "location", "period")
    //getAll("USER_GROUP")
    //    dropTable("AIV")
    //    dropTable("APP_VERSION")
    //    dropTable("APP_USAGE")
    //    dropTable("SINGLE_APP")
    //    dropTable("USER")
    //    dropTable("USER_GROUP")

    //createTable("Test1", "brand", "model", "system_version", "resolution", "net_status", "language", "ISP")
    val cur = System.currentTimeMillis()
    println("开启扫描的时间戳为" + cur)
    val table = connection.getTable(TableName.valueOf("AIV"))
    val scan = new Scan
    val scanner = table.getScanner(scan)
    scanner forEach { rs =>
      println(new String(rs.getRow))
      rs.listCells forEach { cell =>
        println(new String(CellUtil.cloneFamily(cell)) + " " +
          new String(CellUtil.cloneQualifier(cell)) + " " +
          new String(CellUtil.cloneValue(cell)))
      }
    }
    val now = System.currentTimeMillis()
    println("结束扫描的时间戳为" + now)
    println("消耗的时间为:" + (now - cur) + "ms")
  }
}
