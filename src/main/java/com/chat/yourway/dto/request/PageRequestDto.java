package com.chat.yourway.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PageRequestDto {

  @NotNull(message = "Page should not be null")
  @Min(value = 0, message = "Page should be greater or equals 0")
  private Integer page;

  @NotNull(message = "Page size should not be null")
  @Min(value = 1, message = "Page size should be greater or equals 1")
  private Integer pageSize;

}
