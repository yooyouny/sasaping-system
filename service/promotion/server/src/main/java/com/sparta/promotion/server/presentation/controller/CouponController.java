package com.sparta.promotion.server.presentation.controller;

import com.sparta.auth.auth_dto.jwt.JwtClaim;
import com.sparta.common.domain.response.ApiResponse;
import com.sparta.promotion.server.application.service.CouponService;
import com.sparta.promotion.server.presentation.request.CouponRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/coupons")
@RestController
public class CouponController {

  private final CouponService couponService;

  @PostMapping("/event")
  public ApiResponse<?> createEventCoupon(@RequestBody @Valid CouponRequest.Create request) {
    couponService.createEventCoupon(request);
    return ApiResponse.ok();
  }

  @PostMapping("/event/{couponId}")
  public ApiResponse<?> provideEventCoupon(
      @PathVariable(name = "couponId") Long couponId,
      @AuthenticationPrincipal JwtClaim claim
  ) {
    couponService.provideEventCoupon(claim.getUserId(), couponId);
    return ApiResponse.ok();
  }

}
