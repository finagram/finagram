import ru.finagram._
import ru.finagram.api._
import com.twitter.util._

object LongPollingExample extends App {
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
  Thread.sleep(13000)
  // and close server
  Await.result(server.close())
}