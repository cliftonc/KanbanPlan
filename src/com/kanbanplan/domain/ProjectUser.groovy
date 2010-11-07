package com.kanbanplan.domain

import javax.persistence.*
import com.kanbanplan.domain.ProjectRole
import com.google.appengine.api.users.User

class ProjectUser
{
	@Id String id
	String project
	User user
	String projectRole
	Date createDate = new Date()
}