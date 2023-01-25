package d2s.stratz.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import d2s.stratz.CorruptedMatchException;
import d2s.stratz.StratzMatchSniffer;
import d2s.stratz.domain.Match;

@SpringBootApplication  
@RestController  
public class StratzParserController {
	
	@Autowired
	StratzMatchSniffer stratzSniffer;
	
	@GetMapping("/d2stratz")
	public String defaultAnswer() {
		return "hi, I can request info from https://stratz.com via its API and return a huge JSON containing all info stratz has about a certain match";
	}
	
	@GetMapping("/d2stratz/{id}")
	@ResponseBody
	public Match returnMatchInfo(@PathVariable("id") long id) throws CorruptedMatchException {
		
		Match result;
		try {
			result = stratzSniffer.formById(id);
		} catch (CorruptedMatchException e) {
			return Match.corrupted();
		}
		
		return result;
	}
	
}
