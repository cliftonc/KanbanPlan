// example routes
/*
get "/blog/@year/@month/@day/@title", forward: "/WEB-INF/groovy/blog.groovy?year=@year&month=@month&day=@day&title=@title"
get "/something", redirect: "/blog/2008/10/20/something", cache: 2.hours
get "/book/isbn/@isbn", forward: "/WEB-INF/groovy/book.groovy?isbn=@isbn", validate: { isbn ==~ /\d{9}(\d|X)/ }
*/

// routes for the Wiki examples
all "/wiki", forward: "/WEB-INF/groovy/WikiController.groovy?project=none"
all "/wiki/**", forward: "/WEB-INF/groovy/WikiController.groovy?project=none"
all "/project", forward: "/WEB-INF/groovy/ProjectController.groovy"
all "/project/@project/wiki", forward: "/WEB-INF/groovy/WikiController.groovy?project=@project"
all "/project/@project/wiki/**", forward: "/WEB-INF/groovy/WikiController.groovy?project=@project"
all "/project/@project", forward: "/WEB-INF/groovy/ProjectController.groovy?project=@project"
all "/project/@project/story/@story", forward: "/WEB-INF/groovy/StoryController.groovy?project=@project&story=@story"
all "/project/@project/iteration/@iteration", forward: "/WEB-INF/groovy/IterationController.groovy?project=@project&iteration=@iteration"
