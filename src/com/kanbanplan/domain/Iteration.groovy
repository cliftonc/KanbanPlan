package com.kanbanplan.domain

import javax.persistence.*
import com.google.appengine.api.users.User

class Iteration
{
	@Id String id
	String project
	String iterationDescription	
	Date startDate
	Date endDate
	User owner
	Date createDate = new Date()
}