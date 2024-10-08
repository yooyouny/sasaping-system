package com.sparta.promotion.server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sparta.promotion.server.application.service.CouponService;
import com.sparta.promotion.server.domain.model.Coupon;
import com.sparta.promotion.server.domain.model.UserCoupon;
import com.sparta.promotion.server.domain.model.vo.CouponType;
import com.sparta.promotion.server.domain.model.vo.DiscountType;
import com.sparta.promotion.server.domain.repository.CouponRepository;
import com.sparta.promotion.server.domain.repository.UserCouponRepository;
import com.sparta.promotion.server.exception.PromotionErrorCode;
import com.sparta.promotion.server.exception.PromotionException;
import com.sparta.promotion.server.presentation.request.CouponRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

// TODO(경민): 이벤트 테이블 생성 후 테스트 로직 수정 필요
@SpringBootTest
class CouponServiceTests {

  @MockBean
  private CouponRepository couponRepository;

  @MockBean
  private UserCouponRepository userCouponRepository;

  /*
  @MockBean
  private EventRepository eventRepository;
   */

  @Autowired
  private CouponService couponService;

  @Test
  void test_이벤트_쿠폰_생성() {
    // given
    CouponRequest.Create request = new CouponRequest.Create(
        "Test Coupon",
        CouponType.EVENT,
        DiscountType.PRICE,
        new BigDecimal("1000.00"),
        null,
        null,
        100,
        Timestamp.valueOf("2024-01-01 00:00:00"),
        Timestamp.valueOf("2024-12-31 23:59:59"),
        null,
        1L
    );
    // Event event = new Event();  // 임의의 이벤트 객체 생성

    // when
    // Mockito.when(eventRepository.findById(request.getEventId())).thenReturn(Optional.of(event)); // 이벤트 조회 시 임의의 이벤트 반환
    when(couponRepository.save(any(Coupon.class))).thenReturn(Coupon.create(request));

    // then
    couponService.createEventCoupon(request);

    verify(couponRepository).save(Mockito.any(Coupon.class));
  }

  @Test
  void test_이벤트_쿠폰_실패() {
    // given
    CouponRequest.Create request = new CouponRequest.Create(
        "Test Coupon",
        CouponType.EVENT,
        DiscountType.PRICE,
        new BigDecimal("1000.00"),
        null,
        null,
        100,
        Timestamp.valueOf("2024-01-01 00:00:00"),
        Timestamp.valueOf("2024-12-31 23:59:59"),
        null,
        1L
    );

    // when
    // when(eventRepository.findById(request.getEventId())).thenReturn(Optional.empty());

    // PromotionException exception = assertThrows(PromotionException.class, () -> couponService.createEventCoupon(request));
    // assertEquals(PromotionErrorCode.EVENT_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  void test_이벤트_쿠폰_제공_성공() {
    // given
    Long userId = 1L;
    Long couponId = 100L;

    Coupon coupon = mock(Coupon.class);
    when(coupon.getQuantity()).thenReturn(10);

    UserCoupon userCoupon = UserCoupon.create(userId, couponId);

    // when
    when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
    when(userCouponRepository.save(any(UserCoupon.class))).thenReturn(userCoupon);

    // then
    couponService.provideEventCoupon(userId, couponId);

    verify(couponRepository).findById(couponId);
    verify(coupon).updateQuantity(anyInt());
    verify(userCouponRepository).save(any(UserCoupon.class));
  }

  @Test
  void test_이벤트_쿠폰_실패_수량부족() {
    // given
    Long userId = 1L;
    Long couponId = 100L;

    Coupon coupon = mock(Coupon.class);
    when(coupon.getQuantity()).thenReturn(0); // 수량이 0일 경우

    // when
    when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

    // then
    PromotionException exception = assertThrows(PromotionException.class, () -> {
      couponService.provideEventCoupon(userId, couponId);
    });

    assertEquals(PromotionErrorCode.INSUFFICIENT_COUPON.getMessage(), exception.getMessage());
    verify(couponRepository).findById(couponId);
    verify(coupon, never()).updateQuantity(anyInt());
    verify(userCouponRepository, never()).save(any(UserCoupon.class));
  }

  @Test
  void test_이벤트_쿠폰_실패_쿠폰없음() {
    // given
    Long userId = 1L;
    Long couponId = 100L;

    // when
    when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

    // then
    PromotionException exception = assertThrows(PromotionException.class, () -> {
      couponService.provideEventCoupon(userId, couponId);
    });

    assertEquals(PromotionErrorCode.COUPON_NOT_FOUND.getMessage(), exception.getMessage());
    verify(couponRepository).findById(couponId);
    verify(userCouponRepository, never()).save(any(UserCoupon.class)); // 저장이 일어나지 않음
  }

}
