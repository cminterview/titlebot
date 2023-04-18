package com.eck

import spray.json.RootJsonFormat

//#json-formats
import spray.json.DefaultJsonProtocol

object JsonFormats {

  import DefaultJsonProtocol._

  implicit val pageInfoJsonFormat: RootJsonFormat[PageInfo] = jsonFormat3(PageInfo.apply)

}

