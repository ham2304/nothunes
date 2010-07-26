import fr.xebia.nothunes.security.*

class BootStrap {
	
	def authenticateService
	
	def init = { servletContext ->
		
		def roleAdmin = new Role(authority: 'ROLE_ADMIN', description: 'Administrateur').save()
		def roleUser = new Role(authority: 'ROLE_USER', description: 'Utilisateur').save()
		
		def userAdmin = new User(username: 'admin', userRealName: 'ATTAN Charles',
		passwd: authenticateService.encodePassword('admin'),
		enabled: true, email: 'admin@nothunes.xebia.fr')
		
		userAdmin.addToAuthorities(roleAdmin)
		userAdmin.save()
		
		
		def userStd = new User(username: 'user', userRealName: 'ATTAN Charles',
		passwd: authenticateService.encodePassword('user'),
		enabled: true, email: 'admin@nothunes.xebia.fr')
		userStd.addToAuthorities(roleUser)
		userStd.save()
		
		def protectUserManaging = new RequestMap(url: '/user/*', configAttribute: 'ROLE_ADMIN').save()
		def protectRoleManaging = new RequestMap(url: '/role/*', configAttribute: 'ROLE_ADMIN').save()
		def protectSecurityMappingManaging = new RequestMap(url: '/requestMap/*', configAttribute: 'ROLE_ADMIN').save()
		def protectUserProfileManaging = new RequestMap(url: '/profile/*', configAttribute: 'ROLE_ADMIN,ROLE_USER').save()
		def protectBandManaging = new RequestMap(url: '/band/*', configAttribute: 'ROLE_ADMIN,ROLE_USER').save()
		
	}
	def destroy = {
	}
}
