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
	depends(checkVersion, parseArguments, loadApp)
	
	targetDir = "${basedir}/src/templates/test"
	overwrite = false
	
	if (new File(targetDir).exists()) {
		if (!isInteractive || confirmInput("Overwrite existing templates?", "overwrite.templates")) {
			overwrite = true
		}
	} else {
		ant.mkdir(dir: targetDir)
	}
	
	def plugin = appCtx.getBean('pluginManager').getGrailsPlugin('quickInput')
	
	ant.copy(todir: "$targetDir/scaffolding") {
		fileset(dir: "${grailsSettings.projectPluginsDir}/${plugin.name}-${plugin.version}/*")
	}
	
	event("StatusUpdate", [ "Templates installed successfully"])
}
