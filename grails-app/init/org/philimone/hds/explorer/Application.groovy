package org.philimone.hds.explorer

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource

class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    void setEnvironment(Environment environment) {

        String conf = "hds_explorer.config.location"

        def configFile = getExternalConfigurationFile()

        println "path: ${configFile}, ${conf}, istest=${isTestEnvironment()}"

        if (configFile != null){
            Resource resourceConfig = new FileSystemResource(configFile)

            YamlPropertiesFactoryBean ypfb = new YamlPropertiesFactoryBean()

            ypfb.setResources([resourceConfig] as Resource[])
            ypfb.afterPropertiesSet()

            Properties properties = ypfb.getObject()

            environment.propertySources.addFirst(new PropertiesPropertySource(conf, properties))
        }
    }

    File getExternalConfigurationFile(){
        try {
            def url = getClass().classLoader.getResource("app-config.yml")

            return new File(url.toURI())
        }catch (Exception ex){
            //ex.printStackTrace()

            return null
        }

    }

    boolean isTestEnvironment(){
        return grails.util.Environment.current==grails.util.Environment.TEST
    }
}