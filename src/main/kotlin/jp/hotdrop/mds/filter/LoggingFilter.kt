package jp.hotdrop.mds.filter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LoggingFilter: Filter {

    private val log = LoggerFactory.getLogger("jp.hotdrop.mds.access")

    override fun init(filterConfig: FilterConfig?) {
    }

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {

        val startTime = System.currentTimeMillis()
        val req = request as HttpServletRequest
        log.info("client address= ${req.remoteAddr}, requestURI= ${req.requestURI}, method= ${req.method} ")
        chain?.doFilter(request, response)

        val httpStatus = (response as HttpServletResponse).status
        val processTime = System.currentTimeMillis() - startTime
        log.info("  Process End. time = $processTime. status code = $httpStatus ")

    }

    override fun destroy() {
    }

}