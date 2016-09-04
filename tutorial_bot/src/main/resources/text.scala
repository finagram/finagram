import ru.finagram.{ FinagramBot, Polling }
import ru.finagram.Answers.text

object TextAnswerExample extends App
  with FinagramBot
  with Polling {

  override val token: String = _

  on("/text") {
    text("Flat text")
  }

  run()
}