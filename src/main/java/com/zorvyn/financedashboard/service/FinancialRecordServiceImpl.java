package com.zorvyn.financedashboard.service;

import com.zorvyn.financedashboard.dto.FilteredRecordsResponseDTO;
import com.zorvyn.financedashboard.dto.FinancialRecordRequestDTO;
import com.zorvyn.financedashboard.dto.FinancialRecordResponseDTO;
import com.zorvyn.financedashboard.dto.RecordFilterCriteria;
import com.zorvyn.financedashboard.entity.FinancialRecord;
import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.exception.ResourceNotFoundException;
import com.zorvyn.financedashboard.mapper.FinancialRecordMapper;
import com.zorvyn.financedashboard.repository.FinancialRecordRepository;
import com.zorvyn.financedashboard.repository.FinancialRecordSpecification;
import com.zorvyn.financedashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Financial Record operations.
 * Contains business logic and comprehensive logging for all operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;
    private final FinancialRecordMapper financialRecordMapper;

    @Override
    @Transactional
    public FinancialRecordResponseDTO createRecord(FinancialRecordRequestDTO requestDTO) {
        log.info("Creating financial record - userId: {}, type: {}, amount: {}, category: {}",
                requestDTO.getUserId(), requestDTO.getType(), requestDTO.getAmount(), requestDTO.getCategory());

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> {
                    log.error("Failed to create record - User not found with id: {}", requestDTO.getUserId());
                    return new ResourceNotFoundException("User not found with id: " + requestDTO.getUserId());
                });

        FinancialRecord record = financialRecordMapper.toEntity(requestDTO, user);
        FinancialRecord savedRecord = financialRecordRepository.save(record);

        log.info("Financial record created successfully - id: {}, userId: {}, type: {}, amount: {}",
                savedRecord.getId(), user.getId(), savedRecord.getType(), savedRecord.getAmount());
        
        return financialRecordMapper.toResponseDTO(savedRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialRecordResponseDTO> getAllRecords() {
        log.info("Fetching all financial records");
        
        List<FinancialRecord> records = financialRecordRepository.findAll();
        
        log.info("Retrieved {} financial records", records.size());
        
        return records.stream()
                .map(financialRecordMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FilteredRecordsResponseDTO filterRecords(RecordFilterCriteria criteria) {
        log.info("Filtering records - page: {}, size: {}, sortBy: {}, order: {}",
                criteria.getPage(), criteria.getSize(), criteria.getSortBy(), criteria.getOrder());
        
        if (criteria.getType() != null) {
            log.debug("Filter by type: {}", criteria.getType());
        }
        if (criteria.getCategory() != null) {
            log.debug("Filter by category: {}", criteria.getCategory());
        }
        if (criteria.getStartDate() != null || criteria.getEndDate() != null) {
            log.debug("Filter by date range: {} to {}", criteria.getStartDate(), criteria.getEndDate());
        }

        Sort.Direction direction = criteria.getOrder().equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
                
        Pageable pageable = PageRequest.of(
                criteria.getPage(), 
                criteria.getSize(), 
                Sort.by(direction, criteria.getSortBy())
        );

        FinancialRecordSpecification spec = new FinancialRecordSpecification(criteria);
        Page<FinancialRecord> pageResult = financialRecordRepository.findAll(spec, pageable);

        List<FinancialRecordResponseDTO> dtos = pageResult.getContent().stream()
                .map(financialRecordMapper::toResponseDTO)
                .collect(Collectors.toList());

        log.info("Filter complete - returned {} of {} total records (page {} of {})",
                dtos.size(), pageResult.getTotalElements(), 
                pageResult.getNumber() + 1, pageResult.getTotalPages());

        return FilteredRecordsResponseDTO.builder()
                .records(dtos)
                .totalCount(pageResult.getTotalElements())
                .currentPage(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FinancialRecordResponseDTO getRecordById(Long id) {
        log.info("Fetching financial record with id: {}", id);
        
        FinancialRecord record = financialRecordRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Financial record not found with id: {}", id);
                    return new ResourceNotFoundException("Financial record not found with id: " + id);
                });
        
        log.debug("Found record - id: {}, type: {}, amount: {}", id, record.getType(), record.getAmount());
        
        return financialRecordMapper.toResponseDTO(record);
    }

    @Override
    @Transactional
    public FinancialRecordResponseDTO updateRecord(Long id, FinancialRecordRequestDTO requestDTO) {
        log.info("Updating financial record - id: {}, newAmount: {}, newType: {}",
                id, requestDTO.getAmount(), requestDTO.getType());

        FinancialRecord existingRecord = financialRecordRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to update - Record not found with id: {}", id);
                    return new ResourceNotFoundException("Financial record not found with id: " + id);
                });

        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> {
                    log.error("Failed to update - User not found with id: {}", requestDTO.getUserId());
                    return new ResourceNotFoundException("User not found with id: " + requestDTO.getUserId());
                });

        // Log what's changing
        log.debug("Updating record {} - amount: {} -> {}, type: {} -> {}, category: {} -> {}",
                id,
                existingRecord.getAmount(), requestDTO.getAmount(),
                existingRecord.getType(), requestDTO.getType(),
                existingRecord.getCategory(), requestDTO.getCategory());

        existingRecord.setAmount(requestDTO.getAmount());
        existingRecord.setType(requestDTO.getType());
        existingRecord.setCategory(requestDTO.getCategory());
        existingRecord.setDate(requestDTO.getDate());
        existingRecord.setNotes(requestDTO.getNotes());
        existingRecord.setUser(user);

        FinancialRecord updatedRecord = financialRecordRepository.save(existingRecord);

        log.info("Financial record updated successfully - id: {}", updatedRecord.getId());
        
        return financialRecordMapper.toResponseDTO(updatedRecord);
    }

    @Override
    @Transactional
    public void deleteRecord(Long id) {
        log.info("Soft deleting financial record with id: {}", id);

        FinancialRecord record = financialRecordRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to delete - Record not found with id: {}", id);
                    return new ResourceNotFoundException("Financial record not found with id: " + id);
                });

        log.debug("Soft deleting record - id: {}, type: {}, amount: {}, userId: {}",
                id, record.getType(), record.getAmount(), record.getUser().getId());

        financialRecordRepository.softDeleteById(id);
        
        log.info("Financial record soft deleted successfully - id: {}", id);
    }

    @Override
    @Transactional
    public FinancialRecordResponseDTO restoreRecord(Long id) {
        log.info("Restoring soft-deleted financial record with id: {}", id);

        FinancialRecord record = financialRecordRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> {
                    log.error("Failed to restore - Record not found with id: {}", id);
                    return new ResourceNotFoundException("Financial record not found with id: " + id);
                });

        if (!record.getIsDeleted()) {
            log.warn("Record {} is not deleted, cannot restore", id);
            throw new com.zorvyn.financedashboard.exception.BadRequestException(
                    "Record with id " + id + " is not deleted");
        }

        financialRecordRepository.restoreById(id);
        
        // Fetch fresh record after restore
        FinancialRecord restoredRecord = financialRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Failed to fetch restored record"));

        log.info("Financial record restored successfully - id: {}", id);
        
        return financialRecordMapper.toResponseDTO(restoredRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialRecordResponseDTO> getDeletedRecords() {
        log.info("Fetching all soft-deleted records");
        
        List<FinancialRecord> deletedRecords = financialRecordRepository.findAllDeleted();
        
        log.info("Found {} soft-deleted records", deletedRecords.size());
        
        return deletedRecords.stream()
                .map(financialRecordMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
