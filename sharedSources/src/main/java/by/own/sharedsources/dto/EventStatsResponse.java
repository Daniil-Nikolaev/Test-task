package by.own.sharedsources.dto;

public record EventStatsResponse(
    long generated,
    long confirmed,
    long pending
) {}