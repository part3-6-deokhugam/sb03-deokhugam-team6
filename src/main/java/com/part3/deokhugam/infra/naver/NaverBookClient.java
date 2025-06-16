package com.part3.deokhugam.infra.naver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.part3.deokhugam.dto.book.NaverBookDto;
import com.part3.deokhugam.exception.ErrorCode;
import com.part3.deokhugam.exception.NaverBookException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class NaverBookClient {

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.client-secret}")
    private String clientSecret;

    public NaverBookDto getIsbn(String isbn) {
        try {
            isbn = URLEncoder.encode(isbn, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new NaverBookException(ErrorCode.INVALID_INPUT_VALUE, "검색어 인코딩 실패");
        }

        String apiURL = "https://openapi.naver.com/v1/search/book?query=" + isbn;

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        String responseBody;
        try {
            responseBody = get(apiURL, requestHeaders);
        } catch (NaverBookException e) {
            throw e;
        } catch (Exception e) {
            throw new NaverBookException(ErrorCode.NAVER_API_ERROR, "NAVER API 요청 실패: " + e.getMessage());
        }

        JsonNode items;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            items = root.get("items");
        } catch (JsonProcessingException e) {
            throw new NaverBookException(ErrorCode.NAVER_API_RESPONSE_ERROR, "NAVER API 응답 파싱 실패: " + e.getMessage());
        }

        if (items == null || !items.isArray() || items.size() == 0) {
            throw new NaverBookException(ErrorCode.BOOK_NOT_FOUND, "ISBN 검색 결과가 없습니다.");
        }

        JsonNode item = items.get(0);

        try {
            String title = item.get("title").asText().replaceAll("<[^>]+>", "");
            String author = item.get("author").asText();
            String description = item.get("description").asText();
            String publisher = item.get("publisher").asText();
            String pubDateStr = item.get("pubdate").asText();
            String bookIsbn = item.get("isbn").asText();
            String imageUrl = item.get("image").asText();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate publishedDate = LocalDate.parse(pubDateStr, formatter);

            byte[] imageBytes = downloadImage(imageUrl);
            String imageBase64 = Base64.getEncoder().encodeToString(imageBytes);

            return new NaverBookDto(title, author, description, publisher, publishedDate, bookIsbn, imageBase64);
        } catch (Exception e) {
            throw new NaverBookException(ErrorCode.NAVER_API_RESPONSE_ERROR, "도서 정보 파싱 실패: " + e.getMessage());
        }
    }

    private byte[] downloadImage(String imageUrl) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            return in.readAllBytes();
        } catch (Exception e) {
            throw new NaverBookException(ErrorCode.IMAGE_DOWNLOAD_FAILED, "이미지 다운로드 실패: " + e.getMessage());
        }
    }

    private static String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            InputStream responseStream = (responseCode == HttpURLConnection.HTTP_OK)
                ? con.getInputStream()
                : con.getErrorStream();

            return readBody(responseStream);
        } catch (IOException e) {
            throw new NaverBookException(ErrorCode.NAVER_API_ERROR, "API 요청/응답 실패: " + e.getMessage());
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new NaverBookException(ErrorCode.INVALID_INPUT_VALUE, "잘못된 API URL: " + apiUrl);
        } catch (IOException e) {
            throw new NaverBookException(ErrorCode.NAVER_API_ERROR, "API 연결 실패: " + e.getMessage());
        }
    }

    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body);
        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new NaverBookException(ErrorCode.NAVER_API_RESPONSE_ERROR, "API 응답 읽기 실패: " + e.getMessage());
        }
    }
}