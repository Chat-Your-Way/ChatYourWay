package com.chat.yourway.dto.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ContactProfileResponseDto {

    private String nickname;
    private String email;
    private Byte avatarId;
    private Boolean hasPermissionSendingPrivateMessage;
}
