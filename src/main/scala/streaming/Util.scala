package streaming

import play.api.libs.json.{Json, OFormat}

case class Data(appName: String, packageName: String, startTime: Long, endTime: Long)

case class DataBean(user_id: String, date: String, apps: Seq[AppUsage], brand: String, model: String, language: String, system_version: String)

case class AppUsage(package_name: String, beginTime: Long, endTime: Long, version: String)

case class Apps(app_name: String, package_name: String)

object Util {
  implicit val DataFormat: OFormat[Data] = Json.format[Data]
  implicit lazy val AppsFormat: OFormat[AppUsage] = Json.format[AppUsage]

  implicit lazy val DataBeanFormat: OFormat[DataBean] = Json.format[DataBean]
}