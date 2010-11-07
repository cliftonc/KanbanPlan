<% import com.google.appengine.api.datastore.* %>
<% import com.kanbanplan.domain.Project %>

<% include '/WEB-INF/includes/header.gtpl' %>	

<script>

	jQuery(document).ready(function() {
	   // do stuff when DOM is ready

	});

</script>
		<div class="grid_12 body" id="project_view">
			<div class="grid_6 alpha">
				<h1>${request.project.id}</h1>				
			</div>
			<div class="grid_6 omega right_align detail">
				<div>
					${request.project.owner} on <i>${request.project.createDate}</i>
				</div>							
			</div>
			<div class="clear"></div>
			<div class="grid_12 alpha">
				${request.project.projectDescription }				
			</div>			
		</div>
		

<% include '/WEB-INF/includes/footer.gtpl' %>