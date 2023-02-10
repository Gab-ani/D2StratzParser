package d2s.stratz;

import java.util.ArrayList;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import d2s.stratz.domain.Match;

@Component
public class StratzMatchSniffer {

	// will trigger GitGuardian but idgaf, they are free, only used for identification
	// TODO probably rewrite as some file config thing with gitignore or hide into database
	private static final String API_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1laWQiOiJodHRwczovL3N0ZWFtY29tbXVuaXR5LmNvbS9vcGVuaWQvaWQvNzY1NjExOTgyODk3OTU3ODgiLCJ1bmlxdWVfbmFtZSI6IkRhcmsgb2YgQ2hhcmdlbmVzcyIsIlN1YmplY3QiOiIwNTZkZDI3Yi03MTAzLTRlMzctYTc0OC1jZmZjMjgwYjViZTgiLCJTdGVhbUlkIjoiMzI5NTMwMDYwIiwibmJmIjoxNjYwMzM3NTI2LCJleHAiOjE2OTE4NzM1MjYsImlhdCI6MTY2MDMzNzUyNiwiaXNzIjoiaHR0cHM6Ly9hcGkuc3RyYXR6LmNvbSJ9.m_s22LBhQgb863YOUDy0zze5k2I0dXgvw9kOGqbQXT0";
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:103.0) Gecko/20100101 Firefox/103.0";
	
	public Match formById(Long matchId) throws CorruptedMatchException {
		
		String json = fetchJson(matchId);
		
		return jsonToMatch(json);
	}
	
	private String fetchJson(long matchId) {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("user-agent", USER_AGENT);
		headers.add("Authorization", API_TOKEN);
		
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		
		try {
			ResponseEntity<String> response = restTemplate.exchange(urlFromId(matchId), HttpMethod.GET, entity, String.class);
			String json = response.getBody();
			
			return json;
		} catch (Exception e) {
			return ""; // then jsonToMatch throws 
		}
		
	}
	
	private Match jsonToMatch(String json) throws CorruptedMatchException {
		
		if(json == null || json.equals("")) {
			throw new CorruptedMatchException();
		}
		
		Match result = new Match();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jNode;
		
		try {
			
			jNode = mapper.readTree(json);
			result.setDate(jNode.get("startDateTime").asLong());
			result.setMatchId(jNode.get("id").asLong());
			
			var dire = new ArrayList<String>();
			var radiant = new ArrayList<String>();
			for(int i = 0; i < 10; i++) {
				//parsePlayer(result, jNode.get("players").get(i));
				String hero = jNode.get("players").get(i).get("heroId").asText();
				if(jNode.get("players").get(i).get("isRadiant").asBoolean()) {	
					radiant.add(hero);
				} else {
					dire.add(hero);
				}
			}
			result.setTeamDire(dire);
			result.setTeamRadiant(radiant);
			
			if(jNode.get("didRadiantWin").asBoolean()) {
				result.setWinner("radiant");
			} else {
				result.setWinner("dire");
			}
			
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
		result.setParsed(true);
		
		return result;
	}

	private String urlFromId(long id) {
	//	System.out.println("https://api.stratz.com/api/v1/match//breakdown");
		return "https://api.stratz.com/api/v1/match/" + id + "/breakdown";
	}
	
}
