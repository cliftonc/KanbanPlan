<% import com.google.appengine.api.datastore.* %>
<% import com.kanbanplan.domain.Project %>

<% include '/WEB-INF/includes/header.gtpl' %>

<link rel="stylesheet" href="/css/slick.grid.css" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="/css/slick.pager.css" type="text/css" media="screen" charset="utf-8" />        
<link rel="stylesheet" href="/css/slick.columnpicker.css" type="text/css" media="screen" charset="utf-8" />

<script language="JavaScript" src="/js/jquery.jsonp-1.1.0.min.js"></script>
<script language="JavaScript" src="/js/slick.remotemodel.js"></script>
<script language="JavaScript" src="/js/jquery.event.drag-2.0.min.js"></script>
<script language="JavaScript" src="/js/jquery.event.drop-2.0.min.js"></script>
<script language="JavaScript" src="/js/slick.grid.js"></script>
<script language="JavaScript" src="/js/jquery.template.js"></script>

<style>
		.cell-project {
			white-space: normal!important;
      		line-height: 12px!important;
		}

		.loading-indicator {
			display: inline-block;
			padding: 4px;
			margin-top: 14px;
			background: white;
			-opacity: 0.5;
			color: black;
			z-index: 9999;
			border: 1px solid grey;
			-opacity: 0.5;			
			-moz-border-radius: 3px;
			-webkit-border-radius: 3px;
			-text-shadow: 1px 1px 1px white;
		}

		.loading-indicator label {
			padding-left: 20px;
			font-size: 0.8em;
			background: url('/images/ajax-loader-small.gif') no-repeat center left;
		}
		
		.selected {
            background: #EFEFAA !important;
            -opacity: 0.5;			
			-moz-border-radius: 10px;
			-webkit-border-radius: 10px;
        }
        
		.contact-card-cell {
			border-color: transparent!important;
		}

		.slick-cell {			
			border-color: transparent!important;
      		line-height: 19px!important;
      		cursor: hand;
		}
		
		.cell-inner {
			height: 24px;
			margin: 2px;
			padding: 2px;
			background: #fafafa;
			border: 1px solid gray;
			-moz-border-radius: 5px;
			-webkit-border-radius: 5px;
			-moz-box-shadow: 1px 1px 5px silver;
			-webkit-box-shadow: 1px 1px 5px silver;
			-webkit-transition: all 0.5s;
			font-size: 0.8em;
		}

		.cell-inner:hover {
			background: #f0f0f0;
		}

		.cell-left {
			width: 40px;
			height: 100%;
			float: left;
			border-right: 1px dotted gray;
			background: url("../images/pencil.gif") no-repeat center 2px;
		}


		.slick-header {
			display:none!important;
		}
		.cell-main {
			margin-left: 50px;
		}
	    
		</style>

<!-- cell template -->
<script type="text/html" id="cell_template">
<div class="cell-inner">
	<div class="cell-left"></div>
	<div class="cell-main" title="<@=projectDescription@>">
		<@=id@>
	</div>
</div>

</script>


<script>

	var grid;
	var data = [];
	var loader = new Slick.Data.RemoteModel();
	
	var projectFormatter = function(row, cell, value, columnDef, dataContext) {
	    return "<a href='/project/" + dataContext["id"] + "'>" + dataContext["id"] + "</a> : " + dataContext["projectDescription"];
	};

	
	var columns = [		
		{id:"project", name:"Project", width:400, formatter:renderCell, cssClass:"cell-project"}		
	];

	var compiled_template = tmpl("cell_template");

	function renderCell(row, cell, value, columnDef, dataContext) {
		return compiled_template(dataContext);
	}

	
	var options = {
		rowHeight: 40,
		editable: false,
		enableAddRow: false,
		enableCellNavigation: true,
        enableColumnReorder: false,        
        autoHeight: true
	};
	
	var loadingIndicator = null;


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
						refreshProjectList();
					}
				
			};


	   		function refreshProjectList() {

	   			jQuery("#refreshProjectListButton").button("disable");
	   			var vp = grid.getViewport();
				loader.clear();
                loader.ensureData(vp.top, vp.bottom);
                
	   		};

			function deleteSelectedProject() {				

                	var currentRow = grid.getSelectedRows();
					var currentProject = loader.data[currentRow].id;
				
					jQuery.post("${request.baseUrl}", {'action':'delete','project':currentProject});														 

					jQuery("#projectDetail .project").empty();
						
					refreshProjectList();			
			};
			
			
			jQuery("#createProjectButton")
				.button()
				.click(function() {
					updateTips("All fields are required");
					jQuery("#createProject").dialog("open");					
			});
			

			jQuery("#deleteProjectButton")
				.button({disabled:true})				
				.click(function() {
					// Delete					
					deleteSelectedProject();					
			});
					
			jQuery("#refreshProjectListButton")
				.button()				
				.click(function() {
					// Delete					
					refreshProjectList();					
				});
			
			// Grid
			grid = new Slick.Grid(jQuery("#myProjectGrid"), loader.data, columns, options);

			grid.onViewportChanged = function() {
                var vp = grid.getViewport();
                loader.ensureData(vp.top, vp.bottom);
            };

			grid.onSort = function(sortCol, sortAsc) {
                loader.setSort(sortCol.field, sortAsc ? 1 : -1);
                var vp = grid.getViewport();
                loader.ensureData(vp.top, vp.bottom);
            };

            grid.onSelectedRowsChanged = function() {

                var currentRow = grid.getSelectedRows();
				var currentProject = loader.data[currentRow].id;
                showProject(currentProject);
                
            };

			grid.onDblClick = function(e, row, cell) {
				var currentRow = grid.getSelectedRows();
				var currentProject = loader.data[currentRow].id;
				window.location.href = "/project/" + currentProject;        						
			};
            
			function showProject(project) {
				jQuery("#deleteProjectButton").button("enable");				
				jQuery("#projectDetail .project").empty();
				jQuery("#projectDetail .project").append(project);				
			}
            
			loader.onDataLoading.subscribe(function() {
				if (!loadingIndicator)
				{

					loadingIndicator = jQuery("<div class='loading-indicator'><label>Updating ...</label></div>").appendTo(jQuery("#myProjectGrid"));
					var g = jQuery("#myProjectGrid");

					loadingIndicator
						.css("position", "absolute")
						.css("top", g.position().top + g.height()/2 - loadingIndicator.height()/2)
						.css("left", g.position().left + g.width()/2 - loadingIndicator.width()/2)
				}

				loadingIndicator.show();
			});

			loader.onDataLoaded.subscribe(function(args) {
				for (var i = args.from; i <= args.to; i++) {
					grid.removeRow(i);
				}

				grid.updateRowCount();
				grid.render();

				loadingIndicator.fadeOut();
				jQuery("#refreshProjectListButton").button("enable");
			});

			jQuery("#txtSearch").keyup(function(e) {
                if (e.which == 13) {
                    loader.setSearch(jQuery(this).val());
                    var vp = grid.getViewport();
                    loader.ensureData(vp.top, vp.bottom);
                }
            });


			// load the first page
			grid.onViewportChanged();
				
	});

</script>
	 <div class="grid_12 body">		
		<div class="grid_8 alpha left_align pageHeader">	
			My Projects
		</div>		
		<div class="grid_4 omega right_align pageToolbar">
			<div id="refreshProjectListButton" >Refresh List</div>
			&nbsp;|&nbsp;
			<div id="createProjectButton" >New Project</div>
			<div id="deleteProjectButton" >Delete Project</div>						
		</div>
		<div class="clear"></div>
		<div class="grid_12 alpha">
			<div id="myProjectGrid" class="grid_5 alpha"></div>
			<div class="grid_7 omega">				
				<div id="projectDetail" >				
					<div class="project"></div>
				</div>
			</div>										
		</div>		
		
		<div id="createProject" style="display:none">
			<form id="projectForm" class="dialogForm">
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