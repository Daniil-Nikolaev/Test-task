package by.own.generator1.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.own.generator1.service.EventService;
import by.own.sharedsources.exception.handler.RestExceptionHandler;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import by.own.sharedsources.dto.EventStatsResponse;
import by.own.sharedsources.exception.InternalServerError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EventController.class)
@Import(RestExceptionHandler.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void getStats_returns200() throws Exception {
        //Arrange
        when(eventService.getStats())
            .thenReturn(new EventStatsResponse(100L, 70L, 30L));

        //Act & Assert
        mockMvc.perform(get("/event/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.generated").value(100L))
            .andExpect(jsonPath("$.confirmed").value(70L))
            .andExpect(jsonPath("$.pending").value(30L));

        verify(eventService, times(1)).getStats();
    }

    @Test
    void getStats_returns500() throws Exception {
        //Arrange
        when(eventService.getStats()).thenThrow(new InternalServerError("Internal Server Error"));

        //Act & Assert
        mockMvc.perform(get("/event/stats"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errorMessage").value("Internal Server Error"));

        verify(eventService, times(1)).getStats();
    }
}