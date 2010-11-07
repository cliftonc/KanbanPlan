
package com.kanbanplan.domain

import javax.persistence.*
import com.google.appengine.api.users.User

class Project
{	
	@Id String id
	String projectDescription
	User owner		
	Date createDate = new Date()
}


