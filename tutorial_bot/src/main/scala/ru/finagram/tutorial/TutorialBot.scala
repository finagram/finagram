package ru.finagram.tutorial

import com.twitter.util.Future
import ru.finagram.{ FinagramBot, Polling }
import ru.finagram.Answers._
import ru.finagram.api.Keyboard.{ oneTime, resize }
import ru.finagram.api._

object TutorialBot extends App with FinagramBot with Polling {
  override val token: String = from("/tutorial.token")

  on("/start", "/about") {
    markdown(
      Content.start,
      Some(InlineKeyboard("github" -> "https://github.com/finagram/finagram", "tutorial" -> "/tutorial"))
    )
  }

  on("/tutorial") {
    markdown(
      Content.tutorial,
      Some(InlineKeyboard("next" -> "/select_receiver"))
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
      Content.not_supported
    )
  }

  on("/webhooks") {
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
