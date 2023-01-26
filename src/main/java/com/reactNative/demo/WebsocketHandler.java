package com.reactNative.demo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.core.type.TypeReference;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;


@Component
@Log4j2
public class WebsocketHandler extends TextWebSocketHandler{

	// 클라이언트 정보  저장
	private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<String, WebSocketSession>();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private Timer timer;
	private static final long DELAY = 0;
	private static final long PERIOD = 1000 * 10; //ms

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		System.out.println(session.getId() + " 클라이언트 접속");
		CLIENTS.put(session.getId(), session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println(session + " 클라이언트 접속 해제");
		CLIENTS.remove(session.getId());
//		timer.cancel(); // Cancel
	}

	// 사용자의 메시지를 받게 되면 동작하는 메소드
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String id = session.getId();  // 메시지를 보낸 클라이언트
		String payload = message.getPayload(); // 앱에서 보낸 메시지
		HashMap<String, String> map = new HashMap<String, String>();
		map = objectMapper.readValue(payload, new TypeReference<HashMap<String, String>>() {});
		System.out.println(map); //앱에서 보낸 데이터를 변수로 사용하여 DB에서 데이터 조회

		/*
		request == map == payload
		* {
			"publishYn" : "Y",
			"lang": "KO",
			"entType": "N",
		}
		* */

		/*
		response
		* {
		* "code": 200,
		* "message": "정상 호출",
		* "data": [
		* 		{
		* 			"end_at": "2023-01-25 15:30:59",
		* 			"id": 54197,
		* 			"publish_yn": "Y",
		* 			"lang": "KO",
		* 			"title": "한국어 등록 테스트",
		* 			"start_at": "2023-01-06 07:30:00",
		* 			"content": "<p>11111122</p>",
		* 			"n_num": 1
		* 		}
		* 	]
		* }
		*
		* */

		List<HashMap<String, Object>> list = new ArrayList<>();
		HashMap<String, Object> param = new HashMap<>();
		param.put("end_at", "2023-01-25 15:30:59");
		param.put("id", "54197");
		param.put("publish_yn", "Y");
		param.put("lang", "KO");
		param.put("title", "공사 안내");
		param.put("start_at", "2023-01-06 07:30:00");
		param.put("content", "<p>원료 식물원 동쪽 호수 공사중입니다.<p>");
		param.put("n_num", "1");
		list.add(param);

		param = new HashMap<>();
		param.put("end_at", "2023-01-25 15:30:59");
		param.put("id", "54198");
		param.put("publish_yn", "Y");
		param.put("lang", "KO");
		param.put("title", "관람료 인상");
		param.put("start_at", "2023-01-06 07:30:00");
		param.put("content", "<p>관람료를 2천원 인상합니다.</p>");
		param.put("n_num", "2");
		list.add(param);

		HashMap<String, Object> noticeData = new HashMap<>();
		noticeData.put("code", 200);
		noticeData.put("message", "정상 호출");
		noticeData.put("data", list);

		if(payload != null){
			CLIENTS.entrySet().forEach(arg -> {
				try {
					arg.getValue().sendMessage(new TextMessage(objectMapper.writeValueAsString(noticeData)));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
//		timer = new Timer();
//		TimerTask task = new TimerTask() {
//			@Override
//			public void run() {
//				LocalDateTime now = LocalDateTime.now();
//				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//				String formattedNow = now.format(formatter);
//
//				CLIENTS.entrySet().forEach( arg->{
//	//				if(!arg.getKey().equals(id)) {  //같은 아이디가 아니면 메시지를 전달합니다.
//						try {
//							arg.getValue().sendMessage(new TextMessage(objectMapper.writeValueAsString(formattedNow)));
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//	//				}
//				});
//				System.out.println("Time: " + formattedNow);
//			}
//		};

//		timer.scheduleAtFixedRate(task, DELAY, PERIOD);
	}

//	@Override
//	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//		String payload = message.getPayload();
//		System.out.println("payload : " + payload);
//		List<HashMap<String, Object>> listMap = new ArrayList<HashMap<String, Object>>();
//
//		HashMap<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("noticeId", "1");
//		paramMap.put("noticeTitle", "공지사항1");
//		listMap.add(paramMap);
//
//		paramMap = new HashMap<String, Object>();
//		paramMap.put("noticeId", "2");
//		paramMap.put("noticeTitle", "공지사항2");
//		listMap.add(paramMap);
//
//		System.out.println("listMap >>> : " + listMap);
//		for(WebSocketSession sess: list) {
//				sess.sendMessage(new TextMessage(objectMapper.writeValueAsString(listMap))); // writeValueAsString -> Json 문자열로 변환
//		}
//	}
}
