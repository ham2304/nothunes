package fr.xebia.nothunes.domain

import fr.xebia.nothunes.security.User
import grails.converters.*


class BandController {
   
   def authenticateService
   
   def uploadService
   
   static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
   
   def index = {
      redirect(action: "list", params: params)
   }
   
   def list = {
      params.max = Math.min(params.max ? params.int('max') : 10, 100)
      def owner = User.get(authenticateService.userDomain().id)
      [bandInstanceList: Band.findAllByOwner(owner, params), bandInstanceTotal: Band.count()]
   }
   
   def create = {
      def bandInstance = new Band()
      bandInstance.properties = params
      return [bandInstance: bandInstance]
   }
   
   def save = {
      def bandInstance = new Band(params)
      
      // setting owner to current user
      bandInstance.owner = User.get(authenticateService.userDomain().id)
      
      // saving image File with the UploadService
      def imageSaved = uploadService.saveImageFile(request.getFile('logoFile'), bandInstance.name) 
      if (imageSaved) {
         bandInstance.logoPath = imageSaved
      } else {
         flash.message = 'Bad image type. Authorized are : jpeg, gif and png'
         render(view: "create", model: [bandInstance: bandInstance])
         return
      }
      
      // image saving is ok, saving bandInstance to DB
      if (bandInstance.save(flush:true)) {
         flash.message = "${message(code: 'default.created.message', args: [message(code: 'band.label', default: 'Band'), bandInstance.id])}"
         redirect(action: "show", id: bandInstance.id)
      }
      else {
         render(view: "create", model: [bandInstance: bandInstance])
      }
   }
   
   def show = {
      def bandInstance = Band.get(params.id)
      def currentUser = User.get(authenticateService.userDomain().id)
      
      if (bandInstance && currentUser && bandInstance.owner == currentUser) {
         [bandInstance: bandInstance]
      } else {
         flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'band.label', default: 'Band'), params.id])}"
         redirect(action: "list")
      }
   }
   
   def edit = {
      def bandInstance = Band.get(params.id)
      def currentUser = User.get(authenticateService.userDomain().id)
      
      if (bandInstance && currentUser && bandInstance.owner == currentUser) {
         return [bandInstance: bandInstance]
      }
      else {
         flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'band.label', default: 'Band'), params.id])}"
         redirect(action: "list")
      }
   }
   
   def update = {
      def bandInstance = Band.get(params.id)
      def currentUser = User.get(authenticateService.userDomain().id)
      
      if (bandInstance && currentUser && bandInstance.owner == currentUser) {
         if (params.version) {
            def version = params.version.toLong()
            if (bandInstance.version > version) {
               
               bandInstance.errors.rejectValue("version", "default.optimistic.locking.failure", [message(code: 'band.label', default: 'Band')] as Object[], "Another user has updated this Band while you were editing")
               render(view: "edit", model: [bandInstance: bandInstance])
               return
            }
         }
         
         if (params.logoChanged) {
            log.debug 'Band logo has changed, deleting old one, saving new one'
            
            uploadService.removeImageFile(bandInstance.logoPath)
            
            // saving image File with the UploadService
            def imageSaved = uploadService.saveImageFile(request.getFile('logoFile'), bandInstance.name) 
            if (imageSaved) {
               bandInstance.logoPath = imageSaved
            } else {
               flash.message = 'Bad image type. Authorized are : jpeg, gif and png'
               render(view: "create", model: [bandInstance: bandInstance])
               return
            }
         } else {
            log.debug 'Band logo has not changed'
         }
         
         // image saving is ok, saving bandInstance to DB
         bandInstance.properties = params
         if (!bandInstance.hasErrors() && bandInstance.save(flush: true)) {
            flash.message = "${message(code: 'default.updated.message', args: [message(code: 'band.label', default: 'Band'), bandInstance.id])}"
            redirect(action: "show", id: bandInstance.id)
         }
         else {
            render(view: "edit", model: [bandInstance: bandInstance])
         }
         
      }
      else {
         flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'band.label', default: 'Band'), params.id])}"
         redirect(action: "list")
      }
   }
   
   def delete = {
      def bandInstance = Band.get(params.id)
      def currentUser = User.get(authenticateService.userDomain().id)
      
      if (bandInstance && currentUser && bandInstance.owner == currentUser) {
         
         uploadService.removeImageFile(bandInstance.logoPath)
         
         try {
            bandInstance.delete(flush: true)
            flash.message = "${message(code: 'default.deleted.message', args: [message(code: 'band.label', default: 'Band'), params.id])}"
            redirect(action: "list")
         }
         catch (org.springframework.dao.DataIntegrityViolationException e) {
            flash.message = "${message(code: 'default.not.deleted.message', args: [message(code: 'band.label', default: 'Band'), params.id])}"
            redirect(action: "show", id: params.id)
         }
      }
      else {
         flash.message = "${message(code: 'default.not.found.message', args: [message(code: 'band.label', default: 'Band'), params.id])}"
         redirect(action: "list")
      }
   }
   
   def ajaxGetAlbums = {
      
      String chaine = Band.get(params.id).albums as JSON
      log.debug "JSON : "+chaine
      
      render Band.get(params.id).albums as JSON
   }
}
