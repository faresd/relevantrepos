package modules.search;
import play.api.libs.json.Reads
import play.api.libs.json.JsPath
import play.api.libs.json.Json
import play.api.libs.functional.syntax._


case class GithubRepos(owner:String, name:String)

case class SearchResult(users:List[GithubRepos])
// implicit val ug = Json.reads[GithubUser]
// val tata = Json.reads[SearchResult]

object Search {

	val githubRepos:Reads[GithubRepos] = 
	((JsPath \ "owner").read[String] and
	(JsPath \ "name").read[String]) apply(GithubRepos.apply _)

	val repos:Reads[List[GithubRepos]] = Reads.list(githubRepos)

	val resultReads: Reads[SearchResult] = (JsPath \ "repositories").read(repos).map(repos => SearchResult(repos))
}



case class Collaborators(collaborators:List[String])
// implicit val ug = Json.reads[GithubUser]
// val tata = Json.reads[SearchResult]

object Collaborators {

	val login:Reads[String] = (JsPath \ "login").read[String]

	val collaborators:Reads[List[String]] = Reads.list(login)

	//val resultReads: Reads[SearchResult] = (JsPath \ "repositories").read(repos).map(repos => SearchResult(repos))
}

case class Repos(repos:List[String])
// implicit val ug = Json.reads[GithubUser]
// val tata = Json.reads[SearchResult]

object Repos {

	val repoUrl:Reads[String] = (JsPath \ "html_url").read[String]

	val userRepos:Reads[List[String]] = Reads.list(repoUrl)

	//val resultReads: Reads[SearchResult] = (JsPath \ "repositories").read(repos).map(repos => SearchResult(repos))
}
