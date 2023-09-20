package com.chat.yourway.mapper;

import com.chat.yourway.dto.response.ContactResponseDto;
import com.chat.yourway.model.Contact;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactMapper {

  ContactResponseDto toResponseDto(Contact contact);

}
