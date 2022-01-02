package com.example.kukyemarketclone.repository.role;

import com.example.kukyemarketclone.entity.member.Role;
import com.example.kukyemarketclone.entity.member.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Optional<Role> findByRoleType(RoleType roleType);

}
