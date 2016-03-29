package basicActor

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
 
class HelloActor extends Actor {
  def receive = {
    case "hello" => println("hello back at you")
    case _       => println("huh?")
  }
}
 
object Main extends App{
  val system = ActorSystem("HelloSystem")
  
  // default Actor constructor
  val pacoActor = system.actorOf(Props[HelloActor], name = "pacoactor")
  pacoActor ! "hello"
  pacoActor ! "buenos dias"

}
