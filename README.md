# Finagram API [![Build Status](https://travis-ci.org/finagram/finagram.svg?branch=master)](https://travis-ci.org/finagram/finagram) [![](https://jitpack.io/v/finagram/finagram.svg)](https://jitpack.io/#finagram/finagram) [![Coverage Status](https://coveralls.io/repos/github/finagram/finagram/badge.svg?branch=master)](https://coveralls.io/github/finagram/finagram?branch=master)


This library give you set of scala classes that represent [Telegram Bot API](https://core.telegram.org/bots/api).

The heart of this library is a [Finagle](https://twitter.github.io/finagle/guide/index.html). 
Powerful tool for work with HTTP and concurrency.

## How add this library to your project

Step 1. Add the JitPack repository to your build file

``` 
resolvers += "jitpack" at "https://jitpack.io"
```        
    
Step 2. Add the dependency

```
libraryDependencies += "com.github.finagram" % "finagram-api" % "0.1.0"	
```

## How issue requests to the Telegram api 

`ru.finagram.TelegramClient` is a main class for issue requests to the Telegram.

Supported requests at this moment:
- Get updates
- Get file
- Edit message reply markup
- Send answer

## How receive messages from Telegram users

You can receive messages by two way: use long polling or use webhooks.
 
### Long polling

If you doesn't want run web server for receive webhooks, you can use `ru.finagram.PollingServer` for
receive updates from Telegram by long polling.

This class take two main parameters as arguments of the constructor: _token_ and _updatesHandler_.
First is your Telegram token, second is a function, that take update and return result of the 
handling update as [Future[Unit]](https://twitter.github.io/finagle/guide/Futures.html).

For begin receive updates you should invoke method `run` that can take number of the offset which
will used in the first updates request. 

For break receiving you should invoke method `close`.

Result of the method `run` is a future with number of the last used offset. This future will 
completed when server will closed or handler return result with exception. 

In case when handler return result with exception, future from method `run` will completed with 
`ru.finagram.PollingException` that contains reason and a number of the offset after which polling 
was failed.

```scala
import ru.finagram._
import ru.finagram.api._
import com.twitter.util._

val token = ""
val handler = (update: Update) => Future(println(update))
val server = new PollingServer(token, handler)
server.run()
  .onSuccess {
    case lastOffset =>
      println(s"Last received offset is $lastOffset")
  }
  .onFailure {
    case e: PollingException =>
      println(s"Error occurred when receive update from offset ${e.offset}", e)
  }
// work some times
Thread.sleep(3000)
// and close server
Await.result(server.close())
```

## Telegram model

There are two key entities: TelegramResponses and Answers. The first is responses from Telegram 
that you can receive as webhook or by issue GetUpdates request.

The second is answers is the messages that your want to send to the telegram user.

### Telegram responses
![Class diagram](http://www.plantuml.com/plantuml/png/jLGvRiCm4Epr2jO1z04LAM8aG80OEHHNXWk6Mik4QL8XASIv_XvUaaWTS1KN1hGpEqFDBaMv1hd71D0hCAWbFhs0QWHNWBupP7xYMA0R8HXWdlrc6M5OANH76Onu4bJBTE6WWRXUecqKo87MUT-U2JIQ2XxO5IWYgQiKgDIIyjhLGKeXLwA20ZqwT7lFzMxGspGLrg16dYMyRptYgBH_TLyCxDOzt5S5UX36BdeLfN0D2VVaQ_qf0YSwoGepzeR9yRa5-PdqaXWfKgBjdsQIjqt-SpqPL9PCOucqlapfKhk4N-ML-E1vY2rBWyabhG_Qzi_2NM-ePdFvv0psiS8SfzD9Oujc6Wnys65E_L1DQI_5gP-RtSQThP83DekoCl_soJgDr-3f8BH8nEVK5-HZHJInGutLDZ1LUkjyIleLBrEfAJc2_2TtvTMBpYQ-dcSTcKuSv-nYhzX86wt-Bhir7xJ3udMxaKGz1aTPZD2vs-VoebNq8V7dTfyY5mwEiCMhRJGuenpMGmg1udlDlFu0)

### Answers
![Class diagram](http://www.plantuml.com/plantuml/png/tPJDIiGm58NtUOeymFG52Wgw44LdBCo02-aYJMzdmiGa97UiW_hkDjFIfYt5-hCGkoda76xETsyIeH1imJZG2-LAiFILqEhZEuOtUKpljDgU5dBXM2PXfL4KWZCKMiNqtlZ_uwNM4fXAJqOBHXutUWdEiItu6etBbmeiIsRt1zEgjt3CDBDvlPoITq946sq3BzYBXzL2JDTeHJ_U0sH9jMjYuLb0EGvOLUMIEKUl90ih3d07vUVadHrXTYi_QMIBuXFakbGJE8i1p-AF0b7Q8LtZa_mdqqbs6lKqY_5o0CEPwUrO2-bNCNyuYZEwqWewY3KAlWSxZU5gmtUdyajngoD4WgSYab1Wz7RUl6makFFHmNYXCvF1BHeRWZCv5iCfDsgZz5lNqpu0)

## How convert Twitter Future to Scala Future

Unfortunately `com.twitter.util.Future` is not compatible with `scala.concurrent.Future`, but you 
can use [bijection](https://github.com/twitter/bijection) project for easy convert one into another:
```scala
import com.twitter.bijection.Conversion.asMethod
import com.twitter.bijection.twitter_util.UtilBijections._

import scala.concurrent.ExecutionContext.Implicits.global

import com.twitter.util.{ Future => TwitterFuture }
import scala.concurrent.{ Future => ScalaFuture }

// From twitter future to scala future:
val scalaFuture: ScalaFuture[Unit] = TwitterFuture.Unit.as[ScalaFuture[Unit]]
// or in another way:
val twitterFuture: TwitterFuture[Unit] = ScalaFuture.successful(()).as[TwitterFuture[Unit]]
```
Finagram does not contains bijections as dependency. If you want use it, you should add it 
directly:
```
libraryDependencies += "com.twitter" %% "bijection-util" % "<actual version>"
```