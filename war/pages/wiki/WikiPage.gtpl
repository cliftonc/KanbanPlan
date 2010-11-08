<% import com.google.appengine.api.datastore.* %>
<% import com.kanbanplan.util.KanbanPlanWiki %>

<% include '/WEB-INF/includes/header.gtpl' %>	

<script>

	jQuery(document).ready(function() {
	   // do stuff when DOM is ready

			jQuery("#page_edit").click(function() {
				jQuery("#wiki_text_temp").val(jQuery("#wiki_text").val());
				jQuery("#page_temp").val(jQuery("#page").val());			
				jQuery("#wikiPage_view").hide();
				jQuery("#wikiPage_edit").show();
		   	});

			jQuery("#page_edit_cancel").click(function() {
				jQuery("#wiki_text").val(jQuery("#wiki_text_temp").val());
				jQuery("#page").val(jQuery("#page_temp").val());
				jQuery("#wikiPage_view").show();
				jQuery("#wikiPage_edit").hide();
		   	});


			jQuery("#page").keyup(function() {
				if(jQuery("#page").val() != jQuery("#page_temp").val()) {
					jQuery("#retain_old").removeAttr("disabled");		     
				} else {
					jQuery("#retain_old").attr("disabled", "disabled");	
					jQuery("#retain_old").attr('checked', false);													
				}			
		   	});

	});

</script>
	<%	
		if (request.page) {
	%>
		<div class="grid_12 body" id="wikiPage_view">
			<div class="grid_6 alpha pageHeader">
				${request.page.page}				
			</div>
			<div class="grid_6 omega right_align detail">
				<div>
					${request.page.author} on <i>${request.page.createDate}</i> &nbsp; <a href="#" id="page_edit">Edit</a>&nbsp;|&nbsp;<a href="${request.baseUrl}?action=delete&page=${request.page.page}">Delete</a>&nbsp;|&nbsp;<a href="${request.baseUrl}">List</a>
				</div>							
			</div>			
			<div class="clear"></div>
			<div class="grid_12 alpha">
				${KanbanPlanWiki.Parser.renderXHTML(request.page.wikiText)}				
			</div>								
		</div>
		<div class="grid_12 body" id="wikiPage_edit" style="display:none;">
			<form method="POST" action="${request.baseUrl}">						
			<div class="grid_9 alpha">
				<h1><input type="text" name="page" id="page" value="${request.page.page}"/></h1>				
			</div>
			<div class="grid_3 omega right_align detail">
				<div>
					<input type="submit" id="page_edit_save" value="Save" /><input id="page_edit_cancel" type="button" value="Cancel" />|&nbsp;Retain Old Page:&nbsp;<input type="checkbox" name="retain_old" id="retain_old" disabled="disabled" />
				</div>							
			</div>	
			<div class="clear"></div>
			<div class="grid_12 alpha">				
				<input type="hidden" name="action" value="update" />				
				<input type="hidden" name="wiki_text_temp" id="wiki_text_temp" value="${request.page.wikiText}" />				
				<input type="hidden" name="page_temp" id="page_temp" value="${request.page.page}" />							
				<textarea class="page_editor" name="wiki_text" id="wiki_text">${request.page.wikiText}</textarea>
			</div>												
			</form>	
			<h3>Markup</h3>
			<img src="http://www.wikicreole.org/attach/CheatSheet/creole_cheat_sheet.png"/>		
		</div>
		
	<%  } else { %>
	 <div class="grid_12 body">
		<div class="grid_8 alpha left_align pageHeader">	
			Wiki Pages
		</div>
		<div class="grid_4 omega right_align pageToolbar">
			<form method="POST" action="${request.baseUrl}">
				<input type="hidden" name="action" value="show" />
				<b>New Page</b> <input type="text" name="page" value="" /><input type="submit" value="Create" />&nbsp;|&nbsp;<a href="${request.baseUrl}?action=deleteAll">Clear All</a>
			</form>		
		</div>
		<div class="clear"></div>
		<div class="grid_12">
			<ul>
		    <%
				for(Entity entity in request.entities)
				{
			%>
				<li>
					<a href="${request.baseUrl}?page=${entity.page}">${entity.page}</a> <small>by <b>${entity.author}</b> on <i>${entity.createDate}</i></small>
				</li>
			<%
				}
			%>
			</ul>
		</p>
			
		</div>
	</div><!--  End of Grid_12 -->
	
	<%  }  %>
	

<% include '/WEB-INF/includes/footer.gtpl' %>