package controllers

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import play.api.libs.json.Reads
import play.api.libs.json.JsPath
import play.api.libs.json.Json
import play.api.libs.functional.syntax._
import modules.search._
import modules.search.Search._



object Application extends Controller {
  
	def index = Action {
		Ok(views.html.index(""))
	}

/*
	val usernameReads:Reads[String] = (JsPath \ "username").read[String]
	val loginReads:Reads[String] = (JsPath \ "login").read[String]
	val searchResultReads:Reads[SearchResult] = usernameReads.and(loginReads).apply(SearchResult.apply _)

	*/

	def search(keyword: String) = Action {
		println(keyword)
		Async {
			val promise: Future[play.api.libs.ws.Response] = WS.url("https://api.github.com/legacy/repos/search/:" + keyword).get()
			promise.map(i => {
				val searchResult = resultReads.reads(i.json)
				Ok(views.html.search(searchResult.get.users))

			})
			// Ok(views.html.index("Your new application is ready."))

		}
		//val cra = Json.fromJson[List[Cra]](json)

		/*Async {
		val promise: Future[play.api.libs.ws.Response] = WS.url("https://api.github.com/legacy/user/search/:play").get()
		promise.map(i => Ok(views.html.index("Got result: " + i.json.toString())))*/
	}
  
}