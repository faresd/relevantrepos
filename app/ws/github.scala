package ws

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
import models.search.ReposName._
import models.search.Commits._
import scala.concurrent.Await
import scala.concurrent.duration._
import java.net.URLEncoder


object githubWs {

  def search(keyword: String): Future[List[GithubRepos]] = {
    val encodedKeyword = URLEncoder.encode(keyword,"UTF-8"); 	
	val url = "https://api.github.com/legacy/repos/search/" + encodedKeyword

	val promise: Future[play.api.libs.ws.Response] = WS.url(url).withQueryString("access_token" -> "5b94b73ca5b609f471c642b770ea694a096b5dd3").get()
	promise.map(i => {
		val searchResult = resultReads.reads(i.json)
		searchResult.get.users
	})
  }

  def getCollaborators(owner: String, rname: String): Future[List[String]] = {
  	val encodedOwner = URLEncoder.encode(owner,"UTF-8");
  	val encodedRname = URLEncoder.encode(rname,"UTF-8"); 	
    val url = "https://api.github.com/repos/" + encodedOwner + "/" + encodedRname + "/collaborators"
	val promise: Future[play.api.libs.ws.Response] = WS.url(url).withQueryString("access_token" -> "5b94b73ca5b609f471c642b770ea694a096b5dd3").get()
	var reposByUsers:Future[List[String]] = promise.map(i => {
		val resultCollaborators:List[String] = collaborators.reads(i.json).get
		resultCollaborators
	})
	reposByUsers
  }

  def getReposByUser(user: String): Future[List[String]] = {
  	val encodedUser = URLEncoder.encode(user,"UTF-8");
   	val userReposUrl = "https://api.github.com/users/" + encodedUser + "/repos"
	val promise: Future[play.api.libs.ws.Response] = WS.url(userReposUrl).withQueryString("access_token" -> "5b94b73ca5b609f471c642b770ea694a096b5dd3").get()
	promise.map(i => {
		var x = userRepos.reads(i.json).get
		x
	})
  }

  def getReposNameByUser(user: String): Future[List[String]] = {
  	val encodedUser = URLEncoder.encode(user,"UTF-8");
   	val userReposUrl = "https://api.github.com/users/" + encodedUser + "/repos"
	val promise: Future[play.api.libs.ws.Response] = WS.url(userReposUrl).withQueryString("access_token" -> "5b94b73ca5b609f471c642b770ea694a096b5dd3").get()
	promise.map(i => {
		var x = userReposByName.reads(i.json).get
		x
	})
  }

  def getCommitsByUserAndRepos(user: String, repo: String): Future[List[String]] = {
  	val encodedUser = URLEncoder.encode(user,"UTF-8");
  	val encodedRepo = URLEncoder.encode(repo,"UTF-8");
   	val userReposUrl = "https://api.github.com/repos/" + user + "/" + repo + "/commits"
   	println(userReposUrl)
	val promise: Future[play.api.libs.ws.Response] = WS.url(userReposUrl).withQueryString("access_token" -> "5b94b73ca5b609f471c642b770ea694a096b5dd3").get()
	promise.map(i => {
		var x = userCommits.reads(i.json).get
		x
	})
  }
}