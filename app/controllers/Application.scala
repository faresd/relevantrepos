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

	def getCommitsByCollaborators(owner:String, rname:String) = Action {
		Async {
			// var reposByUsersByCommits:Future[List[Map[String,List[String]]]] = githubWs.getCollaborators(owner, rname).flatMap (listCollaborators => {
			// 	val repositories:Future[List[Map[String,List[String]]]] = Future.sequence(listCollaborators.map (user => {
			// 		val commitsByUser:Future[List[String]] = githubWs.getReposNameByUser(user).flatMap(l => { Future.sequence(l.map(repo => {
			// 			githubWs.getCommitsByUserAndRepos(user,repo)
			// 		}))}).map(_.flatten)
			// 		val commitsMapTolist:List[Future[(String, List[String])]] = Map(user -> commitsByUser).toList.map( key_value => key_value._2.map(xs => (key_value._1, xs) ))

			// 		val commitsByUserAndRepos:Future[Map[String, List[String]]] = Future.sequence(commitsMapTolist).map(_.toMap)
			// 		commitsByUserAndRepos
			// 	}))

			// 	repositories
			// })

			val collaborators: Future[List[String]] = githubWs.getCollaborators(owner, rname)

			val repositoriesByCol: Future[List[(String, List[String])]] = collaborators.flatMap( collaborators => 
				Future.sequence(collaborators.map { user =>
					val repositories: Future[List[String]] = githubWs.getReposNameByUser(user)

					repositories.map { repositories =>
						user -> repositories
					}
			}))

			val commitsByUserAndRepos: Future[List[(String, List[String])]] = repositoriesByCol.flatMap( repositoriesByCol => 
				Future.sequence(repositoriesByCol.map { case (user, repos) =>
					val commits: Future[List[String]] = Future.sequence(repos.map { repo =>
						githubWs.getCommitsByUserAndRepos(user, repo)
					}).map(_.flatten)

					commits.map { commits =>
						user -> commits
					}
			}))

			val reposByUsersByCommitsToMap:Future[Map[String,List[String]]] = commitsByUserAndRepos.map(_.toMap)

			//val reposByUsersByCommitsToMap:Future[Map[String,List[String]]] = reposByUsersByCommits.map(i => listOfMapsToMap(i))
			reposByUsersByCommitsToMap.map(map => Ok(views.html.collaboratorsAndCommits(map)))

		}
	}

	def listOfMapsToMap(listOfMaps:List[Map[String,List[String]]]):Map[String,List[String]] = {
		val keys = listOfMaps.map(_.keySet).reduceLeft(_ | _)
		val resultMap = keys.map(k => k -> listOfMaps.flatMap(_ get k).reduceLeft(_ ++ _))
		resultMap.toMap

	}
}