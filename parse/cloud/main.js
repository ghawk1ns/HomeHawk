Parse.Cloud.define("createNetwork", function(request, response) {
	//get networkName
	var networkName = request.params.networkName
	//create network object
	var Network = Parse.Object.extend("Network");
	var network = new Network();
	//create token
	//TODO: query networks and make sure we don't have a match
	var tokenLength = 20;
    var token = "";
    var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    for( var i=0; i < tokenLength; i++ ){
        token += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    network.save({
		  networkName: networkName,
		  networkId : token
		}, {
		  success: function(network) {
		    response.success(network);
		  },
		  error: function(network, error) {
		    // The save failed.
		    // error is a Parse.Error with an error code and description.
		    response.error("Could not create network");
		  }
	});
});