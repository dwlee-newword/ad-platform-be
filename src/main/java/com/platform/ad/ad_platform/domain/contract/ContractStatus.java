package com.platform.ad.ad_platform.domain.contract;

public enum ContractStatus {
    PENDING,       // 집행 전 (체결완료: 시작일 도달 전)
    IN_PROGRESS,   // 진행 중 (집행 중)
    CANCELLED,     // 광고취소 (중도해지: 인위적 해지/취소)
    COMPLETED      // 광고종료 (기간만료)
}
