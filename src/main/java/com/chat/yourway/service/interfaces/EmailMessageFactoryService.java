package com.chat.yourway.service.interfaces;

import com.chat.yourway.dto.common.EmailMessageDto;
import com.chat.yourway.dto.common.EmailMessageInfoDto;

public interface EmailMessageFactoryService {
    /**
     * Generates email message according to message information.
     *
     * @param emailMessageInfoDto The data for building email message.
     * @return An EmailMessageDto object which representing the generated email message.
     */
    EmailMessageDto generateEmailMessage(EmailMessageInfoDto emailMessageInfoDto);
}
