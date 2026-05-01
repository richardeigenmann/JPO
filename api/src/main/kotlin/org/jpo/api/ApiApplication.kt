package org.jpo.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.resource.PathResourceResolver

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class RootController {
    @GetMapping("/")
    fun index(): String {
        return "forward:/index.html"
    }

    @GetMapping("/api")
    fun apiIndex(): String {
        return "api/index"
    }
}

@SpringBootApplication(scanBasePackages = ["org.jpo"])
class ApiApplication {

    @Bean
    fun webMvcConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
                registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/static/browser/") // Point to the folder
                    .resourceChain(true)
                    .addResolver(object : PathResourceResolver() {
                        override fun getResource(resourcePath: String, location: Resource): Resource? {
                            val resource = location.createRelative(resourcePath)

                            // If the file exists (like a .js or .png), return it.
                            // If it DOESN'T exist, return index.html (the SPA fallback)
                            return if (resource.exists() && resource.isReadable) {
                                resource
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
