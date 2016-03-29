package pi

import akka.actor._
import scala.concurrent.duration._
import akka.routing.RoundRobinPool

sealed trait PiMessage
//sent to the Master actor to start the calculation:
case object Calculate extends PiMessage 
//sent from the Master actor to the Worker actors containing the work assignment:
case class Work(start: Int, nrOfElements: Int) extends PiMessage 
//sent from the Worker actors to the Master actor containing the result from the worker’s calculation:
case class Result(value: Double) extends PiMessage
//sent from the Master actor to the Listener actor containing the the final pi result and how long time the calculation too:
case class PiApproximation(pi: Double, duration: Duration)


class Worker extends Actor {
 
  def receive = {
    case Work(start, nrOfElements) ⇒
      sender ! Result(calculatePiFor(start, nrOfElements)) // perform the work
  }
  
  def calculatePiFor(start: Int, nrOfElements: Int): Double = {
    var acc = 0.0
    for (i ← start until (start + nrOfElements))
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    acc
  }
}

/*
 * nrOfWorkers: defining how many workers we should start up
 * nrOfMessages: defining how many number chunks to send out to the workers
 * nrOfElements: defining how big the number chunks sent to each worker should be
 */
class Master(nrOfWorkers: Int, nrOfMessages: Int, nrOfElements: Int, listener: ActorRef) extends Actor {
 
  var pi: Double = _
  var nrOfResults: Int = _
  val start: Long = System.currentTimeMillis
 
  //use a round-robin router 
  val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinPool(nrOfWorkers)), name = "workerRouter")
 
  def receive = {
  case Calculate ⇒
    for (i ← 0 until nrOfMessages) workerRouter ! Work(i * nrOfElements, nrOfElements)
  case Result(value) ⇒
    pi += value
    nrOfResults += 1
    if (nrOfResults == nrOfMessages) {
      // Send the result to the listener:
      listener ! PiApproximation(pi, duration = (System.currentTimeMillis - start).millis)
      // Stops this actor and all its supervised children:
      context.stop(self)
    }
  }
 
}

class Listener extends Actor {
  def receive = {
    case PiApproximation(pi, duration) ⇒
      println("\n\tPi approximation: \t\t%s\n\tCalculation time: \t%s"
        .format(pi, duration))
      context.system.shutdown()
  }
}

object Pi extends App{
  
  calculate(nrOfWorkers = 4, nrOfElements = 10000, nrOfMessages = 10000)
 

  def calculate(nrOfWorkers: Int, nrOfElements: Int, nrOfMessages: Int) {

    val system = ActorSystem("PiSystem")
 
    // Actor listener, which will print the result and shutdown the system:
    val listener = system.actorOf(Props[Listener], name = "listener")
 
    // Actor master:
    val master = system.actorOf(Props(new Master(
      nrOfWorkers, nrOfMessages, nrOfElements, listener)),
      name = "master")
 
    // Start the calculation:
    master ! Calculate
 
  }
}