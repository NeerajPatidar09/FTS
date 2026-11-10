package com.lost.fts.menu.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    boolean existsByMenuCodeIgnoreCase(String menuCode);

    Optional<Menu> findByMenuCode(String menuCode);

    List<Menu> findAllByOrderByDisplayOrderAsc();

    List<Menu> findByActiveTrueOrderByDisplayOrderAsc();
}