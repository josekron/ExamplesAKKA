package city

import akka.actor._

case object TurnMessage

class SignalCity(name: String, left: ActorRef, right: ActorRef) extends Actor{
  val rd = scala.util.Random
  def receive = {
    case TurnMessage =>
      val turn = rd.nextInt(2)
      turn match {
        case 0 =>
          //println("In %s, turn left".format(name))
          println("    ||    ");
          println("    %s    ".format(name))
          println("    ||    ");
          println("   <=    ");
          left ! TurnMessage
        case _ =>
          println("    ||    ");
          println("    %s    ".format(name))
          println("    ||    ");
          println("    =>    ");
          right ! TurnMessage
      }

  }
}

class SignalCityFinish(name: String) extends Actor{
  def receive = {
    case TurnMessage =>
          println("Exit in %s  =o=º=o=".format(name))
          context.stop(self)
  }
}

object City extends App{
   val system = ActorSystem("CitySystem")
  
   val s7 = system.actorOf(Props(new SignalCityFinish("s7")), name = "s7")
   val s6 = system.actorOf(Props(new SignalCityFinish("s6")), name = "s6")
   val s5 = system.actorOf(Props(new SignalCityFinish("s5")), name = "s5")
   
   val s4 = system.actorOf(Props(new SignalCity("s4",s5,s7)), name = "s4")   
   val s3 = system.actorOf(Props(new SignalCity("s3",s4,s5)), name = "s3") 
   val s2 = system.actorOf(Props(new SignalCity("s2",s3,s6)), name = "s2")
     
   val s1 = system.actorOf(Props(new SignalCity("s1",s2,s3)), name = "s1")
   
   s1 ! TurnMessage
}