package org.philimone.hds.explorer

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import org.philimone.hds.explorer.io.SystemPath
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertiesPropertySource
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
class Application extends GrailsAutoConfiguration implements EnvironmentAware {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    @Override
    void setEnvironment(Environment environment) {

        String conf = "hds_explorer.config.location"

        Resource resourceConfig = getConfigurationResource()

        println "path: ${resourceConfig}, ${conf}, istest=${isTestEnvironment()}"

        if (resourceConfig != null && resourceConfig.exists()){
            YamlPropertiesFactoryBean ypfb = new YamlPropertiesFactoryBean()

            ypfb.setResources([resourceConfig] as Resource[])
            ypfb.afterPropertiesSet()

            Properties properties = ypfb.getObject()

            environment.propertySources.addFirst(new PropertiesPropertySource(conf, properties))
        }
    }

    Resource getConfigurationResource(){
        // 1. Check if external /var/lib/hds-explorer/app-config.yml exists
        File externalConfig = new File(SystemPath.HOME_PATH, "app-config.yml")
        if (externalConfig.exists()){
            return new FileSystemResource(externalConfig)
        }

        // 2. Fallback to bundled app-config.yml inside WAR
        return new ClassPathResource("app-config.yml")
    }

    boolean isTestEnvironment(){
        return grails.util.Environment.current==grails.util.Environment.TEST
    }
}