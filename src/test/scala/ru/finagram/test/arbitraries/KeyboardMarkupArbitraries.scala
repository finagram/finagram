package ru.finagram.test.arbitraries

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{ Arbitrary, Gen }
import ru.finagram.api._

trait KeyboardMarkupArbitraries {
  implicit val arbId: Arbitrary[Long] = Arbitrary(Gen.posNum[Long])
  implicit val arbText: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  implicit val arbInlineCallbackKeyboardButton: Arbitrary[InlineCallbackKeyboardButton] = Arbitrary {
    for {
      text <- arbitrary[String]
      callbackData <- arbitrary[String]
      switchInlineQuery <- Gen.option(arbitrary[String])
    } yield InlineCallbackKeyboardButton(text, callbackData, switchInlineQuery)
  }

  implicit val arbInlineUrlKeyboardButton: Arbitrary[InlineUrlKeyboardButton] = Arbitrary {
    for {
      text <- arbitrary[String]
      host <- arbitrary[String]
      switchInlineQuery <- Gen.option(arbitrary[String])
    } yield InlineUrlKeyboardButton(text, s"http://$host", switchInlineQuery)
  }

  implicit val arbInlineKeyboardButton: Arbitrary[InlineKeyboardButton] = Arbitrary {
    Gen.oneOf(arbitrary[InlineCallbackKeyboardButton], arbitrary[InlineUrlKeyboardButton])
  }

  implicit val arbInlineKeyboardMarkup: Arbitrary[InlineKeyboardMarkup] = Arbitrary {
    for {
      buttons <- Gen.nonEmptyListOf(arbitrary[InlineKeyboardButton])
    } yield InlineKeyboardMarkup(Seq(buttons))
  }

  implicit val arbKeyboardButton: Arbitrary[KeyboardButton] = Arbitrary {
    for {
      text <- arbitrary[String]
      requestContact <- Gen.option(arbitrary[Boolean])
      requestLocation <- Gen.option(arbitrary[Boolean])
    } yield KeyboardButton(text, requestContact, requestLocation)
  }

  implicit val arbReplyKeyboardMarkup: Arbitrary[ReplyKeyboardMarkup] = Arbitrary {
    for {
      buttons <- Gen.nonEmptyListOf(arbitrary[KeyboardButton])
      resizeKeyboard <- Gen.option(arbitrary[Boolean])
      oneTimeKeyboard <- Gen.option(arbitrary[Boolean])
      selective <- Gen.option(arbitrary[Boolean])
    } yield ReplyKeyboardMarkup(Seq(buttons), resizeKeyboard, oneTimeKeyboard, selective)
  }

  implicit val arbKeyboardMarkup: Arbitrary[KeyboardMarkup] = Arbitrary {
    Gen.oneOf(
      arbitrary[InlineKeyboardMarkup],
      arbitrary[ReplyKeyboardMarkup]
    )
  }
}
