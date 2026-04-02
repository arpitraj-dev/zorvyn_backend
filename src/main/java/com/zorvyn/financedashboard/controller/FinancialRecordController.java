package com.zorvyn.financedashboard.controller;

import com.zorvyn.financedashboard.dto.ApiResponseDTO;
import com.zorvyn.financedashboard.dto.FilteredRecordsResponseDTO;
import com.zorvyn.financedashboard.dto.FinancialRecordRequestDTO;
import com.zorvyn.financedashboard.dto.FinancialRecordResponseDTO;
import com.zorvyn.financedashboard.dto.RecordFilterCriteria;
import com.zorvyn.financedashboard.security.annotation.AdminOnly;
import com.zorvyn.financedashboard.security.annotation.ReadAccess;
import com.zorvyn.financedashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class FinancialRecordController {

        private final FinancialRecordService financialRecordService;

        /**
         * POST /records → Create a new financial record
         * Only ADMIN can create records
         */
        @PostMapping
        @AdminOnly
        public ResponseEntity<ApiResponseDTO<FinancialRecordResponseDTO>> createRecord(
                        @Valid @RequestBody FinancialRecordRequestDTO requestDTO) {
                FinancialRecordResponseDTO createdRecord = financialRecordService.createRecord(requestDTO);
                return new ResponseEntity<>(
                                ApiResponseDTO.success("Financial record created successfully", createdRecord),
                                HttpStatus.CREATED);
        }

        /**
         * GET /records → Get financial records with optional filtering, pagination, and
         * sorting
         * All authenticated users can view (ADMIN, ANALYST, VIEWER)
         */
        @GetMapping
        @ReadAccess
        public ResponseEntity<ApiResponseDTO<FilteredRecordsResponseDTO>> getRecords(
                        @Valid @ModelAttribute RecordFilterCriteria criteria) {

                FilteredRecordsResponseDTO responseDTO = financialRecordService.filterRecords(criteria);

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Financial records fetched successfully", responseDTO));
        }

        /**
         * GET /records/{id} → Get financial record by ID
         * All authenticated users can view (ADMIN, ANALYST, VIEWER)
         */
        @GetMapping("/{id}")
        @ReadAccess
        public ResponseEntity<ApiResponseDTO<FinancialRecordResponseDTO>> getRecordById(
                        @PathVariable Long id) {
                FinancialRecordResponseDTO record = financialRecordService.getRecordById(id);
                return ResponseEntity.ok(
                                ApiResponseDTO.success("Financial record fetched successfully", record));
        }

        /**
         * PUT /records/{id} → Update financial record
         * Only ADMIN can update records
         */
        @PutMapping("/{id}")
        @AdminOnly
        public ResponseEntity<ApiResponseDTO<FinancialRecordResponseDTO>> updateRecord(
                        @PathVariable Long id,
                        @Valid @RequestBody FinancialRecordRequestDTO requestDTO) {
                FinancialRecordResponseDTO updatedRecord = financialRecordService.updateRecord(id, requestDTO);
                return ResponseEntity.ok(
                                ApiResponseDTO.success("Financial record updated successfully", updatedRecord));
        }

        /**
         * DELETE /records/{id} → Soft delete financial record
         * Only ADMIN can delete records
         */
        @DeleteMapping("/{id}")
        @AdminOnly
        public ResponseEntity<ApiResponseDTO<Void>> deleteRecord(@PathVariable Long id) {
                financialRecordService.deleteRecord(id);
                return ResponseEntity.ok(
                                ApiResponseDTO.success("Financial record deleted successfully"));
        }

        /**
         * POST /records/{id}/restore → Restore a soft-deleted record
         * Only ADMIN can restore records
         */
        @PostMapping("/{id}/restore")
        @AdminOnly
        public ResponseEntity<ApiResponseDTO<FinancialRecordResponseDTO>> restoreRecord(
                        @PathVariable Long id) {
                FinancialRecordResponseDTO restoredRecord = financialRecordService.restoreRecord(id);
                return ResponseEntity.ok(
                                ApiResponseDTO.success("Financial record restored successfully", restoredRecord));
        }

        /**
         * GET /records/deleted → Get all soft-deleted records
         * Only ADMIN can view deleted records
         */
        @GetMapping("/deleted")
        @AdminOnly
        public ResponseEntity<ApiResponseDTO<List<FinancialRecordResponseDTO>>> getDeletedRecords() {
                List<FinancialRecordResponseDTO> deletedRecords = financialRecordService.getDeletedRecords();
                return ResponseEntity.ok(
                                ApiResponseDTO.success("Deleted records fetched successfully", deletedRecords));
        }
}
