날씨조회 API
=====================
## 사용 
* SpringBoot  
* MySQL  
* 기상청 API(초단기 예보)[SKY, Temperature, humidity, wind 사용]


## URL형태
> localhost:8080/weather/[seoul,busan,gangwon,gywonggi,sejong,jeju] 중 하나  
 -  ex) localhost:8080/weather/seoul   ---> 서울의 현재 시간 날씨정보 

> localhost:8080/weather/seoul/[00 ~ 23]
 - ex) localhost:8080/weather/seoul/22   ---> 서울의 오늘 22시의 날씨정보
 
 
## 내부 동작



## DB<br/>
![DB 형태](https://user-images.githubusercontent.com/43934497/126314966-7523d8c8-70d8-44a1-a3da-1129db5ee92c.png)
## 결과 예시
* 서울 8시 날씨 조회<br/>  
![결과](https://user-images.githubusercontent.com/43934497/126315014-5b0735b7-b097-4732-8821-6fa300dc3e94.png)
* 현재 시간의 서울 날씨 조희<br/>  
![결과2](https://user-images.githubusercontent.com/43934497/126315035-166e9494-6c71-422f-84af-0db9b0020844.png)

## 예외 처리
 * URL을 잘못 요청할 경우<br/>  
 ![예외처리1](https://user-images.githubusercontent.com/43934497/126315767-e3743ca5-4cb8-4d94-a8f1-d81206390b4b.png)
 
 * DB에 등록되지 않은 지역을 요청할 경우<br/>  
 ![예외처리2](https://user-images.githubusercontent.com/43934497/126315812-e630888b-a8a8-423e-bded-41c68a8f6d50.png)
 
 * DB에 넣어두지 않은 값을 요청할 경우<br/>   
 ![예외처리3](https://user-images.githubusercontent.com/43934497/126315844-b0fa0174-fc05-48c6-a5fb-ffd05a274d34.png)
 
* 00 ~ 23 이외의 시간을 요청할 경우<br/>  
![예외처리4](https://user-images.githubusercontent.com/43934497/126315876-5bcfef11-a38d-49f8-afb1-9f033193a69c.png)

## 배운점
* @RestController  
    + @Controller + @ResponseBody와 같음
    + @ResponseBody 안붙여줘도 객체를 JSON객체로 리턴 시켜줌</br>
* @PostConstruct
    + Bean LifeCycle에서 한번만 수행됨
    + 빈이 완전히 생성된 상태(@Autowired 주입 완료 후)에서 한번만 실행됨</br>
    ![PostConstruct](https://user-images.githubusercontent.com/43934497/126335684-5af432c2-b800-42e2-881f-b16d9435dfee.png)
* @PathVariable
    + URI 템플릿 변수에 접근시 사용</br>
    ![Pathvariable](https://user-images.githubusercontent.com/43934497/126335565-b1b98591-852d-426a-abed-6071b684bbea.png)  
* @Mapper
    + 해당 인터페이스를 빈으로 주입</br>
    ![Mapper](https://user-images.githubusercontent.com/43934497/126335915-df376fb1-d604-4471-86a2-9cdbb94f1589.png)
    
