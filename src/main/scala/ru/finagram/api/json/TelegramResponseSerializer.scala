package ru.finagram.api.json

import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._
import org.json4s._
import ru.finagram.api._

object TelegramResponseSerializer extends Serializer[TelegramResponse] {

  private val ResponseClass = classOf[TelegramResponse]

  override def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), TelegramResponse] = {
    case (TypeInfo(ResponseClass, _), json: JObject) =>
      val ok = (json \ "ok").extract[Boolean]
      if (ok) {
        json \ "result" match {
          case _: JArray =>
            json.extract[Updates]
          case obj: JObject if obj.values.contains("fileId") =>
            json.extract[FileResponse]
          case obj: JObject if obj.values.contains("id") =>
            json.extract[MeResponse]
          case _ => ???
        }
      } else {
        json.extract[TelegramException]
      }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case me: MeResponse =>
      ("ok" -> true) ~~ ("result" -> json(me.result))
    case u: Updates =>
      ("ok" -> true) ~~ ("result" -> json(u.result))
    case u: FileResponse =>
      ("ok" -> true) ~~ ("result" -> json(u.result))
    case e: TelegramException =>
      if (e.errorCode.isEmpty)
        ("ok" -> false) ~ ("description" -> e.description)
      else
        ("ok" -> false) ~ ("description" -> e.description) ~ ("errorCode" -> e.errorCode.get)
  }

  private def json(obj: AnyRef): JValue = {
    import ru.finagram.api.json.Implicit.formats
    Extraction.decompose(obj)
  }
}
