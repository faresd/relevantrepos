$('#search-form').submit(function(e){
	e.preventDefault();
	var keyword = $(this).find('input').val();
	window.location.href = "/search/" + keyword;

})