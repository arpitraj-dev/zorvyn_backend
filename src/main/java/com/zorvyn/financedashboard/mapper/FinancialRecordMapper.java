package com.zorvyn.financedashboard.mapper;

import com.zorvyn.financedashboard.dto.FinancialRecordRequestDTO;
import com.zorvyn.financedashboard.dto.FinancialRecordResponseDTO;
import com.zorvyn.financedashboard.entity.FinancialRecord;
import com.zorvyn.financedashboard.entity.User;
import org.springframework.stereotype.Component;

@Component
public class FinancialRecordMapper {

    /**
     * Convert FinancialRecordRequestDTO → FinancialRecord entity
     */
    public FinancialRecord toEntity(FinancialRecordRequestDTO dto, User user) {
        return FinancialRecord.builder()
                .amount(dto.getAmount())
                .type(dto.getType())
                .category(dto.getCategory())
                .date(dto.getDate())
                .notes(dto.getNotes())
                .user(user)
                .build();
    }

    /**
     * Convert FinancialRecord entity → FinancialRecordResponseDTO
     */
    public FinancialRecordResponseDTO toResponseDTO(FinancialRecord record) {
        return FinancialRecordResponseDTO.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .date(record.getDate())
                .notes(record.getNotes())
                .userId(record.getUser().getId())
                .userName(record.getUser().getName())
                .build();
    }
}
