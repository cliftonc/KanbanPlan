<html>
    <head>
        <title>KanbanPlan</title>
        <link rel="stylesheet" type="text/css" href="/css/main.css"/>
        <link rel="stylesheet" type="text/css" href="/css/grid.css"/>
        <link type="text/css" href="/css/smoothness/jquery-ui-1.8.6.custom.css" rel="stylesheet" />
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js"></script>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.6/jquery-ui.min.js"></script>                
    </head>
    <body>
        <div class="container_12 body">
        	<div class="grid_12 header">
        		<div class="grid_9 alpha">
        			Header
        		</div>				        		
				<div class="grid_3 omega right_align login">
					<% if (user) { %>
						${user} <br/> <a href="${users.createLogoutURL("/")}">Log Out</a>
					<% } else { %>
						<a href="${users.createLoginURL("/")}">Log In</a>
					<% } %>	
				</div>
			</div>
			<div class="clear"></div>		        		        	
        
