package ru.finagram.tutorial

import com.twitter.util.Future
import ru.finagram.{ FinagramBot, Polling }
import ru.finagram.Answers._
import ru.finagram.api._

object TutorialBot extends App with FinagramBot with ClientDescription with Polling {
  override val token: String = from("/tutorial.token")

  on("/start", "/about") {
    markdown(
      Content.start,
      Some(InlineKeyboard("github" -> "https://github.com/finagram/finagram", "next" -> "/select_receiver"))
    )
  }

  on("/select_receiver") {
    markdown(
      Content.select_message_receiver,
      Some(InlineKeyboard("polling" -> "/polling", "webhooks" -> "/webhooks"))
    )
  }

  on("/polling") {
    markdown(
      Content.polling,
      new InlineKeyboard()
        .buttons("▲ How to receive message" -> "/select_receiver", "How to send answer ▼" -> "/handler")
        .buttons("Telegram client" -> "/client")
        .createOpt()
    )
  }

  on("/webhooks") {
    markdown(
      Content.not_supported
    )
  }

  on("/handler") {
    markdown(
      Content.not_supported
    )
  }

  /**
   * Default handler for commands without handler.
   */
  override def defaultHandler(update: Update): Future[Option[Answer]] = text(s"Unsupported update $update")(update).map(Some.apply)

  run()

}
