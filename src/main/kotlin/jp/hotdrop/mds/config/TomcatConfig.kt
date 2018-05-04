package jp.hotdrop.mds.config

import org.apache.catalina.connector.Connector
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TomcatConfig {

    @Value("\${server.http.port}")
    private var httpPort: Int = 0

    @Bean
    fun servletContainer(): ServletWebServerFactory =
        TomcatServletWebServerFactory().apply {
            addAdditionalTomcatConnectors(
                    Connector("org.apache.coyote.http11.Http11NioProtocol").apply {
                        port = httpPort
                    })
        }
}