package com.sparta.user.presentation.controller;

import com.sparta.user.application.service.PointHistoryService;
import com.sparta.user.application.service.UserService;
import com.sparta.user.user_dto.infrastructure.PointHistoryDto;
import com.sparta.user.user_dto.infrastructure.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/internal/users")
@RestController
public class UserInternalController {

  private final UserService userService;
  private final PointHistoryService pointHistoryService;

  @GetMapping
  public UserDto getUserByUsername(@RequestParam(value = "username") String username) {
    return userService.getUserByUsername(username);
  }

  @PostMapping("/point")
  public void createPointHistory(@RequestBody PointHistoryDto request) {
    pointHistoryService.createPointHistory(request);
  }

}
