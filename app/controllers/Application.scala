package controllers
import ws.githubWs
import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future
import play.api.libs.json.Reads
import play.api.libs.json.JsPath
import play.api.libs.json.Json
import play.api.libs.functional.syntax._
import models.search._
import models.search.Search._
import models.search.Collaborators._
import models.search.Repos._
import scala.concurrent.Await
import scala.concurrent.duration._
import java.net.URLEncoder



object Application extends Controller {
  
	def index = Action {
		Ok(views.html.index(""))
	}

	def search(keyword: String) = Action {
		Async {
			val searchResult = githubWs.search(keyword)
			searchResult.map(i => {
				Ok(views.html.search(i))
			})
		}
	}

	def getCollaborators(owner:String, rname:String) = Action {
		Async {
			var reposByUsers:Future[List[String]] = githubWs.getCollaborators(owner, rname).flatMap (listCollaborators => {
				val repositories:Future[List[String]] = Future.sequence(listCollaborators.map (c => {
					githubWs.getReposByUser(c)
				})).map(_.flatten.distinct)

				repositories
			})

			reposByUsers.map(firstlist => Ok(views.html.collaborators(firstlist)))

		}
	}
}