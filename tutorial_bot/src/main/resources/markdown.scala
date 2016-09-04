import ru.finagram.{ FinagramBot, Polling }
import ru.finagram.Answers.markdown

object MarkdownAnswerExample extends App
  with FinagramBot
  with Polling {

  override val token: String = _

  on("/markdown") {
    markdown("*Markdown text*")
  }

  run()
}