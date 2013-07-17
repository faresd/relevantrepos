$('#search-form').submit(function(e){
	e.preventDefault();
	var keyword = $(this).find('input').val();
	console.log("keyword is :"  +  keyword)
	window.location.href = "/search/" + keyword;

})

$('#count-repos').click(function(e){
	e.preventDefault();
	var owner = $(this).attr("data-owner");
	var repoName = $(this).attr("data-repo-name");
	window.location.href = "/collaborators/?" + "owner=" + owner + "&rname=" + repoName;

})