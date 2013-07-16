package modules.search;
import play.api.libs.json.Reads
import play.api.libs.json.JsPath
import play.api.libs.json.Json
import play.api.libs.functional.syntax._


case class GithubRepos(username:String, name:String)

case class SearchResult(users:List[GithubRepos])
// implicit val ug = Json.reads[GithubUser]
// val tata = Json.reads[SearchResult]

object Search {

	val githubRepos:Reads[GithubRepos] = 
	((JsPath \ "username").read[String] and
	(JsPath \ "name").read[String]) apply(GithubRepos.apply _)

	val repos:Reads[List[GithubRepos]] = Reads.list(githubRepos)

	val resultReads: Reads[SearchResult] = (JsPath \ "repositories").read(repos).map(repos => SearchResult(repos))
}
