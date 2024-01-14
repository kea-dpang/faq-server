package kea.dpang.faq.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import kea.dpang.faq.base.ErrorResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import java.time.LocalDateTime


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig {

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring()
                .requestMatchers(
                    "/api-document/**",
                    "/swagger-ui/**"
                )
        }
    }

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .httpBasic { it.disable() } // rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
            .csrf { it.disable() } // rest api이므로 csrf 보안이 필요없으므로 disable처리.
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // jwt token으로 인증 -> 세션은 필요없으므로 생성안함.
            .cors { it.disable() } // CORS(Cross-Origin Resource Sharing) 설정 비활성화.
            .headers { headers -> headers.frameOptions { it.disable() } } // X-Frame-Options 비활성화 (IFrame 사용 가능하도록).
            .formLogin { it.disable() } // formLogin 대신 Jwt를 사용하기 때문에 disable로 설정
            .logout { it.disable() } // 로그아웃 기능 비활성화.
            .exceptionHandling {
                it.authenticationEntryPoint { request, response, authException ->
                    // 인증 오류 처리 로직
                    val errorResponse = ErrorResponse(
                        timestamp = LocalDateTime.now(),
                        status = HttpStatus.UNAUTHORIZED.value(),
                        error = HttpStatus.UNAUTHORIZED.name,
                        message = authException?.message ?: "세부 정보가 제공되지 않았습니다",
                        path = request.requestURI
                    )

                    // JSON 변환 기능 설정
                    val mapper = ObjectMapper()
                        .registerModule(JavaTimeModule())
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

                    val responseBody = mapper.writeValueAsString(errorResponse)

                    response.contentType = MediaType.APPLICATION_JSON_VALUE
                    response.status = HttpStatus.UNAUTHORIZED.value()
                    response.writer.write(responseBody)
                }
            } // 인증되지 않은 사용자가 보호된 리소스에 접근하려고 시도하면 401 Unauthorized를 반환하도록 설정
            .authorizeHttpRequests { it.anyRequest().authenticated() } // 모든 요청이 인증을 필요로 하도록 설정
            .build()
    }
}
