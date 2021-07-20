package com.inspien.weather.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.inspien.weather.model.WeatherBox;

@Mapper
public interface WeatherBoxMapper {
	@Insert("INSERT INTO WeatherBox VALUES(#{status}, #{district}, #{time}, #{sky}, #{temperature}, #{humidity}, #{wind})")
	int putWeatherBox(@Param("status") String status, @Param("district") String district,@Param("time") String time,@Param("sky") String sky,@Param("temperature") String temperature,@Param("humidity") String humidity,@Param("wind") String wind);
	
	@Select("SELECT * FROM WeatherBox WHERE district = #{district} AND time = #{time}")
	WeatherBox getWeatherBox(@Param("district") String district, @Param("time") String time);
}
