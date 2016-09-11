import ru.finagram.{ FinagramBot, Polling }
import ru.finagram.Answers.text

object TextAnswerExample extends App
  with FinagramBot
  with Polling {

  override val token: String = _

  on("/hello") {
    text("Hello world!")
  }

  run()
}