var myApp = angular.module('myApp',[]);

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

myApp.controller("IndexController" , function($scope,$http )
{ 
	$("#table").hide();
	$("#btns").hide();
	
	$scope.listid="";
	$scope.at="";
	
	var code = getParameterByName('code');
	if(code === "" || code === null || code === undefined)
	{
		$("#b1").show();
		
	}
	else
	{
		$("#b1").hide();
		$("#btns").show();
		$http({
		    method: 'GET',
		    url: "http://localhost:8080/GoogleTaskManager/AppServlet", 
		    params: { key: "code" , code:code }
		}).then(function(response) {
			
			$("#table").show();
			
			$scope.title = response.data.items[0].title;
			$scope.listid = response.data.items[0].id;
			var tasklistid = response.data.items[0].id;
			var at = response.data.at;
			$scope.at = at;
			$scope.listid = tasklistid;
						
			//call to get tasks
			
			$http({
			    method: 'GET',
			    url: "http://localhost:8080/GoogleTaskManager/AppServlet", 
			    params: { key: "tasks" , tasklistid:tasklistid , at:at , method:"GetAll"}
			}).then(function(response) {
				$scope.itemsArray = response.data.items;
				
			});
		});
	}
	
	$scope.call = function()
	{
		window.close();
		$http({
		    method: 'GET',
		    url: 'http://localhost:8080/GoogleTaskManager/AppServlet', 
		    params: { key: "access"}
		}).then(function(response)
		{
			window.close();
		});
	};

	
	$scope.deletetask = function(item)
	{
		$http({
		    method: 'GET',
		    url: "http://localhost:8080/GoogleTaskManager/AppServlet", 
		    params: { key: "tasks" , tasklistid:$scope.listid , at:$scope.at , method : "Delete" , taskid:item.id}
		}).then(function(response) {
			$http({
			    method: 'GET',
			    url: "http://localhost:8080/GoogleTaskManager/AppServlet", 
			    params: { key: "tasks" , tasklistid:$scope.listid , at:$scope.at , method:"GetAll"}
			}).then(function(response) {
				$("#list").show();
				$scope.itemsArray = response.data.items;
				
			});
			
		});
	};
	
	$scope.AddTask = function()
	{
		//$('#edit').dialog("close");
		//send  to servlet for adding new task through google api
		$http({
		    method: 'GET',
		    url: "http://localhost:8080/GoogleTaskManager/AppServlet", 
		    params: { key: "tasks" , tasklistid:$scope.listid , at:$scope.at , method:"AddNew" , task:$scope.newtask}
		}).then(function(response) {
			if(response.data==="Success")
				{
					$scope.newtask = "";
				}
			$http({
			    method: 'GET',
			    url: "http://localhost:8080/GoogleTaskManager/AppServlet", 
			    params: { key: "tasks" , tasklistid:$scope.listid , at:$scope.at , method:"GetAll"}
			}).then(function(response) {
				$("#list").show();
				$scope.itemsArray = response.data.items;
				
			});
			
		});
	};
	
	$scope.DeleteAll = function()
	{
		$http({
		    method: 'GET',
		    url: "http://localhost:8080/GoogleTaskManager/AppServlet", 
		    params: { key: "tasks" , tasklistid:$scope.listid , at:$scope.at , method:"DeleteAll"}
		}).then(function(response) {
			$("#list").hide();
			$scope.itemsArray = [];
			
		});
	};
	
});

