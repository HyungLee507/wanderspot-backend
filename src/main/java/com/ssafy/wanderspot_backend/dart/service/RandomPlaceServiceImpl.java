package com.ssafy.wanderspot_backend.dart.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.wanderspot_backend.dart.dto.LocationDto;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RandomPlaceServiceImpl implements RandomPlaceService {

    @Value("${place.api-key}")
    private String apiKey;

    public List<LocationDto> getRandomCoordinates(String lng, String lat) {
        StringBuilder result = new StringBuilder();
        List<LocationDto> randomCoordinates = new ArrayList<>();

        try {
            // OpenAPI URL
            String apiUrl = "https://apis.data.go.kr/B551011/KorService1/locationBasedList1?" +
                    "serviceKey=" + apiKey +
                    "&numOfRows=10" +
                    "&MobileOS=ETC" +
                    "&MobileApp=MobileTest" +
                    "&_type=json" +
                    "&mapX=" + lng +
                    "&mapY=" + lat +
                    "&radius=20000";

            // OpenAPI 요청
            URL url = new URL(apiUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            urlConnection.disconnect();

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(result.toString());
            JsonNode items = rootNode.path("response").path("body").path("items").path("item");

            // 위도와 경도 정보 추출
            List<LocationDto> coordinates = new ArrayList<>();
            for (JsonNode item : items) {
                String mapX = item.path("mapx").asText(); // 경도
                String mapY = item.path("mapy").asText();
                String addr = item.path("addr1").asText();
                // 위도
                coordinates.add(new LocationDto(mapX, mapY, addr));
            }

            // 랜덤하게 3개 선택
            Collections.shuffle(coordinates);
            randomCoordinates = coordinates.subList(0, Math.min(3, coordinates.size()));

        } catch (Exception e) {
            log.error("공공 데이터 API 조회에 실패했습니다. ", e);
        }

        log.info("Random Coordinates: {}", randomCoordinates);
        return randomCoordinates;
    }
}
