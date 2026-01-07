package by.own.generator1.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.own.generator1.repository.EventRepository;
import by.own.sharedsources.dto.EventStatsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository repository;

    @InjectMocks
    private EventService service;

    @Test
    void getStats_returnsCorrectStatistics() {
        //Arrange
        when(repository.count()).thenReturn(100L);
        when(repository.countByIsConfirmedTrue()).thenReturn(70L);
        when(repository.countByIsConfirmedFalse()).thenReturn(30L);

        //Act
        EventStatsResponse response = service.getStats();

        //Assert
        assertThat(response.generated()).isEqualTo(100L);
        assertThat(response.confirmed()).isEqualTo(70L);
        assertThat(response.pending()).isEqualTo(30L);

        verify(repository, times(1)).count();
        verify(repository, times(1)).countByIsConfirmedTrue();
        verify(repository, times(1)).countByIsConfirmedFalse();
    }
}