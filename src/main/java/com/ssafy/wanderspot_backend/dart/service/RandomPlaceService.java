package com.ssafy.wanderspot_backend.dart.service;

import com.ssafy.wanderspot_backend.dart.dto.LocationDto;
import java.util.List;

public interface RandomPlaceService {

    List<LocationDto> getRandomCoordinates(String lng, String lat);
}
