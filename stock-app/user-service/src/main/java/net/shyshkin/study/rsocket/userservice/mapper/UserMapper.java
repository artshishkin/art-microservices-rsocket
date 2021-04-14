package net.shyshkin.study.rsocket.userservice.mapper;

import net.shyshkin.study.rsocket.userservice.dto.UserDto;
import net.shyshkin.study.rsocket.userservice.entity.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    User asUser(UserDto userDto);

    UserDto asDto(User user);
}
