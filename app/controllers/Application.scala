package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future

object Application extends Controller {
  
  def index = Action {
  	Async {
    	val promise: Future[play.api.libs.ws.Response] = WS.url("https://api.github.com/legacy/user/search/:play").get()
    	promise.map(i => Ok(views.html.index("Got result: " + i.json.toString())))
    	// Ok(views.html.index("Your new application is ready."))

    }
  }
  
}