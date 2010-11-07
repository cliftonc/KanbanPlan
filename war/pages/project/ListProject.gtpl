<% import com.google.appengine.api.datastore.* %>
<% import com.kanbanplan.domain.Project %>

<% include '/WEB-INF/includes/header.gtpl' %>	
<script>

	jQuery(document).ready(function() {
	   // do stuff when DOM is ready
	   
			   var project = jQuery( "#project" ),
					projectDescription = jQuery( "#projectDescription" ),
					allFields = jQuery( [] ).add( project ).add( projectDescription ),
					tips = jQuery( ".validateTips" );

			   function updateTips( t ) {
					tips
						.text( t )
						.addClass( "ui-state-highlight" );
					setTimeout(function() {
						tips.removeClass( "ui-state-highlight", 1500 );
					}, 500 );
				}

				function checkLength( o, n, min, max ) {
					if ( o.val().length > max || o.val().length < min ) {
						o.addClass( "ui-state-error" );
						updateTips( "Length of " + n + " must be between " +
							min + " and " + max + "." );
						return false;
					} else {
						return true;
					}
				}

				function checkRegexp( o, regexp, n ) {
					if ( !( regexp.test( o.val() ) ) ) {
						o.addClass( "ui-state-error" );
						updateTips( n );
						return false;
					} else {
						return true;
					}
				}

				jQuery("#createProject").bind("keypress", function(e) {
					  if (e.keyCode == 13) {
						  	createProject();							
					  		return false;
					  }
				});
				   
	   			jQuery("#createProject").dialog({
					autoOpen: false,
					height: 259,
					width: 450,
					modal: true,
					title: 'New Project',
					resizable : false,
					buttons: {
						"Create Project": createProject,
						Cancel: function() {
							jQuery( this ).dialog( "close" );							
						}
					},
					close: function() {
						allFields.val( "" ).removeClass( "ui-state-error" );
					}
				});

			function createProject() {
				
					var bValid = true;
					allFields.removeClass( "ui-state-error" );

					bValid = bValid && checkLength( project, "the project name", 1, 16 );
					bValid = bValid && checkLength( projectDescription, "the project description", 1, 4000 );							
					bValid = bValid && checkRegexp( project, /^[a-z]([0-9a-z_])+\$/i, "Project may consist of a-z, 0-9, underscores, begin with a letter." );
					
					if ( bValid ) {									
						jQuery.post("${request.baseUrl}", jQuery("#projectForm").serialize());														 
						jQuery( jQuery("#createProject") ).dialog( "close" );								
					}
				
			};
		   		
	   
			jQuery("#createProjectButton")
				.button()
				.click(function() {
					updateTips("All fields are required");
					jQuery("#createProject").dialog("open");					
				})
	});

</script>
	 <div class="grid_12 body">
		<h1>Projects</h1>
		<p>
			<ul>
		    <%
				for(Project entity in request.entities)
				{
			%>
				<li>
					<a href="${request.baseUrl}/${entity.id}">${entity.id}</a>
				</li>
			<%
				}
			%>
			</ul>
		</p>
		
		
		<div id="createProjectButton">New Project</div>
			
		<div id="createProject" style="display:none">
			<form id="projectForm">
				<fieldset>
					<label for="project">Name</label>
					<input type="text" name="project" id="project" value="" />
					<label for="projectDescription">Description</label>
					<textarea name="projectDescription" id="projectDescription"></textarea>
					<input type="hidden" name="action" value="update" />		
				</fieldset>
				<p class="validateTips">All form fields are required.</p>					
			</form>
		</div>
		
		
	</div><!--  End of Grid_12 -->
	

<% include '/WEB-INF/includes/footer.gtpl' %>