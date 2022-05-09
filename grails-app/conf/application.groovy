grails.databinding.dateFormats = ['yyyy-MM-dd HH:mm:ss']

// configurations taken from Config.groovy
grails.plugins.twitterbootstrap.fixtaglib = true

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.philimone.hds.explorer.server.model.authentication.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.philimone.hds.explorer.server.model.authentication.UserRole'
grails.plugin.springsecurity.authority.className = 'org.philimone.hds.explorer.server.model.authentication.Role'
grails.plugin.springsecurity.requestMap.className = 'org.philimone.hds.explorer.server.model.authentication.SecurityMap'
grails.plugin.springsecurity.securityConfigType = 'Requestmap'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]


grails.plugin.springsecurity.password.algorithm = 'bcrypt'

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.philimone.hds.explorer.server.model.authentication.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.philimone.hds.explorer.server.model.authentication.UserRole'
grails.plugin.springsecurity.authority.className = 'org.philimone.hds.explorer.server.model.authentication.Role'
grails.plugin.springsecurity.requestMap.className = 'org.philimone.hds.explorer.server.model.authentication.SecurityMap'
grails.plugin.springsecurity.securityConfigType = 'Requestmap'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']]
]

grails.plugin.springsecurity.useBasicAuth = true
grails.plugin.springsecurity.basic.realmName = "HDS Explorer Api"
grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/api/**',         filters: 'JOINED_FILTERS,-exceptionTranslationFilter'],  /*use basicAuth only on those*/
	[pattern: '/**',             filters: 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'] /*use basicAuth only on those*/
]

// Use BCRYPT to encode passwords
grails.plugin.springsecurity.password.algorithm = 'bcrypt'
grails.plugin.springsecurity.logout.postOnly = false

// add this fix to be able to get current user from springSecurityService
// grails.plugin.springsecurity.sch.strategyName = org.springframework.security.core.context.SecurityContextHolder.MODE_INHERITABLETHREADLOCAL

grails.plugin.springsecurity.rejectIfNoRule = true

//database migration
grails.plugin.databasemigration.updateOnStart = true
grails.plugin.databasemigration.updateOnStartFileName = "changelog.groovy"