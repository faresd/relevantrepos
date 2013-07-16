$('#search-form').submit(function(e){
	e.preventDefault();
	var keyword = $(this).find('input').val();
	console.log("keyword is :"  +  keyword)
	window.location.href = "/search/" + keyword;

})