package streaming

import play.api.libs.json.{Json, OFormat}

object Util {
  implicit val DataFormat: OFormat[Data] = Json.format[Data]
  implicit val AppsFormat: OFormat[AppUsage] = Json.format[AppUsage]

  implicit val DataBeanFormat: OFormat[DataBean] = Json.format[DataBean]
}
