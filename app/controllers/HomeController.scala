package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent.Promise
import scala.concurrent.ExecutionContext
import scala.util.Success
import akka.actor.typed.Scheduler
import scala.concurrent.duration._

/** This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (
    val controllerComponents: ControllerComponents,
    scheduler: Scheduler
)(implicit
    ec: ExecutionContext
) extends BaseController {

  val log = Logger("play")

  /** Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method will be
    * called when the application receives a `GET` request with a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def delay(seconds: Int) = Action.async {
    val p = Promise[String]()
    val runnable = new Runnable {
      def run(): Unit = {
        p.complete(Success(s"Delayed for $seconds second(s)"))
      }
    }
    scheduler.scheduleOnce(seconds.seconds, runnable)

    p.future map { s =>
      log.info(s)
      Ok(s)
    }
  }
}
