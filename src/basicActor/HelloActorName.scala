package basicActor

import akka.actor._
 
// (1) changed the constructor here
class HelloActorName(myName: String) extends Actor {
  def receive = {
    // (2) changed these println statements
    case "hello" => println("hello from %s!".format(myName))
    case "goodbye" => println("goodbye from %s!".format(myName))
    case _       => println("'huh?', said %s!".format(myName))
  }
}
 
object Main2 extends App {
  val system = ActorSystem("HelloSystem")
  
  val pacoActor = system.actorOf(Props(new HelloActorName("Paco")), name = "pacoactor")
  pacoActor ! "hello"
  pacoActor ! "goodbye"
  pacoActor ! "buenos dias"
  
  val mariaActor = system.actorOf(Props(new HelloActorName("Maria")), name = "mariaactor")
  mariaActor ! "hello"
  mariaActor ! "goodbye"
  mariaActor ! "buenos dias"
}