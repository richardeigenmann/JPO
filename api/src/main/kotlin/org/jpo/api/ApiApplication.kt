package org.jpo.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.resource.PathResourceResolver

@SpringBootApplication(scanBasePackages = ["org.jpo"])
class ApiApplication {

    @Bean
    fun webMvcConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
                registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/static/browser/")
                    .resourceChain(true)
                    .addResolver(object : PathResourceResolver() {
                        override fun getResource(resourcePath: String, location: org.springframework.core.io.Resource): org.springframework.core.io.Resource? {
                            val requestedResource = location.createRelative(resourcePath)
                            return if (requestedResource.exists() && requestedResource.isReadable) {
                                requestedResource
                            } else {
                                ClassPathResource("/static/browser/index.html")
                            }
                        }
                    })
            }

            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:4200")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
