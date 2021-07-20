package com.inspien.weather.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.inspien.weather.mapper.WeatherBoxMapper;
import com.inspien.weather.model.Box;
import com.inspien.weather.model.ErrorBox;
import com.inspien.weather.model.WeatherBox;

@RestController
public class WeatherController {

	private String serviceKey = "qymOwHVyzvmARnEwrX%2F59aQSWvoPyYyj9vCpD1M8zs9qmmFPoyyMmNWRVCKnhNw4t5HPHbHdGk0D%2FbPGZs%2FHGQ%3D%3D";
	private Map<String, String[]> areaMap;
	private Map<String, String> DBMap;
	private WeatherBoxMapper mapper;

	public WeatherController(WeatherBoxMapper mapper) { // 스프링 DI
		this.mapper = mapper;
	}

	@PostConstruct
	public void init() {
		DBMap = new HashMap<>();
		areaMap = new HashMap<>();
		areaMap.put("seoul", new String[] { "60", "127" });
		areaMap.put("busan", new String[] { "98", "76" });
		areaMap.put("gangwon", new String[] { "73", "134" });
		areaMap.put("gyeonggi", new String[] { "60", "120" });
		areaMap.put("sejong", new String[] { "66", "103" });
		areaMap.put("jeju", new String[] { "52", "38" });
	}

	@GetMapping("/**")
	public Box getError() {
		return new ErrorBox("400",
				"잘못된 URL 요청입니다. /weather/{Seoul | Gyeonggi | Busan | Gangwon | Sejong | Jeju 중 하나로 요청하세요}");
	}

	@GetMapping("/weather/{admin_district}")
	public Box getWeather(@PathVariable("admin_district") String district) {
		LocalDateTime present = LocalDateTime.now();
		LocalDateTime dt = present.minusHours(1);
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH30");
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
		String reqTime = dt.format(timeFormat);
		String reqDate = dt.format(dateFormat);
		String DB_key = district.toUpperCase() + reqDate + reqTime;

		if (!areaMap.containsKey(district.toLowerCase())) {
			return new ErrorBox("400",
					"등록되지 않은 지역입니다. 현재 등록되어 있는 지역은 서울(Seoul),경기(Gyeonggi),부산(Busan),강원(Gangwon),세종(Sejong),제주(Jeju)입니다.");
		} else if (DBMap.containsKey(DB_key)) { //DB에 이미 있을경우, API 재요청이 아닌 DB에서 가져온다.
			String[] param = DBMap.get(DB_key).split("_");
			String presentTime = present.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
			return mapper.getWeatherBox(param[0], param[1]).setTime(presentTime);
		} else {
			String[] pos = areaMap.get(district.toLowerCase());
			String position_x = pos[0];
			String position_y = pos[1];
			String forecast_time = present.plusHours(1).format(DateTimeFormatter.ofPattern("HH00"));
			String presentTime = present.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

			// 2번째 요청부터는 API 재요청이 아닌, DB에서 꺼내올 수 있도록 DBMap에 저장
			DBMap.put(DB_key, district.toUpperCase() + "_" + presentTime);

			try {
				// 기상청 API 요청값 예시 -> base_date=20210716&base_time=0500
				URL url = new URL("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst?serviceKey="
						+ serviceKey + "&pageNo=1&numOfRows=60&dataType=JSON&base_date=" + reqDate + "&base_time="
						+ reqTime + "&nx=" + position_x + "&ny=" + position_y);
				BufferedReader bf;
				String line;
				StringBuilder result = new StringBuilder();

				bf = new BufferedReader(new InputStreamReader(url.openStream()));
				while ((line = bf.readLine()) != null) {
					result.append(line);
				}

				return makeWeatherBox(result.toString(), forecast_time, district, presentTime);

			} catch (IOException e) {
				// 서비스key가 만료되었거나, 기상청 URL 요청주소 혹은 방식이 변경되었는지 확인해주세요
				return new ErrorBox("500", "서비스 key가 만료되었거나, 기상청 URL 요청주소 혹은 요청방식이 변경되었는지 확인이 필요합니다.");
			}

			catch (ParseException e) {
				// makeWeatherBox에서 파싱에 문제가 생겼을시 들어옴
				return new ErrorBox("500", "기상청 자료 파싱과정 중 문제가 발생하였습니다.");
			}
		}
	}

	@GetMapping("/weather/{admin_district}/{req_time}") // req_time의 경우 00~23(시간)으로 지정
	public Box getWeatherBytime(@PathVariable("admin_district") String district,
			@PathVariable("req_time") String req_time) {
		String pattern = "[0-2][0-9]";
		LocalDateTime present = LocalDateTime.now();
		
		if (!areaMap.containsKey(district.toLowerCase())) {
			return new ErrorBox("400",
					"등록되지 않은 지역입니다. 현재 등록되어 있는 지역은 서울(Seoul),경기(Gyeonggi),부산(Busan),강원(Gangwon),세종(Sejong),제주(Jeju)입니다.");
		}
		if (!Pattern.matches(pattern, req_time) || Integer.parseInt(req_time) > 24) {
			return new ErrorBox("400",
					"시간은 (00 ~ 23)을 넣어주셔야 합니다.");
		}
		String presentDate = present.format(DateTimeFormatter.ofPattern("yyyy-MM-dd "));
		
		WeatherBox weather = mapper.getWeatherBox(district.toUpperCase(), (presentDate + req_time + ":00"));
		
		return (weather != null) ? weather : new ErrorBox("500","DB에 저장되지 않은 값입니다. DB에 먼저 생성 후 진행해주세요.");
		
				
	}

	public WeatherBox makeWeatherBox(String line, String forecast_time, String district, String present_time)
			throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(line);
		JSONObject response = (JSONObject) obj.get("response");
		JSONObject body = (JSONObject) response.get("body");
		JSONObject items = (JSONObject) body.get("items");
		JSONArray item = (JSONArray) items.get("item");

		JSONObject weather;
		String category;
		String time;
		String value;
		String sky = "";
		String humidity = "";
		String temperature = "";
		String wind = "";

		for (int i = 0; i < item.size(); i++) {
			weather = (JSONObject) item.get(i);
			category = (String) weather.get("category");
			time = (String) weather.get("fcstTime");
			value = (String) weather.get("fcstValue");

			if (time.equals(forecast_time)) {
				// 하늘 상태
				if (category.equals("SKY")) {
					switch (value) {
					case "1":
						sky = "맑음";
						break;
					case "3":
						sky = "구름 많음";
						break;
					case "4":
						sky = "흐림";
						break;
					default:
						sky = "눈 또는 비";
						break;
					}
				}

				// 기온
				if (category.equals("T1H")) {
					temperature = value + "도";
				}
				// 습도
				if (category.equals("REH")) {
					humidity = value + "%";
				}
				// 풍속
				if (category.equals("WSD")) {
					wind = value + "m/s";
				}
			}
		}

		// DB(MYSQL)에 넣기
		mapper.putWeatherBox("200", district.toUpperCase(), present_time, sky, temperature, humidity, wind);

		return new WeatherBox("200", district.toUpperCase(), present_time, sky, temperature, humidity, wind);
	}

}
