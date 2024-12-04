package com.ssafy.wanderspot_backend.common;

public enum LocationInfo {
    SEOUL("서울특별시", 1),
    GYEONGGI("경기도", 2),
    INCHEON("인천광역시", 3),
    GANGWON("강원도", 4),
    CHUNGCHEONG("충청도", 5),
    JEOLLA("전라도", 6),
    GYEONGSANG("경상도", 7),
    JEJU("제주도", 8),
    OTHER("기타 지역", 9);

    private final String regionName;
    private final int regionCode;

    LocationInfo(String regionName, int regionCode) {
        this.regionName = regionName;
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public int getRegionCode() {
        return regionCode;
    }

    /**
     * 지역 코드로 LocationInfo를 찾는 메서드
     */
    public static LocationInfo fromRegionCode(int code) {
        for (LocationInfo location : LocationInfo.values()) {
            if (location.getRegionCode() == code) {
                return location;
            }
        }
        throw new IllegalArgumentException("Invalid region code: " + code);
    }

}
