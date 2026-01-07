package by.own.registrar.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.own.registrar.dto.RegisteredEventResponse;
import by.own.registrar.service.RegisteredEventService;
import by.own.registrar.util.EventFilter;
import by.own.sharedsources.exception.InternalServerError;
import by.own.sharedsources.exception.handler.RestExceptionHandler;
import by.own.sharedsources.model.EventType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RegisteredEventController.class)
@Import(RestExceptionHandler.class)
public class RegisteredEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisteredEventService service;

    @Test
    void getEvents_withData_returns200() throws Exception {
        //Arrange
        RegisteredEventResponse response = new RegisteredEventResponse(
            UUID.randomUUID(),
            EventType.USER_LOGIN,
            "generator1",
            Instant.now(),
            "test"
        );

        Page<RegisteredEventResponse> page = new PageImpl<>(
            List.of(response), PageRequest.of(0, 10), 1);

        when(service.getEvents(any(EventFilter.class), any(Pageable.class))).thenReturn(page);

        //Act & Assert
        mockMvc.perform(get("/event/info")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).getEvents(any(), any());
    }

    @Test
    void getEvents_emptyResult_returns204() throws Exception {
        //Arrange
        Page<RegisteredEventResponse> emptyPage = new PageImpl<>(
            List.of(), PageRequest.of(0, 10), 0);

        when(service.getEvents(any(EventFilter.class), any(Pageable.class))).thenReturn(emptyPage);

        //Act & Assert
        mockMvc.perform(get("/event/info"))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));

        verify(service, times(1)).getEvents(any(), any());
    }

    @Test
    void getEvents_serviceThrows_returns500() throws Exception {
        //Arrange
        when(service.getEvents(any(EventFilter.class), any(Pageable.class)))
            .thenThrow(new InternalServerError("Internal Server Error"));

        //Act & Assert
        mockMvc.perform(get("/event/info"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.errorMessage").value("Internal Server Error"));

        verify(service, times(1)).getEvents(any(), any());
    }
}