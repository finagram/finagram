import ru.finagram.{ FinagramBot, Polling }
import ru.finagram.Answers.html

object HtmlAnswerExample extends App
  with FinagramBot
  with Polling {

  override val token: String = _

  on("/html") {
    html("<b>HTML text</b>")
  }

  run()
}