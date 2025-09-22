package me.owlsleep.owlab.controller;

import me.owlsleep.owlab.service.WordleService;
import me.owlsleep.owlab.service.WordleStatisticService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WordleController.class)
class WordleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WordleService wordleService;

    @MockBean
    private WordleStatisticService wordleStatisticService;

    @Test
    void gamesAreIsolatedPerSession() throws Exception {
        when(wordleService.getRandomWord()).thenReturn("APPLE", "BERRY");
        when(wordleService.exists(anyString())).thenReturn(true);
        when(wordleService.checkGuess(eq("APPLE"), eq("APPLE"))).thenReturn(List.of("G", "G", "G", "G", "G"));
        when(wordleService.checkGuess(eq("APPLE"), eq("BERRY"))).thenReturn(List.of("B", "B", "B", "B", "B"));
        when(wordleService.checkGuess(eq("BERRY"), eq("BERRY"))).thenReturn(List.of("G", "G", "G", "G", "G"));

        MockHttpSession session1 = new MockHttpSession();
        MockHttpSession session2 = new MockHttpSession();

        mockMvc.perform(post("/wordle/start").session(session1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("새 게임 시작"));

        mockMvc.perform(post("/wordle/start").session(session2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("새 게임 시작"));

        mockMvc.perform(post("/wordle/guess").session(session1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"guess\":\"APPLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result[0]").value("G"));

        mockMvc.perform(post("/wordle/guess").session(session1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"guess\":\"APPLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("게임이 시작되지 않았습니다."));

        mockMvc.perform(post("/wordle/guess").session(session2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"guess\":\"APPLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0]").value("B"))
                .andExpect(jsonPath("$.success").doesNotExist());

        mockMvc.perform(post("/wordle/guess").session(session2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"guess\":\"BERRY\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result[0]").value("G"));
    }
}
