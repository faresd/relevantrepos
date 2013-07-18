package models.search;
import play.api.libs.json.Reads
import play.api.libs.json.JsPath
import play.api.libs.json.Json
import play.api.libs.functional.syntax._


case class GithubRepos(owner:String, name:String)

case class SearchResult(users:List[GithubRepos])


object Search {

	val githubRepos:Reads[GithubRepos] = 
	((JsPath \ "owner").read[String] and
	(JsPath \ "name").read[String]) apply(GithubRepos.apply _)

	val repos:Reads[List[GithubRepos]] = Reads.list(githubRepos)

	val resultReads: Reads[SearchResult] = (JsPath \ "repositories").read(repos).map(repos => SearchResult(repos))
}



case class Collaborators(collaborators:List[String])


object Collaborators {

	val login:Reads[String] = (JsPath \ "login").read[String]

	val collaborators:Reads[List[String]] = Reads.list(login)
}

case class Repos(repos:List[String])

object Repos {

	val repoUrl:Reads[String] = (JsPath \ "html_url").read[String]

	val userRepos:Reads[List[String]] = Reads.list(repoUrl)

}
case class ReposName(repos:List[String])

object ReposName {

	val repoName:Reads[String] = (JsPath \ "name").read[String]

	val userReposByName:Reads[List[String]] = Reads.list(repoName)

}

case class Commits(commits:List[String])

object Commits {

	val sha:Reads[String] = (JsPath \ "commit" \ "message").read[String]

	val userCommits:Reads[List[String]] = Reads.list(sha)

}
