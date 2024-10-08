package com.sparta.product.presentation.exception;

import com.sparta.common.domain.exception.BusinessException;
import lombok.Getter;

@Getter
public class ProductServerException extends BusinessException {
  private final ProductErrorCode errorCode;

  public ProductServerException(ProductErrorCode errorCode) {
    super(errorCode.getStatus().name(), errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ProductServerException(ProductErrorCode errorCode, Object... args) {
    super(errorCode.getStatus().name(), errorCode.getMessage(), args);
    this.errorCode = errorCode;
  }
}
