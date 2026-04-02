package com.zorvyn.financedashboard.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.zorvyn.financedashboard.entity.enums.Role;
import com.zorvyn.financedashboard.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "financialRecords")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FinancialRecord> financialRecords = new ArrayList<>();

    /**
     * Check if user account is active.
     */
    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }
}
