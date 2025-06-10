// com.part3.deokhugam.api.NotificationApi.java
package com.part3.deokhugam.api;

import com.part3.deokhugam.dto.notification.NotificationDto;
import com.part3.deokhugam.dto.notification.NotificationUpdateRequest;
import com.part3.deokhugam.dto.pagination.CursorPageResponseNotificationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@Tag(name = "알림 관리", description = "알림 관련 API")
@RequestMapping("/api/notifications")
public interface NotificationApi {

  @Operation(
      summary = "알림 목록 조회",
      parameters = {
          @Parameter(name = "userId",
              description = "사용자 ID",
              required = true,
              in = ParameterIn.QUERY,
              schema = @Schema(type="string", format="uuid")),
          @Parameter(name = "direction",
              description = "정렬 방향(ASC/DESC)",
              in = ParameterIn.QUERY,
              schema = @Schema(type="string",
                  allowableValues={"ASC","DESC"},
                  defaultValue="DESC")),
          @Parameter(name = "cursor",
              description = "커서(ISO-8601 createdAt 이전)",
              in = ParameterIn.QUERY,
              schema = @Schema(type="string", format="date-time")),
          @Parameter(name = "after",
              description = "보조 커서(ISO-8601 createdAt 이후)",
              in = ParameterIn.QUERY,
              schema = @Schema(type="string", format="date-time")),
          @Parameter(name = "limit",
              description = "페이지 크기",
              in = ParameterIn.QUERY,
              schema = @Schema(type="integer", format="int32", defaultValue="20"))
      },
      responses = {
          @ApiResponse(responseCode="200", description="알림 목록 조회 성공",
              content=@Content(schema=@Schema(implementation=CursorPageResponseNotificationDto.class))),
          @ApiResponse(responseCode="400", description="잘못된 요청 (파라미터 오류)", content=@Content),
          @ApiResponse(responseCode="404", description="사용자 정보 없음",        content=@Content),
          @ApiResponse(responseCode="500", description="서버 내부 오류",        content=@Content)
      }
  )
  @GetMapping
  ResponseEntity<CursorPageResponseNotificationDto> getNotifications(
      @RequestParam("userId") UUID userId,
      @RequestParam(value="direction", defaultValue="DESC") String direction,
      @RequestParam(value="cursor",    required=false)   String cursor,
      @RequestParam(value="after",     required=false)   String after,
      @RequestParam(value="limit",     defaultValue="20") int limit
  );

  @Operation(
      summary = "알림 읽음 상태 업데이트",
      parameters = {
          @Parameter(name="notificationId", in=ParameterIn.PATH, description="알림 ID", required=true),
          @Parameter(name="Deokhugam-Request-User-ID", in=ParameterIn.HEADER,
              description="요청자 ID", required=true,
              schema=@Schema(type="string", format="uuid"))
      },
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description="알림 상태 정보", required=true,
          content=@Content(schema=@Schema(implementation=NotificationUpdateRequest.class))
      ),
      responses = {
          @ApiResponse(responseCode="200", description="알림 상태 업데이트 성공",
              content=@Content(schema=@Schema(implementation=NotificationDto.class))),
          @ApiResponse(responseCode="400", description="잘못된 요청", content=@Content),
          @ApiResponse(responseCode="403", description="알림 수정 권한 없음", content=@Content),
          @ApiResponse(responseCode="404", description="알림 정보 없음", content=@Content),
          @ApiResponse(responseCode="500", description="서버 내부 오류", content=@Content)
      }
  )
  @PatchMapping("/{notificationId}")
  ResponseEntity<NotificationDto> patchNotification(
      @PathVariable("notificationId") UUID notificationId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId,
      @Valid @RequestBody NotificationUpdateRequest req
  );

  @Operation(
      summary = "모든 알림 읽음 처리",
      parameters = {
          @Parameter(name="Deokhugam-Request-User-ID",
              in=ParameterIn.HEADER,
              required=true,
              description="요청자 ID",
              schema=@Schema(type="string", format="uuid"))
      },
      responses = {
          @ApiResponse(responseCode="204", description="알림 읽음 처리 성공"),
          @ApiResponse(responseCode="400", description="잘못된 요청 (사용자 ID 누락)", content=@Content),
          @ApiResponse(responseCode="404", description="사용자 정보 없음",      content=@Content),
          @ApiResponse(responseCode="500", description="서버 내부 오류",      content=@Content)
      }
  )
  @PatchMapping("/read-all")
  ResponseEntity<Void> patchReadAll(
      @RequestHeader("Deokhugam-Request-User-ID") UUID userId
  );
}