package com.zoho.getzoho.service;

public record IngestResult(
        int accepted,
        int created,
        int updated,
        int skippedOlder,
        int errors,
        int duplicatesInBatch
) {
}
