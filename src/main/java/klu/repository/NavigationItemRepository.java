package klu.repository;

import klu.model.NavigationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NavigationItemRepository extends JpaRepository<NavigationItem, Long> {}





