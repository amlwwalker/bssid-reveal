var express        =        require("express");
var bodyParser     =        require("body-parser");
var app            =        express();

app.use(bodyParser.json());

app.post('/receive',function(request,response){
	console.log(JSON.stringify(request.body));
	 response.end(JSON.stringify({"status": "complete", "message":"Thank you for helping out!"}));
});

app.listen(3000,function(){
  console.log("Started on PORT 3000");
})