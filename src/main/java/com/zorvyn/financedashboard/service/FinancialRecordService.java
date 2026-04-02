package com.zorvyn.financedashboard.service;

import com.zorvyn.financedashboard.dto.FilteredRecordsResponseDTO;
import com.zorvyn.financedashboard.dto.FinancialRecordRequestDTO;
import com.zorvyn.financedashboard.dto.FinancialRecordResponseDTO;
import com.zorvyn.financedashboard.dto.RecordFilterCriteria;

import java.util.List;

public interface FinancialRecordService {

    FinancialRecordResponseDTO createRecord(FinancialRecordRequestDTO requestDTO);

    /** Returns all records without filtering (kept for backward compatibility). */
    List<FinancialRecordResponseDTO> getAllRecords();

    /**
     * Returns a paginated, filtered, and sorted subset of records.
     * All filter fields in criteria are optional.
     */
    FilteredRecordsResponseDTO filterRecords(RecordFilterCriteria criteria);

    FinancialRecordResponseDTO getRecordById(Long id);

    FinancialRecordResponseDTO updateRecord(Long id, FinancialRecordRequestDTO requestDTO);

    /**
     * Soft delete a record (sets isDeleted = true).
     */
    void deleteRecord(Long id);

    /**
     * Restore a soft-deleted record (sets isDeleted = false).
     */
    FinancialRecordResponseDTO restoreRecord(Long id);

    /**
     * Get all soft-deleted records (for admin recovery).
     */
    List<FinancialRecordResponseDTO> getDeletedRecords();
}
