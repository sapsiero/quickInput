/**
 * 
 * @author Tim Rademacher
 * 
 * @since 0.1
 *
 */
includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsBootstrap")

target('default': "Installs Quick Input scaffolding templates") {
	depends(checkVersion, parseArguments, packageApp, classpath)
    loadApp()
	
	targetDir = "${basedir}/src/templates/scaffolding"
	overwrite = false
	
	if (new File(targetDir).exists()) {
		if (!isInteractive || confirmInput("Overwrite existing templates?", "overwrite.templates")) {
			overwrite = true
		}
	} else {
		ant.mkdir(dir: targetDir)
	}
	
	def plugin = appCtx.getBean('pluginManager').getGrailsPlugin('quickInput')
	
	ant.copy(todir: "$targetDir") {
		fileset(dir: "${grailsSettings.projectPluginsDir}/${grails.util.GrailsNameUtils.getScriptName(plugin.name)}-${plugin.version}/src/templates/")
	}
	
	event("StatusUpdate", [ "Templates installed successfully"])
}
