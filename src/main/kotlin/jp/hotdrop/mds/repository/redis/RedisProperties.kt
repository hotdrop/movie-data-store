package jp.hotdrop.mds.repository.redis

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty

@Component
@ConfigurationProperties(prefix = "spring.redis")
data class RedisProperties(

        @NotEmpty
        var host: String? = null,

        @Min(1024)
        @Max(65535)
        var port: Int? = null
)