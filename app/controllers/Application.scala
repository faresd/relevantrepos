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
import modules.search.Collaborators._
import modules.search.Repos._
import scala.concurrent.Await
import scala.concurrent.duration._
import java.net.URLEncoder



object Application extends Controller {
  
	def index = Action {
		Ok(views.html.index(""))
	}

	def search(keyword: String) = Action {
		Async {
			val encodedKeyword = URLEncoder.encode(keyword,"UTF-8"); 	
			val url = "https://api.github.com/legacy/repos/search/" + encodedKeyword

			val promise: Future[play.api.libs.ws.Response] = WS.url(url).withQueryString("access_token" -> "5b94b73ca5b609f471c642b770ea694a096b5dd3").get()
			promise.map(i => {
				val searchResult = resultReads.reads(i.json)
				Ok(views.html.search(searchResult.get.users))

			})

		}
	}
	def getCollaborators(owner:String, rname:String) = Action {
		Async {
			val url = "https://api.github.com/repos/" + owner + "/" + rname + "/collaborators"
			val promise: Future[play.api.libs.ws.Response] = WS.url(url).withQueryString("access_token" -> "5b94b73ca5b609f471c642b770ea694a096b5dd3").get()
			var reposByUsers:Future[List[String]] = promise.flatMap(i => {
				val resultCollaborators:List[String] = collaborators.reads(i.json).get
				val repositories:Future[List[String]] = Future.sequence( resultCollaborators.map(c => {
					val userReposUrl = "https://api.github.com/users/" + c + "/repos"
					val promise: Future[play.api.libs.ws.Response] = WS.url(userReposUrl).withQueryString("access_token" -> "5b94b73ca5b609f471c642b770ea694a096b5dd3").get()
					promise.map(i => {
						var x = userRepos.reads(i.json).get
						x
					})

				})).map(_.flatten.distinct)

				repositories
			})

			reposByUsers.map(firstlist => Ok(views.html.collaborators(firstlist)))

		}
	}
}