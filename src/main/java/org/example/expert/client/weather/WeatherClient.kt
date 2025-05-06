package org.example.expert.client.weather

import org.example.expert.client.weather.dto.WeatherDto
import org.example.expert.domain.common.exception.ServerException
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class WeatherClient(builder: RestTemplateBuilder) {
    private val restTemplate: RestTemplate = builder.build()

    fun getTodayWeather(): String {
        val responseEntity: ResponseEntity<Array<WeatherDto>> =
            restTemplate.getForEntity(
                buildWeatherApiUri(),
                Array<WeatherDto>::class.java
            )

        if (HttpStatus.OK != responseEntity.statusCode) {
            throw ServerException("날씨 데이터를 가져오는데 실패했습니다. 상태 코드: " + responseEntity.statusCode)
        }

        val weatherArray: Array<WeatherDto> = responseEntity.getBody()
            ?: throw ServerException("날씨 데이터가 없습니다.")

        return weatherArray.find { it.date == currentDate }
            ?.weather
            ?: throw ServerException("오늘에 해당하는 날씨 데이터를 찾을 수 없습니다.")
    }

    private fun buildWeatherApiUri(): URI {
        return UriComponentsBuilder
            .fromUriString("https://f-api.github.io")
            .path("/f-api/weather.json")
            .encode()
            .build()
            .toUri()
    }

    private val currentDate: String
        get() {
            val formatter = DateTimeFormatter.ofPattern("MM-dd")
            return LocalDate.now().format(formatter)
        }

}
