package ru.finagram.test.arbitraries

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{ Arbitrary, Gen }
import ru.finagram.api.{ InlineKeyboardButton, _ }

trait Arbitraries extends KeyboardMarkupArbitraries {

  implicit val arbFlatAnswer: Arbitrary[FlatAnswer] = Arbitrary {
    for {
      chatId <- arbitrary[Long]
      text <- arbitrary[String]
      replyMarkup <- Gen.option(arbitrary[KeyboardMarkup])
      disableWebPagePreview <- Gen.option(arbitrary[Boolean])
      disableNotification <- Gen.option(arbitrary[Boolean])
      replyToMessageId <- Gen.option(arbitrary[Long])
    } yield FlatAnswer(
      chatId,
      text,
      replyMarkup,
      disableWebPagePreview,
      disableNotification,
      replyToMessageId
    )
  }
}
