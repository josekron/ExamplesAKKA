package pingpong

import akka.actor._

/*
 * This example is from:
 * http://alvinalexander.com/scala/scala-akka-actors-ping-pong-simple-example
 */

case object PingMessage
case object PongMessage
case object StartMessage
case object StopMessage

class Ping(pong: ActorRef) extends Actor {
  var count = 0
  
  def incrementAndPrint { 
    count += 1 
    println("ping") 
  }
  
  def receive = {
    case StartMessage =>
        incrementAndPrint
        pong ! PingMessage
    case PongMessage => 
        incrementAndPrint
        if (count > 5) {
          sender ! StopMessage
          println("ping stopped")
          context.stop(self)
        } else {
          sender ! PingMessage
        }
  }
}
 
class Pong extends Actor {
  
  def receive = {
    case PingMessage =>
        println("  pong")
        sender ! PongMessage
    case StopMessage =>
        println("pong stopped")
        context.stop(self)
  }
}
 
object PingPongTest extends App {
  
  val system = ActorSystem("PingPongSystem")
  
  val pong = system.actorOf(Props[Pong], name = "pong")
  val ping = system.actorOf(Props(new Ping(pong)), name = "ping")
  // start them going
  ping ! StartMessage
}